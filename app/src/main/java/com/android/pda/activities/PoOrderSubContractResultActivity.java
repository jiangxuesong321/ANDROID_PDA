package com.android.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.PurchaseOrderSubContractAdapter;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.PurchaseOrderSubContractTask;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.PurchaseOrderSubContractController;
import com.android.pda.database.pojo.Offline;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.PurchaseOrderQuery;
import com.android.pda.models.PurchaseOrderSubContract;
import com.android.pda.models.PurchaseOrderSubContractResult;

import java.util.ArrayList;
import java.util.List;


public class PoOrderSubContractResultActivity extends AppCompatActivity implements ActivityInitialization,
        AdapterView.OnItemClickListener{

    private static final String INTENT_KEY_PO = "PO";
    private List<PurchaseOrderSubContract> purchaseOrderList;
    private List<PurchaseOrderSubContractResult> purchaseOrderResultList = new ArrayList<>();
    private final static int REQUESTCODE = 10000;
    private PurchaseOrderSubContractAdapter purchaseOrderAdapter;
    private ListView lvPurchase;
    private WaitDialog waitDialog;
    private PurchaseOrderQuery query;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderSubContractController purchaseOrderController = app.getPurchaseOrderSubContractController();
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private PurchaseOrderSubContractResult currentPurchaseOrderResult;
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private String functionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        getPoData();
        //initData();
    }

    public static Intent createIntent(Context context, PurchaseOrderQuery query, String functionId) {
        Intent intent = new Intent(context, PoOrderSubContractResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO, query);
        intent.putExtras(bundle);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, functionId);
        return intent;
    }

    @Override
    public void initView() {
        lvPurchase = findViewById(R.id.lv_purchase_order);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        purchaseOrderResultList = purchaseOrderController.groupPurchase(purchaseOrderList);
        showPurchaseData();
    }
    private void showPurchaseData() {
        purchaseOrderAdapter = new PurchaseOrderSubContractAdapter(getApplicationContext(), purchaseOrderResultList);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvPurchase.setDividerHeight(1);
        this.lvPurchase.setAdapter(purchaseOrderAdapter);
        this.lvPurchase.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
               //Refresh Data
                getPoData();
                purchaseOrderAdapter.notifyDataSetChanged();
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
        query = (PurchaseOrderQuery)this.getIntent().getSerializableExtra(INTENT_KEY_PO);
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
    }

    private void getPoData(){
        waitDialog.showWaitDialog(PoOrderSubContractResultActivity.this);
        PurchaseOrderSubContractTask task = new PurchaseOrderSubContractTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderSubContractResultActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderSubContractResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);

            }

            @Override
            public void bindModel(Object o) {
                List<PurchaseOrderSubContract> purchaseOrders = (List<PurchaseOrderSubContract>) o;
                if(purchaseOrders != null && purchaseOrders.size() > 0){
                    purchaseOrderList = purchaseOrders;
                    initData();
                }else{
                    displayDialog(getString(R.string.text_service_no_result_po), AppConstants.REQUEST_FAILED, 1);
                }
            }
        }, query, functionId);
        task.execute();
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
                    List<PurchaseOrderSubContract> orders =(List<PurchaseOrderSubContract>) JSON.parseArray(offline.getOrderBody(), PurchaseOrderSubContract.class);
                    startActivityForResult(PoOrderSubContractDetailActivity.createIntent(app, orders, functionId), REQUESTCODE);
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_FAILED == action){
                    finish();
                }
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(functionId);
                    startActivityForResult(PoOrderSubContractDetailActivity.createIntent(app, currentPurchaseOrderResult.getPurchaseOrders(),
                            functionId), REQUESTCODE);
                }
            }
        });
        noticeDialog.create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(purchaseOrderResultList != null && purchaseOrderResultList.size() > 0){
            currentPurchaseOrderResult = purchaseOrderResultList.get(position);
        }
        offline = offlineController.getOfflineData(functionId);
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }else{
            startActivityForResult(PoOrderSubContractDetailActivity.createIntent(app, currentPurchaseOrderResult.getPurchaseOrders(),
                    functionId), REQUESTCODE);
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