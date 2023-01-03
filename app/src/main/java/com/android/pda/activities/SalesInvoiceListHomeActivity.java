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
import android.widget.Spinner;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.DeliveryStatusSpinnerAdapter;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.SalesInvoiceController;
import com.android.pda.models.DeliveryStatusList;
import com.android.pda.models.DeliveryStatus;
import com.android.pda.models.SalesInvoiceQuery;
import com.android.pda.utils.AppUtil;
import com.android.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SalesInvoiceListHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();

    private WaitDialog waitDialog;

    private EditText etDeliveryDateFrom;
    private EditText etDeliveryDateTo;
    private EditText etSalesInvoiceNumber;

    private Spinner spinnerDeliveryStatus;
    private DeliveryStatusSpinnerAdapter spinnerAdapterDeliveryStatus;

    private final static int REQUESTCODE = 10001;
    private static final SalesInvoiceController controller = app.getSalesInvoiceController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_invoice_list_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SalesInvoiceListHomeActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        etSalesInvoiceNumber = findViewById(R.id.et_sales_invoice_number);
        etDeliveryDateFrom = findViewById(R.id.et_delivery_date_from);
        hideSoftInput(etDeliveryDateFrom);
        etDeliveryDateTo = findViewById(R.id.et_delivery_date_to);
        hideSoftInput(etDeliveryDateTo);
        spinnerDeliveryStatus = findViewById(R.id.sp_delivery_status);
        waitDialog = new WaitDialog();
    }

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initData() {
        String number = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SALES_INVOICE);
        etSalesInvoiceNumber.setText(number);

        DeliveryStatusList deliveryStatusList = new DeliveryStatusList();
        spinnerAdapterDeliveryStatus = new DeliveryStatusSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, deliveryStatusList.getDeliveryStatusList());
        spinnerAdapterDeliveryStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeliveryStatus.setAdapter(spinnerAdapterDeliveryStatus);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {
        etDeliveryDateFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog(1);
                }
            }
        });
        etDeliveryDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog(1);
            }
        });
        etDeliveryDateTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog(2);
                }
            }
        });
        etDeliveryDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog(2);
            }
        });
    }



    @Override
    public void initIntent() {

    }


    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

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
    public void download(View view){

    }

    public void search(View view){
        String number = etSalesInvoiceNumber.getText().toString();
        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SALES_INVOICE, number);

        String deliveryDateFrom = etDeliveryDateFrom.getText().toString();
        String deliveryDateFromTo = etDeliveryDateTo.getText().toString();
        DeliveryStatus deliveryStatus = (DeliveryStatus) spinnerDeliveryStatus.getSelectedItem();
        List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
        if(deliveryStatus != null && !StringUtils.isEmpty(deliveryStatus.getCode())){
            deliveryStatuses.add(deliveryStatus);
        }

        SalesInvoiceQuery query = new SalesInvoiceQuery(number, deliveryDateFrom, deliveryDateFromTo, deliveryStatuses);
        String error = controller.verifyQuery(query);
        if(StringUtils.isEmpty(error)){
            startActivityForResult(SalesInvoiceResultActivity.createIntent(app, query), REQUESTCODE);
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
                    case 1 : etDeliveryDateFrom.setText(date);
                        break;
                    case 2 : etDeliveryDateTo.setText(date);
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
        etSalesInvoiceNumber.setText("");
    }

    public void clearDeliveryFrom(View view){
        etDeliveryDateFrom.setText("");
    }
    public void clearDeliveryTo(View view){
        etDeliveryDateTo.setText("");
    }

}