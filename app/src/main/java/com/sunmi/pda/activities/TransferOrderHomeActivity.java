package com.sunmi.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.models.DeliveryStatus;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class TransferOrderHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static String functionId;
    private EditText etTransferOrderNr;
    private WaitDialog waitDialog;
    private final static int REQUESTCODE = 20001;
    private final static int REQUESTCODE_RECEIVE = 20002;

    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    public static Offline offline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_order_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initListener();
        initData();

        if (functionId == AppConstants.FUNCTION_ID_TRANSFER_OUT) {
            getSupportActionBar().setTitle(R.string.title_transfer_order);
        } else {
            getSupportActionBar().setTitle(R.string.title_transfer_order_detail_receive);
        }

        //for offline
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }
    }

    public static Intent createIntent(Context context, String funcId) {
        functionId = funcId;
        Intent intent = new Intent(context, TransferOrderHomeActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        etTransferOrderNr= findViewById(R.id.et_transfer_order_number);
        waitDialog = new WaitDialog();
    }

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initData() {
        String number = "";
        //for offline
        offline = offlineController.getOfflineData(functionId);
        if (functionId == AppConstants.FUNCTION_ID_TRANSFER_OUT) {
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_TRANSFER_ORDER);
        } else {
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_TRANSFER_ORDER_RECEIVE);
        }
        etTransferOrderNr.setText(number);
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

    public void search(View view){
        String number = etTransferOrderNr.getText().toString();
        if (functionId == AppConstants.FUNCTION_ID_TRANSFER_OUT) {
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_TRANSFER_ORDER, number);
        } else {
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_TRANSFER_ORDER_RECEIVE, number);
        }

        if(number == null || number.isEmpty()){
            displayDialog(app.getString(R.string.error_order_number_required), AppConstants.REQUEST_STAY, 1);
        } else {
            List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
            deliveryStatuses.add(new DeliveryStatus("A", ""));
            SalesInvoiceQuery query = new SalesInvoiceQuery(number, "", "", deliveryStatuses);
            //for offline
            offline = offlineController.getOfflineData(functionId);
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else {
                if (functionId == AppConstants.FUNCTION_ID_TRANSFER_OUT) {
                    startActivityForResult(TransferOrderDetailActivity.createIntent(app, query), REQUESTCODE);
                } else if (functionId == AppConstants.FUNCTION_ID_TRANSFER_IN) {
                    startActivityForResult(TransferOrderReceiveDetailActivity.createIntent(app, query), REQUESTCODE_RECEIVE);
                }
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
                    if (functionId == AppConstants.FUNCTION_ID_TRANSFER_OUT) {
                        startActivityForResult(TransferOrderDetailActivity.createIntent(app, null), REQUESTCODE);
                    } else if (functionId == AppConstants.FUNCTION_ID_TRANSFER_IN) {
                        startActivityForResult(TransferOrderReceiveDetailActivity.createIntent(app, null), REQUESTCODE_RECEIVE);
                    }
                }
            }
            @Override
            public void callClose() {
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(functionId);
                    search(null);
                }
                if(AppConstants.REQUEST_SUCCEED == action){
                    setResult(RESULT_OK);
                    finish();
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