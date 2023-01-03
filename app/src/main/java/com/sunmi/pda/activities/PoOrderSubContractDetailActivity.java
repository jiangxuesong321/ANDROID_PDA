package com.sunmi.pda.activities;


import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.DialogPurchaseOrder;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.LogisticSpinnerAdapter;
import com.sunmi.pda.adapters.PurchaseOrderSubContractDetailAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.asynctasks.PurchaseOrderSubContractInPostingTask;
import com.sunmi.pda.asynctasks.PurchaseOrderSubContractPostingTask;
import com.sunmi.pda.asynctasks.SerialInfoPostingTask;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.PurchaseOrderSubContractController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.PurchaseOrderSubContract;
import com.sunmi.pda.models.PurchaseOrderSubContractInPostingRequest;
import com.sunmi.pda.models.PurchaseOrderSubContractOutPosting;
import com.sunmi.pda.models.Reason;
import com.sunmi.pda.models.ScanResult;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PoOrderSubContractDetailActivity extends AppCompatActivity implements ActivityInitialization,
        PurchaseOrderSubContractDetailAdapter.SplitCallback, PurchaseOrderSubContractDetailAdapter.OnItemClickListener,
        DialogPurchaseOrder.InputCallback, PurchaseOrderSubContractDetailAdapter.SubCallback{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderSubContractController purchaseOrderController = app.getPurchaseOrderSubContractController();
    private List<StorageLocation> storageLocations;
    private static final String INTENT_KEY_PO = "PO";
    private ListView lvPurchase;
    private TextView orderValue;
    private TextView supplierValue;
    private EditText etDeliveryNumberValue, etConfirmDate;
    private WaitDialog waitDialog;
    private List<PurchaseOrderSubContract> purchaseOrderList;
    private PurchaseOrderSubContractDetailAdapter purchaseOrderAdapter;
    private PurchaseOrderSubContract splitPurchaseOrder;
    private static final OfflineController offlineController = app.getOfflineController();
    private User user;
    private static final UserController userController = app.getUserController();
    private static final LoginController loginController = app.getLoginController();
    private Login login;
    private final static int REQUESTCODE = 20001;
    private final static int REQUESTCODE_COMPONENT = 20002;
    private TextView tvMaxCountValue, tvTotalCountValue;
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private String functionId;
    private TextView tvOrderLabel, tvQuantityLabel, tvScanQuantityLabel;
    private LinearLayout llSupplier, llDeliveryNumber, llLogistic, llLogisticNumber;
    private List<LogisticsProvider> logisticsProviders;
    private LogisticSpinnerAdapter spinnerAdapter;
    private Spinner spinner;
    private EditText etLogisticsValue;
    private Button btnCheckSerial;
    private boolean isAlive;
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private static final SerialInfoController serialInfoController = app.getSerialInfoController();
    private TextView tvItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_order_sub_contract_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initService();
        initIntent();
        initData();
        setCount();
    }
    public static Intent createIntent(Context context, List<PurchaseOrderSubContract> purchaseOrderList,
                                      String functionId) {
        Intent intent = new Intent(context, PoOrderSubContractDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO, (Serializable) purchaseOrderList);
        intent.putExtras(bundle);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, functionId);
        return intent;
    }

    @Override
    public void initView() {
        lvPurchase = findViewById(R.id.lv_purchase_order);
        waitDialog = new WaitDialog();
        orderValue = findViewById(R.id.tv_order_value);
        supplierValue = findViewById(R.id.tv_supplier_value);
        etDeliveryNumberValue = findViewById(R.id.et_delivery_number_value);
        etConfirmDate = findViewById(R.id.et_confirm_date);
        String confirmDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
        etConfirmDate.setText(confirmDate);
        hideSoftInput(etConfirmDate);

        tvMaxCountValue = findViewById(R.id.tv_max_count_value);
        tvTotalCountValue = findViewById(R.id.tv_total_count_value);
        tvOrderLabel = findViewById(R.id.tv_order_label);
        llSupplier = findViewById(R.id.ll_supplier);
        llDeliveryNumber = findViewById(R.id.ll_delivery_number);
        tvQuantityLabel = findViewById(R.id.tv_quantity_label);
        tvScanQuantityLabel = findViewById(R.id.tv_scan_quantity_label);

        llLogistic = findViewById(R.id.ll_logistic);
        llLogisticNumber = findViewById(R.id.ll_logistic_number);
        etLogisticsValue = findViewById(R.id.et_logistics_value);

        spinner = findViewById(R.id.sp_supplier);
        btnCheckSerial = findViewById(R.id.btn_checkSerial);
        tvItem = findViewById(R.id.tv_item);
    }

    @Override
    public void initData() {

        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT)){
            getSupportActionBar().setTitle(getString(R.string.text_po_sub_contract_out_detail));
            llDeliveryNumber.setVisibility(View.GONE);
            llLogistic.setVisibility(View.VISIBLE);
            llLogisticNumber.setVisibility(View.VISIBLE);
            logisticsProviders = logisticsProviderController.getLogisticsProvider();
            spinnerAdapter = new LogisticSpinnerAdapter(getApplicationContext(),
                    R.layout.li_spinner_adapter, logisticsProviders);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            tvQuantityLabel.setText(getString(R.string.text_unout_quantity));
            tvScanQuantityLabel.setText(getString(R.string.text_scan_count));
            tvItem.setVisibility(View.GONE);
        }else{

            getSupportActionBar().setTitle(getString(R.string.text_po_sub_contract_in_detail));
            llDeliveryNumber.setVisibility(View.VISIBLE);
            llLogistic.setVisibility(View.GONE);
            llLogisticNumber.setVisibility(View.GONE);
            tvQuantityLabel.setText(getString(R.string.text_unreceived_quantity));
            tvScanQuantityLabel.setText(getString(R.string.text_receipt_quantity));
            tvItem.setVisibility(View.VISIBLE);
        }
        Offline offline = offlineController.getOfflineData(functionId);
        if(offline != null){
            etDeliveryNumberValue.setText(offline.getLogisticNumber());
        }
        llSupplier.setVisibility(View.VISIBLE);


        if(purchaseOrderList != null && purchaseOrderList.size() > 0){
            orderValue.setText(purchaseOrderList.get(0).getPurchaseOrder());
            supplierValue.setText(purchaseOrderList.get(0).getSupplier());
        }
        user = userController.getLoginUser();
        login = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        showPurchaseData();
    }
    private void showPurchaseData() {

        purchaseOrderAdapter = new PurchaseOrderSubContractDetailAdapter(getApplicationContext(),
                purchaseOrderList, storageLocations, login, functionId);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvPurchase.setDividerHeight(1);
        this.lvPurchase.setAdapter(purchaseOrderAdapter);
        purchaseOrderAdapter.setSplitCallback(this);
        purchaseOrderAdapter.setClickListener(this);
        purchaseOrderAdapter.setSubCallback(this);
        //this.lvPurchase.setOnItemClickListener(this);
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
        purchaseOrderList = (List<PurchaseOrderSubContract>) this.getIntent().getSerializableExtra(INTENT_KEY_PO);
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
    }

    public void post(View view){
        String deliveryNumber = etDeliveryNumberValue.getText().toString();

        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT)){
            String logisticNumber = etLogisticsValue.getText().toString();
            if(StringUtils.isEmpty(logisticNumber)){
                if(user != null){
                    if(StringUtils.contains(user.getGroup(), app.getString(R.string.text_sunmi))){
                        displayDialog(getString(R.string.error_require_fields), AppConstants.REQUEST_STAY, 1);
                        return;
                    }
                }
            }
            String error = purchaseOrderController.verifyData(purchaseOrderList);
            if(StringUtils.isEmpty(error)){
                LogisticsProvider logisticsProvider = (LogisticsProvider) spinner.getSelectedItem();
                List<PurchaseOrderSubContractOutPosting> requestList =
                        purchaseOrderController.buildRequest(purchaseOrderList, logisticsProvider.getZtName(),
                                logisticNumber, etConfirmDate.getText().toString());
                LogUtils.d("PurchaseOrderPosting", "request---->" + JSON.toJSONString(requestList));
                postPoData(requestList);

            }else{
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }
        }else{
            if(StringUtils.isEmpty(deliveryNumber)){
                if(user != null){
                    if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_sunmi))){
                        displayDialog(getString(R.string.error_require_delivery_number), AppConstants.REQUEST_STAY, 1);
                        return;
                    }
                }
            }
            String error = purchaseOrderController.verifyData(purchaseOrderList);
            if(StringUtils.isEmpty(error)){
                List<PurchaseOrderSubContractInPostingRequest> requestList
                        = purchaseOrderController.buildInRequest(purchaseOrderList, deliveryNumber, etConfirmDate.getText().toString());
                postPoInData(requestList);

            }else{
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }
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
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    //暂存当前数据
                    offlineController.saveOfflineData(functionId, JSON.toJSONString(purchaseOrderList),
                            orderValue.getText().toString(), null, etDeliveryNumberValue.getText().toString(),
                            null, null, null);
                    finish();
                }else{
                    if(AppConstants.REQUEST_DELETE == action){
                        deletePurchaseOrder();
                    }else{
                        splitPurchaseOrder();
                    }
                }
            }
            @Override
            public void callClose() {
                if(AppConstants.REQUEST_SUCCEED == action){
                    setResult(RESULT_OK);
                    finish();
                }
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    finish();
                }
            }
        });
        noticeDialog.create();

    }

    private void postPoData(List<PurchaseOrderSubContractOutPosting> postingList){
        waitDialog.showWaitDialog(this);
        PurchaseOrderSubContractPostingTask task = new PurchaseOrderSubContractPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderSubContractDetailActivity.this);
                offlineController.clearOfflineData(functionId);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderSubContractDetailActivity.this);
                //Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }

            @Override
            public void bindModel(Object o) {
                displayDialog(getString(R.string.text_material_document) + ": "
                        + (String) o, AppConstants.REQUEST_SUCCEED, 1);

            }
        }, postingList);
        task.execute();
    }

    //进行委外采购收货过账
    private void postPoInData(List<PurchaseOrderSubContractInPostingRequest> requestList){
        waitDialog.showWaitDialog(this);
        PurchaseOrderSubContractInPostingTask task = new PurchaseOrderSubContractInPostingTask(getApplicationContext(),
                new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderSubContractDetailActivity.this);
                offlineController.clearOfflineData(functionId);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderSubContractDetailActivity.this);
                //Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }

            @Override
            public void bindModel(Object o) {
                displayDialog(getString(R.string.text_material_document) + ": "
                        + (String) o, AppConstants.REQUEST_SUCCEED, 1);

            }
        }, requestList);
        task.execute();
    }

    int deletePosition;
    @Override
    public void onCallBack(PurchaseOrderSubContract purchaseOrder, int position) {
        splitPurchaseOrder = purchaseOrder;

        if(purchaseOrder.isSubOrder()){
            deletePosition = position;
            displayDialog(getString(R.string.text_confirm_delete),
                    AppConstants.REQUEST_DELETE,
                    2);
        }else{
            displayDialog(getString(R.string.text_make_sure_split),
                    AppConstants.REQUEST_FAILED,
                    2);
        }

    }

    private void deletePurchaseOrder(){

        purchaseOrderList.remove(deletePosition);
        purchaseOrderAdapter.notifyDataSetChanged();
        setCount();
        //showPurchaseData();
    }

    private void splitPurchaseOrder(){
        purchaseOrderController.splitPurchaseOrder(purchaseOrderList, splitPurchaseOrder);
        purchaseOrderAdapter.notifyDataSetChanged();
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

    private int currentPosition;
    @Override
    public void onItemClick(int position) {

        currentPosition = position;
        if(StringUtils.isEmpty(purchaseOrderList.get(position).getSerialFlag())){
            DialogPurchaseOrder dialogInput = new DialogPurchaseOrder(this, purchaseOrderList.get(position).getPurchaseOrderItem(),
                    purchaseOrderList.get(position).getMaterial(), purchaseOrderList.get(position).getPurchaseOrderItemText(),
                    purchaseOrderList.get(position).getBatch(),
                    purchaseOrderList.get(position).getOpenQuantity(), purchaseOrderList.get(position).getStorageLocation(),
                    purchaseOrderList.get(position).getPlant(), purchaseOrderList.get(position).getQuantityInEntryUnit(),
                    purchaseOrderList.get(position).isBatchFlag(), functionId);
            dialogInput.setInputCallback(this);
            dialogInput.show();
            isAlive = false;
            dialogInput.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    isAlive = true;
                }
            });
        }else{
            offlineController.clearOfflineData(AppConstants.LAST_SCAN);
            offlineController.saveOfflineData(AppConstants.LAST_SCAN, JSON.toJSONString(purchaseOrderList.get(position).getSnList()),
                    "", null, "", null, null, null);
            startActivityForResult(DeliveryScanActivity.createIntent(app,
                    purchaseOrderList.get(position).getMaterial(), purchaseOrderList.get(position).getBatch(),
                    purchaseOrderList.get(position).getOpenQuantity(), purchaseOrderList.get(position).getStorageLocation(),
                    purchaseOrderList.get(position).getPlant(), false, functionId), REQUESTCODE);
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
                ScanResult result = (ScanResult) data.getSerializableExtra(DeliveryScanActivity.INTENT_KEY_SCAN_RESULT);
                if(purchaseOrderList.size() > currentPosition){
                    Offline offline = offlineController.getOfflineData(AppConstants.LAST_SCAN);
                    List<String> snList = new ArrayList<>();
                    if(offline != null){
                        snList = (List<String>) JSON.parseArray(offline.getOrderBody(), String.class);
                    }
                    PurchaseOrderSubContract po = purchaseOrderList.get(currentPosition);
                    po.setQuantityInEntryUnit(String.valueOf(snList.size()));
                    po.setSnList(snList);
                    po.setStorageLocation(result.getStorageLocation());
                    po.setBatch(result.getBatch());
                    if(result.getReason() != null){
                        po.setReason(result.getReason().getId());
                    }
                    PurchaseOrderSubContract parent = purchaseOrderController.findParentItem(purchaseOrderList, po);
                    if(parent != null){
                        if(parent.isSubOrder()){
                            parent.setOpenQuantity(parent.getOpenQuantity() + (po.getOpenQuantity() - snList.size()));
                            po.setOpenQuantity(snList.size());
                            if(snList.size() == 0){
                                purchaseOrderList.remove(currentPosition);
                            }
                        }
                    }
                    if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_IN )){
                        purchaseOrderController.setComponentCount(po);
                    }
                }
                purchaseOrderAdapter.notifyDataSetChanged();
                setCount();
            }else if(requestCode == REQUESTCODE_COMPONENT){
                PurchaseOrderSubContract result = (PurchaseOrderSubContract) data.getSerializableExtra(PoOrderComponentDetailActivity.INTENT_KEY_ORDER);
                if(purchaseOrderList.size() > currentPosition){
                    PurchaseOrderSubContract po = purchaseOrderList.get(currentPosition);
                    po.setComponents(result.getComponents());;

                }
                purchaseOrderAdapter.notifyDataSetChanged();
                setCount();
            }
        }
    }

    @Override
    public void setInput(String item, String batch, int count, StorageLocation storageLocation, Reason reason) {
        if(purchaseOrderList != null && purchaseOrderList.size() > currentPosition){
            PurchaseOrderSubContract order = purchaseOrderList.get(currentPosition);
            order.setBatch(batch);
            order.setQuantityInEntryUnit(String.valueOf(count));
            order.setStorageLocation(storageLocation.getStorageLocation());
            if(reason != null){
                order.setReason(reason.getId());
            }
            int total = purchaseOrderController.getItemQuantityTotal(purchaseOrderList, order);

            //找出原始行，将累加的数量赋值给原始行
            PurchaseOrderSubContract findPurchaseOrderSubContract = purchaseOrderController.findParentItem(purchaseOrderList, order);
            if(findPurchaseOrderSubContract != null){
                findPurchaseOrderSubContract.setQuantityInEntryUnit(total + "");
            }
            if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_IN )){
                purchaseOrderController.setComponentCount(order);
            }
        }
        isAlive = true;
        purchaseOrderAdapter.notifyDataSetChanged();
        setCount();

    }


    private void setCount(){
        if(purchaseOrderList != null){
            double maxCount = purchaseOrderList.stream().mapToDouble(PurchaseOrderSubContract::getOpenQuantity).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = purchaseOrderList.stream().mapToDouble(PurchaseOrderSubContract::getScanQuantity).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }
    }

    @Override
    protected void onResume() {
        isAlive = true;
        boolean isShow = purchaseOrderController.isShowCheckBtn(purchaseOrderList);
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT)){
            if(isShow){
                btnCheckSerial.setVisibility(View.VISIBLE);
            }else{
                btnCheckSerial.setVisibility(View.GONE);
            }
        }else{
            btnCheckSerial.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        isAlive = false;
        super.onPause();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra(AppConstants.DATA);
            if(isAlive){
                etLogisticsValue.setText(code);
            }
        }
    };

    public void checkSerial(View view){

        /*List<String> snList = new ArrayList<>();
        Set<String> sameSet = new HashSet<>();*/
        List<SerialNumberResults> serialNumberResults = new ArrayList<>();
        int total = 0;
        for (PurchaseOrderSubContract order : purchaseOrderList) {
            if(StringUtils.isNotEmpty(order.getSerialFlag())){
                total += order.getScanQuantity();
                if(order.getSnList() != null && order.getSnList().size() > 0){
                    for(String sn : order.getSnList()){
                        SerialNumberResults serialNumberResult = new SerialNumberResults(sn,
                                order.getMaterial(), order.getPlant());
                        serialNumberResults.add(serialNumberResult);
                    }
                }
            }
        }
        /*if(sameSet.size() != snList.size()){
            displayDialog( app.getString(R.string.text_repeat_scan), -1, 1);
            return;
        }*/
        if(total == 0){
            displayDialog(getString(R.string.text_please_scan_first), AppConstants.REQUEST_STAY,1);
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
                waitDialog.hideWaitDialog(PoOrderSubContractDetailActivity.this);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderSubContractDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if(items != null && items.size() > 0){
                    List<PurchaseOrderSubContract> orders = purchaseOrderController.splitBatch(purchaseOrderList, items);
                    //LogUtils.d("transferOrders", "transferOrders---->" + JSON.toJSONString(transferOrders, SerializerFeature.DisableCircularReferenceDetect));
                    purchaseOrderList.clear();
                    purchaseOrderList.addAll(orders);
                    purchaseOrderAdapter.notifyDataSetChanged();
                }else{
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_STAY, 1);
                }
            }
        }, serials);
        task.execute();
    }

    @Override
    public void onSubCallBack(PurchaseOrderSubContract purchaseOrder, int position) {
        if(purchaseOrder.getScanQuantity() > 0){

            startActivityForResult(PoOrderComponentDetailActivity.createIntent(app, purchaseOrder), REQUESTCODE_COMPONENT);
        }else{
            displayDialog(getString(R.string.text_greater_than_zero), AppConstants.REQUEST_STAY, 1);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineController.clearOfflineData(AppConstants.LAST_SCAN);
    }
}