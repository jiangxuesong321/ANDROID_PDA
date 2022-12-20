package com.sunmi.pda.activities;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.LogisticsProviderTask;
import com.sunmi.pda.asynctasks.MaterialTask;
import com.sunmi.pda.asynctasks.StorageLocationTask;
import com.sunmi.pda.asynctasks.UserTask;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.BusinessOrderQuery;
import com.sunmi.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;


public class MasterDataActivity extends AppCompatActivity implements ActivityInitialization{
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private ProgressBar pbLogisticsProvider, pbUser, pbLocation, pbMaterial;
    private TextView tvLastChangeDateMaterial, tvLastChangeDateLocation, tvLastChangeDateUser,
            tvLastChangeDateLogisticsProvider, tvVersion;
    private Button btnReview;
    private static final UserController userController = app.getUserController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MasterDataActivity.class);
        return intent;
    }


    @Override
    public void initView() {
        pbLogisticsProvider = findViewById(R.id.pb_logistics_provider);
        pbUser = findViewById(R.id.pb_user);
        pbLocation = findViewById(R.id.pb_location);
        pbMaterial = findViewById(R.id.pb_material);
        tvLastChangeDateMaterial = findViewById(R.id.tv_last_change_date_material);
        tvLastChangeDateLocation = findViewById(R.id.tv_last_change_date_location);
        tvLastChangeDateUser = findViewById(R.id.tv_last_change_date_user);
        tvLastChangeDateLogisticsProvider = findViewById(R.id.tv_last_change_date_logistics_provider);
        tvVersion = findViewById(R.id.tv_version);
        btnReview = findViewById(R.id.btn_review);
        if(!userController.userHasAllFunction()){
            btnReview.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void initData() {
        tvLastChangeDateMaterial.setText(AppUtil.getLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_MATERIAL));
        tvLastChangeDateLocation.setText(AppUtil.getLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_LOCATION));
        tvLastChangeDateUser.setText(AppUtil.getLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_USER));
        tvLastChangeDateLogisticsProvider.setText(AppUtil.getLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_LOGISTICS));
        tvVersion.setText("V" + AppUtil.getVersionName(getApplicationContext()));
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
    public void downloadMaterial(View view){
        downloadMaterial();
    }
    public void reviewMaterial(View view){
        startActivity(MaterialActivity.createIntent(getApplicationContext()));
    }

    public void downloadLocation(View view){
        downloadLocation();
    }

    public void reviewLocation(View view){
        startActivity(StorageLocationActivity.createIntent(getApplicationContext()));
    }

    public void downloadUser(View view){
        downloadUser();
    }
    public void reviewUser(View view){
        startActivity(UserActivity.createIntent(getApplicationContext()));
    }

    public void downloadLogisticsProvider(View view){
        downloadLogisticsProvider();
    }
    public void reviewLogisticsProvider(View view){
        startActivity(LogisticsProviderActivity.createIntent(getApplicationContext()));
    }

    private void downloadMaterial(){
        pbMaterial.setVisibility(View.VISIBLE);
        MaterialTask task = new MaterialTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                pbMaterial.setVisibility(View.INVISIBLE);
                initData();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_material_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                pbMaterial.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_material_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void downloadLocation(){
        pbLocation.setVisibility(View.VISIBLE);
        StorageLocationTask task = new StorageLocationTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                pbLocation.setVisibility(View.INVISIBLE);
                initData();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_location_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                pbLocation.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_location_failed) +  ": " +error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void downloadUser(){
        pbUser.setVisibility(View.VISIBLE);
        UserTask task = new UserTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                pbUser.setVisibility(View.INVISIBLE);
                initData();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_user_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                pbUser.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_user_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void downloadLogisticsProvider(){
        pbLogisticsProvider.setVisibility(View.VISIBLE);
        LogisticsProviderTask task = new LogisticsProviderTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                pbLogisticsProvider.setVisibility(View.INVISIBLE);
                initData();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_logistics_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                pbLogisticsProvider.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_logistics_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void downloadAll(View view){
        downloadMaterial();
        downloadLocation();
        downloadUser();
        downloadLogisticsProvider();
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