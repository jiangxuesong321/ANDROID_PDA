package com.sunmi.pda.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.adapters.TransferOrderReceiveAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.MaterialVoucherTask;
import com.sunmi.pda.asynctasks.PrototypeBorrowPostingTask;
import com.sunmi.pda.controllers.MaterialVoucherController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.TransferOrderController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.MaterialVoucher;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.TransferOrder;
import com.sunmi.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransferOrderReceiveDetailActivity extends AppCompatActivity implements ActivityInitialization {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final MaterialVoucherController controller = app.getMaterialVoucherController();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();

    private final static int REQUESTCODE_RECEIVE = 20002;
    private static final String INTENT_KEY_TRANSFERORDER_RECEIVE = "TransferOrderRecieve";
    public static final String INTENT_KEY_SCAN_RESULT = "ScanResult";
    private static final String INTENT_KEY_DATA = "Data";
    private ListView lvMaterialVoucher;
    private TextView orderValue;
    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;
    private EditText etConfirmDate;

    private User user;
    private List<StorageLocation> storageLocations;

    private WaitDialog waitDialog;
    private List<MaterialVoucher> list;
    private TransferOrderReceiveAdapter adapter;
    private SalesInvoiceQuery query;

    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private TextView tvMaxCountValue, tvTotalCountValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_order_receive_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initIntent();
        initData();
        if (list == null) {
            getData();
        }
        bindView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static Intent createIntent(Context context, SalesInvoiceQuery query) {
        Intent intent = new Intent(context, TransferOrderReceiveDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_TRANSFERORDER_RECEIVE, query);
        //for offline
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        this.lvMaterialVoucher = findViewById(R.id.lv_material_voucher);
        waitDialog = new WaitDialog();
        orderValue = findViewById(R.id.tv_order_value);
        spinnerLocation = findViewById(R.id.sp_location);
        etConfirmDate = findViewById(R.id.et_confirm_date);
        hideSoftInput(etConfirmDate);
        tvMaxCountValue = findViewById(R.id.tv_max_count_value);
        tvTotalCountValue = findViewById(R.id.tv_total_count_value);
    }

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initData() {
        user = userController.getLoginUser();
        storageLocations = storageLocationController.getStorageLocation();

        if(offline != null) {
            //for offline
            list = (List<MaterialVoucher>) JSON.parseArray(offline.getOrderBody(), MaterialVoucher.class);
            orderValue.setText(list.get(0).getRsnum());
        }
        if (list != null && list.size() > 0) {
            orderValue.setText(list.get(0).getRsnum());
            showData();
        }

        String date = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(date);
        setCount();
    }

    private void bindView(){
        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);
        if(offline != null) {
            //for offline
            int pos = storageLocationController.getStorageLocationPosition(offline.getReceiveLocation(), storageLocations);
            spinnerLocation.setSelection(pos);
            locationSpinnerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUESTCODE) {
//                //Refresh Data
//                ScanResult result = (ScanResult) data.getSerializableExtra(INTENT_KEY_SCAN_RESULT);
//                for (TransferOrder transferOrder : list) {
//                    if (StringUtils.equalsIgnoreCase(transferOrder.getMaterialNo(),result.getMaterialNumber()) && StringUtils.equalsIgnoreCase(transferOrder.getBatchNo(),result.getBatch())){
//                        transferOrder.setScanQuantity(result.getSnList().size());
//                        transferOrder.setSnList(result.getSnList());
//                        transferOrder.setStorageLocation(result.getStorageLocation());
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//        }
    }

    private void showData() {
        adapter = new TransferOrderReceiveAdapter(getApplicationContext(), list);
        this.lvMaterialVoucher.setDividerHeight(1);
        this.lvMaterialVoucher.setAdapter(adapter);
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

    @Override
    public void initIntent() {
        query = (SalesInvoiceQuery) this.getIntent().getSerializableExtra(INTENT_KEY_TRANSFERORDER_RECEIVE);
        //for offline
        if(query == null){
            offline = TransferOrderHomeActivity.offline;
        }
    }

    private void getData(){
        waitDialog.showWaitDialog(TransferOrderReceiveDetailActivity.this);
        MaterialVoucherTask task = new MaterialVoucherTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(TransferOrderReceiveDetailActivity.this);
                offlineController.clearOfflineData(AppConstants.FUNCTION_ID_TRANSFER_IN);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(TransferOrderReceiveDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_BACK, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<MaterialVoucher> materialVouchers= (List<MaterialVoucher>) o;
                if(materialVouchers!= null && materialVouchers.size() > 0 ){
                    list = materialVouchers;
                    int pos = storageLocationController.getStorageLocationPosition(list.get(0).getUmlgo(), storageLocations);
                    spinnerLocation.setSelection(pos);
                    locationSpinnerAdapter.notifyDataSetChanged();
                    initData();
                } else {
                    displayDialog(app.getString(R.string.error_order_not_found), AppConstants.REQUEST_BACK , 1);
                }
            }
        }, query);
        task.execute();
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    //暂存当前数据
                    StorageLocation location = (StorageLocation) spinnerLocation.getSelectedItem();
                    offlineController.saveOfflineData(AppConstants.FUNCTION_ID_TRANSFER_IN, JSON.toJSONString(list), list.get(0).getRsnum(), null, "", "", null, location);
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
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    finish();
                }
            }
        });
        noticeDialog.create();
    }


    public void post(View view){
        StorageLocation location = (StorageLocation) spinnerLocation.getSelectedItem();
        if(location == null){
            displayDialog(getString(R.string.error_require_location), AppConstants.REQUEST_FAILED, 1);
            return;
        }

        GeneralPostingRequest request = controller.buildRequest(list, location, etConfirmDate.getText().toString());
        LogUtils.d("GeneralPostingRequest", "request---->" + JSON.toJSONString(request));
        postData(request);

    }

    private void postData(GeneralPostingRequest request){
        waitDialog.showWaitDialog(this);
        PrototypeBorrowPostingTask task = new PrototypeBorrowPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(TransferOrderReceiveDetailActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(TransferOrderReceiveDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                displayDialog(getString(R.string.text_material_document) + ": "
                        + (String) o, AppConstants.REQUEST_SUCCEED, 1);

            }
        }, request);
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //for offline
                displayDialog(getString(R.string.text_offline_not_finished),
                        AppConstants.REQUEST_OFFLINE_DATA, 2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //for offline
            displayDialog(getString(R.string.text_offline_not_finished),
                    AppConstants.REQUEST_OFFLINE_DATA, 2);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void setCount(){
        if(list != null){
            double maxCount = list.stream().mapToDouble(MaterialVoucher::getMengeDouble).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = list.stream().mapToDouble(MaterialVoucher::getBstmgDouble).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }
    }
}