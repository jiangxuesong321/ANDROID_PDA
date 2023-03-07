package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.android.pda.R;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.application.AndroidApplication;

public class POStorageMainActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private final static int REQUESTCODE = 10001;
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postorage_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, POStorageMainActivity.class);
        return intent;
    }

    // TODO: 初始化视图（视图控件对象获取）
    @Override
    public void initView() {
        waitDialog = new WaitDialog();
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
     * po入库查询功能
     *
     * @param view
     */
    public void topostoragehome(View view) {
        startActivityForResult(POReceiveHomeActivity.createIntent(app), 10000);
        waitDialog.hideWaitDialog(POStorageMainActivity.this);
    }

    /**
     * 凭证入库查询功能
     *
     * @param view
     */
    public void tomaterialstoragehome(View view) {
        startActivityForResult(POStorageHomeActivity.createIntent(app), 10000);
        waitDialog.hideWaitDialog(POStorageMainActivity.this);
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