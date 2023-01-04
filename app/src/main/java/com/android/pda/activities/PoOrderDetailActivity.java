package com.android.pda.activities;


import android.app.DatePickerDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.DialogPurchaseOrder;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;

import com.android.pda.adapters.PurchaseOrderDetailAdapter;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.PurchaseOrderPostingTask;

import com.android.pda.asynctasks.PurchaseOrderReturnPostingTask;
import com.android.pda.controllers.LoginController;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.PurchaseOrderController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.Login;
import com.android.pda.database.pojo.Offline;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.database.pojo.User;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.log.LogUtils;
import com.android.pda.models.PurchaseOrderPostingRequest;
import com.android.pda.models.PurchaseOrderReturnPostingRequest;
import com.android.pda.models.Reason;
import com.android.pda.models.ScanResult;
import com.android.pda.utils.DateUtils;


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class PoOrderDetailActivity extends AppCompatActivity implements ActivityInitialization,
        PurchaseOrderDetailAdapter.SplitCallback, PurchaseOrderDetailAdapter.OnItemClickListener,
        DialogPurchaseOrder.InputCallback{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderController purchaseOrderController = app.getPurchaseOrderController();
    private List<StorageLocation> storageLocations;
    private static final String INTENT_KEY_PO = "PO";
    private ListView lvPurchase;
    private TextView orderValue;
    private TextView supplierValue;
    private EditText etDeliveryNumberValue, etConfirmDate;
    private WaitDialog waitDialog;
    private List<PurchaseOrder> purchaseOrderList;
    private PurchaseOrderDetailAdapter purchaseOrderAdapter;
    private PurchaseOrder splitPurchaseOrder;
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
    private LinearLayout llSupplier, llDeliveryNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initService();
        initIntent();
        initData();
        setCount();
    }
    public static Intent createIntent(Context context, List<PurchaseOrder> purchaseOrderList,
                                      String functionId) {
        Intent intent = new Intent(context, PoOrderDetailActivity.class);
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

    }

    @Override
    public void initData() {


        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN)){
            Offline offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
            if(offline != null){
                //System.out.println("getLogisticNumber--->" + offline.getLogisticNumber());
                etDeliveryNumberValue.setText(offline.getLogisticNumber());
            }
            getSupportActionBar().setTitle(getString(R.string.text_po_return_detail));
            tvOrderLabel.setText(getString(R.string.text_material_document_number));
            llSupplier.setVisibility(View.GONE);
            llDeliveryNumber.setVisibility(View.GONE);
            tvQuantityLabel.setText(getString(R.string.text_no_return_quantity));
            tvScanQuantityLabel.setText(getString(R.string.text_return_quantity));
            if(purchaseOrderList != null && purchaseOrderList.size() > 0){
                orderValue.setText(purchaseOrderList.get(0).getMaterialDocument());
            }

        }else{
            Offline offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER);
            if(offline != null){
                //System.out.println("getLogisticNumber--->" + offline.getLogisticNumber());
                etDeliveryNumberValue.setText(offline.getLogisticNumber());
            }
            getSupportActionBar().setTitle(getString(R.string.title_activity_po_order_detail));
            tvOrderLabel.setText(getString(R.string.title_activity_po_order));
            llSupplier.setVisibility(View.VISIBLE);
            llDeliveryNumber.setVisibility(View.VISIBLE);
            tvQuantityLabel.setText(getString(R.string.text_unreceived_quantity));
            tvScanQuantityLabel.setText(getString(R.string.text_receipt_quantity));
            if(purchaseOrderList != null && purchaseOrderList.size() > 0){
                orderValue.setText(purchaseOrderList.get(0).getPurchaseOrder());
                supplierValue.setText(purchaseOrderList.get(0).getSupplier());
            }
        }
        user = userController.getLoginUser();
        login = loginController.getLoginUser();
        storageLocations = userController.getUserLocation();
        showPurchaseData();
    }
    private void showPurchaseData() {

        purchaseOrderAdapter = new PurchaseOrderDetailAdapter(getApplicationContext(),
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
        purchaseOrderList = (List<PurchaseOrder>) this.getIntent().getSerializableExtra(INTENT_KEY_PO);
        functionId = getIntent().getStringExtra(INTENT_KEY_FUNCTION_ID);
    }

    public void post(View view){
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER)){
            String deliveryNumber = etDeliveryNumberValue.getText().toString();
            if(StringUtils.isEmpty(deliveryNumber)){
                if(user != null){
                    if(StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_company_name_a))){
                        displayDialog(getString(R.string.error_require_delivery_number), AppConstants.REQUEST_STAY, 1);
                        return;
                    }
                }
            }
            String error = purchaseOrderController.verifyData(purchaseOrderList);
            if(StringUtils.isEmpty(error)){
                PurchaseOrderPostingRequest request = purchaseOrderController.buildRequest(purchaseOrderList, deliveryNumber, etConfirmDate.getText().toString());
                LogUtils.d("PurchaseOrderPosting", "request---->" + JSON.toJSONString(request));
                postPoData(request);

            }else{
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }
        }else{
            String error = purchaseOrderController.verifyData(purchaseOrderList);
            if(StringUtils.isEmpty(error)){
                List<PurchaseOrderReturnPostingRequest> requestList = purchaseOrderController.buildReturnRequest(purchaseOrderList, etConfirmDate.getText().toString());
                postPoReturnData(requestList);

            }else{
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }

        }

    }

    private void export(){
        try {
            purchaseOrderController.exportExcel(purchaseOrderList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
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
                    if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN)){
                        offlineController.saveOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN, JSON.toJSONString(purchaseOrderList), orderValue.getText().toString(), null, etDeliveryNumberValue.getText().toString(), null, null, null);
                    }else{
                        offlineController.saveOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER, JSON.toJSONString(purchaseOrderList), orderValue.getText().toString(), null, etDeliveryNumberValue.getText().toString(), null, null, null);
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

    private void postPoData(PurchaseOrderPostingRequest request){
        waitDialog.showWaitDialog(this);
        PurchaseOrderPostingTask task = new PurchaseOrderPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderDetailActivity.this);
                if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN)){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
                }else{
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER);
                }

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderDetailActivity.this);
                //Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                displayDialog(error, AppConstants.REQUEST_FAILED, 1);
            }

            @Override
            public void bindModel(Object o) {
                displayDialog(getString(R.string.text_material_document) + ": "
                        + (String) o, AppConstants.REQUEST_SUCCEED, 1);

            }
        }, request);
        task.execute();
    }

    private void postPoReturnData(List<PurchaseOrderReturnPostingRequest> requestList){
        waitDialog.showWaitDialog(this);
        PurchaseOrderReturnPostingTask task = new PurchaseOrderReturnPostingTask(getApplicationContext(),
                new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                waitDialog.hideWaitDialog(PoOrderDetailActivity.this);
                if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN)){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN);
                }
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(PoOrderDetailActivity.this);
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
    public void onCallBack(PurchaseOrder purchaseOrder, int position) {
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

            dialogInput.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

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
                    PurchaseOrder po = purchaseOrderList.get(currentPosition);
                    po.setQuantityInEntryUnit(String.valueOf(snList.size()));
                    po.setSnList(snList);
                    po.setStorageLocation(result.getStorageLocation());
                    po.setBatch(result.getBatch());
                    if(result.getReason() != null){
                        po.setReason(result.getReason().getId());
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
            PurchaseOrder order = purchaseOrderList.get(currentPosition);
            order.setBatch(batch);
            order.setQuantityInEntryUnit(String.valueOf(count));
            order.setStorageLocation(storageLocation.getStorageLocation());
            if(reason != null){
                order.setReason(reason.getId());
            }
        }
        purchaseOrderAdapter.notifyDataSetChanged();
        setCount();
    }
    private void setCount(){
        if(purchaseOrderList != null){
            double maxCount = purchaseOrderList.stream().mapToDouble(PurchaseOrder::getOpenQuantity).sum();
            tvMaxCountValue.setText(String.valueOf(maxCount));

            double scanCount = purchaseOrderList.stream().mapToDouble(PurchaseOrder::getScanQuantity).sum();
            tvTotalCountValue.setText(String.valueOf(scanCount));
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        offlineController.clearOfflineData(AppConstants.LAST_SCAN);
    }
}