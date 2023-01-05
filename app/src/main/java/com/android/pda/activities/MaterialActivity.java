package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.adapters.MaterialAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.MaterialController;
import com.android.pda.database.pojo.Material;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 根据物料编码查询物料信息
 */

public class MaterialActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final MaterialController materialController = app.getMaterialController();
    private List<Material> materials = new ArrayList<>();
    private MaterialAdapter adapter;
    private ListView lvMaterial;
    private TextView etMaterialValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        showMaterial();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MaterialActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 配置 Adapter 展示查询后的物料信息
     */
    private void showMaterial() {
        adapter = new MaterialAdapter(getApplicationContext(), materials);
        this.lvMaterial.setDividerHeight(1);
        this.lvMaterial.setAdapter(adapter);
    }

    @Override
    public void initView() {
        lvMaterial = findViewById(R.id.lv_material);
        etMaterialValue = findViewById(R.id.et_material_value);
    }

    /**
     * 根据物料编号查询信息
     * @param view
     */
    public void search(View view){
        String material = etMaterialValue.getText().toString();
        if(StringUtils.isEmpty(material)){
            displayDialog(getString(R.string.text_input_material));
            return;
        }
        materials.clear();
        materials.addAll(materialController.getMaterial(material));
        adapter.notifyDataSetChanged();
        if(materials == null || materials.size() == 0){
            displayDialog(getString(R.string.text_no_material));
            return;
        }
    }

    /**
     * 消息提示
     * @param message
     */
    private void displayDialog(String message){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, 1);
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