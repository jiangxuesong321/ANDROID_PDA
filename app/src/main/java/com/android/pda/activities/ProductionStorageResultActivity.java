package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.ProductionStorageTask;
import com.android.pda.controllers.ProductionStorageController;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;
import com.android.pda.models.POStorageQuery;
import com.android.pda.models.ProductionStorage;
import com.android.pda.models.ProductionStorageQuery;

import java.util.ArrayList;
import java.util.List;

public class ProductionStorageResultActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String INTENT_KEY_PRODUCTION_STORAGE = "ProductionStorage";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;

    private List<ProductionStorage> productionStorageList;
    private ProductionStorageQuery query;

    private ListView lvMaterialItem;
    private EditText etPlant;
    private EditText etOriLocation;
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_storage_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();

        // 获取物料凭证行项目数据
        if (productionStorageList == null) {
            getData();
        }

        bindView();
    }

    public static Intent createIntent(Context context, ProductionStorageQuery query) {
        Intent intent = new Intent(context, ProductionStorageResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PRODUCTION_STORAGE, query);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        spinnerLocation = findViewById(R.id.sp_to_location);

        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        // TODO: 获取目标仓库地点
        // 填充 目标库存地点 Spinner
        storageLocations = new ArrayList<>();
        // 暂时使用模拟数据
        storageLocations.add(new StorageLocation("1000","1001", "瑞博生物", "大仓仓库"));
        storageLocations.add(new StorageLocation("1000","1002", "瑞博生物", "小仓仓库"));
        storageLocations.add(0, new StorageLocation("","", "", ""));

        LogUtils.d("storageLocations","storageLocations---->" + JSON.toJSONString(storageLocations));
        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {
        query = (ProductionStorageQuery) this.getIntent().getSerializableExtra(INTENT_KEY_PRODUCTION_STORAGE);
    }

    /**
     * 获取物料凭证行项目数据
     */
    private void getData() {
        waitDialog.showWaitDialog(ProductionStorageResultActivity.this);
        ProductionStorageTask task = new ProductionStorageTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(ProductionStorageResultActivity.this);
//                offlineController.clearOfflineData(AppConstants.FUNCTION_ID_TRANSFER_IN);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(ProductionStorageResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_BACK, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<ProductionStorage> productionStorages= (List<ProductionStorage>) o;
                if(productionStorages!= null && productionStorages.size() > 0 ){
                    productionStorageList = productionStorages;
                    locationSpinnerAdapter.notifyDataSetChanged();
                    initData();
                } else {
                    displayDialog(app.getString(R.string.error_order_not_found), AppConstants.REQUEST_BACK , 1);
                }
            }
        }, query);
        task.execute();
    }

    private void bindView(){
        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);
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

    private void displayDialog(String message, int action, int buttonCount) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

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
}