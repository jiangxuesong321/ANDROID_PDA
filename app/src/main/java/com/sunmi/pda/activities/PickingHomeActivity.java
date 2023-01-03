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
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.models.BusinessOrderQuery;
import com.sunmi.pda.models.DeliveryStatus;
import com.sunmi.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class PickingHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private EditText etPickingValue;
    private WaitDialog waitDialog;
    private TextView tvPickingLabel;
    private final static int REQUESTCODE = 10001;
    private int functionId;
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }
    }

    public static Intent createIntent(Context context, int id) {
        Intent intent = new Intent(context, PickingHomeActivity.class);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, id);
        return intent;
    }


    @Override
    public void initView() {

        etPickingValue = findViewById(R.id.et_picking_value);
        tvPickingLabel = findViewById(R.id.tv_picking_label);
        if(functionId == 5){
            getSupportActionBar().setTitle(getString(R.string.text_picking_material));
            tvPickingLabel.setText(getString(R.string.text_picking_order_material));
        }
    }

    @Override
    public void initData() {
        String number = "";
        if(functionId == 5){
            offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PICKING);
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PICKING_MATERIAL);
        }else{
            offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PICKING_MATERIAL);
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PICKING);
        }
        etPickingValue.setText(number);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }



    @Override
    public void initIntent() {
        functionId = getIntent().getIntExtra(INTENT_KEY_FUNCTION_ID, 0);
    }

    public void search(View view){
        String number = etPickingValue.getText().toString();
        if(functionId == 5){
            offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PICKING);
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PICKING_MATERIAL, number);
        }else{
            offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PICKING_MATERIAL);
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PICKING, number);
        }
        if(StringUtils.isEmpty(number)){
            displayDialog(getString(R.string.text_input_borrow_number), AppConstants.REQUEST_STAY, 1);
        } else {
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else{
                BusinessOrderQuery query = new BusinessOrderQuery(number);
                List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
                deliveryStatuses.add(new DeliveryStatus("A", ""));
                query.setDeliveryStatuses(deliveryStatuses);
                startActivityForResult(PickingDetailActivity.createIntent(app, query, functionId, null), REQUESTCODE);
            }
        }
    }
    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    startActivityForResult(PickingDetailActivity.createIntent(app, null, functionId, offline), REQUESTCODE);
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    if(functionId == 5){
                        offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PICKING);
                    }else{
                        offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PICKING_MATERIAL);
                    }
                    /*String number = etPickingValue.getText().toString();
                    BusinessOrderQuery query = new BusinessOrderQuery(number);
                    startActivityForResult(PickingDetailActivity.createIntent(app, query, functionId, null), REQUESTCODE);*/
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