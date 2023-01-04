package com.android.pda.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.LoginAsyncTask;
import com.android.pda.asynctasks.LogisticsProviderTask;
import com.android.pda.asynctasks.MaterialTask;
import com.android.pda.asynctasks.StorageLocationTask;
import com.android.pda.asynctasks.UserTask;
import com.android.pda.controllers.LoginController;
import com.android.pda.controllers.MaterialController;
import com.android.pda.controllers.StorageLocationController;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;
import com.android.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

public class LoginActivity extends AppCompatActivity implements ActivityInitialization {
    private EditText etUser;
    private EditText etPwd;
    private ProgressDialog progressDialog;

    protected static final String TAG = LoginActivity.class.getSimpleName();
    private static final AndroidApplication application = AndroidApplication.getInstance();
    private static final LoginController loginController = application.getLoginController();
    private static final MaterialController materialController = application.getMaterialController();
    private static final StorageLocationController storageLocationController = application.getStorageLocationController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
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
    }

    private class UserFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (etUser.getText().toString() != null && !etUser.getText().toString().isEmpty()) {
                    etUser.setBackground(null);
                }
            }
        }
    }

    private class PwdFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (etPwd.getText().toString() != null && !etPwd.getText().toString().isEmpty()) {
                    etPwd.setBackground(null);
                }
            }
        }
    }

    @Override
    public void initIntent() {

    }

    /**
     * 用户登录校验
     * @param view
     */
    public void login(View view) {
        String userId = etUser.getText().toString();
        String pwd = etPwd.getText().toString();

        // 配置 Host Url
        AppUtil.saveServiceHost(getApplicationContext(), "https://sapqas.sunmi.com");
        LogUtils.e(TAG, "Selected Host-------->" + AppUtil.getServiceHost(getApplicationContext()));

        if ((userId != null && !userId.isEmpty()) && (pwd != null && !pwd.isEmpty())) {
            showWaitDialog();
            LoginAsyncTask task = new LoginAsyncTask(loginHandler, userId.toUpperCase(), pwd, LoginController.FLAG_LOGIN);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            etUser.setBackground(null);
            etPwd.setBackground(null);
        }
    }

    /**
     * 负责接收登录校验后 Handler Message 结果处理
     */
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
        }

        ;
    };

    /**
     * 隐藏弹出的等待对话框
     */
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

    /**
     * 登录成功，下载指定主数据，并隐藏进度框
     */
    private void loginSucceed() {
        if (loginController.getLoginUser() != null) {
            //TODO: download master data for first login
            int count = storageLocationController.getStorageLocationCount();
            if (count == 0) {
                // don not download material when login
                //downloadMaterial();
                downloadLocation();
                downloadUser();
                downloadLogisticsProvider();
            } else {
                hideWaitDialog();
                startActivity(MainActivity.createIntent(getApplicationContext()));
                finish();
            }
        }

    }

    private int finishedCount;

    private void downloadFinished() {
        finishedCount++;
        if (finishedCount == 3) {
            hideWaitDialog();
            startActivity(MainActivity.createIntent(getApplicationContext()));
            finish();
        }
    }

    /**
     * 下载物料主数据信息
     */
    private void downloadMaterial() {
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

    /**
     * 下载库存地点信息
     */
    private void downloadLocation() {
        StorageLocationTask task = new StorageLocationTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_location_succeed), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String error) {
                downloadFinished();
                Toast.makeText(getApplicationContext(), getString(R.string.text_download_location_failed) + ": " + error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void bindModel(Object o) {

            }
        });
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 下载用户主数据信息
     */
    private void downloadUser() {
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

    /**
     * 下载物流商主数据
     */
    private void downloadLogisticsProvider() {
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

    /**
     * 用户点击登录，弹出等待窗口
     */
    private void showWaitDialog() {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                if (progressDialog == null) {
                    progressDialog = ProgressDialog.show(LoginActivity.this,
                            "",
                            getString(R.string.alert_waiting),
                            true);
                }
            }
        });
    }

    /**
     * 关闭进度对话，提示错误信息对话框
     * @param message
     */
    private void displayDialog(String message) {
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
}