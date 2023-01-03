package com.android.pda.activities;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.PurchaseOrderSubContractController;
import com.android.pda.database.pojo.Offline;
import com.android.pda.models.PurchaseOrderQuery;
import com.android.pda.models.PurchaseOrderSubContract;
import com.android.pda.utils.AppUtil;
import com.android.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.List;

public class PoSubContractHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();

    private EditText etReceivingDateFrom;
    private EditText etReceivingDateTo;
    private EditText etCreateDateFrom;
    private EditText etCreateDateTo;
    private EditText etPoNumber;
    private EditText etSupplierValue;
    private WaitDialog waitDialog;
    private final static int REQUESTCODE = 10001;

    private static final PurchaseOrderSubContractController purchaseOrderController = app.getPurchaseOrderSubContractController();
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private PurchaseOrderQuery purchaseOrderQuery;
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private String functionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }
    }

    public static Intent createIntent(Context context, String functionId) {
        Intent intent = new Intent(context, PoSubContractHomeActivity.class);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, functionId);
        return intent;
    }

    @Override
    public void initView() {

        etPoNumber = findViewById(R.id.et_po_number);
        etSupplierValue = findViewById(R.id.et_supplier_value);
        etReceivingDateFrom = findViewById(R.id.et_receiving_date_from);
        hideSoftInput(etReceivingDateFrom);
        etReceivingDateTo = findViewById(R.id.et_receiving_date_to);
        hideSoftInput(etReceivingDateTo);
        etCreateDateFrom = findViewById(R.id.et_create_date_from);
        hideSoftInput(etCreateDateFrom);
        etCreateDateTo = findViewById(R.id.et_create_date_to);
        hideSoftInput(etCreateDateTo);
        waitDialog = new WaitDialog();
    }

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initData() {
        offline = offlineController.getOfflineData(functionId);
        String number = "";
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT)){
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_SUBCONTRACT_OUT);
            getSupportActionBar().setTitle(getString(R.string.text_po_sub_contract_out));
        }else{
            number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_SUBCONTRACT_IN);
            getSupportActionBar().setTitle(getString(R.string.text_po_sub_contract_in));
        }
        etPoNumber.setText(number);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {
        etReceivingDateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog(1);
                }
            }
        });
        etReceivingDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog(1);
            }
        });
        etReceivingDateTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog(2);
                }
            }
        });
        etReceivingDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog(2);
            }
        });
        etCreateDateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog(3);
                }
            }
        });
        etCreateDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog(3);
            }
        });
        etCreateDateTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog(4);
                }
            }
        });
        etCreateDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog(4);
            }
        });
    }



    @Override
    public void initIntent() {
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
        System.out.println("functionId---->" + functionId);
    }



    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    List<PurchaseOrderSubContract> orders =(List<PurchaseOrderSubContract>) JSON.parseArray(offline.getOrderBody(), PurchaseOrderSubContract.class);
                    System.out.println("orders---->" + JSON.toJSONString(orders));
                    startActivityForResult(PoOrderSubContractDetailActivity.createIntent(app, orders, functionId), REQUESTCODE);
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER);
                    startActivityForResult(PoOrderSubContractResultActivity.createIntent(app, purchaseOrderQuery, functionId), REQUESTCODE);
                }
            }
        });
        noticeDialog.create();
    }
    public void download(View view){

    }

    public void search(View view){
        String number = etPoNumber.getText().toString();
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT)){
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_SUBCONTRACT_OUT, number);
        }else{
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER_SUBCONTRACT_IN, number);
        }
        String supplier = etSupplierValue.getText().toString();
        String deliveryDateFrom = etReceivingDateFrom.getText().toString();
        String deliveryDateFromTo = etReceivingDateTo.getText().toString();
        String createDateFrom = etCreateDateFrom.getText().toString();
        String createDateTo = etCreateDateTo.getText().toString();
        purchaseOrderQuery = new PurchaseOrderQuery(number, createDateFrom, createDateTo,
                deliveryDateFrom, deliveryDateFromTo, supplier);
        String error = purchaseOrderController.verifyData(purchaseOrderQuery);
        if(StringUtils.isEmpty(error)){
            offline = offlineController.getOfflineData(functionId);
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else{
                startActivityForResult(PoOrderSubContractResultActivity.createIntent(app, purchaseOrderQuery, functionId), REQUESTCODE);
            }

        }else{
            displayDialog(error, AppConstants.REQUEST_STAY, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                //Refresh Data

            }
        }
    }


    public void showDatePickerDialog(int flag){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "-" + DateUtils.getMonthOrDate(monthOfYear + 1)+ "-" + DateUtils.getMonthOrDate(dayOfMonth);
                switch (flag){
                    case 1 : etReceivingDateFrom.setText(date);
                        break;
                    case 2 : etReceivingDateTo.setText(date);
                        break;
                    case 3 : etCreateDateFrom.setText(date);
                        break;
                    case 4 : etCreateDateTo.setText(date);
                        break;
                }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clearPo(View view){
        etPoNumber.setText("");
    }

    public void clearSupplier(View view){
        etSupplierValue.setText("");
    }

    public void clearDeliveryFrom(View view){
        etReceivingDateFrom.setText("");
    }
    public void clearDeliveryTo(View view){
        etReceivingDateTo.setText("");
    }

    public void clearCreateFrom(View view){
        etCreateDateFrom.setText("");
    }

    public void clearCreateTo(View view){
        etCreateDateTo.setText("");
    }

}