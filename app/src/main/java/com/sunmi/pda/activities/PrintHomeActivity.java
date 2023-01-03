package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.PrintController;
import com.sunmi.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;


public class PrintHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String INTENT_KEY_LabelFlag = "LabelFlag";
    private EditText etPrintSearchValue;
    private String labelFlag;
    private final static int REQUESTCODE = 10001;
    private TextView tvPrintSearchLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context, String labelFlag) {
        Intent intent = new Intent(context, PrintHomeActivity.class);
        intent.putExtra(INTENT_KEY_LabelFlag, labelFlag);
        return intent;
    }
    @Override
    public void initView() {
        etPrintSearchValue = findViewById(R.id.et_print_search_value);
        tvPrintSearchLabel = findViewById(R.id.tv_print_search_label);
    }


    @Override
    public void initData() {
        String number = "";
        if(StringUtils.equalsIgnoreCase(labelFlag, PrintController.SHIPPING_LABEL)){
            getSupportActionBar().setTitle(R.string.text_shipping_label);
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SHIPPING_LABEL);
            tvPrintSearchLabel.setText(getString(R.string.text_delivery_document));

        }else{
            getSupportActionBar().setTitle(R.string.text_receive_label);
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.RECEIVE_LABEL_INPUT_RECEIVE_LABEL);
            tvPrintSearchLabel.setText(getString(R.string.text_purchase_document));
        }
        etPrintSearchValue.setText(number);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }



    @Override
    public void initIntent() {
        labelFlag = getIntent().getStringExtra(INTENT_KEY_LabelFlag);
    }

    public void download(View view){
        String number = etPrintSearchValue.getText().toString();
        if(StringUtils.equalsIgnoreCase(labelFlag, PrintController.SHIPPING_LABEL)){
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SHIPPING_LABEL, number);
        }else{
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.RECEIVE_LABEL_INPUT_RECEIVE_LABEL, number);
        }

        startActivityForResult(PrintResultActivity.createIntent(app, number, labelFlag), REQUESTCODE);
    }
    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        //for offline
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }

        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }
            @Override
            public void callClose() {

            }
        });
        noticeDialog.create();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                //Refresh Data

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}