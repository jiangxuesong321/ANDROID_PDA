package com.android.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.LogisticsProviderTask;
import com.android.pda.asynctasks.MaterialTask;
import com.android.pda.asynctasks.StorageLocationTask;
import com.android.pda.asynctasks.UserTask;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;

public class TestActivity extends AppCompatActivity {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String ACTION_DATA_CODE_RECEIVED = "com.android.scanner.ACTION_DATA_CODE_RECEIVED";
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
