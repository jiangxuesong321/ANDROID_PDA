package com.sunmi.pda.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;

import com.sunmi.pda.adapters.LendBackDetailAdapter;
import com.sunmi.pda.adapters.LogisticSpinnerAdapter;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;

import com.sunmi.pda.asynctasks.LendBackPostingTask;
import com.sunmi.pda.controllers.LendBackController;
import com.sunmi.pda.controllers.LoginController;

import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.LogisticsProvider;

import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.GeneralMaterialDocumentItemResults;
import com.sunmi.pda.models.GeneralPostingRequest;

import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.Util;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LendBacDetailActivity extends AppCompatActivity implements ActivityInitialization {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private static final UserController userController = app.getUserController();
    private static final LendBackController controller = app.getLendBackController();
    private static final LoginController loginController = app.getLoginController();
    private static final SerialInfoController serialInfoController = app.getSerialInfoController();

    private final static int REQUESTCODE_RECEIVE = 20002;
    private static final String INTENT_KEY_ITEM = "Item";
    private static final String INTENT_KEY_DATA = "Data";

    private static final int ACTION_ADD = 1;
    private static final int ACTION_DELETE = 2;

    private ListView lvSerial;
    private EditText etLogisticsValue, etConfirmDate;

    private User user;
    private Login loginUser;


    private WaitDialog waitDialog;

    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;

    private List<GeneralMaterialDocumentItemResults> postItems;
    private LendBackDetailAdapter adapter;
    private Spinner spinnerLocationTo, spinner;
    private SpinnerAdapter locationFromSpinnerAdapter;
    private LogisticSpinnerAdapter spinnerAdapter;
    private List<LogisticsProvider> logisticsProviders;
    private List<StorageLocation> storageLocations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_back_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initService();
        initIntent();
        initData();
        bindView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    public static Intent createIntent(Context context, List<SerialInfo> serialInfos, Offline offline) {
        Intent intent = new Intent(context, LendBacDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_ITEM, (Serializable) serialInfos);
        bundle.putSerializable(INTENT_KEY_DATA, (Serializable) offline);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        this.lvSerial = findViewById(R.id.lv_list);
        waitDialog = new WaitDialog();
        spinnerLocationTo = findViewById(R.id.sp_location_to);
        etLogisticsValue = findViewById(R.id.et_logistics_value);
        spinner = findViewById(R.id.sp_supplier);
        etConfirmDate = findViewById(R.id.et_confirm_date);
        String confirmDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(confirmDate);
        hideSoftInput(etConfirmDate);
    }

    @Override
    public void initData() {
        user = userController.getLoginUser();
        loginUser = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        storageLocations = storageLocationController.filterStorageLocationByPlant(storageLocations, "1100");
        logisticsProviders = logisticsProviderController.getLogisticsProvider();
        offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_LEND_BACK);
    }

    private void bindView(){
        locationFromSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationFromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocationTo.setAdapter(locationFromSpinnerAdapter);

        spinnerAdapter = new LogisticSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, logisticsProviders);
        spinner.setAdapter(spinnerAdapter);
        adapter = new LendBackDetailAdapter(getApplicationContext(), postItems);
        this.lvSerial.setDividerHeight(1);
        this.lvSerial.setAdapter(adapter);

        if(offline != null){
            postItems = (List<GeneralMaterialDocumentItemResults>) JSON.parseArray(offline.getOrderBody(), GeneralMaterialDocumentItemResults.class);
            etLogisticsValue.setText(offline.getLogisticNumber());
            int index = logisticsProviderController.getLogisticsProviderIndex(offline.getLogisticsProvider(), logisticsProviders);
            spinner.setSelection(index);

            int position = storageLocationController.getStorageLocationPosition(offline.getReceiveLocation(), storageLocations);
            spinnerLocationTo.setSelection(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void initService() {
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

    private void hideSoftInput(EditText editText){
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

        offline = (Offline) this.getIntent().getSerializableExtra(INTENT_KEY_DATA);
        if(offline != null){
            postItems = (List<GeneralMaterialDocumentItemResults>) JSON.parseArray(offline.getOrderBody(), GeneralMaterialDocumentItemResults.class);
        }else{
            List<SerialInfo> serialInfos =  (List<SerialInfo>) this.getIntent().getSerializableExtra(INTENT_KEY_ITEM);
            postItems = serialInfoController.getItems(serialInfos);
            //System.out.println("postItems--->" + JSON.toJSONString(postItems));
        }

    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_yes));
            noticeDialog.setNegativeButtonText(getString(R.string.text_no));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if(action == ACTION_DELETE){

                }
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    //暂存当前数据
                    StorageLocation storageLocation = (StorageLocation)spinnerLocationTo.getSelectedItem();
                    LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();
                    offlineController.saveOfflineData(AppConstants.FUNCTION_ID_LEND_BACK, JSON.toJSONString(postItems),
                            "", logisticsProvider, etLogisticsValue.getText().toString(), null, null, storageLocation);
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
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    finish();
                }
            }
        });
        noticeDialog.create();
    }


    public void post(View view){
        if(StringUtils.isEmpty(etLogisticsValue.getText().toString())){
            if(user != null){
                if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_sunmi))){
                    displayDialog(getString(R.string.error_require_fields), AppConstants.REQUEST_STAY, 1);
                    return;
                }
            }
        }
        StorageLocation storageLocation = (StorageLocation)spinnerLocationTo.getSelectedItem();
        boolean isValid = controller.checkStorageLocation(storageLocation.getStorageLocation());
        if(!isValid){
            displayDialog(getString(R.string.error_in_out_same), AppConstants.REQUEST_STAY, 1);
            return;
        }
        LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();

        GeneralPostingRequest request = controller.buildRequest(postItems, storageLocation.getStorageLocation(),
                logisticsProvider, etLogisticsValue.getText().toString(), storageLocation.getPlant(), etConfirmDate.getText().toString());
        postData(request);
    }

    private void postData(GeneralPostingRequest request){
        waitDialog.showWaitDialog(this);
        LendBackPostingTask task = new LendBackPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(LendBacDetailActivity.this);
                offlineController.clearOfflineData(AppConstants.FUNCTION_ID_LEND_BACK);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(LendBacDetailActivity.this);
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

    //******* Scan *******//

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);
            if(codes != null && codes.size() > 0){
                etLogisticsValue.setText(codes.get(0));
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}