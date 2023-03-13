package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.pda.R;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.StorageLocation;

import java.io.Serializable;
import java.util.List;

public class MaterialPickingPostActivity extends AppCompatActivity implements ActivityInitialization {

    private static final String INTENT_KEY_MATERIAL_PICKING = "MaterialPicking";
    private static final String INTENT_KEY_ORI_LOCATION = "OriLocation";
    private static final String INTENT_KEY_TO_LOCATION = "ToLocation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_picking_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context, List<Material> materialList, StorageLocation oriLocation, StorageLocation toLocation) {
        Intent intent = new Intent(context, MaterialPickingPostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_MATERIAL_PICKING, (Serializable) materialList);
        bundle.putSerializable(INTENT_KEY_ORI_LOCATION, (Serializable) oriLocation);
        bundle.putSerializable(INTENT_KEY_TO_LOCATION, (Serializable) toLocation);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

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