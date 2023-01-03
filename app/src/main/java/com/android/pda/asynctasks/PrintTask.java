package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.PrintController;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.PrintLabel;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PrintTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final PrintController controller = app.getPrintController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private String labelFlag;
    private String number;
    public PrintTask(Context context, OnTaskEventListener callback, String number, String labelFlag) {
        mCallBack = callback;
        mContext = context;
        this.labelFlag = labelFlag;
        this.number = number;
    }
    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<PrintLabel> printLabels = controller.syncPrintLabel(number, labelFlag);
            if(printLabels != null){
                return printLabels;
            }
        }catch (AuthorizationException e){
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (Exception e) {
            e.printStackTrace();
            error = mContext.getString(R.string.text_service_failed);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (mCallBack != null) {
            if (StringUtils.isEmpty(error)) {
                mCallBack.onSuccess(null);
                mCallBack.bindModel(o);
            } else {
                mCallBack.onFailure(error);
            }
        }
    }
}
