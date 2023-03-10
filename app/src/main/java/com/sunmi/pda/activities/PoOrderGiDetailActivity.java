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
import com.sunmi.pda.adapters.PurchaseOrderGiDetailAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.PurchaseOrdeGiPostingTask;
import com.sunmi.pda.asynctasks.PurchaseOrderPostingTask;
import com.sunmi.pda.asynctasks.PurchaseOrderReturnPostingTask;
import com.sunmi.pda.asynctasks.SerialInfoPostingTask;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.PurchaseOrderGiController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.PurchaseOrderGi;
import com.sunmi.pda.models.PurchaseOrderGiPostingRequest;
import com.sunmi.pda.models.PurchaseOrderGiResult;
import com.sunmi.pda.models.PurchaseOrderPostingRequest;
import com.sunmi.pda.models.PurchaseOrderReturnPostingRequest;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PoOrderGiDetailActivity extends AppCompatActivity implements ActivityInitialization,
        PurchaseOrderGiDetailAdapter.SplitCallback, PurchaseOrderGiDetailAdapter.OnItemClickListener,
        DialogPurchaseOrder.InputCallback{
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final PurchaseOrderGiController purchaseOrderController = app.getPurchaseOrderGiController();
    private List<StorageLocation> storageLocations;
    private static final String INTENT_KEY_PO = "PO";
    private ListView lvPurchase;
    private TextView orderValue;
    private TextView supplierValue;
    private EditText etDeliveryNumberValue, etConfirmDate, etLogisticsValue;
    private WaitDialog waitDialog;
    private List<PurchaseOrderGi> purchaseOrderList;
    private PurchaseOrderGiDetailAdapter purchaseOrderAdapter;
    private PurchaseOrderGi splitPurchaseOrder;
    private static final OfflineController offlineController = app.getOfflineController();
    private User user;
    private static final UserController userController = app.getUserController();
    private static final LoginController loginController = app.getLoginController();
    private Login login;
    private final static int REQUESTCODE = 20001;
    private TextView tvMaxCountValue, tvTotalCountValue;
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private String functionId;
    private TextView tvOrderLabel, tvQuantityLabel, tvScanQuantityLabel;
    private LinearLayout llSupplier, llDeliveryNumber, llProvider, llLogistic;
    private Spinner spSupplier;
    private LogisticSpinnerAdapter spinnerAdapter;
    private List<LogisticsProvider> logisticsProviders;
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private boolean isAlive;
    private Button btnCheckSerial;
    private static final SerialInfoController serialInfoController = app.getSerialInfoController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_order_gi_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initService();
        initIntent();
        initData();
        setCount();
    }
    public static Intent createIntent(Context context, List<PurchaseOrderGi> purchaseOrderList,
                                      String functionId) {
        Intent intent = new Intent(context, PoOrderGiDetailActivity.class);
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
        llProvider = findViewById(R.id.ll_provider);
        llLogistic = findViewById(R.id.ll_logistic);
        spSupplier = findViewById(R.id.sp_supplier);
        etLogisticsValue = findViewById(R.id.et_logistics_value);
        btnCheckSerial = findViewById(R.id.btn_checkSerial);
    }

    @Override
    public void initData() {


        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
            Offline offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI);
            if(offline != null){
                //System.out.println("getLogisticNumber--->" + offline.getLogisticNumber());
                etDeliveryNumberValue.setText(offline.getLogisticNumber());
            }
            getSupportActionBar().setTitle(getString(R.string.text_po_gi_detail));
            tvOrderLabel.setText(getString(R.string.text_po_gi_nr));
            llDeliveryNumber.setVisibility(View.GONE);
            llProvider.setVisibility(View.VISIBLE);
            llLogistic.setVisibility(View.VISIBLE);
            tvQuantityLabel.setText(getString(R.string.text_not_shipped_count));
            tvScanQuantityLabel.setText(getString(R.string.text_scan_count));
            logisticsProviders = logisticsProviderController.getLogisticsProvider();
            spinnerAdapter = new LogisticSpinnerAdapter(getApplicationContext(),
                    R.layout.li_spinner_adapter, logisticsProviders);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSupplier.setAdapter(spinnerAdapter);
            if(purchaseOrderList != null && purchaseOrderList.size() > 0){
                orderValue.setText(purchaseOrderList.get(0).getProductionOrder());
                supplierValue.setText(purchaseOrderList.get(0).getPlant());
            }

        }else{
            Offline offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR);
            if(offline != null){
                //System.out.println("getLogisticNumber--->" + offline.getLogisticNumber());
                etDeliveryNumberValue.setText(offline.getLogisticNumber());
            }
            getSupportActionBar().setTitle(getString(R.string.text_po_pgr_detail));
            tvOrderLabel.setText(getString(R.string.text_po_gi_nr));

            llDeliveryNumber.setVisibility(View.VISIBLE);
            llProvider.setVisibility(View.GONE);
            llLogistic.setVisibility(View.GONE);
            tvQuantityLabel.setText(getString(R.string.text_not_shipped_count));
            tvScanQuantityLabel.setText(getString(R.string.text_scan_count));
            if(purchaseOrderList != null && purchaseOrderList.size() > 0){
                orderValue.setText(purchaseOrderList.get(0).getProductionOrder());
                supplierValue.setText(purchaseOrderList.get(0).getPlant());
            }
        }
        user = userController.getLoginUser();
        login = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        showPurchaseData();
    }
    private void showPurchaseData() {

        purchaseOrderAdapter = new PurchaseOrderGiDetailAdapter(getApplicationContext(),
                purchaseOrderList, storageLocations, login, functionId);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvPurchase.setDividerHeight(1);
        this.lvPurchase.setAdapter(purchaseOrderAdapter);
        purchaseOrderAdapter.setSplitCallback(this);
        purchaseOrderAdapter.setClickListener(this);
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
        purchaseOrderList = (List<PurchaseOrderGi>) this.getIntent().getSerializableExtra(INTENT_KEY_PO);
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
    }

    public void post(View view){
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
            String logisticNumber = etLogisticsValue.getText().toString();
            LogisticsProvider logisticsProvider = (LogisticsProvider) spSupplier.getSelectedItem();
            if(StringUtils.isEmpty(logisticNumber)){
                if(user != null){
                    if(StringUtils.contains(user.getGroup(), app.getString(R.string.text_sunmi))){
                        displayDialog(getString(R.string.error_require_fields), AppConstants.REQUEST_STAY, 1);
                        return;
                    }
                }
            }
            String error = purchaseOrderController.verifyData(purchaseOrderList, functionId);
            if(StringUtils.isEmpty(error)){
                List<PurchaseOrderGiPostingRequest> request =
                        purchaseOrderController.buildRequest(purchaseOrderList, logisticsProvider, logisticNumber,
                                etConfirmDate.getText().toString(), "", functionId);
                LogUtils.d("PurchaseOrderGiPostingRequest", "request---->" + JSON.toJSONString(request));
                postPoData(request);

            }else{
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }
        }else{
            String deliveryNumber = etDeliveryNumberValue.getText().toString();
            if(StringUtils.isEmpty(deliveryNumber)){
                if(user != null){
                    if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_sunmi))){
                        displayDialog(getString(R.string.error_require_delivery_number), AppConstants.REQUEST_STAY, 1);
                        return;
                    }
                }
            }
            String error = purchaseOrderController.verifyData(purchaseOrderList, functionId);
            if(StringUtils.isEmpty(error)){
                List<PurchaseOrderGiPostingRequest> request =
                        purchaseOrderController.buildRequest(purchaseOrderList, null, "",
                                etConfirmDate.getText().toString(), deliveryNumber, functionId);
                LogUtils.d("PurchaseOrderGiPostingRequest", "request---->" + JSON.toJSONString(request));
                postPoData(request);
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
                    //??????????????????
                    if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
                        LogisticsProvider logisticsProvider = (LogisticsProvider) spSupplier.getSelectedItem();
                        offlineController.saveOfflineData(functionId, JSON.toJSONString(purchaseOrderList), orderValue.getText().toString(), logisticsProvider, etLogisticsValue.getText().toString(), null, null, null);
                    }else{
                        offlineController.saveOfflineData(functionId, JSON.toJSONString(purchaseOrderList), orderValue.getText().toString(), null, etDeliveryNumberValue.getText().toString(), null, null, null);
                    }
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

    private void postPoData(List<PurchaseOrderGiPostingRequest> request){
        waitDialog.showWaitDialog(this);
        PurchaseOrdeGiPostingTask task = new PurchaseOrdeGiPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderGiDetailActivity.this);
                offlineController.clearOfflineData(functionId);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderGiDetailActivity.this);
                //Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }

            @Override
            public void bindModel(Object o) {
                displayDialog(getString(R.string.text_material_document) + ": "
                        + (String) o, AppConstants.REQUEST_SUCCEED, 1);

            }
        }, request, functionId);
        task.execute();
    }


    int deletePosition;
    @Override
    public void onCallBack(PurchaseOrderGi purchaseOrder, int position) {
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

        System.out.println("deletePosition---->" + deletePosition);
        purchaseOrderList.remove(deletePosition);
        System.out.println("purchaseOrderList---->" + JSON.toJSONString(purchaseOrderList));
        showPurchaseData();
        setCount();
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
            DialogPurchaseOrder dialogInput = new DialogPurchaseOrder(this, purchaseOrderList.get(position).getOrderItem(),
                    purchaseOrderList.get(position).getMaterial(), purchaseOrderList.get(position).getMaterialDesc(),
                    purchaseOrderList.get(position).getBatch(),
                    purchaseOrderList.get(position).getQty(), purchaseOrderList.get(position).getStorageLocation(),
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
                    purchaseOrderList.get(position).getQty(), purchaseOrderList.get(position).getStorageLocation(),
                    purchaseOrderList.get(position).getPlant(), false, functionId), REQUESTCODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK?????????????????????activity?????????????????????????????????Standard activity result:
        // operation succeeded. ????????????-1
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
                    PurchaseOrderGi po = purchaseOrderList.get(currentPosition);
                    po.setQuantityInEntryUnit(String.valueOf(snList.size()));
                    po.setSnList(snList);
                    po.setStorageLocation(result.getStorageLocation());
                    po.setBatch(result.getBatch());

                    PurchaseOrderGi parent = purchaseOrderController.findParentItem(purchaseOrderList, po);
                    if(parent != null){
                        if(parent.isSubOrder()){
                            parent.setQty(parent.getQty() + (po.getQty() - snList.size()));
                            po.setRequireQty(snList.size());
                            if(snList.size() == 0){
                                purchaseOrderList.remove(currentPosition);
                            }
                        }
                    }
                }
                purchaseOrderAdapter.notifyDataSetChanged();
                setCount();
            }
        }
    }

    @Override
    public void setInput(String item, String batch, int count, StorageLocation storageLocation, Reason reason) {
        if(purchaseOrderList != null && purchaseOrderList.size() > currentPosition){
            PurchaseOrderGi order = purchaseOrderList.get(currentPosition);
            order.setBatch(batch);
            order.setQuantityInEntryUnit(String.valueOf(count));
            order.setStorageLocation(storageLocation.getStorageLocation());
          /*  int total = purchaseOrderController.getItemQuantityTotal(purchaseOrderList, order);

            //??????????????????????????????????????????????????????
            PurchaseOrderGi findPurchaseOrderSubContract = purchaseOrderController.findParentItem(purchaseOrderList, order);
            if(findPurchaseOrderSubContract != null){
                findPurchaseOrderSubContract.setQuantityInEntryUnit(total + "");
            }*/
        }
        isAlive = true;
        purchaseOrderAdapter.notifyDataSetChanged();
        setCount();
    }
    private void setCount(){
        if(purchaseOrderList != null){
            double maxCount = purchaseOrderList.stream().mapToDouble(PurchaseOrderGi::getQty).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = purchaseOrderList.stream().mapToDouble(PurchaseOrderGi::getScanQuantity).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }
    }

    @Override
    protected void onResume() {
        isAlive = true;
        boolean isShow = purchaseOrderController.isShowCheckBtn(purchaseOrderList);

        if(isShow){
            if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
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

       /* List<String> snList = new ArrayList<>();
        Set<String> sameSet = new HashSet<>();*/
        List<SerialNumberResults> serialNumberResults = new ArrayList<>();
        int total = 0;
        for (PurchaseOrderGi order : purchaseOrderList) {
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
            postSerials(serialNumberResults);
        }
    }

    private void postSerials(List<SerialNumberResults> serials){
        waitDialog.showWaitDialog(this);
        SerialInfoPostingTask task = new SerialInfoPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(PoOrderGiDetailActivity.this);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderGiDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if(items != null && items.size() > 0){
                    List<PurchaseOrderGi> orders = purchaseOrderController.splitBatch(purchaseOrderList, items);
                    //LogUtils.d("transferOrders", "transferOrders---->" + JSON.toJSONString(transferOrders, SerializerFeature.DisableCircularReferenceDetect));
                    purchaseOrderList.clear();
                    purchaseOrderList.addAll(orders);
                    purchaseOrderAdapter.notifyDataSetChanged();
                    setCount();
                }else{
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_STAY, 1);
                }
            }
        }, serials);
        task.execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineController.clearOfflineData(AppConstants.LAST_SCAN);
    }
}