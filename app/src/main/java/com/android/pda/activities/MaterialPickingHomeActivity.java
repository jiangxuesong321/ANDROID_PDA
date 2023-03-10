package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.MaterialPickingHomeAdapter;
import com.android.pda.adapters.ProductionStorageResultAdapter;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.adapters.SpinnerPlantAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.log.LogUtils;
import com.android.pda.utils.XmlUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 物料领用主页面，物料相关查询
 */
public class MaterialPickingHomeActivity extends AppCompatActivity implements ActivityInitialization, MaterialPickingHomeAdapter.DeleteCallback {

    private final static AndroidApplication app = AndroidApplication.getInstance();

    private SpinnerAdapter locationAdapter;
    private SpinnerPlantAdapter plantAdapter;
    private List<StorageLocation> storageLocations;
    private List<Material> materialList = new ArrayList<>();
    private MaterialPickingHomeAdapter adapter;
    private ListView lvMaterialItem;

    private int removePosition;

    private Spinner spPlant;
    private Spinner spOriLocation;
    private Spinner spinnerLocation;
    private Spinner spToLocation;
    private EditText etMaterialNumber;
    private WaitDialog waitDialog;

    private static final int ACTION_DELETE = 1;
    private static final int ACTION_DELETE_ALL_MATERIAL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_picking_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MaterialPickingHomeActivity.class);
        return intent;
    }

    @Override
    public void initView() {
        spPlant = findViewById(R.id.sp_plant);
        spOriLocation = findViewById(R.id.sp_ori_location);
        spToLocation = findViewById(R.id.sp_to_location);
        spinnerLocation = findViewById(R.id.sp_to_location);
        etMaterialNumber = findViewById(R.id.et_material_value);
        lvMaterialItem = findViewById(R.id.lv_material_item);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        /*-- 配置 工厂 - 库存地点 数据源 --*/
        storageLocations = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.storage_locations);

        try {
            storageLocations = XmlUtils.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*-- 配置 工厂 Spinner 数据 --*/
        // 工厂去重
        List<StorageLocation> plantList = storageLocations.stream()
                .map(StorageLocation::getPlant)
                .distinct()
                .map(plant -> storageLocations.stream()
                        .filter(location -> location.getPlant().equals(plant))
                        .findFirst().get())
                .sorted(Comparator.comparing(StorageLocation::getPlant))
                .collect(Collectors.toList());

        plantList.add(0, new StorageLocation("", "", "", ""));

        // 清空冗余数据
//        plantList.forEach(location -> {
//            location.setPlantName(null);
//            location.setStorageLocation(null);
//            location.setStorageLocationName(null);
//        });

        plantAdapter = new SpinnerPlantAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, plantList);
        plantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlant.setAdapter(plantAdapter);

        spPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // 库存地点内容重置
                spOriLocation.setSelection(0);
                spToLocation.setSelection(0);

                // 工厂 - 库存地点 二级联动
                StorageLocation selectedItem = (StorageLocation) spPlant.getSelectedItem();
                String selectedItemPlant = selectedItem.getPlant();
                if (StringUtils.isNotEmpty(selectedItemPlant)) {
                    List<StorageLocation> storageLocationArrayList = storageLocations;
                    List<StorageLocation> filteredList = storageLocationArrayList.stream()
                            .filter(storageLocation -> storageLocation.getPlant().equals(selectedItemPlant))
                            .collect(Collectors.toList());
                    filteredList.add(0, new StorageLocation("", "", "", ""));

                    List<StorageLocation> sortFilterList = filteredList.stream()
                            .sorted(Comparator.comparing(StorageLocation::getPlant))
                            .collect(Collectors.toList());

                    Log.d("sortFilterList", "sortFilterList---->" + JSON.toJSONString(sortFilterList));

                    if (locationAdapter == null) {
                        /*-- 配置 库存地点 Spinner 数据 --*/
                        locationAdapter = new SpinnerAdapter(getApplicationContext(),
                                R.layout.li_spinner_adapter, sortFilterList);
                        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spOriLocation.setAdapter(locationAdapter);
                        spToLocation.setAdapter(locationAdapter);
                    } else {
                        locationAdapter.clear();
                        locationAdapter.addAll(sortFilterList);
                        locationAdapter.notifyDataSetChanged();
                    }

                } else {
                    if (locationAdapter != null) {
                        locationAdapter.clear();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spOriLocation.setSelection(0);
                spToLocation.setSelection(0);
            }
        });

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

    private void displayDialog(String message, int action, int buttonCount) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if (action == ACTION_DELETE) {
                    materialList.remove(removePosition);
                    adapter.notifyDataSetChanged();
                }

                if (action == ACTION_DELETE_ALL_MATERIAL) {
                    materialList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void callClose() {
            }
        });
        noticeDialog.create();
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
            case R.id.action_menu_delete:
                // 删除所有行项目
                if (materialList.size() > 0) {
                    displayDialog(getString(R.string.text_confirm_delete_all_material), ACTION_DELETE_ALL_MATERIAL, 2);
                } else {
                    displayDialog(getString(R.string.error_no_material_for_delete), -1, 1);
                }
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
                if (AppConstants.REQUEST_SUCCEED == action) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        noticeDialog.create();
    }

    /**
     * 添加物料条目
     *
     * @param view
     */
    public void addMaterial(View view) {
        String materialNumber = etMaterialNumber.getText().toString();
        // 非空校验
        if (StringUtils.isEmpty(materialNumber)) {
            // 提示输入
            displayDialog("请输入物料编号", AppConstants.REQUEST_FAILED);
        } else {
            // TODO: 校验物料编码是否存在，暂无对应 API
//            if (true) {

            Material material = new Material(materialNumber, "", "", "", "", "", 20000101, 20000101);

            // 若存在，校验是否已存在
            if (!materialList.isEmpty()) {
                for (Material m : materialList) {
                    if (materialNumber.equals(m.getMaterial())) {
                        displayDialog("同一个物料号不能重复添加", AppConstants.REQUEST_FAILED);
                        return;
                    }
                }
            }

            materialList.add(material);

            adapter = new MaterialPickingHomeAdapter(getApplicationContext(), materialList);
            this.lvMaterialItem.setDividerHeight(1);
            this.lvMaterialItem.setAdapter((ListAdapter) adapter);
            adapter.setDeleteCallback(this);

//            } else {
            // 否则提示检查物料号是否正确
//                displayDialog("请检查物料号是否正确", AppConstants.REQUEST_FAILED);
//            }
        }

    }

    /**
     * 删除指定物料条目
     *
     * @param position
     */
    @Override
    public void onCallBack(int position) {
        removePosition = position;
        displayDialog(app.getString(R.string.text_confirm_delete), ACTION_DELETE, 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 提交物料查询
     *
     * @param view
     */
    public void confirm(View view) {
        // TODO: 添加的物料编码有效性检查
        StorageLocation oriLocation = (StorageLocation) spOriLocation.getSelectedItem();
        StorageLocation toLocation = (StorageLocation) spToLocation.getSelectedItem();

        if (oriLocation != null && toLocation != null) {
            // 配置传递参数
            Log.d("MaterialList--->", JSON.toJSONString(materialList));
            Log.d("oriLocation--->", JSON.toJSONString(oriLocation));
            Log.d("toLocation--->", JSON.toJSONString(toLocation));

            // 跳转前做非空校验
            // 校验文本原因：每一个 Spinner 中都配置了一行空白行（默认显示）
            if (StringUtils.isNotEmpty(oriLocation.getPlant()) && StringUtils.isNotEmpty(oriLocation.getStorageLocation()) && StringUtils.isNotEmpty(toLocation.getStorageLocation()) && materialList != null && materialList.size() > 0) {
                startActivityForResult(MaterialPickingResultActivity.createIntent(app, materialList, oriLocation, toLocation), 10000);
            } else {
                displayDialog("请填写所有信息，并添加物料行项目", AppConstants.REQUEST_FAILED);
            }
        } else {
            displayDialog("请填写所有信息，并添加物料行项目", AppConstants.REQUEST_FAILED);
        }


    }
}