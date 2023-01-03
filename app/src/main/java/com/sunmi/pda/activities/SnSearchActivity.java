package com.sunmi.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;



import com.sunmi.pda.adapters.SnSearchAdapter;

import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;


import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class SnSearchActivity extends AppCompatActivity implements ActivityInitialization, SnSearchAdapter.DeleteCallback{
    private final static AndroidApplication app = AndroidApplication.getInstance();


    private ListView lvSerial;
    private List<String> snList = new ArrayList<>();
    private SnSearchAdapter adapter;
    private static final SerialInfoController controller = app.getSerialInfoController();
    private TextView etSn;
    private int removePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sn_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        initData();
        bindView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SnSearchActivity.class);
        return intent;
    }

    @Override
    public void initView() {
        this.lvSerial = findViewById(R.id.lv_list);
        etSn = findViewById(R.id.et_sn);
    }

    @Override
    public void initData() {

    }

    private void bindView(){
        adapter = new SnSearchAdapter(getApplicationContext(), snList);
        this.lvSerial.setDividerHeight(1);
        this.lvSerial.setAdapter(adapter);
        adapter.setSplitCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    public void search(View view){
        if(snList == null || snList.size() == 0){
            displayDialog( app.getString(R.string.error_input_sn), -1, 1);
            return;
        }
        startActivity(SnSearchDetailActivity.createIntent(getApplicationContext(), snList));
        etSn.setText("");
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if(AppConstants.REQUEST_DELETE == action){
                    snList.remove(removePosition);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void callClose() {
            }
        });
        noticeDialog.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        snList.clear();
        adapter.notifyDataSetChanged();
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


    //******* Scan *******//

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);
            addSnNumbers(codes);
        }
    };

    private void addSnNumbers(List<String> codes){

        int errorId = controller.verifyScanData(snList, codes);
        if(errorId < 0){
            //没有错误，验证通过
            snList.addAll(codes);
            adapter.notifyDataSetChanged();
        }else{
            switch (errorId){
                case ScanController.ERROR_REPEAT_SCAN:
                    displayDialog( app.getString(R.string.text_repeat_scan), -1, 1);
                    break;
                case ScanController.ERROR_MAX_COUNT:
                    displayDialog( app.getString(R.string.text_max_scan_count), -1, 1);
                    break;
            }
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void add(View view){
        String sn = etSn.getText().toString();
        List<String> codes = new ArrayList<>();
        codes.add(sn);
        addSnNumbers(codes);
        etSn.setText("");
    }

    @Override
    public void onCallBack(int position) {
        removePosition = position;
        displayDialog( app.getString(R.string.text_confirm_delete), AppConstants.REQUEST_DELETE, 2);
    }
}