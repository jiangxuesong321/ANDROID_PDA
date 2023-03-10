package com.sunmi.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.LogisticsProviderTask;
import com.sunmi.pda.asynctasks.MasterTask;
import com.sunmi.pda.asynctasks.MaterialTask;
import com.sunmi.pda.asynctasks.StorageLocationTask;
import com.sunmi.pda.asynctasks.UserTask;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;

public class TestActivity extends AppCompatActivity {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final String ACTION_DATA_CODE_RECEIVED = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED";
    private static final String DATA = "data";
    private static final String SOURCE = "source_byte";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testPlant();
        testLogisticsProvider();
        testMaterial();
        testUser();
    }

    private void testPlant(){
        StorageLocationTask task = new StorageLocationTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {
                
            }
        });
        task.execute();
    }

    private void testLogisticsProvider(){
        LogisticsProviderTask task = new LogisticsProviderTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getApplicationContext(), "LogisticsProvider SUCCESS: ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "LogisticsProvider ERROR: " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.execute();
    }

    private void testMaterial(){
        MaterialTask task = new MaterialTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "ERROR: " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.execute();
    }

    private void testUser(){
        UserTask task = new UserTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getApplicationContext(), "SYNC USER SUCCESS ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "SYNC USER ERROR: " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.execute();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(DATA);
            //String arr = intent.getByteArrayExtra(SOURCE);
            if (code != null && !code.isEmpty()) {
                LogUtils.d("Scan code--->", code);
                //mCode.setText(code);
            }
        }
    };
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
