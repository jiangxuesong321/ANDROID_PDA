package com.android.pda.activities.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.android.pda.R;

import org.apache.commons.lang3.StringUtils;


public class NoticeDialog extends Dialog {
    private Context context;
    private String message;
    private int buttonCount;
    private String positiveButtonText, negativeButtonText;
    public NoticeDialog(@NonNull Context context, String message, int buttonCount) {
        super(context);
        this.context = context;
        this.message = message;
        this.buttonCount = buttonCount;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.text_prompt));
        builder.setMessage(message);
        if(buttonCount > 1){
            builder.setPositiveButton(StringUtils.isEmpty(positiveButtonText)? context.getString(R.string.text_ok) : positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buttonCallback.callOk();
                }
            });
        }
        builder.setNegativeButton(StringUtils.isEmpty(negativeButtonText)?context.getString(R.string.text_close): negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonCallback.callClose();
            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ButtonCallback buttonCallback;
    public void setButtonCallback(ButtonCallback buttonCallback) {
        this.buttonCallback = buttonCallback;
    }
    public interface ButtonCallback {
        public void callOk();
        public void callClose();
    }
}
