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
    private static final String INTENT_KEY_ORI_LOCATION = "OriLocation";
    private static final String INTENT_KEY_TO_LOCATION = "ToLocation";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;
    private ProductionStorageResultAdapter adapter;

    private List<Material> list = new ArrayList<>();
    private ProductionStorageQuery query;

    private ListView lvMaterialItem;
    private EditText etPlant;
    private EditText etOriLocation;
    private EditText etToLocation;
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

    public static Intent createIntent(Context context, List<Material> materialList, StorageLocation oriLocation, StorageLocation toLocation) {
        Intent intent = new Intent(context, MaterialPickingResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_MATERIAL_PICKING, (Serializable) materialList);
        bundle.putSerializable(INTENT_KEY_ORI_LOCATION, (Serializable) oriLocation);
        bundle.putSerializable(INTENT_KEY_TO_LOCATION, (Serializable) toLocation);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        lvMaterialItem = findViewById(R.id.lv_material_item);
        etPlant = findViewById(R.id.et_plant);
        etOriLocation = findViewById(R.id.et_ori_location);
        etToLocation = findViewById(R.id.et_to_location);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        // 展示携带的数据
        StorageLocation oriLocation = (StorageLocation) this.getIntent().getSerializableExtra(INTENT_KEY_ORI_LOCATION);
        etPlant.setText(oriLocation.getPlant());
        etOriLocation.setText(oriLocation.getStorageLocation());
        StorageLocation toLocation = (StorageLocation) this.getIntent().getSerializableExtra(INTENT_KEY_TO_LOCATION);
        etToLocation.setText(toLocation.getStorageLocation());
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
}