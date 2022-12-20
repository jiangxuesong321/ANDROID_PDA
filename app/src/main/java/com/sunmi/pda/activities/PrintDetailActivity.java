package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.DialogPrintSetting;
import com.sunmi.pda.activities.view.DialogStockTransferInput;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.PrintController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.PrintLabel;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import ZPL.ZPLPrinterHelper;


public class PrintDetailActivity extends AppCompatActivity implements ActivityInitialization{
    private final static SunmiApplication app = SunmiApplication.getInstance();

    private WaitDialog waitDialog;

    private ZPLPrinterHelper zplPrinterHelper;
    private static final String INTENT_KEY_ORDER = "Order";
    private static final String INTENT_KEY_LabelFlag = "LabelFlag";
    private static String strIP = "192.168.3.253";
    private final static String strPort = "9100";
    private EditText tvCodeValue, tvNameValue, tvSpecValue, tvModelValue, tvUnitValue,
            tvQuantityValue, tvBatchValue, tvDateValue, tvOrderValue, tvRemarksValue, tvCountValue;
    private PrintLabel printLabel;
    private String labelFlag;
    private User user;
    private Login login;
    private static final UserController userController = app.getUserController();
    private static final LoginController loginController = app.getLoginController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_detail);
        initView();
        initIntent();
        initData();
        zplPrinterHelper = ZPLPrinterHelper.getZPL(getApplicationContext());
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
        connect();
        //print();

    }


    @Override
    public void initView() {
        //relativeLayoutLabel = findViewById(R.id.rl_label);
        waitDialog = new WaitDialog();
        tvCodeValue = findViewById(R.id.tv_code_value);
        tvNameValue = findViewById(R.id.tv_name_value);
        tvSpecValue = findViewById(R.id.tv_spec_value);
        tvModelValue = findViewById(R.id.tv_model_value);
        tvUnitValue = findViewById(R.id.tv_unit_value);
        tvQuantityValue = findViewById(R.id.tv_quantity_value);
        tvBatchValue = findViewById(R.id.tv_batch_value);
        tvDateValue = findViewById(R.id.tv_date_value);
        tvOrderValue = findViewById(R.id.tv_order_value);
        tvRemarksValue = findViewById(R.id.tv_remarks_value);
        tvCountValue = findViewById(R.id.tv_count_value);

    }

    @Override
    public void initData() {
        login = loginController.getLoginUser();
        user = userController.getUserById(login.getZuid());
        if(printLabel != null){
            tvCodeValue.setText(printLabel.getMaterial());
            tvCodeValue.setEnabled(false);

            tvNameValue.setText(printLabel.getMaterialDes());
            tvNameValue.setEnabled(false);

            tvSpecValue.setText(printLabel.getSpec());
            tvSpecValue.setEnabled(false);

            tvModelValue.setText(printLabel.getModel());
            tvModelValue.setEnabled(false);

            tvUnitValue.setText(printLabel.getUnitDesciption());
            tvUnitValue.setEnabled(false);

            tvQuantityValue.setText(printLabel.getQuantity());
            tvBatchValue.setText(printLabel.getBatch());

            tvDateValue.setText(printLabel.getPrintDate());
            tvDateValue.setEnabled(false);
            if(StringUtils.equalsIgnoreCase(labelFlag, PrintController.SHIPPING_LABEL)){
                tvOrderValue.setText(printLabel.getSalesDocument());
                tvRemarksValue.setText(printLabel.getCustomerMaterial());

            } else if(StringUtils.equalsIgnoreCase(labelFlag, PrintController.RECEIVE_LABEL)){
                tvOrderValue.setText(printLabel.getPurchaseDocument());
                tvOrderValue.setEnabled(false);
                tvRemarksValue.setText(printLabel.getPurchaseDocument());
            }
        }
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {
        printLabel = (PrintLabel) this.getIntent().getSerializableExtra(INTENT_KEY_ORDER);
        labelFlag = this.getIntent().getStringExtra(INTENT_KEY_LabelFlag);
    }
    public static Intent createIntent(Context context, PrintLabel printLabel, String labelFlag) {
        Intent intent = new Intent(context, PrintDetailActivity.class);
        intent.putExtra(INTENT_KEY_LabelFlag, labelFlag);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_ORDER, printLabel);
        intent.putExtras(bundle);
        return intent;
    }

    public void connect() {

        try {

            waitDialog.showWaitDialog(PrintDetailActivity.this);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        if(zplPrinterHelper != null) {
                            zplPrinterHelper.PortClose();
                        }
                        if(StringUtils.isNotEmpty(AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PRINT_IP))){
                            strIP = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PRINT_IP);
                        }
                        if(strIP.length() == 0) {
                            Toast.makeText(getApplicationContext(), "请输入ip地址", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(zplPrinterHelper.PortOpen("WiFi,"+strIP+","+strPort)!=0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    waitDialog.hideWaitDialog(PrintDetailActivity.this);
                                    Toast.makeText(getApplicationContext(), "连接打印机失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    waitDialog.hideWaitDialog(PrintDetailActivity.this);
                                    Toast.makeText(getApplicationContext(), "连接打印机成功，正在打印请稍后！", Toast.LENGTH_SHORT).show();

                                }
                            });
                            print();
                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitDialog.hideWaitDialog(PrintDetailActivity.this);
                                Toast.makeText(getApplicationContext(), "连接打印机异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    waitDialog.hideWaitDialog(PrintDetailActivity.this);
                    Toast.makeText(getApplicationContext(), "连接打印机异常 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void print(){
        String code = tvCodeValue.getText().toString();
        String name = tvNameValue.getText().toString();
        String spec = tvSpecValue.getText().toString();
        if(spec.length() > 30){
            spec = spec.substring(0, 30);
        }
        String model = tvModelValue.getText().toString();
        String unit = tvUnitValue.getText().toString();
        String quantity = tvQuantityValue.getText().toString();
        String batch = tvBatchValue.getText().toString();
        String date = tvDateValue.getText().toString();
        String order = tvOrderValue.getText().toString();
        String remarks = tvRemarksValue.getText().toString();
        String count = tvCountValue.getText().toString();
        final String labelX = "60";
        final String valueX = "240";
        String title = "SUNMI";
        try {
            zplPrinterHelper.start();
            if (user != null && StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_chuantian))) {
                title = "CITAQ";
            }
            zplPrinterHelper.printText(labelX,"20",0,"N",4,title);
            zplPrinterHelper.printData("^CI14\r\n");
            zplPrinterHelper.printText("350","10",7,"N",3,"货物标签");

            zplPrinterHelper.printData("^CI14\r\n");
            zplPrinterHelper.printText(labelX,"70",7,"N",2, app.getString(R.string.text_print_code));
            zplPrinterHelper.printBarcode(valueX,"70",0,"N","50","Y",code);

            zplPrinterHelper.printText(labelX,"170",7,"N",2, app.getString(R.string.text_print_name));
            List<String> nameList = Util.getPrintLineBreaks(name);
            int index = 1;
            for(String str : nameList){
                zplPrinterHelper.printText(valueX,String.valueOf(140 + 30 * index),7,"N",2,str);
                index ++;
            }

            zplPrinterHelper.printText(labelX,String.valueOf(170 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_spec));
            zplPrinterHelper.printText(valueX,String.valueOf(170 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2,spec);

            zplPrinterHelper.printText(labelX,String.valueOf(200 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_model));
            zplPrinterHelper.printText(valueX,String.valueOf(200 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2,model);

            zplPrinterHelper.printText(labelX,String.valueOf(230 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_unit));
            zplPrinterHelper.printText(valueX,String.valueOf(230 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2,unit);

            zplPrinterHelper.printText(labelX,String.valueOf(260 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_quantity));
            zplPrinterHelper.printText(valueX,String.valueOf(260 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, quantity);

            zplPrinterHelper.printText(labelX,String.valueOf(290 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_batch));
            if(batch.length() > 0){
                zplPrinterHelper.printBarcode(valueX,String.valueOf(290 + 30 * (nameList.size() == 0? 1: nameList.size())),0,"N","50","Y",batch);
            }
            zplPrinterHelper.printText(labelX,String.valueOf(batch.length() > 0? 420 : 320 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_date));
            zplPrinterHelper.printText(valueX,String.valueOf(batch.length() > 0? 420 : 320 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2,date);

            zplPrinterHelper.printText(labelX,String.valueOf(batch.length() > 0? 450 : 350 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_order));
            zplPrinterHelper.printText(valueX,String.valueOf(batch.length() > 0? 450 : 350 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2,order);
            List<String> remarkList = Util.getPrintLineBreaks(remarks);
            zplPrinterHelper.printText(labelX,String.valueOf(batch.length() > 0? 480 : 380 + 30 * (nameList.size() == 0? 1: nameList.size())),7,"N",2, app.getString(R.string.text_print_remarks));
            System.out.println("remarkList---->" + remarkList);
            index = 0;
            for(String str : remarkList){
                if(index == 0){
                    int y = batch.length() > 0? 480 : 380 + 30  * (nameList.size() == 0? 1: nameList.size());
                    System.out.println("y1---->" + y);
                    zplPrinterHelper.printText(valueX,String.valueOf(y),7,"N",2,str);
                }else{
                    int y = batch.length() > 0? 480 + (30 * index) : 380 + (30 *  (nameList.size() == 0? 1 + index: nameList.size() + index));
                    System.out.println("y2---->" + y);
                    zplPrinterHelper.printText(valueX,String.valueOf(y),7,"N",2,str);
                }
                index ++;
            }
            zplPrinterHelper.PQ(count,"1","1","Y");
            zplPrinterHelper.end();
            /*try {
                zplPrinterHelper.PortClose();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "断开打印机失败！", Toast.LENGTH_SHORT).show();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("HPRTSDKSample", (new StringBuilder("Activity_1DBarcodes --> onClickPrint ")).append(e.getMessage()).toString());
        }
    }

    public void onClickCancel(View view) {
        try {
            if(zplPrinterHelper!=null) {
                zplPrinterHelper.PortClose();
            }
            this.finish();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cancel Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.print_setting, menu);
        //menu.add(userController.getLoginUser().getUserId());
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogPrintSetting dialogPrintSetting = new DialogPrintSetting(this);
        dialogPrintSetting.show();
        return super.onOptionsItemSelected(item);
    }
}
