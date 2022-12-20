package com.sunmi.pda.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.MenuItem;

import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.DialogInputComponent;

import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.adapters.PurchaseOrderComponentDetailAdapter;

import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;

import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.models.Component;

import com.sunmi.pda.models.PurchaseOrderSubContract;
import com.sunmi.pda.models.Reason;
import com.sunmi.pda.models.ScanResult;

import java.util.List;


public class PoOrderComponentDetailActivity extends AppCompatActivity implements ActivityInitialization,
        PurchaseOrderComponentDetailAdapter.OnItemClickListener, DialogInputComponent.InputCallback{
    private final static SunmiApplication app = SunmiApplication.getInstance();

    private static final String INTENT_KEY_PO = "PO";
    public static final String INTENT_KEY_ORDER = "Order";

    private ListView lvPurchase;
    private PurchaseOrderSubContract order;
    private PurchaseOrderComponentDetailAdapter purchaseOrderAdapter;
    private List<Component> components;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_order_component_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initService();
        initIntent();
        initData();
    }
    public static Intent createIntent(Context context, PurchaseOrderSubContract purchaseOrder) {
        Intent intent = new Intent(context, PoOrderComponentDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO, purchaseOrder);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        lvPurchase = findViewById(R.id.lv_purchase_order);
    }

    @Override
    public void initData() {
        if(order != null){
            components = order.getComponents();
            showPurchaseData();
        }

    }
    private void showPurchaseData() {
        purchaseOrderAdapter = new PurchaseOrderComponentDetailAdapter(getApplicationContext(), components);
        this.lvPurchase.setDividerHeight(1);
        this.lvPurchase.setAdapter(purchaseOrderAdapter);
        purchaseOrderAdapter.setClickListener(this);
    }
    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {
        order = (PurchaseOrderSubContract)this.getIntent().getSerializableExtra(INTENT_KEY_PO);
    }

    private void over(){
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_ORDER, order);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                over();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            over();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onItemClick(int position) {
        currentPosition = position;
        DialogInputComponent dialogInput = new DialogInputComponent(PoOrderComponentDetailActivity.this,
                components.get(position).getBatch(), components.get(position).getOpenQuantity(),
                components.get(position).getQuantity(), order.isBatchFlag());
        dialogInput.setInputCallback(this);
        dialogInput.show();
        dialogInput.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    private int currentPosition;

    @Override
    public void setInput(String batch, double count) {
        if(components != null && components.size() > currentPosition){
            Component component = components.get(currentPosition);
            component.setBatch(batch);
            component.setQuantity(count);
        }

        purchaseOrderAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    public void ok(View view){
        over();
    }


}