package com.sunmi.pda.activities;

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
import android.widget.Toast;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.models.DeliveryStatus;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SalesInvoiceHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private EditText etRequestDeliveryDateFrom;
    private EditText etRequestDeliveryDateTo;
    private EditText evSalesInvoiceNumber;
    private WaitDialog waitDialog;
    private final static int REQUESTCODE = 20001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_invoice_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initData();
        initListener();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SalesInvoiceHomeActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {

        evSalesInvoiceNumber= findViewById(R.id.et_sales_invoice_number);
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
        evSalesInvoiceNumber.setText(number);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }



    @Override
    public void initIntent() {

    }

    public void download(View view){
        String number = evSalesInvoiceNumber.getText().toString();
        AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SALES_INVOICE, number);
        System.out.println("download---->");
        if(number == null || number.isEmpty()){
            Toast.makeText(this, "请输入发货单!", Toast.LENGTH_SHORT).show();
        } else {
            List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
            deliveryStatuses.add(new DeliveryStatus("A", ""));
            SalesInvoiceQuery query = new SalesInvoiceQuery(number, "", "", deliveryStatuses);
            startActivityForResult(SalesInvoiceDetailActivity.createIntent(app, query, null), REQUESTCODE);
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
                    case 1 : etRequestDeliveryDateFrom.setText(date);
                        break;
                    case 2 : etRequestDeliveryDateTo.setText(date);
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
}