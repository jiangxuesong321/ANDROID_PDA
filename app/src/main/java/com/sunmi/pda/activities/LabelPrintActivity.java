package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AndroidApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ZPL.ZPLPrinterHelper;


public class LabelPrintActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String ACTION_DATA_CODE_RECEIVED = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
    private static final String DATA = "data";
    private static final String SOURCE = "source_byte";
    private RelativeLayout relativeLayoutLabel;
    private WaitDialog waitDialog;
    private EditText edtIP=null;
    private EditText edtPort, txtWifiCode;
    private ZPLPrinterHelper hPRTPrinter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lable_print);
        initView();
        hPRTPrinter = ZPLPrinterHelper.getZPL(getApplicationContext());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void print(View view) {
        /*relativeLayoutLabel.setDrawingCacheEnabled(true);
        relativeLayoutLabel.buildDrawingCache();

        final Bitmap bmp = relativeLayoutLabel.getDrawingCache(); // 获取图片
        savePicture(bmp, "test.jpg");// 保存图片*/
        waitDialog.showWaitDialog(LabelPrintActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*try {
                    String result =  PrintUtil.ftpUpload(getApplicationContext());
                    System.out.println("result---->" + result);
                    waitDialog.hideWaitDialog(LabelPrintActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        }).start();
    }

    public void savePicture(Bitmap bm, String fileName) {
        String state = Environment.getExternalStorageState();

        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (null == bm) {
            return;
        }
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File myCaptureFile = new File(folder, fileName);
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            //压缩保存到本地
            bm.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bos.flush();
            bos.close();
            Uri uri = Uri.fromFile(myCaptureFile);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(uri);
            getApplication().sendBroadcast(mediaScanIntent);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "保存成功!", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void initView() {
        //relativeLayoutLabel = findViewById(R.id.rl_label);
        waitDialog = new WaitDialog();
        edtIP = (EditText) findViewById(R.id.txtIPAddress);
        edtPort = (EditText) findViewById(R.id.txtWifiPort);
        txtWifiCode = (EditText) findViewById(R.id.txtWifiCode);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LabelPrintActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    public void onClickConnect(View view) {

        try {
            if(hPRTPrinter!=null) {
                hPRTPrinter.PortClose();
            }

            final String strIP = edtIP.getText().toString().trim();
            final String strPort = edtPort.getText().toString().trim();
            if(strIP.length()==0) {
                Toast.makeText(getApplicationContext(), "Please input IP address", Toast.LENGTH_SHORT).show();
                return;
            }
            waitDialog.showWaitDialog(LabelPrintActivity.this);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        if(hPRTPrinter.PortOpen("WiFi,"+strIP+","+strPort)!=0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    waitDialog.hideWaitDialog(LabelPrintActivity.this);
                                    Toast.makeText(getApplicationContext(), "Connect Error!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    waitDialog.hideWaitDialog(LabelPrintActivity.this);
                                    Toast.makeText(getApplicationContext(), "Connect Succeed!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitDialog.hideWaitDialog(LabelPrintActivity.this);
                                Toast.makeText(getApplicationContext(), "Connect Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();
        }
        catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waitDialog.hideWaitDialog(LabelPrintActivity.this);
                    Toast.makeText(getApplicationContext(), "Connect Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onClickCancel(View view) {
        try {
            if(hPRTPrinter!=null) {
                hPRTPrinter.PortClose();
            }
            this.finish();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cancel Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickPrint(View view){
        if(txtWifiCode.getText().toString().trim().length() == 0){
            Toast.makeText(getApplicationContext(), "Please input code", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            System.out.println("print....");
           /* int x = UtilityTooth.chackEdtextArea(getApplicationContext(), txtqrcode_x, 0, 9999, getString(R.string.activity_parameter_error));
            if(x==-1){
                return;
            }
            int y = UtilityTooth.chackEdtextArea(getApplicationContext(), txtqrcode_y, 0, 9999, getString(R.string.activity_parameter_error));
            if(y==-1){
                return;
            }*/
            hPRTPrinter.start();
            hPRTPrinter.printData("^CI14\r\n");
            hPRTPrinter.printQRcode(""+10,""+20, "N", "2", ""+3, txtWifiCode.getText().toString());
            hPRTPrinter.end();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
