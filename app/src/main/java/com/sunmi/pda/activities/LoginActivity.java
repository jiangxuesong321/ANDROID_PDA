package com.sunmi.pda.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.LoginAsyncTask;
import com.sunmi.pda.asynctasks.LogisticsProviderTask;
import com.sunmi.pda.asynctasks.MaterialTask;
import com.sunmi.pda.asynctasks.StorageLocationTask;
import com.sunmi.pda.asynctasks.UserTask;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity implements ActivityInitialization{
    private EditText etUser;
    private EditText etPwd;
    private ProgressDialog progressDialog;

    private TextView tvQAS, tvProduction;
    private ImageView ivProduction, ivQas;
    private RelativeLayout rlProduction, rlQas;
    private LinearLayout llSelectEnv;
    private String env;
    protected static final String TAG = LoginActivity.class.getSimpleName();
    private static final SunmiApplication application = SunmiApplication.getInstance();
    private static final LoginController loginController = application.getLoginController();
    private static final MaterialController materialController = application.getMaterialController();
    private static final StorageLocationController storageLocationController = application.getStorageLocationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
        if(StringUtils.equalsIgnoreCase("P", getString(R.string.default_environment))){
            llSelectEnv.setVisibility(View.INVISIBLE);
            production(null);
        }else{
            qas(null);
        }
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }
    @Override
    public void initView() {
        etUser = findViewById(R.id.et_user);
        etPwd = findViewById(R.id.et_pwd);
        //rg = findViewById(R.id.rg_env);
        tvQAS =  findViewById(R.id.tv_qas);
        tvProduction =  findViewById(R.id.tv_production);
        ivProduction = findViewById(R.id.iv_production);
        ivQas = findViewById(R.id.iv_qas);
        rlProduction = findViewById(R.id.rl_production);
        rlQas = findViewById(R.id.rl_qas);
        llSelectEnv = findViewById(R.id.ll_select_env);

    }

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {
        etUser.setOnFocusChangeListener(new UserFocusChangeListener());
        etPwd.setOnFocusChangeListener(new PwdFocusChangeListener());
        //rg.setOnCheckedChangeListener(new MyRadioButtonListener());
    }

    private class UserFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus){
                if(etUser.getText().toString() != null && !etUser.getText().toString().isEmpty()){
                    etUser.setBackground(null);
                }
            }
        }
    }

    private class PwdFocusChangeListener implements View.OnFocusChangeListener{

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus){
                if(etPwd.getText().toString() != null && !etPwd.getText().toString().isEmpty()){
                    etPwd.setBackground(null);
                }
            }
        }
    }

    class MyRadioButtonListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

           /* switch (checkedId) {
                case R.id.rb_qas:
                    env = rbQAS.getText().toString();
                    break;
                case R.id.rb_production:
                    env = rbProduction.getText().toString();
                    break;
            }*/
        }
    }

    @Override
    public void initIntent() {

    }

    public void login(View view){
        String userId = etUser.getText().toString();
        String pwd = etPwd.getText().toString();
        if(userId == null || userId.isEmpty()){
//            etUser.setBackground(getResources().getDrawable(R.drawable.group_error));
        }

        if(pwd == null || pwd.isEmpty()){
          //  etPwd.setBackground(getResources().getDrawable(R.drawable.group_error));
        }
        if(env == null){
            env = tvProduction.getText().toString();
        }
        if(StringUtils.equalsIgnoreCase(env, getString(R.string.login_prd))){
            AppUtil.saveServiceHost(getApplicationContext(), "https://sap.sunmi.com");
        }else{
            LogUtils.e(TAG, "Selected env-------->" + env);
            AppUtil.saveServiceHost(getApplicationContext(), "https://sapqas.sunmi.com");
        }
        LogUtils.e(TAG, "Selected env-------->" + AppUtil.getServiceHost(getApplicationContext()));
        if((userId != null && !userId.isEmpty()) && (pwd != null && !pwd.isEmpty())){
            showWaitDialog();
            LoginAsyncTask task = new LoginAsyncTask(loginHandler, userId.toUpperCase(), pwd, env, LoginController.FLAG_LOGIN);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            etUser.setBackground(null);
            etPwd.setBackground(null);
        }
    }

    private Handler loginHandler = new Handler() {
        public void handleMessage(Message msg) {

            LogUtils.e(TAG, "Login msg.what--------->" + msg.what);
            switch (msg.what) {
                case 1:
                    loginSucceed();
                    break;
                case 0:
                    Bundle bundle = msg.getData();
                    String error = (String) bundle.getSerializable(LoginAsyncTask.PARAM_OUT_ERROR);
                    displayDialog(error);
                    break;
            }
        };
    };

    //hide progressDialog
    private void hideWaitDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                //Log.e(TAG, "Hide Wait Dialog....");
                if (progressDialog != null) {
                    //Log.e(TAG, "progressDialog is.... " + progressDialog);
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    private void loginSucceed(){
        if(loginController.getLoginUser() != null){
            //TODO: download master data for first login
            int count = storageLocationController.getStorageLocationCount();
            if (count == 0){
                // don not download material when login
                //downloadMaterial();
                downloadLocation();
                downloadUser();
                downloadLogisticsProvider();
            }else{
                hideWaitDialog();
                startActivity(MainActivity.createIntent(getApplicationContext()));
                finish();
            }
        }

    }
    private int finishedCount;
    private void downloadFinished(){
        finishedCount ++;
        if(finishedCount == 3){
            hideWaitDialog();
            startActivity(MainActivity.createIntent(getApplicationContext()));
            finish();
        }
    }

    private void downloadMaterial(){
        MaterialTask task = new MaterialTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_material_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_material_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void downloadLocation(){
        StorageLocationTask task = new StorageLocationTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_location_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_location_failed) +  ": " +error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void downloadUser(){
        UserTask task = new UserTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_user_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_user_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void downloadLogisticsProvider(){
        LogisticsProviderTask task = new LogisticsProviderTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_logistics_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_logistics_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showWaitDialog() {
        if(isFinishing()){
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                if(progressDialog == null){
                    progressDialog = ProgressDialog.show(LoginActivity.this,
                            "",
                            getString(R.string.alert_waiting),
                            true);
                }
            }
        });
    }

    private void displayDialog(String message){
        hideWaitDialog();
        NoticeDialog noticeDialog = new NoticeDialog(this, message, 1);
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

    public void production(View view){
        env = tvProduction.getText().toString();
        ivProduction.setVisibility(View.VISIBLE);
        ivQas.setVisibility(View.GONE);
        rlProduction.setBackground(getDrawable(R.drawable.view_selected));
        rlQas.setBackground(getDrawable(R.drawable.view_unselected));
        AppUtil.saveServiceHost(getApplicationContext(), getString(R.string.sap_url_host));
    }
    public void qas(View view){
        env = tvQAS.getText().toString();
        ivProduction.setVisibility(View.GONE);
        ivQas.setVisibility(View.VISIBLE);
        rlProduction.setBackground(getDrawable(R.drawable.view_unselected));
        rlQas.setBackground(getDrawable(R.drawable.view_selected));
        AppUtil.saveServiceHost(getApplicationContext(), getString(R.string.sap_url_host_q));
    }
}