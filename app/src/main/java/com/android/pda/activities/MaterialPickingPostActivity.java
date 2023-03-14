package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.MaterialPickingPostAdapter;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.MaterialPickingPostTask;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaterialPickingPostActivity extends AppCompatActivity implements ActivityInitialization,
        MaterialPickingPostAdapter.CheckCallback {

    private static final String INTENT_KEY_MATERIAL_PICKING = "MaterialPicking";
    private static final String INTENT_KEY_ORI_LOCATION = "OriLocation";
    private static final String INTENT_KEY_TO_LOCATION = "ToLocation";

    private MaterialPickingPostAdapter adapter;
    private WaitDialog waitDialog;
    private List<Material> materialList = new ArrayList<>();
    private ListView lvMaterialItem;

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
        lvMaterialItem = findViewById(R.id.lv_material_item);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        // 物料信息
        materialList = (List<Material>) this.getIntent().getSerializableExtra(INTENT_KEY_MATERIAL_PICKING);
        adapter = new MaterialPickingPostAdapter(getApplicationContext(), materialList);
        this.lvMaterialItem.setDividerHeight(1);
        this.lvMaterialItem.setAdapter(adapter);
        adapter.setCheckCallback(this);
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
     * 提交物料开始创建凭证
     *
     * @param view
     */
    public void confirm(View view) {
//        for (Material material : materialList) {
//            if (StringUtils.isEmpty(material.getConfirmStorageBin()) || StringUtils.isEmpty(material.getInputMaterial())) {
//                displayDialog(getString(R.string.text_material_posting_check_null_error), AppConstants.REQUEST_FAILED);
//                waitDialog.hideWaitDialog(MaterialPickingPostActivity.this);
//                return;
//            }
//            if (!material.getConfirmStorageBin().equals(material.getStorageBin())) {
//                displayDialog(getString(R.string.text_storage_bin_not_same), AppConstants.REQUEST_FAILED);
//                waitDialog.hideWaitDialog(MaterialPickingPostActivity.this);
//                return;
//            }
//            if (!material.getMaterial().equals(material.getInputMaterial())) {
//                displayDialog(getString(R.string.text_material_not_same), AppConstants.REQUEST_FAILED);
//                waitDialog.hideWaitDialog(MaterialPickingPostActivity.this);
//                return;
//            }
//        }
        waitDialog.showWaitDialog(MaterialPickingPostActivity.this);
        MaterialPickingPostTask task = new MaterialPickingPostTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(MaterialPickingPostActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }

            @Override
            public void bindModel(Object o) {
                Map<String, String> materialDocumentInfo = (Map<String, String>) o;
                // 查询参数校验（物料凭证）
                if (StringUtils.isNotEmpty(materialDocumentInfo.get("materialDocument"))) {
                    displayDialog(getString(R.string.text_material_create_document_success) + materialDocumentInfo.get("materialDocument"), AppConstants.REQUEST_BACK);
                } else {
                    displayDialog(getString(R.string.text_to_material_doc_error) + materialDocumentInfo.get("error"), AppConstants.REQUEST_FAILED);
                }
                waitDialog.hideWaitDialog(MaterialPickingPostActivity.this);

            }
        }, materialList);
        task.execute();

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

    @Override
    public void onCallBack(int position, String param, String type) {
        if (type != null && type.equals("material")) {
            if (StringUtils.isNotEmpty(param)) {
                if (!materialList.get(position).getMaterial().contains(param)) {
                    displayDialog(getString(R.string.text_material_not_same), AppConstants.REQUEST_FAILED);
                }
            }
        }

        if (type != null && type.equals("storageBin")) {
            if (StringUtils.isNotEmpty(param)) {
                if (!materialList.get(position).getStorageBin().contains(param)) {
                    displayDialog(getString(R.string.text_storage_bin_not_same), AppConstants.REQUEST_FAILED);
                }
            }
        }

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
                    Intent intent = new Intent(MaterialPickingPostActivity.this, MaterialPickingHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        noticeDialog.create();
    }
}