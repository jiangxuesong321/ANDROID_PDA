package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;


import android.widget.EditText;


import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.models.BusinessOrderQuery;

import com.sunmi.pda.models.DeliveryStatus;
import com.sunmi.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class PrototypeBorrowHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static SunmiApplication app = SunmiApplication.getInstance();
    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;

    private EditText etPrototypeBorrowValue;

    private final static int REQUESTCODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototype_borrow_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initData();
        //for offline
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, PrototypeBorrowHomeActivity.class);
        return intent;
    }
    @Override
    public void initView() {
        etPrototypeBorrowValue = findViewById(R.id.et_prototype_borrow_value);
    }


    @Override
    public void initData() {
        //for offline
        offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_LEND);
        String number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PROTOTYPE_BORROW);
        etPrototypeBorrowValue.setText(number);
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

    public void download(View view){
        String number = etPrototypeBorrowValue.getText().toString();
        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PROTOTYPE_BORROW, number);
        if(StringUtils.isEmpty(number)){
            displayDialog(getString(R.string.text_input_borrow_number), AppConstants.REQUEST_STAY, 1);
            //Toast.makeText(this, "请输入发货单!", Toast.LENGTH_SHORT).show();
        } else {
            //for offline
            offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_LEND);
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else{
                BusinessOrderQuery query = new BusinessOrderQuery(number);
                List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
                deliveryStatuses.add(new DeliveryStatus("A", ""));
                query.setDeliveryStatuses(deliveryStatuses);
                startActivityForResult(PrototypeBorrowDetailActivity.createIntent(app, query, null), REQUESTCODE);
            }
        }
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
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    startActivityForResult(PrototypeBorrowDetailActivity.createIntent(app, null, offline), REQUESTCODE);
                }
            }
            @Override
            public void callClose() {
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_LEND);
                    /*String number = etPrototypeBorrowValue.getText().toString();
                    BusinessOrderQuery query = new BusinessOrderQuery(number);
                    startActivityForResult(PrototypeBorrowDetailActivity.createIntent(app, query, null), REQUESTCODE);*/
                }
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