package com.sunmi.pda.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import android.widget.TextView;

import com.sunmi.pda.R;


public class DialogAddress extends Dialog {

    private Button btnOk;
    private Context context;
    private TextView tvConsigneeValue, tvContactWayValue, tvAddressValue;
    private String consignee, contactWay, address;

    public DialogAddress(Context context, String consignee, String contactWay, String address) {
        super(context, R.style.DialogTheme);
        setCanceledOnTouchOutside(false);
        this.context = context;
        this.consignee = consignee;
        this.contactWay = contactWay;
        this.address = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_address);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvConsigneeValue = findViewById(R.id.tv_consignee_value);
        tvContactWayValue = findViewById(R.id.tv_contact_way_value);
        tvAddressValue = findViewById(R.id.tv_address_value);
        btnOk = findViewById(R.id.btn_close);

    }

    private void bindAllListeners(){

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private void setViewStateBasedOnValue(){
        tvConsigneeValue.setText(consignee);
        tvContactWayValue.setText(contactWay);
        tvAddressValue.setText(address);
    }
}



