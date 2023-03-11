package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.ProductionStoragePostingTask;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.ProductionStorageQuery;
import com.android.pda.utils.XmlUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductionStorageResultActivity extends AppCompatActivity implements ActivityInitialization,
        ProductionStorageResultAdapter.CheckCallback {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String INTENT_KEY_PRODUCTION_STORAGE = "ProductionStorage";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationAdapter;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;
    private ProductionStorageResultAdapter adapter;

    private List<MaterialDocument> list = new ArrayList<>();
    private ProductionStorageQuery query;

    private ListView lvMaterialItem;
    private EditText etPlant;
    private EditText etOriLocation;
    private Spinner spToLocation;
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
//        spinnerLocation = findViewById(R.id.sp_to_location);
        lvMaterialItem = findViewById(R.id.lv_material_item);
        etPlant = findViewById(R.id.et_plant);
        etOriLocation = findViewById(R.id.et_ori_location);
        spToLocation = findViewById(R.id.sp_to_location);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        List<MaterialDocument> materialDocumentList = (List<MaterialDocument>) this.getIntent().getSerializableExtra(INTENT_KEY_PRODUCTION_STORAGE);
        list = materialDocumentList;
        ProductionStorageResultAdapter adapter = new ProductionStorageResultAdapter(getApplicationContext(), list);
        this.lvMaterialItem.setDividerHeight(1);
        this.lvMaterialItem.setAdapter(adapter);
        adapter.setCheckCallback(this);

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

        /*-- 配置 工厂 - 库存地点 数据源 --*/
        storageLocations = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.storage_locations);

        try {
            storageLocations = XmlUtils.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 工厂 - 库存地点 二级联动
        String plantText = etPlant.getText().toString();
        if (StringUtils.isNotEmpty(plantText)) {
            List<StorageLocation> storageLocationArrayList = storageLocations;
            List<StorageLocation> filteredList = storageLocationArrayList.stream()
                    .filter(storageLocation -> storageLocation.getPlant().equals(plantText))
                    .collect(Collectors.toList());
            filteredList.add(0, new StorageLocation("", "", "", ""));

            Log.d("filteredList", "filteredList---->" + JSON.toJSONString(filteredList));

            if (locationAdapter == null) {
                /*-- 配置 库存地点 Spinner 数据 --*/
                locationAdapter = new SpinnerAdapter(getApplicationContext(),
                        R.layout.li_spinner_adapter, filteredList);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spToLocation.setAdapter(locationAdapter);
            } else {
                locationAdapter.clear();
                locationAdapter.addAll(filteredList);
                locationAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 收货过账
     *
     * @param
     */
    public void confirm(View view) {
        //检查是否货位号都已经扫码
        StorageLocation storageLocation = (StorageLocation) spToLocation.getSelectedItem();
        String targetStorageLocation = storageLocation.getStorageLocation();
        //校验仓位是否已经扫码
        for (MaterialDocument materialDocument : list) {
            materialDocument.setTargetStorageLocation(targetStorageLocation);
            if (StringUtils.isEmpty(targetStorageLocation) || StringUtils.isEmpty(materialDocument.getStorageBin())) {
                displayDialog(getString(R.string.text_po_receive_sting_error), AppConstants.REQUEST_FAILED);
                waitDialog.hideWaitDialog(ProductionStorageResultActivity.this);
                return;
            }
            if (!materialDocument.getMaterial().equals(materialDocument.getInputMaterial()) || StringUtils.isEmpty(materialDocument.getInputMaterial())) {
                displayDialog(getString(R.string.text_material_not_same), AppConstants.REQUEST_FAILED);
                waitDialog.hideWaitDialog(ProductionStorageResultActivity.this);
                return;
            }
        }
        waitDialog.showWaitDialog(ProductionStorageResultActivity.this);
        ProductionStoragePostingTask task = new ProductionStoragePostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(ProductionStorageResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }

            @Override
            public void bindModel(Object o) {
                Map<String, String> materialDocumentInfo = (Map<String, String>) o;
                // 查询参数校验（物料凭证）
                if (StringUtils.isNotEmpty(materialDocumentInfo.get("materialDocument"))) {
                    displayDialog(getString(R.string.text_batch_char_value_update_success), AppConstants.REQUEST_BACK);
//                    startActivityForResult(POStorageHomeActivity.createIntent(app), 10000);
                } else {
                    displayDialog(getString(R.string.text_batch_char_value_update_failed) + materialDocumentInfo.get("error"), AppConstants.REQUEST_FAILED);
                }
                waitDialog.hideWaitDialog(ProductionStorageResultActivity.this);

            }
        }, list);
        task.execute();
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
//        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
//                R.layout.li_spinner_adapter, storageLocations);
//        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerLocation.setAdapter(locationSpinnerAdapter);
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

    private void displayDialog(String message, int action) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, 1);
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }

            @Override
            public void callClose() {
                if (AppConstants.REQUEST_BACK == action) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        noticeDialog.create();
    }

    @Override
    public void onCallBack(int position, String material) {
        if (StringUtils.isNotEmpty(material)) {
            if (!list.get(position).getMaterial().equals(material)) {
                displayDialog(getString(R.string.text_material_not_same), AppConstants.REQUEST_FAILED);
            }
        }

    }
}