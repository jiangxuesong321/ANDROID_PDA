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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.DialogAddress;
import com.sunmi.pda.activities.view.DialogInput;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.LogisticSpinnerAdapter;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.adapters.TransferOrderAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.PrototypeBorrowPostingTask;
import com.sunmi.pda.asynctasks.SerialInfoPostingTask;
import com.sunmi.pda.asynctasks.TransferOrderTask;
import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.TransferOrderController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.Picking;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.ScanResult;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.models.TransferOrder;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransferOrderDetailActivity extends AppCompatActivity implements ActivityInitialization,
        TransferOrderAdapter.OnItemClickListener, TransferOrderAdapter.SplitCallback, DialogInput.InputCallback {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final TransferOrderController controller = app.getTransferOrderController();
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();

    private final static int REQUESTCODE = 20000;
    private static final String INTENT_KEY_TRANSFERORDER = "TransferOrder";
    public static final String INTENT_KEY_SCAN_RESULT = "ScanResult";
    private static final String INTENT_KEY_DATA = "Data";
    private ListView lvTransferOrder;
    private TextView orderValue;
    private EditText etLogisticNumber;
    private EditText etConfirmDate;
    private Spinner spinnerLocation;
    private SpinnerAdapter locationSpinnerAdapter;

    private Spinner spinnerLogisticProvider;
    private LogisticSpinnerAdapter logisticSpinnerAdapter;

    private User user;
    private List<LogisticsProvider> logisticsProviders;
    private List<StorageLocation> storageLocations;

    private WaitDialog waitDialog;
    private List<TransferOrder> list = new ArrayList<>();
    private TransferOrderAdapter adapter;
    private SalesInvoiceQuery query;
    private int currentPosition;
    private Button btnCheckSerial;
    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private static final SerialInfoController serialInfoController = app.getSerialInfoController();
    private static final ScanController scanController = app.getScanController();
    private TextView tvMaxCountValue, tvTotalCountValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initListener();
        initIntent();
        initData();
        if (list == null || list.size() == 0) {
            getData();
        }
        bindView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    public static Intent createIntent(Context context, SalesInvoiceQuery query ) {
        Intent intent = new Intent(context, TransferOrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_TRANSFERORDER, query);
        //for offline

        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        this.lvTransferOrder = findViewById(R.id.lv_transfer_order);
        waitDialog = new WaitDialog();
        orderValue = findViewById(R.id.tv_order_value);
        spinnerLocation = findViewById(R.id.sp_location);
        spinnerLogisticProvider = findViewById(R.id.sp_supplier);
        etLogisticNumber = findViewById(R.id.et_logistics_value);
        etConfirmDate = findViewById(R.id.et_confirm_date);
        hideSoftInput(etConfirmDate);
        btnCheckSerial = findViewById(R.id.btn_checkSerial);
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
        //spinner supplier
        logisticsProviders = logisticsProviderController.getLogisticsProvider();
        storageLocations = storageLocationController.getStorageLocation();

        if(list != null && list.size() > 0){
            orderValue.setText(list.get(0).getReservedNo());
            showData();
        } else if(offline != null){
            //for offline
            etLogisticNumber.setText(offline.getLogisticNumber());
            list = (List<TransferOrder>)JSON.parseArray(offline.getOrderBody(), TransferOrder.class);
            if (list != null & list.size() > 0) {
                orderValue.setText(list.get(0).getReservedNo());
            }
            showData();
        }

        String date = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(date);
        boolean isShow = controller.isShowCheckBtn(list);
        if(isShow){
            btnCheckSerial.setVisibility(View.VISIBLE);
        }else{
            btnCheckSerial.setVisibility(View.GONE);
        }
    }

    private void bindView(){
        logisticSpinnerAdapter = new LogisticSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, logisticsProviders);
        logisticSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLogisticProvider.setAdapter(logisticSpinnerAdapter);
        if(offline != null) {
            int index = logisticsProviderController.getLogisticsProviderIndex(offline.getLogisticsProvider(), logisticsProviders);
            spinnerLogisticProvider.setSelection(index);
            logisticSpinnerAdapter.notifyDataSetChanged();
        }

        locationSpinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        locationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationSpinnerAdapter);

        if(offline != null) {
            int indexLocation = storageLocationController.getStorageLocationPosition(offline.getSendLocation(), storageLocations);
            spinnerLocation.setSelection(indexLocation);
            locationSpinnerAdapter.notifyDataSetChanged();
        }
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
                    Offline offline = offlineController.getOfflineData(AppConstants.LAST_SCAN);
                    List<String> snList = new ArrayList<>();
                    if(offline != null){
                        snList = (List<String>) JSON.parseArray(offline.getOrderBody(), String.class);
                    }
                    TransferOrder transferOrder = list.get(currentPosition);
                    transferOrder.setScanQuantity(snList.size());
                    transferOrder.setSnList(snList);
                    transferOrder.setStorageLocation(result.getStorageLocation());
                    //if(result.getSnList().size() == 0){
                    TransferOrder parent = controller.findParentItem(list, transferOrder);
                    if(parent != null){
                        if(transferOrder.isSub()){
                            parent.setQuantity(parent.getQuantity() + (transferOrder.getQuantity() - snList.size()));
                            transferOrder.setQuantity(Double.valueOf(snList.size()));
                            if(snList.size() == 0){
                                list.remove(currentPosition);
                            }
                        }
                    }
                    //}
                }
                adapter.notifyDataSetChanged();
                setCount();
            }
        }
    }

    private void showData() {
        adapter = new TransferOrderAdapter(getApplicationContext(), list);
        adapter.setClickListener(this);
        adapter.setSplitCallback(this);
        this.lvTransferOrder.setDividerHeight(1);
        this.lvTransferOrder.setAdapter(adapter);
        setCount();
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
        query = (SalesInvoiceQuery) this.getIntent().getSerializableExtra(INTENT_KEY_TRANSFERORDER);
        //for offline
        if(query == null){
            offline = TransferOrderHomeActivity.offline;
        }
    }

    private void getData(){
        waitDialog.showWaitDialog(TransferOrderDetailActivity.this);
        TransferOrderTask task = new TransferOrderTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(TransferOrderDetailActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(TransferOrderDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_BACK, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<TransferOrder> transferOrders = (List<TransferOrder>) o;
                if(transferOrders.size() > 0 ){
                    list = transferOrders;
                    if(list != null && list.size() > 0) {
                        int pos = storageLocationController.getStorageLocationPosition(list.get(0).getReceiverLoc(), storageLocations);
                        spinnerLocation.setSelection(pos);
                        locationSpinnerAdapter.notifyDataSetChanged();
                    }
                    initData();
                } else {
                    displayDialog(app.getString(R.string.error_order_not_found), AppConstants.REQUEST_BACK, 1);
                }
            }
        }, query);
        task.execute();
    }

    public void onClickAddress(View view){
        showAddress();
    }

    private void showAddress() {
        String consignee = "", contactWay = "", address = "";
        if(list != null && list.size() > 0){
            consignee = list.get(0).getReceivePerson();
            contactWay = list.get(0).getReceiveContact();
            address = list.get(0).getReceivePlace();
            DialogAddress dialogSuccess = new DialogAddress(this, consignee, contactWay, address);
            dialogSuccess.show();
        }
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
                    StorageLocation location = (StorageLocation) spinnerLocation.getSelectedItem();
                    LogisticsProvider logisticsProvider = (LogisticsProvider) spinnerLogisticProvider.getSelectedItem();
                    String logisticNr = etLogisticNumber.getText().toString();
                    offlineController.saveOfflineData(AppConstants.FUNCTION_ID_TRANSFER_OUT, JSON.toJSONString(list), list.get(0).getReservedNo(), logisticsProvider, logisticNr, "", location, null);
                    finish();
                }else{
                    if(AppConstants.REQUEST_DELETE == action){
                        deleteItem();
                    }else{
                        splitItem();
                    }
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(AppConstants.DATA);
            if(isAlive){
//                etLogisticNumber.setText(code);
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    public void post(View view){
        LogisticsProvider logisticsProvider = (LogisticsProvider) spinnerLogisticProvider.getSelectedItem();
        StorageLocation location = (StorageLocation) spinnerLocation.getSelectedItem();
        if(location == null){
            displayDialog(getString(R.string.error_require_location), AppConstants.REQUEST_FAILED, 1);
            return;
        }
        if(logisticsProvider == null){
            if(user != null){
                if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_sunmi))){
                    displayDialog(getString(R.string.error_require_fields), AppConstants.REQUEST_FAILED, 1);
                    return;
                }
            }
        }
        String logisticNumber = etLogisticNumber.getText().toString();
        if(StringUtils.isEmpty(logisticNumber)){
            if(user != null){
                if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_sunmi))){
                    displayDialog(getString(R.string.error_require_fields), AppConstants.REQUEST_FAILED, 1);
                    return;
                }
            }
        }
        //Test max count
        /*for(TransferOrder order : list){
            List<String> snList = scanController.testScan(order.getMaterialNo(), 1000, order.getBatchNo().substring(order.getBatchNo().length() - 1));
            ScanResult result = new ScanResult(order.getReceiverLoc(), order.getMaterialNo(), order.getBatchNo(), snList);
            order.setScanQuantity(result.getSnList().size());
            order.setSnList(result.getSnList());
            order.setStorageLocation(result.getStorageLocation());
        }*/

        String error = controller.verifyData(list);
        if(StringUtils.isEmpty(error)){
            GeneralPostingRequest request = controller.buildRequest(list, location, logisticsProvider, logisticNumber, 8, etConfirmDate.getText().toString());
            LogUtils.d("GeneralPostingRequest", "request---->" + JSON.toJSONString(request));
            postData(request);

        }else{
            displayDialog(error, AppConstants.REQUEST_FAILED, 1);
        }

    }

    private void postData(GeneralPostingRequest request){
        waitDialog.showWaitDialog(this);
        PrototypeBorrowPostingTask task = new PrototypeBorrowPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(TransferOrderDetailActivity.this);
                offlineController.clearOfflineData(AppConstants.FUNCTION_ID_TRANSFER_OUT);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(TransferOrderDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY,1);
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
    public void setInput(String item, String batch, int count, StorageLocation storageLocation) {
        if(list != null && list.size() > currentPosition){
            TransferOrder transferOrder = list.get(currentPosition);
            transferOrder.setBatchNo(batch);
            transferOrder.setScanQuantity(count);
            int total = controller.getItemQuantityTotal(list, transferOrder);

            //找出原始行，将累加的数量赋值给原始行
            TransferOrder findTransferOrder = controller.findParentItem(list, transferOrder);
            if(findTransferOrder != null){
                findTransferOrder.setScanTotal(total);
            }
        }

        adapter.notifyDataSetChanged();
        setCount();
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

    private boolean isAlive;

    @Override
    protected void onPause() {
        isAlive = false;
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineController.clearOfflineData(AppConstants.LAST_SCAN);
        unregisterReceiver(receiver);
    }

    @Override
    public void onCallBack(int position) {
        currentPosition = position;
        if(list != null && list.size() > 0){
            //TransferOrder transferOrder = list.get(position);
            if(StringUtils.isEmpty(list.get(position).getSerialFlag())){
                DialogInput dialogInput = new DialogInput(this, list.get(position).getReservedItemNo(),
                        list.get(position).getMaterialNo(), list.get(position).getBatchNo(),
                        list.get(position).getQuantity(), list.get(position).getReceiverLoc(),
                        list.get(position).getFactory(), list.get(position).getScanQuantity(), list.get(position).isBatchFlag());
                dialogInput.setInputCallback(this);
                dialogInput.show();
            } else {
                String loc = list.get(position).getStorageLocation();
                if(StringUtils.isEmpty(loc)){
                    loc = list.get(position).getSenderLoc();
                }
                offlineController.clearOfflineData(AppConstants.LAST_SCAN);
                offlineController.saveOfflineData(AppConstants.LAST_SCAN, JSON.toJSONString(list.get(position).getSnList()),
                        "", null, "", null, null, null);
                startActivityForResult(DeliveryScanActivity.createIntent(app,
                        list.get(position).getMaterialNo(), list.get(position).getBatchNo(),
                        list.get(position).getQuantity(), loc, list.get(position).getFactory(),
                        false), REQUESTCODE);
            }
        }
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

    private int deletePosition;
    private TransferOrder splitItem;
    @Override
    public void onSplitCallBack(TransferOrder transferOrder, int position) {
        splitItem = transferOrder;
        if(transferOrder.isSub()){
            deletePosition = position;
            displayDialog(getString(R.string.text_confirm_delete),
                    AppConstants.REQUEST_DELETE,
                    2);
        }else{
            displayDialog(getString(R.string.text_make_sure_split),
                    AppConstants.REQUEST_SPLIT,
                    2);
        }
    }
    private void splitItem(){
        controller.splitItem(list, splitItem);
        adapter.notifyDataSetChanged();
    }

    private void deleteItem(){

        if(list != null && list.size() > deletePosition){
            TransferOrder parentOrder = controller.findParentItem(list, list.get(deletePosition));
            TransferOrder order = list.get(deletePosition);
            int total = parentOrder.getScanTotal();
            if(order != null){
                total = total - order.getScanQuantity();
            }
            parentOrder.setScanTotal(total);
            list.remove(deletePosition);
        }
        showData();
    }

    public void checkSerial(View view){
        /*List<String> snList = new ArrayList<>();
        Set<String> sameSet = new HashSet<>();*/
        List<SerialNumberResults> serialNumberResults = new ArrayList<>();
        int total = 0;
        for (TransferOrder transferOrder : list) {
            if(StringUtils.isNotEmpty(transferOrder.getSerialFlag())){
                total += transferOrder.getQuantity();
                if(transferOrder.getSnList() != null && transferOrder.getSnList().size() > 0){
                    for(String sn : transferOrder.getSnList()){
                        SerialNumberResults serialNumberResult = new SerialNumberResults(sn,
                                transferOrder.getMaterialNo(), transferOrder.getFactory());
                        serialNumberResults.add(serialNumberResult);
                    }
                }
            }
        }
        /*if(sameSet.size() != snList.size()){
            displayDialog( app.getString(R.string.text_repeat_scan), -1, 1);
            return;
        }*/
        if(total == 0 || total != serialNumberResults.size()){
            displayDialog(getString(R.string.error_low_quantity), AppConstants.REQUEST_STAY,1);
            return;
        }
        if(serialNumberResults.size() > 0){
            //List<SerialNumberResults> serialNumberResults = serialInfoController.buildSerialNumberResults(snList);
            postSerials(serialNumberResults);
        }

    }

    private void postSerials(List<SerialNumberResults> serials){
        waitDialog.showWaitDialog(this);
        SerialInfoPostingTask task = new SerialInfoPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(TransferOrderDetailActivity.this);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(TransferOrderDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if(items != null && items.size() > 0){
                    //TODO 取回批次，填充批次，如果有需要拆分的批次添加相应逻辑
                    List<TransferOrder> transferOrders = controller.splitBatch(list, items);
                    //LogUtils.d("transferOrders", "transferOrders---->" + JSON.toJSONString(transferOrders, SerializerFeature.DisableCircularReferenceDetect));
                    list.clear();
                    list.addAll(transferOrders);
                    adapter.notifyDataSetChanged();
                    setCount();
                }else{
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_STAY, 1);
                }
            }
        }, serials);
        task.execute();
    }
    @Override
    protected void onResume() {
        isAlive = true;
        boolean isShow = controller.isShowCheckBtn(list);
        if(isShow){
            btnCheckSerial.setVisibility(View.VISIBLE);
        }else{
            btnCheckSerial.setVisibility(View.GONE);
        }

        super.onResume();
    }

    private void setCount(){
        if(list != null){
            double maxCount = list.stream().mapToDouble(TransferOrder::getQuantity).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = list.stream().mapToDouble(TransferOrder::getScanQuantity).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }
    }
}