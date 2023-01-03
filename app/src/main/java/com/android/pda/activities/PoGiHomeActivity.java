package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.OfflineController;

import com.android.pda.controllers.PurchaseOrderGiController;
import com.android.pda.database.pojo.Offline;
import com.android.pda.models.PurchaseOrderGi;
import com.android.pda.models.PurchaseOrderGr;
import com.android.pda.models.PurchaseOrderQuery;
import com.android.pda.utils.AppUtil;


import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PoGiHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();

    private EditText etPoNumber;

    private WaitDialog waitDialog;
    private final static int REQUESTCODE = 10001;
    private String functionId;
    private static final PurchaseOrderGiController purchaseOrderController = app.getPurchaseOrderGiController();
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private PurchaseOrderQuery purchaseOrderQuery;
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private LinearLayout llBtns, llBtnGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_gi_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();

    }

    public static Intent createIntent(Context context, String functionId) {
        Intent intent = new Intent(context, PoGiHomeActivity.class);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, functionId);
        return intent;
    }

    @Override
    public void initView() {
        etPoNumber = findViewById(R.id.et_po_number);
        llBtns = findViewById(R.id.ll_btns);
        llBtnGroup = findViewById(R.id.ll_btn_group);
        waitDialog = new WaitDialog();
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
            llBtnGroup.setVisibility(View.VISIBLE);
            llBtns.setVisibility(View.GONE);
            getSupportActionBar().setTitle(getString(R.string.text_po_gi));
        }else{
            llBtnGroup.setVisibility(View.GONE);
            llBtns.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getString(R.string.text_po_gr));
        }
    }

    @Override
    public void initData() {
        String number = "";
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR)){
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_PGR);
        }else{
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_GI);
        }        etPoNumber.setText(number);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }



    @Override
    public void initIntent() {
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
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
                    if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_COMPLETE)){
                        List<PurchaseOrderGr> orders =(List<PurchaseOrderGr>) JSON.parseArray(offline.getOrderBody(), PurchaseOrderGr.class);

                        startActivityForResult(PoOrderGrDetailActivity.createIntent(app, orders, functionId), REQUESTCODE);
                    }else{
                        List<PurchaseOrderGi> orders =(List<PurchaseOrderGi>) JSON.parseArray(offline.getOrderBody(), PurchaseOrderGi.class);
                        startActivityForResult(PoOrderGiDetailActivity.createIntent(app, orders, functionId), REQUESTCODE);
                    }
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(functionId);
                }
            }
        });
        noticeDialog.create();
    }
    public void download(View view){

    }

    public void search(View view){
        functionId = AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI;
        String lastInputKey = AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_GI;
        giPgrParts(lastInputKey);
    }

    public void pgr(View view){
        functionId = AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS;
        String lastInputKey = AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_PGR;
        giPgrParts(lastInputKey);
    }

    //整机
    public void gr(View view){
        String number = etPoNumber.getText().toString();
        functionId = AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_COMPLETE;
        String lastInputKey = AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_PGR;
        AppUtil.saveLastInput(getApplicationContext(), lastInputKey, number);
        purchaseOrderQuery = new PurchaseOrderQuery(number);
        String error = purchaseOrderController.verifyData(purchaseOrderQuery);
        if(StringUtils.isEmpty(error)){
            offline = offlineController.getOfflineData(functionId);
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else{
                startActivityForResult(PoOrderGrResultActivity.createIntent(app, purchaseOrderQuery, functionId), REQUESTCODE);
            }
        }else{
            displayDialog(error, AppConstants.REQUEST_STAY, 1);
        }
    }

    //生产订单发料&生产订单拆解
    private void giPgrParts(String lastInputKey){
        String number = etPoNumber.getText().toString();

        AppUtil.saveLastInput(getApplicationContext(), lastInputKey, number);
        purchaseOrderQuery = new PurchaseOrderQuery(number);

        String error = purchaseOrderController.verifyData(purchaseOrderQuery);

        if(StringUtils.isEmpty(error)){
            offline = offlineController.getOfflineData(functionId);
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else{
                startActivityForResult(PoOrderGiResultActivity.createIntent(app, purchaseOrderQuery, functionId), REQUESTCODE);
            }
        }else{
            displayDialog(error, AppConstants.REQUEST_STAY, 1);
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

}