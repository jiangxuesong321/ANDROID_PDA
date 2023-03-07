package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.log.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 物料领用主页面，物料相关查询
 */
public class MaterialPickingHomeActivity extends AppCompatActivity implements ActivityInitialization, MaterialPickingHomeAdapter.DeleteCallback {

    private final static AndroidApplication app = AndroidApplication.getInstance();

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;
    private List<Material> materialList = new ArrayList<>();
    private MaterialPickingHomeAdapter adapter;
    private ListView lvMaterialItem;

    private int removePosition;

    private EditText etPlant;
    private EditText etOriLocation;
    private EditText etToLocation;
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
        etPlant = findViewById(R.id.et_plant);
        etOriLocation = findViewById(R.id.et_ori_location);
        spinnerLocation = findViewById(R.id.sp_to_location);
        etMaterialNumber = findViewById(R.id.et_material_value);
        lvMaterialItem = findViewById(R.id.lv_material_item);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        // TODO: 获取工厂和库存地点数据，暂无对应 API

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
}