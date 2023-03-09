package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.POStorageTask;
import com.android.pda.controllers.POStorageController;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.POStorageQuery;
import com.android.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description 采购入库初始页，查询凭证的界面
 */
public class POStorageHomeActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final POStorageController poStorageController = app.getPoStorageController();

    private EditText etMaterialDocument;
    private final static int REQUESTCODE = 10001;
    private WaitDialog waitDialog;

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

    @Override
    public void initView() {
        etMaterialDocument = findViewById(R.id.et_material_doc);
        waitDialog = new WaitDialog();
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
     * 采购入库查询功能
     *
     * @param view
     */
    public void confirm(View view) {
        String materialDocument = etMaterialDocument.getText().toString();

        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_MATERIAL_DOC_NUMBER, materialDocument);
        if (StringUtils.isEmpty(materialDocument)) {
            displayDialog(app.getString(R.string.text_input_material_doc_num), AppConstants.REQUEST_STAY, 1);
            return;
        }
        waitDialog.showWaitDialog(POStorageHomeActivity.this);
        POStorageQuery query = new POStorageQuery(materialDocument, "");
        POStorageTask task = new POStorageTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(POStorageHomeActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }

            @Override
            public void bindModel(Object o) {
                // 查询参数校验（物料凭证）
                List<MaterialDocument> materialDocumentList = (List<MaterialDocument>) o;
                if (materialDocumentList != null && materialDocumentList.size() > 0) {
                    startActivityForResult(POStorageResultActivity.createIntent(app, materialDocumentList), 10000);
                } else {
                    displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                }
                waitDialog.hideWaitDialog(POStorageHomeActivity.this);
            }
        }, query);
        task.execute();
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