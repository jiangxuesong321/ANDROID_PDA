package com.sunmi.pda.activities.view;

import android.app.Activity;
import android.app.ProgressDialog;

import com.sunmi.pda.R;


public class WaitDialog {

    private  ProgressDialog progressDialog;
    public  void showWaitDialog(final Activity activity) {

        if(activity.isFinishing()){
            return;
        }
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if(progressDialog == null){
                    //Log.e(TAG, "Show Wait Dialog....");
                    progressDialog = ProgressDialog.show(activity,
                            "",
                            activity.getString(R.string.text_alert_waiting),
                            true);
                }
            }
        });


    }
    //hide progressDialog
    public void hideWaitDialog(final Activity activity) {
        if(activity != null && !activity.isFinishing()){
            activity.runOnUiThread(new Runnable() {
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
    }
}
