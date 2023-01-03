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
import com.android.pda.asynctasks.OrderInvoiceOthersTask;
import com.android.pda.asynctasks.PickingTask;
import com.android.pda.asynctasks.PrototypeBorrowTask;
import com.android.pda.asynctasks.SalesInvoiceTask;
import com.android.pda.asynctasks.TransferOrderTask;
import com.android.pda.controllers.OrderInvoiceOthersController;
import com.android.pda.controllers.PickingController;
import com.android.pda.controllers.PrototypeBorrowController;
import com.android.pda.controllers.SalesInvoiceController;
import com.android.pda.controllers.StorageLocationController;
import com.android.pda.controllers.TransferOrderController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;
import com.android.pda.models.BusinessOrderQuery;
import com.android.pda.models.DeliveryStatus;
import com.android.pda.models.DeliveryStatusList;
import com.android.pda.models.OrderInvoiceOthersQuery;
import com.android.pda.models.OrderInvoiceOthersResult;
import com.android.pda.models.OrderTypeList;
import com.android.pda.models.Picking;
import com.android.pda.models.PrototypeBorrow;
import com.android.pda.models.SalesInvoice;
import com.android.pda.models.SalesInvoiceQuery;
import com.android.pda.models.TransferOrder;
import com.android.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class OrderQueryHomeActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final SalesInvoiceController salesInvoiceController = app.getSalesInvoiceController();
    private static final TransferOrderController transferOrderController = app.getTransferOrderController();
    private static final PrototypeBorrowController prototypeBorrowController = app.getPrototypeBorrowController();
    private static final PickingController pickingController = app.getPickingController();
    private WaitDialog waitDialog;

    private EditText etDeliveryDateFrom;
    private EditText etDeliveryDateTo;

    private Spinner spinnerOrderType;
    private DeliveryStatusSpinnerAdapter spinnerAdapterOrderType;
    private MultiSelectionSpinner spinnerDeliveryStatus;
    private DeliveryStatusSpinnerAdapter spinnerAdapterDeliveryStatus;
    private MultiSelectionSpinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private static final UserController userController = app.getUserController();
    private List<StorageLocation> storageLocations;

    private final static int REQUESTCODE = 10001;
    private List<String> deliveryStatus = new ArrayList<>();
    private List<String> locationSelected = new ArrayList<>();
    private static final SalesInvoiceController controller = app.getSalesInvoiceController();
    private static final OrderInvoiceOthersController OrderInvoiceOthersController = app.getOrderInvoiceOthersController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_query_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, OrderQueryHomeActivity.class);
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
        spinnerDeliveryStatus = findViewById(R.id.sp_delivery_status);
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
        //storageLocations.add(0, new StorageLocation("","", "", ""));
        LogUtils.d("storageLocations","storageLocations---->" + JSON.toJSONString(storageLocations));
        /*locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);*/
        List<String> strListLocations = storageLocationController.getStorageLocationStr(storageLocations);
        ArrayAdapter<String> adapterLocations = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, strListLocations);
        adapterLocations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setItems(strListLocations);
        //int[] selectLocations = new int[strListLocations.size()];
        //spinnerDeliveryStatus.setSelection(selectLocations);
        spinnerLocation.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {

            }
            @Override
            public void selectedStrings(List<String> strings) {
                locationSelected = strings;
            }
        });

        DeliveryStatusList deliveryStatusList = new DeliveryStatusList();
        /*spinnerAdapterDeliveryStatus = new DeliveryStatusSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, deliveryStatusList.getDeliveryStatusList());
        spinnerAdapterDeliveryStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeliveryStatus.setAdapter(spinnerAdapterDeliveryStatus);*/

        String[] strArray = new String[]{"A-尚未发货",
                "B-部分发货", "C-完全发货"};
        List<String> strList = Arrays.asList(strArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeliveryStatus.setItems(strList);
        int[] select = new int[strArray.length];
        /*for(int i = 0; i < strArray.length; i ++){
            select[i] = i;
            deliveryStatus.add(strArray[i]);
        }*/
        select[0] = 0;
        deliveryStatus.add(strArray[0]);
        spinnerDeliveryStatus.setSelection(select);
        spinnerDeliveryStatus.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {

            }
            @Override
            public void selectedStrings(List<String> strings) {
                deliveryStatus = strings;
            }
        });


        OrderTypeList orderTypeList = new OrderTypeList(getApplicationContext(), 1);
        spinnerAdapterOrderType = new DeliveryStatusSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, orderTypeList.getOrderTypeList());
        //spinnerAdapterDeliveryStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderType.setAdapter(spinnerAdapterOrderType);
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

    public void search(View view){
        DeliveryStatus orderType = (DeliveryStatus) spinnerOrderType.getSelectedItem();
        //DeliveryStatus deliveryStatus = (DeliveryStatus) spinnerDeliveryStatus.getSelectedItem();
        List<DeliveryStatus> deliveryStatuses = new ArrayList<>();
        for(String status: this.deliveryStatus){
            String code = StringUtils.substring(status, 0, 1);
            String name = StringUtils.substring(status, StringUtils.indexOf(status, "-") + 1);
            DeliveryStatus deliveryStatus = new DeliveryStatus(code, name);
            deliveryStatuses.add(deliveryStatus);
        }

        List<String> storageLocations = new ArrayList<>();
        //StorageLocation storageLocation = (StorageLocation) spinnerLocation.getSelectedItem();

        if(this.locationSelected != null && this.locationSelected.size() > 100){
            displayDialog(getString(R.string.text_location_size_more_100));
            return;
        }
        for(String location: this.locationSelected){
            String code = StringUtils.substring(location, 0, StringUtils.indexOf(location, "-") - 1);
            storageLocations.add(code);
        }

        if(storageLocations == null || storageLocations.size() == 0){
            for(StorageLocation storageLocation : this.storageLocations){
                if(StringUtils.equalsIgnoreCase(storageLocation.getStorageLocation(), getString(R.string.storage_location_all))){
                    storageLocations.clear();
                    break;
                }else{
                    storageLocations.add(storageLocation.getStorageLocation());
                }
            }
        }


        String deliveryDateFrom = etDeliveryDateFrom.getText().toString();
        String deliveryDateFromTo = etDeliveryDateTo.getText().toString();


        switch (orderType.getCode()){
            case AppConstants.FUNCTION_ID_SALES_INVOICE : //销售发货
                downloadSalesInvoice(deliveryStatuses, storageLocations, deliveryDateFrom, deliveryDateFromTo);
                break;
            case AppConstants.FUNCTION_ID_LEND :   //•	03 借出发货 --- 样机借用发货
                //downloadPrototypeBorrow(deliveryStatuses, storageLocations, deliveryDateFrom, deliveryDateFromTo);
                break;
            case AppConstants.FUNCTION_ID_PICKING_MATERIAL : //04 材料领用 - picking_material
                //downloadPicking(deliveryStatuses, storageLocations, deliveryDateFrom, deliveryDateFromTo, AppConstants.FUNCTION_ID_PICKING_MATERIAL);
                break;
            case AppConstants.FUNCTION_ID_PICKING :  //05 样机领用
                //downloadPicking(deliveryStatuses, storageLocations, deliveryDateFrom, deliveryDateFromTo, AppConstants.FUNCTION_ID_PICKING);
                break;
            case AppConstants.FUNCTION_ID_TRANSFER_OUT :
                //downloadTransferOrder(deliveryStatuses, storageLocations, deliveryDateFrom, deliveryDateFromTo);
            case AppConstants.FUNCTION_IDS_OTHERS :
                downloadOthersInvoice(deliveryStatuses, storageLocations, deliveryDateFrom, deliveryDateFromTo);
                break;
        }
    }

    /******** SALES INVOICE *******/
    private void downloadOthersInvoice (List<DeliveryStatus> deliveryStatuses, List<String> storageLocations, String deliveryDateFrom, String deliveryDateFromTo ) {
        OrderInvoiceOthersQuery query = new OrderInvoiceOthersQuery("", deliveryDateFrom, deliveryDateFromTo, deliveryStatuses,null);
        query.setStorageLocations(storageLocations);
        String error = OrderInvoiceOthersController.verifyQuery(query);
        if(!StringUtils.isEmpty(error)){
            displayDialog(error);
        }else{
            waitDialog.showWaitDialog(OrderQueryHomeActivity.this);
            OrderInvoiceOthersTask task = new OrderInvoiceOthersTask(getApplicationContext(), new OnTaskEventListener<String>() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailure(String error) {
                    waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                    displayDialog(error, AppConstants.REQUEST_FAILED);
                }
                @Override
                public void bindModel(Object o) {
                    List<OrderInvoiceOthersResult> orderInvoiceOthersResults = (List<OrderInvoiceOthersResult>) o;
                    if(orderInvoiceOthersResults != null && orderInvoiceOthersResults.size() > 0){
                        exportInvoiceOthers(orderInvoiceOthersResults);
                    }else{
                        displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                    }
                    waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                }
            }, query);
            task.execute();
        }
    }

    private void exportInvoiceOthers(List<OrderInvoiceOthersResult> orderInvoiceOthersResults){
        try {
            OrderInvoiceOthersController.exportExcel(orderInvoiceOthersResults);
            displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /******** TRANSFER ORDER *******/
    private void downloadTransferOrder(List<DeliveryStatus> deliveryStatuses, List<String> storageLocations, String deliveryDateFrom, String deliveryDateFromTo ) {
        SalesInvoiceQuery query = new SalesInvoiceQuery("", deliveryDateFrom, deliveryDateFromTo, deliveryStatuses);
        query.setStorageLocations(storageLocations);
        String error = controller.verifyQuery(query);
        if(!StringUtils.isEmpty(error)){
            displayDialog(error);
        }else{
            waitDialog.showWaitDialog(OrderQueryHomeActivity.this);
            TransferOrderTask task = new TransferOrderTask(getApplicationContext(), new OnTaskEventListener<String>() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailure(String error) {
                    waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                    displayDialog(error, AppConstants.REQUEST_FAILED);
                }
                @Override
                public void bindModel(Object o) {
                    List<TransferOrder> transferOrders = (List<TransferOrder>) o;
                    if(transferOrders != null && transferOrders.size() > 0){
                        exportTransferOrder(transferOrders);
                    }else{
                        displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                    }
                    waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                }
            }, query);
            task.execute();
        }
    }

    private void exportTransferOrder(List<TransferOrder> transferOrders){
        try {
            transferOrderController.exportExcel(transferOrders);
            displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /******** SALES INVOICE *******/
    private void downloadSalesInvoice (List<DeliveryStatus> deliveryStatuses, List<String> storageLocations, String deliveryDateFrom, String deliveryDateFromTo ) {
        SalesInvoiceQuery query = new SalesInvoiceQuery("", deliveryDateFrom, deliveryDateFromTo, deliveryStatuses);
        query.setStorageLocations(storageLocations);
        String error = controller.verifyQuery(query);
        if(!StringUtils.isEmpty(error)){
            displayDialog(error);
        }else{
            waitDialog.showWaitDialog(OrderQueryHomeActivity.this);
            SalesInvoiceTask task = new SalesInvoiceTask(getApplicationContext(), new OnTaskEventListener<String>() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailure(String error) {
                    waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                    displayDialog(error, AppConstants.REQUEST_FAILED);
                }
                @Override
                public void bindModel(Object o) {
                    List<SalesInvoice> salesInvoices = (List<SalesInvoice>) o;
                    if(salesInvoices != null && salesInvoices.size() > 0){
                        exportSalesInvoice(salesInvoices);
                    }else{
                        displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                    }
                    waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                }
            }, query, AppConstants.FUNCTION_ID_SHIP_ORDER);
            task.execute();
        }
    }

    private void exportSalesInvoice(List<SalesInvoice> salesInvoices){
        try {
            salesInvoiceController.exportExcel(salesInvoices);
            displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /******** MATERIAL LEND *******/
    private void downloadPrototypeBorrow(List<DeliveryStatus> deliveryStatuses, List<String> storageLocations, String deliveryDateFrom, String deliveryDateFromTo ) {
        BusinessOrderQuery query = new BusinessOrderQuery(deliveryDateFrom, deliveryDateFromTo, storageLocations, deliveryStatuses);

        waitDialog.showWaitDialog(OrderQueryHomeActivity.this);
        PrototypeBorrowTask task = new PrototypeBorrowTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }
            @Override
            public void bindModel(Object o) {
                List<PrototypeBorrow> prototypeBorrows = (List<PrototypeBorrow>) o;
                if(prototypeBorrows != null && prototypeBorrows.size() > 0){
                    exportPrototypeBorrows(prototypeBorrows);
                }else{
                    displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                }
                waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
            }
        }, query);
        task.execute();

    }

    private void exportPrototypeBorrows(List<PrototypeBorrow> prototypeBorrows){
        try {
            prototypeBorrowController.exportExcel(prototypeBorrows);
            displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    /******** PICKING MATERIAL *******/
    private void downloadPicking(List<DeliveryStatus> deliveryStatuses, List<String> storageLocations, String deliveryDateFrom, String deliveryDateFromTo , String functionId) {
        BusinessOrderQuery query = new BusinessOrderQuery(deliveryDateFrom, deliveryDateFromTo, storageLocations, deliveryStatuses);

        waitDialog.showWaitDialog(OrderQueryHomeActivity.this);
        PickingTask task = new PickingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }
            @Override
            public void bindModel(Object o) {
                List<Picking> pickings = (List<Picking>) o;
                if(pickings != null && pickings.size() > 0){
                    exportPicking(pickings, functionId);
                }else{
                    displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_BACK);
                }
                waitDialog.hideWaitDialog(OrderQueryHomeActivity.this);
            }
        }, query, Integer.valueOf(functionId));
        task.execute();

    }

    private void exportPicking(List<Picking> pickings, String functionId){
        try {
            pickingController.exportExcel(pickings, functionId);
            displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
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