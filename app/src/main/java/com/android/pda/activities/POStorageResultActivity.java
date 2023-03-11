package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.POStorageResultAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.POStoragePostingTask;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.User;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description 采购入库详情页，点击确定过账的页面
 */
//public class POStorageResultActivity extends AppCompatActivity implements ActivityInitialization,
//        POStorageResultAdapter.OnItemClickListener, POStorageResultAdapter.SplitCallback, DialogInput.InputCallback {
public class POStorageResultActivity extends AppCompatActivity implements ActivityInitialization {
    private static final String INTENT_KEY_PO_STORAGE = "POStorage";
    private static final String INTENT_KEY_ITEM = "Item";
    private static final String INTENT_KEY_DATA = "Data";
    private final static int REQUESTCODE = 10000;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final UserController userController = app.getUserController();

    private ListView lvPOItem;
    private EditText etPONumber;
    private EditText etVendor;
    private WaitDialog waitDialog;
    private User user;

    private POStorageResultAdapter adapter;
    private List<MaterialDocument> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postorage_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
//        initIntent();
//        initListener();
        initData();
        bindView();
    }

    public static Intent createIntent(Context context, List<MaterialDocument> materialDocumentList) {
        Intent intent = new Intent(context, POStorageResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO_STORAGE, (Serializable) materialDocumentList);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        //初始化行项目view
        lvPOItem = findViewById(R.id.lv_po_item);
        etPONumber = findViewById(R.id.et_po_number);
        etVendor = findViewById(R.id.et_vendor);
        waitDialog = new WaitDialog();
//        lvPOItem.setDividerHeight(1);
    }

    @Override
    public void initData() {
        String materialDocument = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_MATERIAL_DOC_NUMBER);
        etPONumber.setText(materialDocument);
        user = userController.getLoginUser();
        List<MaterialDocument> materialDocumentList = (List<MaterialDocument>) this.getIntent().getSerializableExtra(INTENT_KEY_PO_STORAGE);
        list = materialDocumentList;
        MaterialDocument materialDocumentOne = materialDocumentList.get(0);
        etVendor.setText(materialDocumentOne.getSupplier());
        adapter = new POStorageResultAdapter(getApplicationContext(), list);
        this.lvPOItem.setDividerHeight(1);
        this.lvPOItem.setAdapter(adapter);
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

    private void bindView() {

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
     * 点击确认过账，开始创建物料凭证
     *
     * @param view
     */
    public void confirm(View view) {
        System.out.println("需要过账的数据：" + list);
        //检查是否货位号都已经扫码
        for (MaterialDocument materialDocument : list) {
            if (StringUtils.isEmpty(materialDocument.getStorageBin())) {
                displayDialog(getString(R.string.text_posting_error), AppConstants.REQUEST_BACK);
                waitDialog.hideWaitDialog(POStorageResultActivity.this);
                return;
            }
        }
        waitDialog.showWaitDialog(POStorageResultActivity.this);
        POStoragePostingTask task = new POStoragePostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(POStorageResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }

            @Override
            public void bindModel(Object o) {
                Map<String, String> updateInfo = (Map<String, String>) o;
                // 查询参数校验（物料凭证）
                if (StringUtils.isNotEmpty(updateInfo.get("success"))) {
                    displayDialog(updateInfo.get("success"), AppConstants.REQUEST_BACK);
//                    startActivityForResult(POStorageHomeActivity.createIntent(app), 10000);
                } else {
                    displayDialog(updateInfo.get("error"), AppConstants.REQUEST_FAILED);
                }
                waitDialog.hideWaitDialog(POStorageResultActivity.this);
            }
        }, list);
        task.execute();
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
}