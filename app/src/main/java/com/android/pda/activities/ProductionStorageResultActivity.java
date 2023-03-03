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
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.log.LogUtils;
import com.android.pda.models.POStorageQuery;

import java.util.List;

public class ProductionStorageResultActivity extends AppCompatActivity implements ActivityInitialization {

    private static final String INTENT_KEY_PRODUCTION_STORAGE = "ProductionStorage";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;

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
    }

    public static Intent createIntent(Context context, POStorageQuery query) {
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
//        StorageLocation storageLocation = new StorageLocation("1000","1001", "瑞博生物", "大仓仓库");
//        storageLocations.add(0, storageLocation);
//        LogUtils.d("storageLocations","storageLocations---->" + JSON.toJSONString(storageLocations));
//        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
//                R.layout.li_spinner_adapter, storageLocations);
//        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerLocation.setAdapter(locationSpinnerAdapter);
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