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

import com.sunmi.pda.adapters.PrintLabelAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.asynctasks.PrintTask;

import com.sunmi.pda.controllers.PrintController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.PrintLabel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class PrintResultActivity extends AppCompatActivity implements ActivityInitialization,
        AdapterView.OnItemClickListener{

    private static final String INTENT_KEY_LabelFlag = "LabelFlag";
    private static final String INTENT_KEY_NUMBER = "Number";
    private final static int REQUESTCODE = 10000;
    private ListView lvPrint;
    private PrintLabelAdapter adapter;
    private List<PrintLabel> printLabels = new ArrayList<>();
    private WaitDialog waitDialog;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private String labelFlag;
    private String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        getPrintData();
        initData();
    }

    public static Intent createIntent(Context context, String number, String labelFlag) {
        Intent intent = new Intent(context, PrintResultActivity.class);
        intent.putExtra(INTENT_KEY_LabelFlag, labelFlag);
        intent.putExtra(INTENT_KEY_NUMBER, number);
        return intent;
    }

    @Override
    public void initView() {
        lvPrint = findViewById(R.id.lv_purchase_order);
        waitDialog = new WaitDialog();
    }

    @Override
    public void initData() {
        if(StringUtils.equalsIgnoreCase(labelFlag, PrintController.SHIPPING_LABEL)){
            getSupportActionBar().setTitle(R.string.text_shipping_label);
        }else{
            getSupportActionBar().setTitle(R.string.text_receive_label);
        }
        showData();
    }
    private void showData() {
        adapter = new PrintLabelAdapter(getApplicationContext(), printLabels);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvPrint.setDividerHeight(1);
        this.lvPrint.setAdapter(adapter);
        this.lvPrint.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
               //Refresh Data
                //getPoData();
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
        labelFlag = this.getIntent().getStringExtra(INTENT_KEY_LabelFlag);
        number = this.getIntent().getStringExtra(INTENT_KEY_NUMBER);
    }

    private void getPrintData(){
        waitDialog.showWaitDialog(PrintResultActivity.this);
        PrintTask task = new PrintTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PrintResultActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PrintResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);

            }

            @Override
            public void bindModel(Object o) {
                printLabels = (List<PrintLabel>) o;
                if(printLabels != null && printLabels.size() > 0){
                    initData();
                }else{
                    displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_FAILED, 1);
                }
            }
        }, number, labelFlag);
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
                if(AppConstants.REQUEST_FAILED == action){
                    finish();
                }

            }
        });
        noticeDialog.create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(PrintDetailActivity.createIntent(getApplicationContext(), printLabels.get(position), labelFlag));
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