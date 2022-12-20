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

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.adapters.ReasonSpinnerAdapter;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.models.Reason;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class DialogPurchaseOrder extends Dialog {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();
    private Button btnOk;
    private Context context;
    private TextView tvItemValue, tvMaterialValue, tvBatchValue, tvRequireCountValue, tvMaterialNameValue;
    private String item, material, materialName, batch, defaultStorageLocation, plant;
    private double requireCount;
    private EditText etInputCountValue;
    private SpinnerAdapter spinnerAdapter;
    private List<StorageLocation> storageLocations;
    private Spinner spinner;
    private LinearLayout llStorageLocation;
    private static final LoginController loginController = app.getLoginController();
    private Login login;
    private String scanCount;
    private boolean isBatch;
    private LinearLayout llPlant, llReason;
    private ReasonSpinnerAdapter reasonSpinnerAdapter;
    private Spinner spReason;
    private List<Reason> reasons = new ArrayList<>();
    private TextView tvPlantValue, tvInputCountLabel, tvRequireCountLabel;
    private String functionId;
    public DialogPurchaseOrder(Context context, String item, String material, String materialName,
                               String batch, double requireCount, String defaultStorageLocation,
                               String plant, String scanCount, boolean isBatch, String functionId) {
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
        this.materialName = materialName;
        this.functionId = functionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set dialog layout.
        setContentView(R.layout.dialog_purchase_order);

        bindAllViewsElement();
        bindAllListeners();
        setViewStateBasedOnValue();
    }

    private void bindAllViewsElement(){
        tvItemValue = findViewById(R.id.tv_item_value);
        tvMaterialValue = findViewById(R.id.tv_material_value);
        tvMaterialNameValue = findViewById(R.id.tv_material_name_value);
        etInputCountValue = findViewById(R.id.et_input_count_value);
        tvRequireCountValue = findViewById(R.id.tv_require_count_value);
        llStorageLocation = findViewById(R.id.ll_storage_location);
        btnOk = findViewById(R.id.btn_close);
        spinner = findViewById(R.id.spinner);
        tvBatchValue = findViewById(R.id.tv_batch_value);
        tvPlantValue = findViewById(R.id.tv_plant_value);
        llPlant = findViewById(R.id.ll_plant);
        llReason = findViewById(R.id.ll_reason);
        spReason = findViewById(R.id.sp_reason);
        tvInputCountLabel = findViewById(R.id.tv_input_count_label);
        tvRequireCountLabel = findViewById(R.id.tv_require_count_label);

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
                    StorageLocation storageLocation = (StorageLocation) spinner.getSelectedItem();
                    Reason reason = (Reason) spReason.getSelectedItem();
                    inputCallback.setInput(item, tvBatchValue.getText().toString(), count, storageLocation, reason);
                    dismiss();
                }

            }
        });
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
    private void setViewStateBasedOnValue(){
        tvItemValue.setText(item);
        tvMaterialValue.setText(material);
        tvMaterialNameValue.setText(materialName);
        tvBatchValue.setText(batch);
        if(isBatch){
            tvBatchValue.setEnabled(true);
        }else{
            tvBatchValue.setEnabled(false);
        }
        tvRequireCountValue.setText(String.valueOf(requireCount));
        etInputCountValue.setText(scanCount);
        login = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        storageLocations = storageLocationController.filterStorageLocationByPlant(storageLocations, plant);
        storageLocations.add(0, new StorageLocation("","", "", ""));

        spinnerAdapter = new SpinnerAdapter(context,
                R.layout.li_spinner_adapter, storageLocations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        int index = storageLocationController.getStorageLocationPosition(defaultStorageLocation, storageLocations);
        spinner.setSelection(index);

        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN)){

            llPlant.setVisibility(View.VISIBLE);
            tvPlantValue.setText(plant);
            llReason.setVisibility(View.VISIBLE);
            Reason reason = new Reason("1", "质量低劣");
            Reason reason2 = new Reason("2", "不完整");
            Reason reason3 = new Reason("3", "损坏");
            reasons.add(reason);
            reasons.add(reason2);
            reasons.add(reason3);

            reasonSpinnerAdapter = new ReasonSpinnerAdapter(context,  R.layout.li_spinner_adapter, reasons);
            reasonSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spReason.setAdapter(reasonSpinnerAdapter);
            spReason.setSelection(0);
        }else{
            if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
                tvInputCountLabel.setText(context.getString(R.string.text_out_count));
                tvRequireCountLabel.setText(context.getString(R.string.text_not_out_count));
            }
            llPlant.setVisibility(View.GONE);
            llReason.setVisibility(View.GONE);
        }
    }

    private InputCallback inputCallback;
    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }
    public interface InputCallback {
        public void setInput(String item, String batch, int count, StorageLocation storageLocation, Reason reason);
    }

}



