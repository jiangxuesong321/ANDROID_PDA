package com.android.pda.activities;



import android.content.Context;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.PurchaseOrderAdapter;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.PurchaseOrderTask;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.PurchaseOrderController;
import com.android.pda.database.pojo.Offline;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.PurchaseOrderQuery;
import com.android.pda.models.PurchaseOrderResult;



import java.util.ArrayList;

import java.util.List;


public class PoOrderResultActivity extends AppCompatActivity implements ActivityInitialization,
        AdapterView.OnItemClickListener{

    private static final String INTENT_KEY_PO = "PO";
    private List<PurchaseOrder> purchaseOrderList;
    private List<PurchaseOrderResult> purchaseOrderResultList = new ArrayList<>();
    private final static int REQUESTCODE = 10000;
    private PurchaseOrderAdapter purchaseOrderAdapter;
    private ListView lvPurchase;
    private WaitDialog waitDialog;
    private PurchaseOrderQuery query;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderController purchaseOrderController = app.getPurchaseOrderController();
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private PurchaseOrderResult currentPurchaseOrderResult;
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

    public static Intent createIntent(Context context, PurchaseOrderQuery query) {
        Intent intent = new Intent(context, PoOrderResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO, query);
        intent.putExtras(bundle);
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
        purchaseOrderAdapter = new PurchaseOrderAdapter(getApplicationContext(), purchaseOrderResultList);
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
    }

    private void getPoData(){
        waitDialog.showWaitDialog(PoOrderResultActivity.this);
        PurchaseOrderTask task = new PurchaseOrderTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderResultActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);

            }

            @Override
            public void bindModel(Object o) {
                List<PurchaseOrder> purchaseOrders = (List<PurchaseOrder>) o;
                if(purchaseOrders != null && purchaseOrders.size() > 0){
                    System.out.println("bindModel------");
                    purchaseOrderList = purchaseOrders;
                    initData();
                }else{
                    displayDialog(getString(R.string.text_service_no_result_po), AppConstants.REQUEST_FAILED, 1);
                }
            }
        }, query, AppConstants.FUNCTION_ID_PURCHASE_ORDER);
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
                    List<PurchaseOrder> orders =(List<PurchaseOrder>) JSON.parseArray(offline.getOrderBody(), PurchaseOrder.class);
                    startActivityForResult(PoOrderDetailActivity.createIntent(app, orders, AppConstants.FUNCTION_ID_PURCHASE_ORDER), REQUESTCODE);
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_FAILED == action){
                    finish();
                }
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER);
                    startActivityForResult(PoOrderDetailActivity.createIntent(app, currentPurchaseOrderResult.getPurchaseOrders(),
                            AppConstants.FUNCTION_ID_PURCHASE_ORDER), REQUESTCODE);
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
        offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER);
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }else{
            startActivityForResult(PoOrderDetailActivity.createIntent(app, currentPurchaseOrderResult.getPurchaseOrders(), AppConstants.FUNCTION_ID_PURCHASE_ORDER), REQUESTCODE);
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