package com.sunmi.pda.log;

import android.os.Environment;

import com.sunmi.pda.application.SunmiApplication;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class LogFileManager {
    private static final int LOG_FILES_MAX_NUM = 10; //文件最多有5个
    private static final int LOG_FILE_MAX_SIZE = 1000 * 1000; //文件最大1MB
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final SimpleDateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");

    private File mCurrentLogFile;
    private String mLogFileDir;

    LogFileManager(String logFileDir) {
        mLogFileDir = logFileDir;//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public void writeLogToFile(String logMessage) {
        LogUtils.i("writeLogToFile", "writeLogToFile.....");
        if (mCurrentLogFile == null || mCurrentLogFile.length() >= LOG_FILE_MAX_SIZE) {
            mCurrentLogFile = getNewLogFile();
        }
        try{
            if(mCurrentLogFile != null){
                FileUtils.writeToFile("\n" + logMessage, mCurrentLogFile.getPath());
                FileUtils.notifySystemToScan(mCurrentLogFile, app);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private File getNewLogFile() {
        //LogUtils.e("getNewLogFile", "getNewLogFile.....");
        File dir = new File(mLogFileDir);
        File[] files = dir.listFiles(fileFilter);
        if (files == null || files.length == 0) {
            // 创建新文件
            return createNewLogFile();
        }
        List<File> sortedFiles = sortFiles(files);
        if (files.length > LOG_FILES_MAX_NUM) {
            // 删掉最老的文件
            FileUtils.delete(sortedFiles.get(0));
        }
        // 取最新的文件，看写没写满
        File lastLogFile = sortedFiles.get(sortedFiles.size() - 1);
        if (lastLogFile.length() < LOG_FILE_MAX_SIZE) {
            return lastLogFile;
        } else {
            // 创建新文件
            return createNewLogFile();
        }
    }

    private File createNewLogFile() {
        return FileUtils.createFile(mLogFileDir + "/Log_" + LOG_FILE_DATE_FORMAT.format(new Date()) + ".txt");
    }

    private List<File> sortFiles(File[] files) {
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new FileComparator());
        return fileList;
    }

    private class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if(file1.lastModified() < file2.lastModified()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    private FileFilter fileFilter = new FileFilter() {
        public boolean accept(File file) {
            String tmp = file.getName().toLowerCase();
            if (tmp.startsWith("log") && tmp.endsWith(".txt")) {
                return true;
            }
            return false;
        }
    };
}
