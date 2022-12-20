package com.sunmi.pda.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.utils.AppUtil;


public class DialogPrintSetting extends Dialog {
    private final static SunmiApplication app = SunmiApplication.getInstance();


    private Button btnOk;
    private Context context;

    private EditText etIPValue;

    public DialogPrintSetting(Context context) {
        super(context, R.style.DialogTheme);
        setCanceledOnTouchOutside(false);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_print_setting);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        String ip = AppUtil.getLastInput(context, AppUtil.PROPERTY_LAST_INPUT_PRINT_IP);
        etIPValue = findViewById(R.id.et_ip_value);
        etIPValue.setText(ip);
        btnOk = findViewById(R.id.btn_close);
    }

    private void bindAllListeners(){

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.saveLastInput(context, AppUtil.PROPERTY_LAST_INPUT_PRINT_IP, etIPValue.getText().toString());
                dismiss();
            }
        });
    }
    private void setViewStateBasedOnValue(){

    }
}



