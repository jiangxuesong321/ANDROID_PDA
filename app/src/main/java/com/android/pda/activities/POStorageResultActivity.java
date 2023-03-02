package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.pda.R;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.POStorageAdapter;

public class POStorageResultActivity extends AppCompatActivity implements ActivityInitialization {

    private static final String INTENT_KEY_PO_STORAGE = "SalesInvoice";
    private final static int REQUESTCODE = 10000;

    private ListView lvPOItem;
    private WaitDialog waitDialog;

    private POStorageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postorage_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

//    public static Intent createIntent(Context context, SalesInvoiceQuery query) {
//        Intent intent = new Intent(context, SalesInvoiceResultActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(INTENT_KEY_PO_STORAGE, query);
//        intent.putExtras(bundle);
//        return intent;
//    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, POStorageResultActivity.class);
        return intent;
    }

    @Override
    public void initView() {
        lvPOItem = findViewById(R.id.lv_po_item);
        waitDialog = new WaitDialog();

        lvPOItem.setDividerHeight(1);
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