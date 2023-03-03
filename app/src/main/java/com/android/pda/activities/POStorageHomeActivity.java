package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.controllers.MaterialController;
import com.android.pda.controllers.POStorageController;
import com.android.pda.models.POStorageQuery;
import com.android.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class POStorageHomeActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final POStorageController poStorageController = app.getPoStorageController();

    private EditText etMaterialDocument;
    private final static int REQUESTCODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postorage_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, POStorageHomeActivity.class);
        return intent;
    }

    // TODO: 初始化视图（视图控件对象获取）
    @Override
    public void initView() {
        etMaterialDocument = findViewById(R.id.et_material_doc);
    }

    // TODO: 初始化数据
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
     * 采购入库查询功能
     *
     * @param view
     */
    public void confirm(View view) {
        String materialDocument = etMaterialDocument.getText().toString();
        // TODO: SF 相关
        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_MATERIAL_DOC_NUMBER, materialDocument);

        // 查询参数校验（物料凭证）
        POStorageQuery query = new POStorageQuery(materialDocument);
        String error = poStorageController.verifyQuery(query);

        if (StringUtils.isEmpty(error)) {
            // TODO: 暂无接口，后续修改
            startActivityForResult(POStorageResultActivity.createIntent(app, query), REQUESTCODE);
        } else {
            displayDialog(error, AppConstants.REQUEST_STAY, 1);
        }
    }

    private void displayDialog(String message, int action, int buttonCount) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

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