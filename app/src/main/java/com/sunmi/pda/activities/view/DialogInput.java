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

import com.sunmi.pda.R;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.StorageLocation;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class DialogInput extends Dialog {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();
    private Button btnOk;
    private Context context;
    private TextView tvItemValue, tvMaterialValue, tvBatchValue, tvRequireCountValue, tvLabelLocation;
    private String item, material, batch, defaultStorageLocation, plant;
    private double requireCount;
    private EditText etInputCountValue;
    private SpinnerAdapter spinnerAdapter;
    private List<StorageLocation> storageLocations;
    private Spinner spinner;
    private LinearLayout llStorageLocation;
    private static final LoginController loginController = app.getLoginController();
    private Login login;
    private int scanCount;
    private boolean isBatch;
    public DialogInput(Context context, String item, String material, String batch,
                       double requireCount, String defaultStorageLocation, String plant,
                       int scanCount, boolean isBatch) {
        super(context, R.style.DialogTheme);
        setCanceledOnTouchOutside(false);
        this.context = context;
        this.item = item;
        this.material = material;
        this.batch = batch;
        this.requireCount = requireCount;
        this.plant = plant;
        this.defaultStorageLocation = defaultStorageLocation;
        this.scanCount = scanCount;
        this.isBatch = isBatch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_input);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvItemValue = findViewById(R.id.tv_item_value);
        tvMaterialValue = findViewById(R.id.tv_material_value);
        tvBatchValue = findViewById(R.id.tv_batch_value);
        etInputCountValue = findViewById(R.id.et_input_count_value);
        tvRequireCountValue = findViewById(R.id.tv_require_count_value);
        llStorageLocation = findViewById(R.id.ll_storage_location);
        btnOk = findViewById(R.id.btn_close);
        spinner = findViewById(R.id.spinner);
        tvLabelLocation = findViewById(R.id.tv_label_location);

    }

    private void bindAllListeners(){

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = StringUtils.isEmpty(etInputCountValue.getText().toString())? 0
                        : Integer.valueOf(etInputCountValue.getText().toString());
                StorageLocation storageLocation = (StorageLocation) spinner.getSelectedItem();
                inputCallback.setInput(item, tvBatchValue.getText().toString(), count, storageLocation);
                dismiss();
            }
        });
    }
    private void setViewStateBasedOnValue(){
        tvItemValue.setText(item);
        tvMaterialValue.setText(material);
        tvBatchValue.setText(batch);
        if(isBatch){
            tvBatchValue.setEnabled(true);
        }else{
            tvBatchValue.setEnabled(false);
        }
        tvRequireCountValue.setText(String.valueOf((int)requireCount));
        etInputCountValue.setText(String.valueOf((int)requireCount));
        login = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        storageLocations = storageLocationController.filterStorageLocationByPlant(storageLocations, plant);
        storageLocations.add(0, new StorageLocation("","", "", ""));
        if(StringUtils.isEmpty(defaultStorageLocation)) {
            spinner.setVisibility(View.VISIBLE);
            tvLabelLocation.setVisibility(View.GONE);
            spinnerAdapter = new SpinnerAdapter(context,
                    R.layout.li_spinner_adapter, storageLocations);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }else{
            tvLabelLocation.setText(defaultStorageLocation);
            spinner.setVisibility(View.GONE);
            tvLabelLocation.setVisibility(View.VISIBLE);
        }
        //加载适配器
        /*if (!userController.userHasAllLocation() && !userController.userHasAllFunction()){
            spinner.setClickable(false);
            spinner.setEnabled(false);
        }*/
        spinner.setAdapter(spinnerAdapter);

    }

    private InputCallback inputCallback;
    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }
    public interface InputCallback {
        public void setInput(String item, String batch, int count, StorageLocation storageLocation);
    }


}



