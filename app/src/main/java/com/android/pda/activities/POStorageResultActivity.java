package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.android.pda.R;
import com.android.pda.activities.view.DialogInput;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.POStorageResultAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.User;
import com.android.pda.utils.AppUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 采购入库详情页，点击确定过账的页面
 */
//public class POStorageResultActivity extends AppCompatActivity implements ActivityInitialization,
//        POStorageResultAdapter.OnItemClickListener, POStorageResultAdapter.SplitCallback, DialogInput.InputCallback {
    public class POStorageResultActivity extends AppCompatActivity implements ActivityInitialization {
    private static final String INTENT_KEY_PO_STORAGE = "POStorage";
    private static final String INTENT_KEY_ITEM = "Item";
    private static final String INTENT_KEY_DATA = "Data";
    private final static int REQUESTCODE = 10000;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final UserController userController = app.getUserController();

    private ListView lvPOItem;
    private EditText etPONumber;
    private EditText etVendor;
    private WaitDialog waitDialog;
    private User user;

    private POStorageResultAdapter adapter;
    private List<MaterialDocument> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postorage_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
//        initIntent();

//        initListener();
        initData();
        bindView();
    }

    public static Intent createIntent(Context context, List<MaterialDocument> materialDocumentList) {
        Intent intent = new Intent(context, POStorageResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO_STORAGE, (Serializable) materialDocumentList);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        //初始化行项目view
        lvPOItem = findViewById(R.id.lv_po_item);
        etPONumber = findViewById(R.id.et_po_number);
        etVendor = findViewById(R.id.et_vendor);
        waitDialog = new WaitDialog();
//        lvPOItem.setDividerHeight(1);
    }

    @Override
    public void initData() {
        String materialDocument = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_MATERIAL_DOC_NUMBER);
        etPONumber.setText(materialDocument);
        user = userController.getLoginUser();
        List<MaterialDocument> materialDocumentList = (List<MaterialDocument>) this.getIntent().getSerializableExtra(INTENT_KEY_PO_STORAGE);
        list = materialDocumentList;
        MaterialDocument materialDocumentOne = materialDocumentList.get(0);
        etVendor.setText(materialDocumentOne.getSupplier());
        System.out.println("供应商为:" + materialDocumentOne.getSupplier());
        adapter = new POStorageResultAdapter(getApplicationContext(), list);
//        adapter.setClickListener(this);
//        adapter.setSplitCallback(this);
//        lvPOItem.setAdapter(adapter);
        this.lvPOItem.setDividerHeight(1);
        this.lvPOItem.setAdapter(adapter);
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

    private void bindView() {
//        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
//                R.layout.li_spinner_adapter, storageLocations);
//        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerLocation.setAdapter(locationSpinnerAdapter);
//        if (offline != null) {
//            //for offline
//            int pos = storageLocationController.getStorageLocationPosition(offline.getReceiveLocation(), storageLocations);
//            spinnerLocation.setSelection(pos);
//            locationSpinnerAdapter.notifyDataSetChanged();
////            lvMaterial = findViewById(R.id.lv_material);
////            d66TestAdapter = new D66TestAdapter(getApplicationContext(), mList);
////            this.lvMaterial.setDividerHeight(1);
////            this.lvMaterial.setAdapter(d66TestAdapter);
//        }
    }

    /**
     * Rewrite onOptionsItemSelected 实现返回按钮功能
     *
     * @param item
     * @return
     */
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

    /**
     * 点击确认过账，开始创建物料凭证
     *
     * @param view
     */
    public void confirm(View view) {
        System.out.println("需要过账的数据："+list);

    }
}