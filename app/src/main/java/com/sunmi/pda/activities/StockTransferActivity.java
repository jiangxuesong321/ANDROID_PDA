/**
 * 库存转移
 */
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.content.DialogInterface;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.DialogStockTransferInput;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.adapters.SpinnerAdapterPlant;
import com.sunmi.pda.adapters.StockTransferAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.asynctasks.SerialInfoPostingTask;
import com.sunmi.pda.asynctasks.StockTransferPostingTask;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.controllers.StockTransferController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockTransferActivity extends AppCompatActivity implements ActivityInitialization, StockTransferAdapter.DeleteCallback, DialogStockTransferInput.InputCallback {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final StockTransferController controller = app.getStockTransferController();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();
    private static final ScanController scanController = app.getScanController();
    private static final LoginController loginController = app.getLoginController();
    private static final SerialInfoController serialInfoController = app.getSerialInfoController();

    private final static int REQUESTCODE_RECEIVE = 20002;
    private static final String INTENT_KEY_STOCK_TRANSFER = "SockTransfer";
    public static final String INTENT_KEY_SCAN_RESULT = "ScanResult";
    private static final int ACTION_ADD = 1;
    private static final int ACTION_DELETE = 2;
    private static final int ACTION_DELETE_ALL_SN = 3;
    private static final String INTENT_KEY_DATA = "Data";

    private ListView lvSerial;
    private Spinner spinnerPlant;
    private Spinner spinnerLocationFrom;
    private Spinner spinnerLocationTo;
    private int removePosition;
    private SpinnerAdapter spinnerAdapterLocationFrom;
    private SpinnerAdapterPlant spinnerAdapterPlant;
    private EditText etConfirmDate;

    private User user;
    private Login loginUser;
    private List<StorageLocation> storageLocations;
    private List<StorageLocation> plants;

    private WaitDialog waitDialog;
    private List<String> snList = new ArrayList<>();
    private List<SerialNumberResults> postSerials = new ArrayList<>();
    private List<SerialInfo> serialInfos = new ArrayList<>();
    private StockTransferAdapter stockTransferAdapter;
    private boolean dialogIsOpen;
    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private TextView tvMaxCountValue, tvTotalCountValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initIntent();
        initData();
        bindView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    public static Intent createIntent(Context context, Offline offline) {
        Intent intent = new Intent(context, StockTransferActivity.class);
        Bundle bundle = new Bundle();
        //for offline
        bundle.putSerializable(INTENT_KEY_DATA, (Serializable) offline);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        this.lvSerial = findViewById(R.id.lv_material_voucher);
        this.lvSerial.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        waitDialog = new WaitDialog();
        spinnerLocationFrom = findViewById(R.id.sp_location_from);
        spinnerLocationTo = findViewById(R.id.sp_location_to);
        spinnerPlant = findViewById(R.id.sp_plant);
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
        loginUser = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        plants = userController.getUserPlants();

        if(offline != null) {
            //for offline
            serialInfos = (List<SerialInfo>) JSON.parseArray(offline.getOrderBody(), SerialInfo.class);
            for (SerialInfo serialInfo: serialInfos) {
                snList.add(serialInfo.getSerialnumber());
            }
        }
        String confirmDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(confirmDate);
    }

    private void bindView(){
        spinnerAdapterLocationFrom = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        spinnerAdapterLocationFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocationFrom.setAdapter(spinnerAdapterLocationFrom);
        spinnerLocationTo.setAdapter(spinnerAdapterLocationFrom);

        spinnerAdapterPlant = new SpinnerAdapterPlant(getApplicationContext(),
                R.layout.li_spinner_adapter, plants);
        spinnerAdapterPlant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlant.setAdapter(spinnerAdapterPlant);
        spinnerPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                StorageLocation plant = plants.get(index);
                List<StorageLocation> storageLocationsTemp = storageLocationController.filterStorageLocationByPlant(userController.getUserLocation(), plant.getPlant());
                System.out.println("StorageLocation--->" + storageLocationsTemp);
                storageLocations.clear();
                storageLocations.addAll(storageLocationsTemp);
                spinnerAdapterLocationFrom.notifyDataSetChanged();

                if(offline != null) {
                    int indexLocFrom = storageLocationController.getStorageLocationPosition(offline.getSendLocation(), storageLocations);
                    spinnerLocationFrom.setSelection(indexLocFrom);
                    spinnerAdapterLocationFrom.notifyDataSetChanged();

                    int indexLocTo = storageLocationController.getStorageLocationPosition(offline.getReceiveLocation(), storageLocations);
                    spinnerLocationTo.setSelection(indexLocTo);
                    spinnerAdapterLocationFrom.notifyDataSetChanged();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stockTransferAdapter = new StockTransferAdapter(getApplicationContext(), serialInfos);
        this.lvSerial.setDividerHeight(1);
        this.lvSerial.setAdapter(stockTransferAdapter);
        stockTransferAdapter.setDeleteCallback(this);

        if(offline != null) {
            //for offline
            int index = storageLocationController.getPlantPosition(offline.getPlant(), plants);
            spinnerPlant.setSelection(index);
            spinnerAdapterPlant.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        //for offline
        offline = (Offline) this.getIntent().getSerializableExtra(INTENT_KEY_DATA);
    }

    private void clearData() {
        snList.clear();
        serialInfos.clear();
        postSerials.clear();
        updateList();
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        //for offline
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_yes));
            noticeDialog.setNegativeButtonText(getString(R.string.text_no));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    //暂存当前数据
                    StorageLocation plant = (StorageLocation) spinnerPlant.getSelectedItem();
                    StorageLocation locationFrom = (StorageLocation) spinnerLocationFrom.getSelectedItem();
                    StorageLocation locationTo = (StorageLocation) spinnerLocationTo.getSelectedItem();
                    offlineController.saveOfflineData(AppConstants.FUNCTION_ID_STOCK_MOVE, JSON.toJSONString(serialInfos), "", null, "", plant.getPlant(), locationFrom, locationTo);
                    finish();
                }
                if(action == ACTION_DELETE){
                    System.out.println("ACTION_DELETE--->" + removePosition);
                    if (serialInfos.get(removePosition).getSerialnumber().length() > 0) {
                        snList.remove(removePosition);
                    }
                    serialInfos.remove(removePosition);
                    stockTransferAdapter.notifyDataSetChanged();
                    setCount();
                }
                if(action == ACTION_DELETE_ALL_SN){
                    System.out.println("ACTION_DELETE_ALL_SN--->");
                    snList.clear();
                    serialInfos.clear();
                    stockTransferAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_SUCCEED == action){
                   clearData();
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

    //add manually
    //no serial number
    public void add(View view){
        DialogStockTransferInput dialogStockTransferInput = new DialogStockTransferInput(this);
        dialogStockTransferInput.setInputCallback(this);
        dialogStockTransferInput.show();
        dialogIsOpen = true;
        dialogStockTransferInput.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogIsOpen = false;
            }
        });
    }

    public void checkSerial(View view){
        postSerials.clear();
        for (String sn: snList) {
            if (sn.length() > 0) {
                SerialNumberResults serial = new SerialNumberResults(sn);
                postSerials.add(serial);
            }
        }
        if (postSerials.size() > 0) {
            postSerials(postSerials);
        } else  {
            displayDialog(getString(R.string.error_no_serial), AppConstants.REQUEST_STAY,1);
        }
    }

    private void postSerials(List<SerialNumberResults> serials){
        waitDialog.showWaitDialog(this);
        SerialInfoPostingTask task = new SerialInfoPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(StockTransferActivity.this);
                offlineController.clearOfflineData(AppConstants.FUNCTION_ID_STOCK_MOVE);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(StockTransferActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if(items != null && items.size() > 0){
                    for (SerialInfo serialInfo: items) {
                        for (SerialInfo localSerialInfo: serialInfos){
                            if (StringUtils.equalsIgnoreCase(localSerialInfo.getSerialnumber(), serialInfo.getSerialnumber())){
                                localSerialInfo.setBatch(serialInfo.getBatch());
                                localSerialInfo.setMaterial(serialInfo.getMaterial());
                                localSerialInfo.setMaterialDesc(serialInfo.getMaterialDesc());
                                localSerialInfo.setBatch(serialInfo.getBatch());
                                localSerialInfo.setPlant(serialInfo.getPlant());
                                localSerialInfo.setCount("1");
                            }
                        }
                    }
                    updateList();
                }else{
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_STAY, 1);
                }
            }
        }, serials);
        task.execute();
    }

    public void post(View view){
        if (serialInfos == null || serialInfos.size() == 0) {
            displayDialog(getString(R.string.error_no_serial), AppConstants.REQUEST_STAY,1);
            return;
        }

        //location from should not equal to location to
        StorageLocation locationFrom = (StorageLocation) spinnerLocationFrom.getSelectedItem();
        StorageLocation locationTo = (StorageLocation) spinnerLocationTo.getSelectedItem();
        if (StringUtils.equalsIgnoreCase(locationTo.getStorageLocation(), locationFrom.getStorageLocation())){
            displayDialog(getString(R.string.error_location_from_equal_to), AppConstants.REQUEST_STAY,1);
            return;
        }

        //verify if serial has been checked
        StringBuilder sb = new StringBuilder() ;
        for(SerialInfo serialInfo: serialInfos){
            if (serialInfo.getMaterial() == null || serialInfo.getMaterial().isEmpty()) {
                sb.append(serialInfo.getSerialnumber()).append("; ") ;
            }
        }
        if (sb.toString().length() > 0) {
            displayDialog(getString(R.string.error_check_serial_first) + ": " + sb.toString(), AppConstants.REQUEST_STAY, 1);
            return;
        }

        GeneralPostingRequest request = controller.buildRequest(serialInfos, locationFrom.getStorageLocation(), locationTo.getStorageLocation(), locationFrom.getPlant(), etConfirmDate.getText().toString());
        postData(request);
    }

    private void postData(GeneralPostingRequest request){
        waitDialog.showWaitDialog(this);
        StockTransferPostingTask task = new StockTransferPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(StockTransferActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(StockTransferActivity.this);
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

    //******* Scan *******//
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (dialogIsOpen == true) {
                return;
            }
            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);
            int errorId = scanController.verifyDupScanData(snList, codes);
            if(errorId < 0){
                for (String serialNr: codes) {
                    snList.add(serialNr);
                    SerialInfo serialInfo = new SerialInfo(serialNr);
                    serialInfo.setCount("1");
                    serialInfos.add(serialInfo);
                }
                updateList();
            }else{
                switch (errorId){
                    case ScanController.ERROR_REPEAT_SCAN:
                        displayDialog( app.getString(R.string.text_repeat_scan), -1, 1);
                        break;
                }
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

    @Override
    public void onCallBack(int position) {
        removePosition = position;
        displayDialog( app.getString(R.string.text_confirm_delete), ACTION_DELETE, 2);
    }


    @Override
    public void setInput(String material, String batch, String count, String materialDesc) {
        SerialInfo serialInfo = new SerialInfo("", material, "", batch, "", "");
        serialInfo.setCount(count);
        serialInfo.setMaterialDesc(materialDesc);
        serialInfos.add(serialInfo);
        snList.add("");
        updateList();
        dialogIsOpen = false;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("Item id --->" + item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                //for offline
                displayDialog(getString(R.string.text_offline_not_finished),
                        AppConstants.REQUEST_OFFLINE_DATA, 2);
                break;
            case R.id.action_menu_delete:
                //删除所有条码
                if (snList.size() > 0) {
                    displayDialog(getString(R.string.text_confirm_delete_all_sn), ACTION_DELETE_ALL_SN, 2);
                } else {
                    displayDialog(getString(R.string.error_no_serial_for_delete), -1, 1);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setCount(){
        if(serialInfos != null){
            int maxCount = serialInfos.size();
            tvMaxCountValue.setText(String.valueOf(maxCount));
            tvTotalCountValue.setText(String.valueOf(maxCount));
        }
    }

    private void updateList(){
        stockTransferAdapter.notifyDataSetChanged();
        setCount();
        lvSerial.setSelection(stockTransferAdapter.getCount() -1 );
    }
}