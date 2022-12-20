package com.sunmi.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.adapters.MaterialAdapter;
import com.sunmi.pda.adapters.StorageLocationAdapter;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.database.pojo.StorageLocation;

import java.util.List;

public class StorageLocationActivity extends AppCompatActivity implements ActivityInitialization{
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final StorageLocationController controller = app.getStorageLocationController();
    private List<StorageLocation> storageLocations;
    private StorageLocationAdapter adapter;
    private ListView lvStorageLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        showStorageLocation();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, StorageLocationActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    private void showStorageLocation() {
        adapter = new StorageLocationAdapter(getApplicationContext(), storageLocations);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvStorageLocation.setDividerHeight(1);
        this.lvStorageLocation.setAdapter(adapter);
        //this.lvPurchase.setOnItemClickListener(this);
    }

    @Override
    public void initView() {
        lvStorageLocation = findViewById(R.id.lv_storage_location);
    }

    @Override
    public void initData() {
        storageLocations = controller.getStorageLocation();

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