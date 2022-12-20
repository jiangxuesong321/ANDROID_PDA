package com.sunmi.pda.utils;

import android.content.Context;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrintUtil {


    public static String ftpUpload(Context context) throws IOException {
        String url = "172.28.19.74";
        String port = "21";
        String username = "us";
        String password = "Delaware01";
        String text = "%BTW% /AF=C:\\label\\format\\test.btw /D=%Trigger File Name% /R=3 /P /DD\n" +
                "%END%\n" +
                "PO code;10102345134341341;";
        FTPClient ftpClient = new FTPClient();
        InputStream is = null;
        FileInputStream fis = null;
        String returnMessage = "0";
        try {
            ftpClient.connect(url, Integer.parseInt(port));
            boolean loginResult = ftpClient.login(username, password);
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                is = new ByteArrayInputStream(text.getBytes("UTF-8"));
                ftpClient.storeFile("david_test.dat", is);

                returnMessage = "1";   //上传成功
            } else {// 如果登录失败
                returnMessage = "0";
            }

        } catch (IOException e) {
            e.printStackTrace();
            returnMessage = "-1";
            throw new RuntimeException("FTP客户端出错！", e);

        } finally {
            //IOUtils.closeQuietly(fis);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return returnMessage;
    }
}
