package com.sunmi.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.adapters.PrototypeBorrowDetailAdapter;
import com.sunmi.pda.adapters.ReasonSpinnerAdapter;
import com.sunmi.pda.adapters.ScannerAdapter;
import com.sunmi.pda.adapters.SpinnerAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.Reason;
import com.sunmi.pda.models.ScanResult;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class DeliveryScanActivity extends AppCompatActivity implements ActivityInitialization, ScannerAdapter.DeleteCallback {
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final ScanController scanController = app.getScanController();
    private static final UserController userController = app.getUserController();
    private static final LoginController loginController = app.getLoginController();
    private static final MaterialController materialController = app.getMaterialController();


    private Spinner spinner;
    private SpinnerAdapter spinnerAdapter;
    private List<StorageLocation> storageLocations;
    private User user;
    private Material material;
    private TextView etSnNumberValue;
    private ScannerAdapter scannerAdapter;
    private List<String> snList;
    private boolean supportTemp ;
    private ListView lvScan;
    private LinearLayout llManualAdd;
    private EditText etSnCount;
    private TextView tvTotalCountValue, tvMaxCountValue, tvBatchValue, tvPlantValue;
    private static final int ACTION_ADD = 1;
    private static final int ACTION_DELETE = 2;
    private String materialNumber;
    private String batch;
    private double maxCount;
    private int removePosition;
    private static final String INTENT_KEY_MATERIAL = "MaterialNumber";
    private static final String INTENT_KEY_BATCH = "Batch";
    private static final String INTENT_KEY_MAX_COUNT = "MaxCount";
    private static final String INTENT_KEY_STORAGE_LOCATION = "storageLocation";
    private static final String INTENT_KEY_PLANT = "Plant";
    private static final String INTENT_KEY_SN = "snList";
    private static final String INTENT_KEY_SUPPORT_TEMP = "SupportTemp";
    private static final String INTENT_KEY_FUNCTION_ID = "FunctionId";
    private String storageLocation, plant;
    public static final String INTENT_KEY_SCAN_RESULT = "ScanResult";
    private Login login;
    private Button btnAddSn;
    private String functionId;
    private LinearLayout llBatch, llPlant, llReason;
    private ReasonSpinnerAdapter reasonSpinnerAdapter;
    private Spinner spReason;
    private List<Reason> reasons = new ArrayList<>();
    private static final int ACTION_DELETE_ALL_SN = 3;
    private static final OfflineController offlineController = app.getOfflineController();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_scan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        initData();
        bindView();
    }

    private void bindView(){
        spinnerAdapter = new SpinnerAdapter(getApplicationContext(),
                R.layout.li_spinner_adapter, storageLocations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(spinnerAdapter);
        int position = storageLocationController.getStorageLocationPosition(storageLocation, storageLocations);
        spinner.setSelection(position);
        if (!userController.userHasAllLocation() && !userController.userHasAllFunction()){
            spinner.setClickable(false);
            spinner.setEnabled(false);
        }
        createScannerAdapter();

        reasonSpinnerAdapter = new ReasonSpinnerAdapter(getApplicationContext(),  R.layout.li_spinner_adapter, reasons);
        reasonSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReason.setAdapter(reasonSpinnerAdapter);
        spReason.setSelection(0);

    }

    private void createScannerAdapter(){
        scannerAdapter = null;
        scannerAdapter = new ScannerAdapter(getApplicationContext(), snList, materialNumber, batch);
        this.lvScan.setDividerHeight(1);
        this.lvScan.setAdapter(scannerAdapter);
        scannerAdapter.setSplitCallback(this);
    }

    @Override
    public void initView() {
        spinner = findViewById(R.id.spinner);
        etSnNumberValue = findViewById(R.id.et_sn_number_value);
        lvScan = findViewById(R.id.lv_scan);
        lvScan.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        tvTotalCountValue = findViewById(R.id.count);
        llManualAdd = findViewById(R.id.rl_sn_count);
        etSnCount = findViewById(R.id.et_sn_count);
        tvMaxCountValue= findViewById(R.id.tv_max_count_value);
        btnAddSn = findViewById(R.id.btn_add_sn);
        tvBatchValue = findViewById(R.id.tv_batch_value);
        tvPlantValue = findViewById(R.id.tv_plant_value);
        llBatch = findViewById(R.id.ll_batch);
        llPlant = findViewById(R.id.ll_plant);
        llReason = findViewById(R.id.ll_reason);
        spReason = findViewById(R.id.sp_reason);
        tvBatchValue.addTextChangedListener(new Watcher());
    }

    @Override
    public void initData() {
        storageLocations = userController.getUserLocation();
        storageLocations = storageLocationController.filterStorageLocationByPlant(storageLocations, plant);
        storageLocations.add(0, new StorageLocation("","", "", ""));
        LogUtils.d("plant","plant---->" + plant);
        LogUtils.d("storageLocations","storageLocations---->" + JSON.toJSONString(storageLocations));
        login = loginController.getLoginUser();
        user = userController.getUserById(login.getZuid());
        if (user != null && StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_chuantian))) {
            llManualAdd.setVisibility(View.VISIBLE);
            btnAddSn.setVisibility(View.GONE);
        } else {
            llManualAdd.setVisibility(View.GONE);
            btnAddSn.setVisibility(View.VISIBLE);
        }
        material = materialController.getMaterialByKey(materialNumber);
        if (material == null ){
            displayDialog( app.getString(R.string.error_no_material_data), AppConstants.REQUEST_BACK, 1);
            return;
        }
        if (storageLocations == null ){
            displayDialog( app.getString(R.string.error_no_location_data), -1, 1);
            return;
        }
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_COMPLETE) ) {
            getSupportActionBar().setTitle(getString(R.string.text_scan_in));
        }else{
            getSupportActionBar().setTitle(getString(R.string.text_scan_out));
        }

        tvMaxCountValue.setText(String.valueOf(maxCount));
        tvTotalCountValue.setText(String.valueOf(snList.size()));
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN)) {
            llBatch.setVisibility(View.VISIBLE);
            tvBatchValue.setText(batch);
            llPlant.setVisibility(View.VISIBLE);
            tvPlantValue.setText(plant);
            llReason.setVisibility(View.VISIBLE);
            Reason reason = new Reason("1", "质量低劣");
            Reason reason2 = new Reason("2", "不完整");
            Reason reason3 = new Reason("3", "损坏");
            reasons.add(reason);
            reasons.add(reason2);
            reasons.add(reason3);
        } else if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_IN) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS) ||
                StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_COMPLETE) ){
            llBatch.setVisibility(View.VISIBLE);
            tvBatchValue.setText(batch);
            llPlant.setVisibility(View.GONE);
            llReason.setVisibility(View.GONE);
        }else{
            llBatch.setVisibility(View.GONE);
            llPlant.setVisibility(View.GONE);
            llReason.setVisibility(View.GONE);
        }
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {
        Intent intent = getIntent();
        materialNumber = intent.getStringExtra(INTENT_KEY_MATERIAL);
        batch = intent.getStringExtra(INTENT_KEY_BATCH);
        maxCount = intent.getDoubleExtra(INTENT_KEY_MAX_COUNT, 2);
        storageLocation = intent.getStringExtra(INTENT_KEY_STORAGE_LOCATION);
        plant = intent.getStringExtra(INTENT_KEY_PLANT);
        Offline offline = offlineController.getOfflineData(AppConstants.LAST_SCAN);
        if(offline != null){
            snList = (List<String>)JSON.parseArray(offline.getOrderBody(), String.class);
        }

        supportTemp = intent.getBooleanExtra(INTENT_KEY_SUPPORT_TEMP, false);
        functionId = intent.getStringExtra(INTENT_KEY_FUNCTION_ID);
        if(snList == null){
            snList = new ArrayList<>();
        }
    }

    /**
     * //支持暂存，不验证扫描数量
     * @param view
     */
    public void confirm(View view){
        if (!supportTemp && snList.size() != maxCount){
            displayDialog( app.getString(R.string.error_count_not_match), -1, 1);
            return;
        }
        scanOver();
    }

    private void scanOver(){
        offlineController.saveOfflineData(AppConstants.LAST_SCAN, JSON.toJSONString(snList),
                "", null, "", null, null, null);
        StorageLocation storageLocation = (StorageLocation) spinner.getSelectedItem();
        Reason reason = (Reason) spReason.getSelectedItem();
        ScanResult scanResult = new ScanResult(storageLocation.getStorageLocation(), materialNumber,
                batch, reason);
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SCAN_RESULT, scanResult);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 根据输入的序列号和数量辅助添加序列号
     * 只针对川田用户
     * 序列号组成：13~16位是流水码，总共16位，前8位是条形码，后八是生产周期和流水，13-16位
     * @param view
     */
    public void onClickAdd(View view){
        int count = 0;
        String sCount = etSnCount.getText().toString();
        String sn = etSnNumberValue.getText().toString();
        if (!snList.contains(sn)) {
            List<String> codes = Util.splitCode(sn);
            int errorId = verifyCode(sn, codes);
            if(errorId >= 0){
                return;
            }
        }

        int errorId = scanController.verifyManualSN(sn,maxCount, sCount);
        if(errorId < 0){
            //满足辅助添加sn需求
            addSN(sn, sCount);
        }else{
            switch (errorId){
                case ScanController.ERROR_SN_COUNT_NOTMATCH:
                    displayDialog( app.getString(R.string.error_count_not_match), -1, 1);
                    break;
                case ScanController.ERROR_SN_EMPTY:
                    displayDialog( app.getString(R.string.error_sn_empty), -1, 1);
                    break;
                case ScanController.ERROR_SN_INVALID_LENGTH:
                    displayDialog( app.getString(R.string.error_sn_length_chuantian), -1, 1);
                    break;
                case ScanController.ERROR_SN_INVALID:
                    displayDialog( app.getString(R.string.error_sn_code_chuantian), -1, 1);
                    break;
            }
        }
    }

    private void addSN(String sn, String sCount) {
        String sn_code = sn.substring(12, 16);
        String sn_prefix = sn.substring(0, 12);
        int code=Integer.parseInt(sn_code);
        int count = Integer.parseInt(sCount);

        for (int i=1; i <= count; i++){
            int tempCode = code + i;
            String temp = Integer.toString(tempCode);
            switch (temp.length()){
                case 1:
                    temp = sn_prefix + "000" + temp;
                    break;
                case 2:
                    temp = sn_prefix + "00" + temp;
                    break;
                case 3:
                    temp = sn_prefix + "0" + temp;
                    break;
            }
            if(etSnNumberValue.hasFocus()){
                snList.add(temp);
            }

        }
        tvTotalCountValue.setText(String.valueOf(snList.size()));
        scannerAdapter.notifyDataSetChanged();
        lvScan.setSelection(scannerAdapter.getCount() -1 );
    }

    /**
     * 支持暂存
     * @param context
     * @param materialNumber
     * @param batch
     * @param maxCount
     * @param storageLocation
     * @param plant
     * @param supportTempSave 当支持暂存时，不验证扫码数量和所需数量  --- 暂时关闭该功能，客户希望保持验证数量
     * @return
     */
    public static Intent createIntent(Context context, String materialNumber,
                                      String batch, double maxCount, String storageLocation,
                                      String plant, boolean supportTempSave) {
        Intent intent = new Intent(context, DeliveryScanActivity.class);
        intent.putExtra(INTENT_KEY_MATERIAL, materialNumber);
        intent.putExtra(INTENT_KEY_BATCH, batch);
        intent.putExtra(INTENT_KEY_STORAGE_LOCATION, storageLocation);
        intent.putExtra(INTENT_KEY_PLANT, plant);
        intent.putExtra(INTENT_KEY_MAX_COUNT, maxCount);
        intent.putExtra(INTENT_KEY_SUPPORT_TEMP, supportTempSave);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * 支持暂存 被采购相关功能所调用的方法。
     * @param context
     * @param materialNumber
     * @param batch
     * @param maxCount
     * @param storageLocation
     * @param plant
     * @param supportTempSave 当支持暂存时，不验证扫码数量和所需数量  --- 暂时关闭该功能，客户希望保持验证数量
     *  @param functionId 传递功能模块ID，做界面元素显示判断依据
     * @return
     */
    public static Intent createIntent(Context context, String materialNumber,
                                      String batch, double maxCount, String storageLocation,
                                      String plant, boolean supportTempSave,
                                      String functionId) {
        Intent intent = new Intent(context, DeliveryScanActivity.class);
        intent.putExtra(INTENT_KEY_MATERIAL, materialNumber);
        intent.putExtra(INTENT_KEY_BATCH, batch);
        intent.putExtra(INTENT_KEY_STORAGE_LOCATION, storageLocation);
        intent.putExtra(INTENT_KEY_PLANT, plant);
        intent.putExtra(INTENT_KEY_MAX_COUNT, maxCount);
        intent.putExtra(INTENT_KEY_SUPPORT_TEMP, supportTempSave);
        intent.putExtra(INTENT_KEY_FUNCTION_ID, functionId);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    private void refreshData(List<String> codes, String code){
        if(codes.size() == 1){
            //System.out.println("Set Text code---" + code);
            //etSnNumberValue.setText("");
            //etSnNumberValue.setText(code);
        }else{
            etSnNumberValue.setText("");
        }
        if(etSnNumberValue.hasFocus()){
            snList.addAll(codes);
        }

        tvTotalCountValue.setText(String.valueOf(snList.size()));
        scannerAdapter.notifyDataSetChanged();
        lvScan.setSelection(scannerAdapter.getCount() -1 );
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if(action == ACTION_DELETE){
                    snList.remove(removePosition);
                    scannerAdapter.notifyDataSetChanged();
                    tvTotalCountValue.setText(String.valueOf(snList.size()));
                }
                if(action == ACTION_DELETE_ALL_SN){
                    snList.clear();
                    scannerAdapter.notifyDataSetChanged();
                    tvTotalCountValue.setText(String.valueOf(snList.size()));
                }
            }

            @Override
            public void callClose() {
                if(action == AppConstants.REQUEST_BACK){
                    if(noticeDialog != null){
                        noticeDialog.dismiss();
                    }
                    finish();
                }
            }
        });
        noticeDialog.create();

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //etSnNumberValue.setText("");
            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);
            //codes = Util.testCode();
            //获取扫描取到的最后一个条码
            if(etSnNumberValue.hasFocus()){
                verifyCode(code, codes);
            }
            //String sn = codes.stream().reduce((first, second) -> second).orElse("");


        }
    };
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    private int verifyCode (String code, List<String> codes) {
        System.out.println("matFromCode 川田--->" + JSON.toJSONString(material));
        System.out.println("matFromCode 川田--->" + JSON.toJSONString(codes));
        System.out.println("matFromCode 川田--->" + material.getBarCode());
        int errorId = scanController.verifyScanData(snList, codes, maxCount, material.getBarCode(), user.getGroup());
        if(errorId < 0){
            //没有错误，验证通过
            refreshData(codes, code);
        }else{
            switch (errorId){
                case ScanController.ERROR_REPEAT_SCAN:
                    displayDialog( app.getString(R.string.text_repeat_scan), -1, 1);
                    break;
                case ScanController.ERROR_MAX_COUNT:
                    displayDialog( app.getString(R.string.text_max_scan_count), -1, 1);
                    break;
                case ScanController.ERROR_NOT_MATCH_MATERIAL:
                    String tmp = app.getString(R.string.error_barcode_material_not_match);
                    String msg = String.format(tmp, codes.toString(), material.getBarCode());
                    displayDialog( msg, -1, 1);
                    break;
                case ScanController.ERROR_SN_LENGTH_SHANGMI:
                    displayDialog( app.getString(R.string.error_sn_length_shangmi), -1, 1);
                    break;
            }
        }
        return errorId;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                scanOver();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            scanOver();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    public void addSn(View view){
        String sn = etSnNumberValue.getText().toString();
        if(sn != null){
            sn = sn.toUpperCase(Locale.ROOT);
        }
        if (!snList.contains(sn)) {
            List<String> codes = Util.splitCode(sn);
            verifyCode(sn, codes);
        }
    }

    private class Watcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            batch = tvBatchValue.getText().toString();
            createScannerAdapter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}