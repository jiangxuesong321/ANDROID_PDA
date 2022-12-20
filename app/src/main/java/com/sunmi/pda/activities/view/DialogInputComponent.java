package com.sunmi.pda.activities.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.StorageLocation;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class DialogInputComponent extends Dialog {
    private final static SunmiApplication app = SunmiApplication.getInstance();

    private Button btnOk;
    private Activity context;
    private TextView tvBatchValue, tvRequireCountValue;
    private String batch;
    private double requireCount;
    private double scanCount;
    private EditText etInputCountValue;


    private boolean isBatch;
    public DialogInputComponent(Activity context, String batch, double requireCount,
                                double scanCount, boolean isBatch) {
        super(context, R.style.DialogTheme);
        setCanceledOnTouchOutside(false);
        this.context = context;
        this.batch = batch;
        this.requireCount = requireCount;
        this.scanCount = scanCount;
        this.isBatch = isBatch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_input_component);
        setCancelable(false);
        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){

        tvBatchValue = findViewById(R.id.tv_batch_value);
        etInputCountValue = findViewById(R.id.et_input_count_value);
        tvRequireCountValue = findViewById(R.id.tv_require_count_value);
        btnOk = findViewById(R.id.btn_close);
    }

    private void bindAllListeners(){

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = StringUtils.isEmpty(etInputCountValue.getText().toString())? 0
                        : Integer.valueOf(etInputCountValue.getText().toString());
                if(count > requireCount){
                    displayDialog(context.getString(R.string.text_out_of_gauge),
                            1);
                }else{
                    inputCallback.setInput(tvBatchValue.getText().toString(), count);
                    dismiss();
                }

            }
        });
    }
    private void setViewStateBasedOnValue(){

        tvBatchValue.setText(batch);
        if(isBatch){
            tvBatchValue.setEnabled(true);
        }else{
            tvBatchValue.setEnabled(false);
        }
        tvRequireCountValue.setText(String.valueOf((int)requireCount));
        etInputCountValue.setText(String.valueOf((int)scanCount));
    }

    private InputCallback inputCallback;
    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }
    public interface InputCallback {
        public void setInput(String batch, double count);
    }

    private void displayDialog(String message, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(context, message, buttonCount);
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



