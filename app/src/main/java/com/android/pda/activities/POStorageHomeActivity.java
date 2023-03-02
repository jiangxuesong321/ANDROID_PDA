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
import com.android.pda.application.AppConstants;
import com.android.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class POStorageHomeActivity extends AppCompatActivity implements ActivityInitialization {

    private EditText etPODocNum;

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
        etPODocNum = findViewById(R.id.et_po_doc_num);
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
        String number = etPODocNum.getText().toString();
        // TODO: SF 相关
//        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SALES_INVOICE, number);

//        SalesInvoiceQuery query = new SalesInvoiceQuery(number, deliveryDateFrom, deliveryDateFromTo, deliveryStatuses);
//        String error = controller.verifyQuery(query);
//
//        if(StringUtils.isEmpty(error)){
//            startActivityForResult(SalesInvoiceResultActivity.createIntent(app, query), REQUESTCODE);
//        }else{
//            displayDialog(error, AppConstants.REQUEST_STAY, 1);
//        }

        startActivity(POStorageResultActivity.createIntent(getApplicationContext()));

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