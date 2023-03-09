package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.ProductionStorageResultAdapter;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.log.LogUtils;
import com.android.pda.models.ProductionStorageQuery;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 物料领用详情页，点击确定过账的页面
 */
public class MaterialPickingResultActivity extends AppCompatActivity implements ActivityInitialization {

    private static final String INTENT_KEY_MATERIAL_PICKING = "MaterialPicking";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;
    private ProductionStorageResultAdapter adapter;

    private List<Material> list = new ArrayList<>();
    private ProductionStorageQuery query;

    private ListView lvMaterialItem;
    private EditText etPlant;
    private EditText etOriLocation;
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_picking_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context, List<Material> materialList) {
        Intent intent = new Intent(context, MaterialPickingResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_MATERIAL_PICKING, (Serializable) materialList);
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
}