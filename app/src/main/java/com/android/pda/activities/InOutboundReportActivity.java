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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.MultiSelectionSpinner;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.DeliveryStatusSpinnerAdapter;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.InOutboundReportTask;
import com.android.pda.controllers.InOutboundReportController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;
import com.android.pda.models.DeliveryStatus;
import com.android.pda.models.InOutboundReportQuery;
import com.android.pda.models.OrderTypeList;
import com.android.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @description 出入库报表
 */

public class InOutboundReportActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();

    private static final UserController userController = app.getUserController();
    private static final InOutboundReportController controller = app.getInOutboundReportController();
    private WaitDialog waitDialog;

    private EditText etDeliveryDateFrom;
    private EditText etDeliveryDateTo;

    private MultiSelectionSpinner spinnerOrderType;
    private DeliveryStatusSpinnerAdapter spinnerAdapterOrderType;


    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;

    private List<StorageLocation> storageLocations;

    private final static int REQUESTCODE = 10001;

    private List<Integer> orderIndices = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, InOutboundReportActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        etDeliveryDateFrom = findViewById(R.id.et_delivery_date_from);
        hideSoftInput(etDeliveryDateFrom);
        etDeliveryDateTo = findViewById(R.id.et_delivery_date_to);
        hideSoftInput(etDeliveryDateTo);

        spinnerOrderType = findViewById(R.id.sp_order_type);

        spinnerLocation = findViewById(R.id.sp_storage_location);
        waitDialog = new WaitDialog();
    }

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initData() {
        storageLocations = userController.getUserLocation();
        storageLocations.add(0, new StorageLocation("","", "", ""));
        LogUtils.d("storageLocations","storageLocations---->" + JSON.toJSONString(storageLocations));
        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);



        OrderTypeList orderTypeList = new OrderTypeList(getApplicationContext(), 2);
        DeliveryStatus deliveryStatus = new DeliveryStatus("00", "");
        orderTypeList.getOrderTypeList().add(0, deliveryStatus);

        String[] strArray = new String[]{getString(R.string.title_sales_invoice),
                getString(R.string.text_prototype_borrow), getString(R.string.text_picking),
                getString(R.string.text_picking_material), getString(R.string.title_transfer_order),
                getString(R.string.title_transfer_order_detail_receive), getString(R.string.text_po_receiving),
                getString(R.string.text_returning_material)};
        List<String> strList = Arrays.asList(strArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderType.setItems(strList);
        int[] select = new int[strArray.length];
        for(int i = 0; i < strArray.length; i ++){
            select[i] = i;
            orderIndices.add(i);
        }
        spinnerOrderType.setSelection(select);
        spinnerOrderType.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {
                orderIndices = indices;
            }

            @Override
            public void selectedStrings(List<String> strings) {

            }
        });
    }

    @Override
    public void initService() {

    }

    /**
     * 监听触发日期选择 Dialog 弹出
     */
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


    private void displayDialog(String message){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, 1);
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

    /**
     * 出入库报表查询
     * @param view
     */
    public void search(View view){
        StorageLocation storageLocation = (StorageLocation) spinnerLocation.getSelectedItem();

        String deliveryDateFrom = etDeliveryDateFrom.getText().toString();
        String deliveryDateFromTo = etDeliveryDateTo.getText().toString();
        List<String> storageLocations = new ArrayList<>();
        if(StringUtils.isNotEmpty(storageLocation.getStorageLocation())){
            storageLocations.add(storageLocation.getStorageLocation());
        }else{
            List<StorageLocation> storageLocationList = userController.getUserLocation();
            for(StorageLocation location : storageLocationList){
                if(StringUtils.equalsIgnoreCase(location.getStorageLocation(), getString(R.string.storage_location_all))){
                    storageLocations.clear();
                    break;
                }else{
                    storageLocations.add(location.getStorageLocation());
                }
            }
        }
        downloadInOutReport(deliveryDateFrom, deliveryDateFromTo, storageLocations);
    }

    /**
     * 根据填充条件，查询出入库报表
     * @param deliveryDateFrom
     * @param deliveryDateFromTo
     * @param storageLocations
     */
    private void downloadInOutReport(String deliveryDateFrom, String deliveryDateFromTo, List<String> storageLocations) {

        if(StringUtils.isNotEmpty(deliveryDateFromTo)){
            if(StringUtils.isEmpty(deliveryDateFrom)){
                displayDialog(app.getString(R.string.text_input_delivery_from_date));
                return;
            }
        }
        if(orderIndices == null || orderIndices.size() == 0){
            displayDialog(app.getString(R.string.text_select_order_type));
            return;
        }
        InOutboundReportQuery query = new InOutboundReportQuery(orderIndices, deliveryDateFrom,
                deliveryDateFromTo, storageLocations);

        waitDialog.showWaitDialog(InOutboundReportActivity.this);
            InOutboundReportTask task = new InOutboundReportTask(getApplicationContext(), new OnTaskEventListener<String>() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailure(String error) {
                    waitDialog.hideWaitDialog(InOutboundReportActivity.this);
                    displayDialog(error, AppConstants.REQUEST_FAILED);
                }
                @Override
                public void bindModel(Object o) {
                    boolean success = (boolean)o;
                    if(success){
                        displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY);
                    }else{
                        displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                    }
                    waitDialog.hideWaitDialog(InOutboundReportActivity.this);
                }
            }, query);
            task.execute();
        }


    private void displayDialog(String message, int action){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, 1);
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_SUCCEED == action){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        noticeDialog.create();
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

    public void clearDeliveryFrom(View view){
        etDeliveryDateFrom.setText("");
    }
    public void clearDeliveryTo(View view){
        etDeliveryDateTo.setText("");
    }

}