package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.MaterialPickingResultAdapter;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.MaterialPickingBinTask;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.ProductionStorageQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description 物料领用详情页，点击确定过账的页面
 */
public class MaterialPickingResultActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();

    private static final String INTENT_KEY_MATERIAL_PICKING = "MaterialPicking";
    private static final String INTENT_KEY_ORI_LOCATION = "OriLocation";
    private static final String INTENT_KEY_TO_LOCATION = "ToLocation";

    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private List<StorageLocation> storageLocations;
    private MaterialPickingResultAdapter adapter;

    private StorageLocation oriLocation;
    private StorageLocation toLocation;

    private List<Material> materialList = new ArrayList<>();
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
        oriLocation = (StorageLocation) this.getIntent().getSerializableExtra(INTENT_KEY_ORI_LOCATION);
        etPlant.setText(oriLocation.getPlant());
        etOriLocation.setText(oriLocation.getStorageLocation());
        toLocation = (StorageLocation) this.getIntent().getSerializableExtra(INTENT_KEY_TO_LOCATION);
        etToLocation.setText(toLocation.getStorageLocation());

        // 物料信息
        materialList = (List<Material>) this.getIntent().getSerializableExtra(INTENT_KEY_MATERIAL_PICKING);
        adapter = new MaterialPickingResultAdapter(getApplicationContext(), materialList);
        this.lvMaterialItem.setDividerHeight(1);
        this.lvMaterialItem.setAdapter(adapter);
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

    /**
     * 提交并转至过账页面
     *
     * @param view
     */
    public void confirm(View view) {
        List<Material> chooseList = materialList.stream().filter(e ->
                e.getBatchFlag() != null && e.getBatchFlag().equals("choose")
        ).collect(Collectors.toList());
        waitDialog.showWaitDialog(MaterialPickingResultActivity.this);
        MaterialPickingBinTask task = new MaterialPickingBinTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(MaterialPickingResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }

            @Override
            public void bindModel(Object o) {
                List<Material> materialChooseList = (List<Material>) o;
                if (materialChooseList.size() > 0) {
                    startActivityForResult(MaterialPickingPostActivity.createIntent(app, materialChooseList, oriLocation, toLocation), 10000);
                } else {
                    displayDialog(getString(R.string.text_item_on_least_select_one), AppConstants.REQUEST_FAILED);
                }
                waitDialog.hideWaitDialog(MaterialPickingResultActivity.this);
            }
        }, chooseList);
        task.execute();

//        if (chooseList.size() > 0) {
//            startActivityForResult(MaterialPickingPostActivity.createIntent(app, chooseList, oriLocation, toLocation), 10000);
//        } else {
//            displayDialog(getString(R.string.text_item_on_least_select_one), AppConstants.REQUEST_FAILED);
//        }
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
}