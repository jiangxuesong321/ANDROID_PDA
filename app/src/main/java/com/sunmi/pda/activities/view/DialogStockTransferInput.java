package com.sunmi.pda.activities.view;

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
import android.widget.Toast;

import com.sunmi.pda.R;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.database.pojo.StorageLocation;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class DialogStockTransferInput extends Dialog {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final MaterialController materialController = app.getMaterialController();

    private Button btnOk;
    private Context context;
    private TextView tvMaterial, tvBatch, tvQuantity, tvErrorMsg;

    private EditText etMaterialValue, etBatchValue, etQuantityValue;

    public DialogStockTransferInput(Context context) {
        super(context, R.style.DialogTheme);
        setCanceledOnTouchOutside(false);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_stock_transfer_input);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        etMaterialValue = findViewById(R.id.et_material_value);
        etQuantityValue = findViewById(R.id.et_quantity_value);
        etBatchValue = findViewById(R.id.et_batch_value);
        tvErrorMsg = findViewById(R.id.tv_error_msg);
        btnOk = findViewById(R.id.btn_close);
    }

    private void bindAllListeners(){

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String material = etMaterialValue.getText().toString();
                String batch = etBatchValue.getText().toString();
                String count = etQuantityValue.getText().toString();
                if (material == null || material.isEmpty() || batch == null || batch.isEmpty() || count == null || count.isEmpty()) {
                    tvErrorMsg.setText(app.getString(R.string.error_no_material));
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    return;
                }

                Material oMaterial = materialController.getMaterialByKey(material);
                if (oMaterial == null) {
                    tvErrorMsg.setText(app.getString(R.string.error_no_material));
                    tvErrorMsg.setVisibility(View.VISIBLE);
                } else {
                    tvErrorMsg.setVisibility(View.INVISIBLE);
                    inputCallback.setInput(material, batch, count, oMaterial.getMaterialName());
                    dismiss();
                }
            }
        });
    }
    private void setViewStateBasedOnValue(){

    }

    private InputCallback inputCallback;

    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }
    public interface InputCallback {
        public void setInput(String material, String batch, String count, String materialDesc);
    }

}



