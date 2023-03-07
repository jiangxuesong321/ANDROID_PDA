package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.ProductionStorageResultAdapter;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.log.LogUtils;
import com.android.pda.models.ProductionStorageQuery;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductionStorageResultActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String INTENT_KEY_PRODUCTION_STORAGE = "ProductionStorage";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;
    private ProductionStorageResultAdapter adapter;

    private List<MaterialDocument> list = new ArrayList<>();
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
//        initIntent();
        initView();
//        initListener();
        initData();
        bindView();
    }

    public static Intent createIntent(Context context, List<MaterialDocument> materialDocumentList) {
        Intent intent = new Intent(context, ProductionStorageResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PRODUCTION_STORAGE, (Serializable) materialDocumentList);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        spinnerLocation = findViewById(R.id.sp_to_location);
        lvMaterialItem = findViewById(R.id.lv_material_item);
        etPlant = findViewById(R.id.et_plant);
        etOriLocation = findViewById(R.id.et_ori_location);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        // TODO: 获取目标仓库地点，暂无对应 API
        // 填充 目标库存地点 Spinner
        storageLocations = new ArrayList<>();
        // 暂时使用模拟数据
        storageLocations.add(new StorageLocation("1000", "1001", "瑞博生物", "大仓仓库"));
        storageLocations.add(new StorageLocation("1000", "1002", "瑞博生物", "小仓仓库"));
        storageLocations.add(0, new StorageLocation("", "", "", ""));

        LogUtils.d("storageLocations", "storageLocations---->" + JSON.toJSONString(storageLocations));
        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);

        List<MaterialDocument> materialDocumentList = (List<MaterialDocument>) this.getIntent().getSerializableExtra(INTENT_KEY_PRODUCTION_STORAGE);
        list = materialDocumentList;
        ProductionStorageResultAdapter adapter = new ProductionStorageResultAdapter(getApplicationContext(), list);
        this.lvMaterialItem.setDividerHeight(1);
        this.lvMaterialItem.setAdapter(adapter);

        // 填充工厂
        if (StringUtils.isNotEmpty(materialDocumentList.get(0).getPlant())) {
            etPlant.setText(materialDocumentList.get(0).getPlant());
            Log.d("Current Plant: ", etPlant.getText().toString());
        }
        // 填充源库存地点
        if (StringUtils.isNotEmpty(materialDocumentList.get(0).getStorageLocation())) {
            etOriLocation.setText(materialDocumentList.get(0).getStorageLocation());
            Log.d("Current OriLocation: ", etOriLocation.getText().toString());
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
        query = (ProductionStorageQuery) this.getIntent().getSerializableExtra(INTENT_KEY_PRODUCTION_STORAGE);
    }

    private void bindView() {
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