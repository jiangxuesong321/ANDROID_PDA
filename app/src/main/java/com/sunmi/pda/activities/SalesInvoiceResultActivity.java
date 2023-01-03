package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.SalesInvoiceAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.asynctasks.SalesInvoiceTask;
import com.sunmi.pda.controllers.SalesInvoiceController;
import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.SalesInvoiceResult;

import java.util.List;


public class SalesInvoiceResultActivity extends AppCompatActivity implements ActivityInitialization,
        AdapterView.OnItemClickListener{

    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final SalesInvoiceController salesInvoiceController = app.getSalesInvoiceController();

    private static final String INTENT_KEY_SALESINVOICE = "SalesInvoice";
    private final static int REQUESTCODE = 10000;

    private ListView lvSalesInvoice;
    private WaitDialog waitDialog;
    private SalesInvoiceAdapter adapter;

    private List<SalesInvoice> list;
    private List<SalesInvoiceResult> resultList;
    private SalesInvoiceQuery query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_invoice_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initIntent();
        getData();
        //initData();
    }

    public static Intent createIntent(Context context, SalesInvoiceQuery query) {
        Intent intent = new Intent(context, SalesInvoiceResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SALESINVOICE, query);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        lvSalesInvoice = findViewById(R.id.lv_sales_invoice);
        waitDialog = new WaitDialog();

        lvSalesInvoice.setDividerHeight(1);
        lvSalesInvoice.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        resultList = salesInvoiceController.groupSalesInvoice(list);
        showData();
    }
    private void showData() {
        if (resultList.size() == 1) {
            startActivityForResult(SalesInvoiceDetailActivity.createIntent(app, null, resultList.get(0).getSalesInvoices()), REQUESTCODE);
            finish();
        } else {
            adapter = new SalesInvoiceAdapter(getApplicationContext(), resultList);
            this.lvSalesInvoice.setDividerHeight(1);
            this.lvSalesInvoice.setAdapter(adapter);
            this.lvSalesInvoice.setOnItemClickListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                //Refresh Data
                getData();
            }
        }
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {
        query = (SalesInvoiceQuery) this.getIntent().getSerializableExtra(INTENT_KEY_SALESINVOICE);
    }

    private void getData(){
        waitDialog.showWaitDialog(SalesInvoiceResultActivity.this);
        SalesInvoiceTask task = new SalesInvoiceTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(SalesInvoiceResultActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(SalesInvoiceResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);

            }

            @Override
            public void bindModel(Object o) {
                List<SalesInvoice> salesInvoices = (List<SalesInvoice>) o;
                if(salesInvoices != null && salesInvoices.size() > 0){
                    list = salesInvoices;
                    initData();
                }else{
                    displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK, 1);
                }
            }
        }, query, AppConstants.FUNCTION_ID_SALES_INVOICE);
        task.execute();
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }
            @Override
            public void callClose() {

                if(AppConstants.REQUEST_SUCCEED == action){
                    setResult(RESULT_OK);
                }
                finish();
            }
        });
        noticeDialog.create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(resultList != null && resultList.size() > 0){
            startActivityForResult(SalesInvoiceDetailActivity.createIntent(app, null, resultList.get(position).getSalesInvoices()), REQUESTCODE);
        }
    }

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