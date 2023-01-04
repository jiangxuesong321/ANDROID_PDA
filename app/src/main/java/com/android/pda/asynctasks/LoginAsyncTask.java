package com.android.pda.asynctasks;

import com.android.pda.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.LoginController;
import com.android.pda.database.pojo.Login;


public class LoginAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final AndroidApplication application = AndroidApplication.getInstance();
    private static final LoginController loginController = application.getLoginController();
    protected static final String TAG = LoginAsyncTask.class.getSimpleName();
    public static final String PARAM_OUT_ERROR = "PARAM_OUT_ERROR";
    private Handler mHandler;
    private String userId;
    private String pwd;
    private int loginFlag;

    public LoginAsyncTask(Handler handler, String userId, String pwd, int loginFlag) {
        this.mHandler = handler;
        this.userId = userId;
        this.pwd = pwd;
        this.loginFlag = loginFlag;
    }

    @Override
    protected void onPreExecute() {
        //showProgressDialog();
        super.onPreExecute();
    }

    private void login() {
        try {
            String loginErrorMsg = loginController.login(userId, pwd);
            if (loginErrorMsg.equalsIgnoreCase("")) {
                String permissionErrorMsg = loginController.getUserPermission(userId);
                permissionErrorMsg = "";
                if (permissionErrorMsg.equalsIgnoreCase("")) {
                    Login login = loginController.getLoginUser();
                    if (login != null) {
                        //TODO: goto home screen
                    }
                    sendMessageByWhat(1, loginErrorMsg);
                } else {
                    sendMessageByWhat(0, permissionErrorMsg);
                }
            } else {
                sendMessageByWhat(0, loginErrorMsg);
            }
            //sendMessageByWhat(1, loginErrorMsg);
        } catch (Exception e) {
            sendMessageByWhat(0, application.getString(R.string.text_service_failed));
            e.printStackTrace();
        }
    }

    private void logout() {
        loginController.logout("1215645656");
        sendMessageByWhat(1, "");
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (loginFlag == LoginController.FLAG_LOGIN) {
            login();
        } else {
            logout();
        }
        return null;
    }

    //send handler message
    private void sendMessageByWhat(int messageWhat, String errorMsg) {
        Message message = new Message();
        message.what = messageWhat;
        Bundle bundle = new Bundle();
        if (messageWhat == 0) {
            bundle.putSerializable(PARAM_OUT_ERROR, errorMsg);
        }
        message.setData(bundle);
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

}
