/**
 * 销售发货
 */
package com.sunmi.pda.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.DialogAddress;
import com.sunmi.pda.activities.view.DialogInput;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;


import com.sunmi.pda.adapters.LogisticSpinnerAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.adapters.SalesInvoiceDetailAdapter;
import com.sunmi.pda.asynctasks.SalesInvoicePostingTask;
import com.sunmi.pda.asynctasks.SalesInvoiceTask;

import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.SalesInvoiceController;

import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;


import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;

import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.listeners.OnTaskEventListener;

import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.SalesInvoicePostingRequest;
import com.sunmi.pda.models.SalesInvoiceSN;
import com.sunmi.pda.models.ScanResult;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SalesInvoiceDetailActivity extends AppCompatActivity implements ActivityInitialization, SalesInvoiceDetailAdapter.OnItemClickListener, DialogInput.InputCallback {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final SalesInvoiceController controller = app.getSalesInvoiceController();
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();
    private static final ScanController scanController = app.getScanController();
    private final static int REQUESTCODE = 20000;
    private static final String INTENT_KEY_SALESINVOICE = "SalesInvoice";
    private static final String INTENT_KEY_SALESINVOICE_LIST = "SalesInvoiceList";
    public static final String INTENT_KEY_SCAN_RESULT = "ScanResult";
    private static final String INTENT_KEY_DATA = "Data";
    private ListView lvSalesInvoice;
    private TextView tvOrderValue;
    private TextView tvDeliveryDateValue;
    private TextView tvMaxCountValue, tvTotalCountValue;
    private EditText etLogisticNumber;
    private EditText etConfirmDate;
    private Spinner spinner;
    private LogisticSpinnerAdapter spinnerAdapter;

    private User user;
    private List<LogisticsProvider> logisticsProviders;
    private List<StorageLocation> storageLocations;
    private String number;

    private WaitDialog waitDialog;
    private List<SalesInvoice> list;
    private List<SalesInvoiceSN> listSNs;
    private SalesInvoiceDetailAdapter adapter;
    private List<SalesInvoicePostingRequest> request;
    private SalesInvoiceQuery query;
    private int currentPosition;
    private static final OfflineController offlineController = app.getOfflineController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_invoice_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initListener();
        initIntent();
        if(query != null){
            initData();
            getData();

        }else{
            initData();
        }
        bindView();
    }
    public static Intent createIntent(Context context, SalesInvoiceQuery query, List<SalesInvoice> salesInvoices) {
        Intent intent = new Intent(context, SalesInvoiceDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SALESINVOICE, query);
        bundle.putSerializable(INTENT_KEY_SALESINVOICE_LIST, (Serializable) salesInvoices);

        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        lvSalesInvoice = findViewById(R.id.lv_sales_invoice);
        waitDialog = new WaitDialog();
        tvOrderValue = findViewById(R.id.tv_order_value);
        tvDeliveryDateValue = findViewById(R.id.tv_delivery_date_value);
        spinner = findViewById(R.id.sp_supplier);
        etLogisticNumber = findViewById(R.id.et_logistics_value);
        etConfirmDate = findViewById(R.id.et_confirm_date);
        tvMaxCountValue = findViewById(R.id.tv_max_count_value);
        tvTotalCountValue = findViewById(R.id.tv_total_count_value);
    }

    @Override
    public void initData() {
        user = userController.getLoginUser();
        //spinner supplier
        logisticsProviders = logisticsProviderController.getLogisticsProvider();
        storageLocations = storageLocationController.getStorageLocation();

        if(query != null){
            number = query.getDeliveryDocument();
        }

        if(list != null && list.size() > 0){
            setSalesInvoiceCountForMaterialA(list);
            storageLocations = storageLocationController.filterStorageLocationByPlant(storageLocations, list.get(0).getReceivingPlant());
            tvOrderValue.setText(list.get(0).getDeliveryDocument());
            tvDeliveryDateValue.setText(DateUtils.dateToString(new Date(list.get(0).getPlannedDeliveryDate()), DateUtils.FormatY_M_D));
            showData();
        }

        String date = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(date);
        setCount();
    }

    private void bindView(){
        spinnerAdapter = new LogisticSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, logisticsProviders);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                //Refresh Data
                ScanResult result = (ScanResult) data.getSerializableExtra(INTENT_KEY_SCAN_RESULT);

                if(list.size() > currentPosition){
                    SalesInvoice salesInvoice = list.get(currentPosition);
                    Offline offline = offlineController.getOfflineData(AppConstants.LAST_SCAN);
                    List<String> snList = new ArrayList<>();
                    if(offline != null){
                        snList = (List<String>) JSON.parseArray(offline.getOrderBody(), String.class);
                    }
                    salesInvoice.setScanCount(snList.size());
                    List<String> codes = new ArrayList<>();
                    codes.addAll(snList);
                    salesInvoice.setScanBarcodes(codes);
                    salesInvoice.setStorageLocation(result.getStorageLocation());
                }
                setCount();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showData() {
        adapter = new SalesInvoiceDetailAdapter(getApplicationContext(), list);
        adapter.setClickListener(this);
        this.lvSalesInvoice.setDividerHeight(1);
        this.lvSalesInvoice.setAdapter(adapter);

    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {
        etConfirmDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus){
                    showDatePickerDialog();
                }
            }
        });
        etConfirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePickerDialog();
            }
        });
    }

    @Override
    public void initIntent() {
        query = (SalesInvoiceQuery) this.getIntent().getSerializableExtra(INTENT_KEY_SALESINVOICE);
        list = (List<SalesInvoice>) this.getIntent().getSerializableExtra(INTENT_KEY_SALESINVOICE_LIST);
    }

    private void getData(){
        waitDialog.showWaitDialog(SalesInvoiceDetailActivity.this);
        SalesInvoiceTask task = new SalesInvoiceTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(SalesInvoiceDetailActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(SalesInvoiceDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_BACK, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SalesInvoice> salesInvoices = (List<SalesInvoice>) o;
                String err = controller.verifySalesInvoiceResult(salesInvoices);
                if(err.isEmpty()){
                    setSalesInvoiceCountForMaterialA(salesInvoices);
                    list = salesInvoices;
                    initData();
                    showData();
                } else {
                    displayDialog(err, AppConstants.REQUEST_BACK, 1);
                }
            }
        }, query, AppConstants.FUNCTION_ID_SALES_INVOICE);
        task.execute();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);
            if(codes.size() == 1 && isAlive) {
//                etLogisticNumber.setText(code);
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineController.clearOfflineData(AppConstants.LAST_SCAN);
        unregisterReceiver(receiver);
    }

    public void showDatePickerDialog(){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "-" + DateUtils.getMonthOrDate(monthOfYear + 1)+ "-" + DateUtils.getMonthOrDate(dayOfMonth);
                etConfirmDate.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onClickAddress(View view){
        showAddress();
    }

    private void showAddress() {
        String consignee = "", contactWay = "", address = "";
        if(list != null && list.size() > 0){
            consignee = list.get(0).getShipToParty();
            contactWay = list.get(0).getShipToPartyName();
            address = list.get(0).getShipToPartyAddress();
            DialogAddress dialogSuccess = new DialogAddress(this, consignee, contactWay, address);
            dialogSuccess.show();
        }
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        if(AppConstants.REQUEST_CONFIRM_BACK == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_yes));
            noticeDialog.setNegativeButtonText(getString(R.string.text_no));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if(AppConstants.REQUEST_CONFIRM_BACK == action){
                    finish();
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_SUCCEED == action){
                    setResult(RESULT_OK);
                    finish();
                }
                if(AppConstants.REQUEST_BACK == action){
                    finish();
                }
                if(AppConstants.REQUEST_CONFIRM_BACK == action){
                }
            }
        });
        noticeDialog.create();
    }

    /**
     * 过账
     * @param view
     */
    public void post(View view){
        LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();
        String logisticNr = etLogisticNumber.getText().toString();
        int errorId = controller.verifyData(list, etLogisticNumber.getText().toString(), logisticsProvider, user.getGroup(), false);
        //errorId = -1;
        if(errorId < 0){
            List<SalesInvoicePostingRequest> data = new ArrayList<>();
            for (SalesInvoice salesInvoice : list) {
                if (StringUtils.equalsIgnoreCase(salesInvoice.getSerialFlag(),"X") && salesInvoice.getScanBarcodes() != null ) {
                    for (String barcode : salesInvoice.getScanBarcodes()) {
                        SalesInvoicePostingRequest request = new SalesInvoicePostingRequest(salesInvoice.getDeliveryDocument(), salesInvoice.getDeliveryDocumentItem(),
                                salesInvoice.getMaterial(), salesInvoice.getStorageLocation(), salesInvoice.getBatch(), salesInvoice.getShipmentQuantity().toString(), barcode, etConfirmDate.getText().toString());
                        request.setLogisticsNumber(logisticNr);
                        request.setLogisticsProvider(logisticsProvider.getZtName());
                        request.setDocumentType("1");
                        data.add(request);
                    }
                } else {
                    SalesInvoicePostingRequest request = new SalesInvoicePostingRequest(salesInvoice.getDeliveryDocument(), salesInvoice.getDeliveryDocumentItem(),
                            salesInvoice.getMaterial(), salesInvoice.getInventoryLocation(), salesInvoice.getBatch(), salesInvoice.getShipmentQuantity().toString(), "", etConfirmDate.getText().toString());
                    request.setLogisticsNumber(logisticNr);
                    request.setLogisticsProvider(logisticsProvider.getZtName());
                    request.setDocumentType("1");
                    data.add(request);
                }
            }
            postData(data, false);
        }else{
            switch (errorId){
                case SalesInvoiceController.ERROR_REQUIRE_FIELDS:
                    displayDialog( app.getString(R.string.error_require_fields), -1, 1);
                    break;
                case SalesInvoiceController.ERROR_QUANTITY_NOT_MATCH:
                    displayDialog( app.getString(R.string.error_barcode_not_complete), -1, 1);
                    break;
            }
        }

    }

    /**
     * 暂存
     */
    public void postTemp(View view){
        if(verifyPostData(true) == true){
            List<SalesInvoicePostingRequest> data = getPostData(true);
            postData(data, true);
        }
    }

    private void postData(List<SalesInvoicePostingRequest> request, boolean isTemp){
        waitDialog.showWaitDialog(this);
        SalesInvoicePostingTask task = new SalesInvoicePostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(SalesInvoiceDetailActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(SalesInvoiceDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }

            @Override
            public void bindModel(Object o) {
                String msg = isTemp? getString(R.string.text_sales_order_temp_save_success) : (getString(R.string.text_sales_order) + ": " + (String) o );
                displayDialog(msg, AppConstants.REQUEST_SUCCEED, 1);
            }
        }, request, isTemp);
        task.execute();
    }

    /**
     * 验证Post数据
     * 暂存：不验证扫码数量
     * @return
     */
    private boolean verifyPostData(boolean isTemp) {
        LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();
        int errorId = controller.verifyData(list, etLogisticNumber.getText().toString(), logisticsProvider, user.getGroup(), true);
        if(errorId < 0){
            return true;
        } else {
            switch (errorId) {
                case SalesInvoiceController.ERROR_REQUIRE_FIELDS:
                    displayDialog(app.getString(R.string.error_require_fields), -1, 1);
                    break;
                case SalesInvoiceController.ERROR_QUANTITY_NOT_MATCH:
                    displayDialog(app.getString(R.string.error_barcode_not_complete), -1, 1);
                    break;
            }
            return false;
        }
    }

    /**
     * 组织post数据
     * @param isTemp
     * @return
     */
    private List<SalesInvoicePostingRequest> getPostData(boolean isTemp) {
        LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();
        String logisticNr = etLogisticNumber.getText().toString();
        List<SalesInvoicePostingRequest> data = new ArrayList<>();
        for (SalesInvoice salesInvoice : list) {
            if (StringUtils.equalsIgnoreCase(salesInvoice.getSerialFlag(),"X") && salesInvoice.getScanBarcodes() != null ) {
                for (String barcode : salesInvoice.getScanBarcodes()) {
                    String postingDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
                    SalesInvoicePostingRequest request = new SalesInvoicePostingRequest(salesInvoice.getDeliveryDocument(), salesInvoice.getDeliveryDocumentItem(),
                            salesInvoice.getMaterial(), salesInvoice.getStorageLocation(), salesInvoice.getBatch(), salesInvoice.getShipmentQuantity().toString(), barcode, postingDate);
                    request.setLogisticsNumber(logisticNr);
                    request.setLogisticsProvider(logisticsProvider.getZtName());
                    request.setDocumentType("1");
                    data.add(request);
                }
            } else {
                String postingDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
                SalesInvoicePostingRequest request = new SalesInvoicePostingRequest(salesInvoice.getDeliveryDocument(), salesInvoice.getDeliveryDocumentItem(),
                        salesInvoice.getMaterial(), salesInvoice.getInventoryLocation(), salesInvoice.getBatch(), salesInvoice.getShipmentQuantity().toString(), "", postingDate);

                request.setLogisticsNumber(logisticNr);
                request.setLogisticsProvider(logisticsProvider.getZtName());
                request.setDocumentType("1");
                data.add(request);
            }
        }
        return data;
    }

    @Override
    public void setInput(String item, String batch, int count, StorageLocation storageLocation) {
        for (SalesInvoice salesInvoice : list) {
            if (StringUtils.equalsIgnoreCase(item, salesInvoice.getDeliveryDocumentItem())){
                salesInvoice.setBatch(batch);
                salesInvoice.setScanCount(count);
                if (storageLocation != null) {
                    salesInvoice.setStorageLocation(storageLocation.getStorageLocation());
                }
            }
        }
        setCount();
        adapter.notifyDataSetChanged();
    }

    private void setCount(){

        if(list != null){
            double maxCount = list.stream().mapToDouble(SalesInvoice::getShipmentQuantity).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = list.stream().mapToDouble(SalesInvoice::getScanCount).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //后退 确认
                displayDialog(getString(R.string.text_confirm_back),
                        AppConstants.REQUEST_CONFIRM_BACK, 2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean isAlive;

    @Override
    protected void onPause() {
        isAlive = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        isAlive = true;
        super.onResume();
    }

    //Listview item click
    @Override
    public void onItemClicked(int position) {
        currentPosition = position;
        if(list != null && list.size() > 0){
            SalesInvoice salesInvoice = list.get(position);
            String materialFirst = "";
            if (salesInvoice.getMaterial() != null && salesInvoice.getMaterial().length() > 0){
                materialFirst = salesInvoice.getMaterial().substring(0,1);
            }
            //软件类发货
            //销售发货单里物料以A开头的行项目，不做检查，无须扫码，默认要发的数量等于需求数量。
            if (StringUtils.equalsIgnoreCase(materialFirst, "A")){
                return;
            }
            if (StringUtils.equalsIgnoreCase(salesInvoice.getSerialFlag(),"X") ) {
                String loc = list.get(position).getStorageLocation();
                if(StringUtils.isEmpty(loc)){
                    loc = list.get(position).getInventoryLocation();
                }
                offlineController.clearOfflineData(AppConstants.LAST_SCAN);
                offlineController.saveOfflineData(AppConstants.LAST_SCAN, JSON.toJSONString(list.get(position).getScanBarcodes()),
                        "", null, "", null, null, null);
                startActivityForResult(DeliveryScanActivity.createIntent(app,
                        list.get(position).getMaterial(), list.get(position).getBatch(),
                        list.get(position).getShipmentQuantity(), loc, list.get(position).getReceivingPlant(),
                        false), REQUESTCODE);
            } else {
                DialogInput dialogInput = new DialogInput(this, list.get(position).getDeliveryDocumentItem(),
                        list.get(position).getMaterial(), list.get(position).getBatch(),
                        list.get(position).getShipmentQuantity(), list.get(position).getInventoryLocation(),
                        list.get(position).getReceivingPlant(),
                        list.get(position).getScanCount(), list.get(position).isBatchFlag());
                dialogInput.setInputCallback(this);
                dialogInput.show();
            }
        }
    }

    /**
     * A开头的物料不需扫码
     * @param salesInvoices
     */
    private void setSalesInvoiceCountForMaterialA (List<SalesInvoice> salesInvoices){

        for (SalesInvoice salesInvoice: salesInvoices){
            if (salesInvoice.getMaterial() != null && salesInvoice.getMaterial().length() > 0) {
                String materialFirst = salesInvoice.getMaterial().substring(0, 1);

                if (StringUtils.equalsIgnoreCase(materialFirst, "A")) {
                    salesInvoice.setScanCount(salesInvoice.getShipmentQuantity().intValue());
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            displayDialog(getString(R.string.text_confirm_back),
                    AppConstants.REQUEST_CONFIRM_BACK, 2);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }
}