/**
 * 借出单明细
 */
package com.android.pda.activities;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.DialogAddress;
import com.android.pda.activities.view.DialogInput;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.LogisticSpinnerAdapter;

import com.android.pda.adapters.PrototypeBorrowDetailAdapter;

import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.PrototypeBorrowPostingTask;
import com.android.pda.asynctasks.PrototypeBorrowTask;

import com.android.pda.asynctasks.SerialInfoPostingTask;
import com.android.pda.controllers.LogisticsProviderController;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.PrototypeBorrowController;


import com.android.pda.controllers.SerialInfoController;
import com.android.pda.controllers.StorageLocationController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.LogisticsProvider;
import com.android.pda.database.pojo.Offline;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.database.pojo.User;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;
import com.android.pda.models.PrototypeBorrow;
import com.android.pda.models.GeneralPostingRequest;
import com.android.pda.models.BusinessOrderQuery;

import com.android.pda.models.ScanResult;
import com.android.pda.models.SerialInfo;
import com.android.pda.models.SerialNumberResults;
import com.android.pda.utils.DateUtils;


import org.apache.commons.lang3.StringUtils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PrototypeBorrowDetailActivity extends AppCompatActivity implements ActivityInitialization,
        PrototypeBorrowDetailAdapter.OnItemClickListener, PrototypeBorrowDetailAdapter.SplitCallback, DialogInput.InputCallback {
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final PrototypeBorrowController controller = app.getPrototypeBorrowController();

    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private static final UserController userController = app.getUserController();
    private static final String INTENT_KEY_RESERVATION_NUMBER = "ReservationNumber";

    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final SerialInfoController serialInfoController = app.getSerialInfoController();
    private ListView lvPrototypeBorrow;
    private TextView orderValue;

    private EditText etLogisticNumber, etConfirmDate;

    private WaitDialog waitDialog;
    private List<PrototypeBorrow> list = new ArrayList<>();
    private PrototypeBorrowDetailAdapter adapter;
    private List<StorageLocation> storageLocations;
    private BusinessOrderQuery query;
    private LogisticSpinnerAdapter spinnerAdapter;
    private List<LogisticsProvider> logisticsProviders;
    private Spinner spinner;
    private String number;
    private Button btnCheckSerial;
    private final static int REQUESTCODE = 20000;
    private User user;
    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private static final String INTENT_KEY_DATA = "Data";
    private int currentPosition;
    private TextView tvMaxCountValue, tvTotalCountValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prototype_borrow_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = userController.getLoginUser();
        initView();
        initIntent();
        initService();
        //for offline
        if(query != null){
            getData();
        }else{
            initData();
        }

    }
    public static Intent createIntent(Context context, BusinessOrderQuery query, Offline offline) {
        Intent intent = new Intent(context, PrototypeBorrowDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_RESERVATION_NUMBER, query);
        //for offline
        bundle.putSerializable(INTENT_KEY_DATA, (Serializable) offline);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        lvPrototypeBorrow = findViewById(R.id.lv_prototype_borrow);
        waitDialog = new WaitDialog();
        orderValue = findViewById(R.id.tv_order_value);
        btnCheckSerial = findViewById(R.id.btn_checkSerial);
        etLogisticNumber = findViewById(R.id.et_logistics_value);
        spinner = findViewById(R.id.sp_supplier);
        etConfirmDate = findViewById(R.id.et_confirm_date);
        String confirmDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(confirmDate);
        hideSoftInput(etConfirmDate);
        tvMaxCountValue = findViewById(R.id.tv_max_count_value);
        tvTotalCountValue = findViewById(R.id.tv_total_count_value);
    }

    @Override
    public void initData() {
        storageLocations = storageLocationController.getStorageLocation();
        logisticsProviders = logisticsProviderController.getLogisticsProvider();
        spinnerAdapter = new LogisticSpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, logisticsProviders);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        //for offline
        if(query != null){
            number = query.getNumber();
        }else{
            if(offline != null){
                number = offline.getOrderNumber();
                etLogisticNumber.setText(offline.getLogisticNumber());
                list = (List<PrototypeBorrow>)JSON.parseArray(offline.getOrderBody(), PrototypeBorrow.class);
                int index = logisticsProviderController.getLogisticsProviderIndex(offline.getLogisticsProvider(), logisticsProviders);
                spinner.setSelection(index);
            }
        }
        orderValue.setText(number);


        showData();
    }

    private void getData(){
        waitDialog.showWaitDialog(PrototypeBorrowDetailActivity.this);
        PrototypeBorrowTask task = new PrototypeBorrowTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(PrototypeBorrowDetailActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PrototypeBorrowDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_BACK, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<PrototypeBorrow> prototypeBorrows = (List<PrototypeBorrow>) o;
                if(prototypeBorrows != null && prototypeBorrows.size() > 0){
                    list = prototypeBorrows;
                    initData();
                }else{
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_BACK, 1);
                }
            }
        }, query);
        task.execute();
    }

    private void showData() {
        adapter = new PrototypeBorrowDetailAdapter(getApplicationContext(), list);
        adapter.setClickListener(this);
        adapter.setSplitCallback(this);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvPrototypeBorrow.setDividerHeight(1);
        this.lvPrototypeBorrow.setAdapter(adapter);
        setCount();
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
                    LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();
                    offlineController.saveOfflineData(AppConstants.FUNCTION_ID_LEND, JSON.toJSONString(list),
                            number, logisticsProvider, etLogisticNumber.getText().toString(), null, null, null);
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
        query = (BusinessOrderQuery) this.getIntent().getSerializableExtra(INTENT_KEY_RESERVATION_NUMBER);
        //for offline
        if(query == null){
            offline = (Offline) this.getIntent().getSerializableExtra(INTENT_KEY_DATA);
        }
    }

    public void onClickAddress(View view){
        showAddress();
    }

    private void showAddress() {
        String consignee = "", contactWay = "", address = "";
        if(list != null && list.size() > 0){
            consignee = list.get(0).getConsignee();
            contactWay = list.get(0).getConsigneeContact();
            address = list.get(0).getReceiptPlace();
            DialogAddress dialogSuccess = new DialogAddress(this, consignee, contactWay, address);
            dialogSuccess.show();
        }
    }

    public void post(View view){
        String logisticNumber = etLogisticNumber.getText().toString();
        if(StringUtils.isEmpty(logisticNumber)){
            if(user != null){
                if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_company_name_a))){
                    displayDialog(getString(R.string.error_require_fields), AppConstants.REQUEST_STAY, 1);
                    return;
                }
            }
        }

        String error = controller.verifyData(list);
        if(StringUtils.isEmpty(error)){
            LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();

            GeneralPostingRequest request = controller.buildRequest(list, logisticsProvider, logisticNumber, etConfirmDate.getText().toString());

            LogUtils.d("PrototypeBorrowPosting", "request---->" + JSON.toJSONString(request));
            postData(request);

        }else{
            displayDialog(error, AppConstants.REQUEST_STAY, 1);
        }
    }

    private void postData(GeneralPostingRequest request){
        waitDialog.showWaitDialog(this);
        PrototypeBorrowPostingTask task = new PrototypeBorrowPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(PrototypeBorrowDetailActivity.this);
                offlineController.clearOfflineData(AppConstants.FUNCTION_ID_LEND);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PrototypeBorrowDetailActivity.this);
                //Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                displayDialog(error,
                        AppConstants.REQUEST_STAY, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                //Refresh Data
                ScanResult result = (ScanResult) data.getSerializableExtra(DeliveryScanActivity.INTENT_KEY_SCAN_RESULT);
                if(list.size() > currentPosition){
                    Offline offline = offlineController.getOfflineData(AppConstants.LAST_SCAN);
                    List<String> snList = new ArrayList<>();
                    if(offline != null){
                        snList = (List<String>) JSON.parseArray(offline.getOrderBody(), String.class);
                    }
                    PrototypeBorrow prototypeBorrow = list.get(currentPosition);
                    prototypeBorrow.setScanQuantity(snList.size());
                    prototypeBorrow.setSnList(snList);
                    prototypeBorrow.setStorageLocation(result.getStorageLocation());
                    //if(result.getSnList().size() == 0){
                        PrototypeBorrow parent = controller.findParentItem(list, prototypeBorrow);
                        if(parent != null){
                            if(prototypeBorrow.isSub()){
                                parent.setQuantity(parent.getQuantity() + (prototypeBorrow.getQuantity() - snList.size()));
                                prototypeBorrow.setQuantity(snList.size());
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(AppConstants.DATA);
            if(isAlive){
                //etLogisticNumber.setText(code);
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


    @Override
    public void setInput(String item, String batch, int count, StorageLocation storageLocation) {
        if(list != null && list.size() > currentPosition){
            PrototypeBorrow prototypeBorrow = list.get(currentPosition);
            prototypeBorrow.setBatch(batch);
            prototypeBorrow.setScanQuantity(count);
            int total = controller.getItemQuantityTotal(list, prototypeBorrow);

            //找出原始行，将累加的数量赋值给原始行
            PrototypeBorrow findPrototypeBorrow = controller.findParentItem(list, prototypeBorrow);
            if(findPrototypeBorrow != null){
                findPrototypeBorrow.setScanTotal(total);
            }
        }

        adapter.notifyDataSetChanged();
        setCount();
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
    private boolean isAlive;

    @Override
    protected void onPause() {
        isAlive = false;
        super.onPause();
    }


    @Override
    public void onItemClick(int position) {
        currentPosition = position;
        if(list != null && list.size() > 0){
            if(StringUtils.isEmpty(list.get(position).getSerialFlag())){
                DialogInput dialogInput = new DialogInput(this, list.get(position).getReservedItemNo(),
                        list.get(position).getMaterial(), list.get(position).getBatch(),
                        list.get(position).getQuantity(), list.get(position).getDeliveryStockLocation(),
                        list.get(position).getFactory(), list.get(position).getScanQuantity(), list.get(position).isBatchFlag());
                dialogInput.setInputCallback(this);
                dialogInput.show();
            }else{
                String loc = list.get(position).getStorageLocation();
                if(StringUtils.isEmpty(loc)){
                    loc = list.get(position).getDeliveryStockLocation();
                }
                offlineController.clearOfflineData(AppConstants.LAST_SCAN);
                offlineController.saveOfflineData(AppConstants.LAST_SCAN, JSON.toJSONString(list.get(position).getSnList()),
                        "", null, "", null, null, null);
                startActivityForResult(DeliveryScanActivity.createIntent(app,
                        list.get(position).getMaterial(), list.get(position).getBatch(), list.get(position).getQuantity(),
                        loc, list.get(position).getFactory(), false), REQUESTCODE);
            }
        }
    }

    private int deletePosition;
    private PrototypeBorrow splitItem;

    @Override
    public void onSplitCallBack(PrototypeBorrow prototypeBorrow, int position) {
        splitItem = prototypeBorrow;
        if(prototypeBorrow.isSub()){
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
            PrototypeBorrow parentOrder = controller.findParentItem(list, list.get(deletePosition));
            PrototypeBorrow order = list.get(deletePosition);
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
        for (PrototypeBorrow borrow : list) {
            if(StringUtils.isNotEmpty(borrow.getSerialFlag())){
                total += borrow.getQuantity();
                if(borrow.getSnList() != null && borrow.getSnList().size() > 0){
                    for(String sn : borrow.getSnList()){
                        SerialNumberResults serialNumberResult = new SerialNumberResults(sn,
                                borrow.getMaterial(), borrow.getFactory());
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
                waitDialog.hideWaitDialog(PrototypeBorrowDetailActivity.this);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PrototypeBorrowDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if(items != null && items.size() > 0){
                    List<PrototypeBorrow> orders = controller.splitBatch(list, items);
                    //LogUtils.d("transferOrders", "transferOrders---->" + JSON.toJSONString(transferOrders, SerializerFeature.DisableCircularReferenceDetect));
                    list.clear();
                    list.addAll(orders);
                    adapter.notifyDataSetChanged();
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
            double maxCount = list.stream().mapToDouble(PrototypeBorrow::getQuantity).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = list.stream().mapToDouble(PrototypeBorrow::getScanQuantity).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }
    }

}