package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;

import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.PurchaseOrderTask;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.PurchaseOrderController;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.PurchaseOrderQuery;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;


import org.apache.commons.lang3.StringUtils;


import java.util.List;

public class PoReturnHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static SunmiApplication app = SunmiApplication.getInstance();


    private EditText etPoNumber;
    private EditText etYearValue;

    private final static int REQUESTCODE = 10001;
    private WaitDialog waitDialog;
    private static final PurchaseOrderController purchaseOrderController = app.getPurchaseOrderController();
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private PurchaseOrderQuery purchaseOrderQuery;
    private String functionId;
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private List<PurchaseOrder> purchaseOrderList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initListener();
        initData();
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }
    }

    public static Intent createIntent(Context context, String functionId) {
        Intent intent = new Intent(context, PoReturnHomeActivity.class);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, functionId);
        return intent;
    }

    @Override
    public void initView() {

        etPoNumber = findViewById(R.id.et_po_number);
        etYearValue = findViewById(R.id.et_year_value);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
        String number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_RETURN);
        etPoNumber.setText(number);
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
        etYearValue.setText(DateUtils.getYear() + "");
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
                    List<PurchaseOrder> orders =(List<PurchaseOrder>) JSON.parseArray(offline.getOrderBody(), PurchaseOrder.class);
                    startActivityForResult(PoOrderDetailActivity.createIntent(app, orders, functionId), REQUESTCODE);
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
                    //startActivityForResult(PoOrderResultActivity.createIntent(app, purchaseOrderQuery), REQUESTCODE);
                }
            }
        });
        noticeDialog.create();
    }
    public void download(View view){

    }

    public void search(View view){
        String number = etPoNumber.getText().toString();
        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_RETURN, number);
        String year = etYearValue.getText().toString();

        if(StringUtils.isEmpty(year)){
            displayDialog(getString(R.string.text_input_year), AppConstants.REQUEST_STAY, 1);
            return;
        }

        purchaseOrderQuery = new PurchaseOrderQuery(number, year);
        if(StringUtils.isEmpty(number)){
            displayDialog(getString(R.string.text_input_material_document_number), AppConstants.REQUEST_STAY, 1);
            return;
        }

        offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }else{
            getPoReturnData(purchaseOrderQuery);
        }

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

    public void clearPo(View view){
        etPoNumber.setText("");
    }

    public void clearSupplier(View view){
        etYearValue.setText("");
    }

    private void getPoReturnData(PurchaseOrderQuery query){
        waitDialog.showWaitDialog(PoReturnHomeActivity.this);
        PurchaseOrderTask task = new PurchaseOrderTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoReturnHomeActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoReturnHomeActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);

            }

            @Override
            public void bindModel(Object o) {
                List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>) o;
                if(purchaseOrders != null && purchaseOrders.size() > 0){
                    System.out.println("bindModel------");
                    purchaseOrderList = purchaseOrders;
                    startActivityForResult(PoOrderDetailActivity.createIntent(app, purchaseOrders, functionId), REQUESTCODE);

                }else{
                    displayDialog(getString(R.string.text_service_no_result_po), AppConstants.REQUEST_FAILED, 1);
                }
            }
        }, query, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
        task.execute();
    }

}