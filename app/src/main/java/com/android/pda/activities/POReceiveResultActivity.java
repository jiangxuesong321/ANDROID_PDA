package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.POReceiveResultAdapter;
import com.android.pda.adapters.StorageLocationAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.POReceivePostingTask;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.database.pojo.User;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.utils.AppUtil;
import com.android.pda.utils.XmlUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description 采购入库详情页，点击确定过账的页面
 */
//public class POStorageResultActivity extends AppCompatActivity implements ActivityInitialization,
//        POStorageResultAdapter.OnItemClickListener, POStorageResultAdapter.SplitCallback, DialogInput.InputCallback {
public class POReceiveResultActivity extends AppCompatActivity implements ActivityInitialization {
    private static final String INTENT_KEY_PO_RECEIVE = "POReceive";
    private static final String INTENT_KEY_ITEM = "Item";
    private static final String INTENT_KEY_DATA = "Data";
    private final static int REQUESTCODE = 10000;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final UserController userController = app.getUserController();

    private ListView lvPOItem;
    private EditText etPONumber;
    private EditText etVendor;
    private EditText etPlant;
    private WaitDialog waitDialog;
    private User user;
    private StorageLocationAdapter locationSpinnerAdapter;
    private ListView lvStorageLocation;
    private List<StorageLocation> storageLocations;
    private EditText etItemEndTime;

    private POReceiveResultAdapter adapter;
    private List<PurchaseOrder> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poreceice_result);
//        setContentView(R.layout.li_poreceive_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
//        initIntent();

//        initListener();
        initData();
        bindView();
    }

    public static Intent createIntent(Context context, List<PurchaseOrder> poList) {
        Intent intent = new Intent(context, POReceiveResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_PO_RECEIVE, (Serializable) poList);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        //初始化行项目view
        lvPOItem = findViewById(R.id.lv_po_item);
        etPONumber = findViewById(R.id.et_po_number);
        etVendor = findViewById(R.id.et_vendor);
        etPlant = findViewById(R.id.et_po_plant);
        waitDialog = new WaitDialog();
//        etItemEndTime = findViewById(R.id.et_column6);
//        hideSoftInput(etItemEndTime);
//        lvPOItem.setDividerHeight(1);
    }

    private void showStorageLocation() {
//        storageLocations = storageLocationController.getStorageLocation();
        locationSpinnerAdapter = new StorageLocationAdapter(getApplicationContext(), storageLocations);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvStorageLocation.setDividerHeight(1);
        this.lvStorageLocation.setAdapter(adapter);
        //this.lvPurchase.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        String poNumber = AppUtil.getLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_PURCHASE_ORDER);
        etPONumber.setText(poNumber);
        user = userController.getLoginUser();
        List<PurchaseOrder> poList = (List<PurchaseOrder>) this.getIntent().getSerializableExtra(INTENT_KEY_PO_RECEIVE);
        list = poList;
        PurchaseOrder purchaseOrder = poList.get(0);
//        etPONumber.setText(purchaseOrder.getPurchaseOrder());
        etVendor.setText(purchaseOrder.getSupplierName());
        etPlant.setText(purchaseOrder.getPlant());
        /*-- 配置 工厂 - 库存地点 数据源 --*/
        storageLocations = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.storage_locations);

        try {
            storageLocations = XmlUtils.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 工厂 - 库存地点 二级联动
        String plantText = etPlant.getText().toString();
        if (StringUtils.isNotEmpty(plantText)) {
            List<StorageLocation> storageLocationArrayList = storageLocations;
            List<StorageLocation> filteredList = storageLocationArrayList.stream()
                    .filter(storageLocation -> storageLocation.getPlant().equals(plantText))
                    .collect(Collectors.toList());

            filteredList.add(0, new StorageLocation("", "", "", ""));
            adapter = new POReceiveResultAdapter(POReceiveResultActivity.this, getApplicationContext(), list, filteredList);
        }
//        adapter.setClickListener(this);
//        adapter.setSplitCallback(this);
//        lvPOItem.setAdapter(adapter);
        this.lvPOItem.setDividerHeight(1);
        this.lvPOItem.setAdapter(adapter);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {
//        etItemEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                // TODO Auto-generated method stub
//                if(hasFocus){
//                    showDatePickerDialog(1);
//                }
//            }
//        });
//        etItemEndTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                showDatePickerDialog(1);
//            }
//        });
//        etItemEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                // TODO Auto-generated method stub
//                if(hasFocus){
//                    showDatePickerDialog(2);
//                }
//            }
//        });
//        etItemEndTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                showDatePickerDialog(2);
//            }
//        });
    }

    @Override
    public void initIntent() {

    }

    private void bindView() {

    }

    /**
     * Rewrite onOptionsItemSelected 实现返回按钮功能
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 收货过账
     *
     * @param
     */
    public void confirm(View view) {
        //检查是否货位号都已经扫码
        for (PurchaseOrder purchaseOrder : list) {
            if (StringUtils.isEmpty(purchaseOrder.getStorageLocation()) || StringUtils.isEmpty(purchaseOrder.getOrderQuantity())) {
                displayDialog(getString(R.string.text_po_receive_sting_error), AppConstants.REQUEST_BACK);
                waitDialog.hideWaitDialog(POReceiveResultActivity.this);
                return;
            }
        }
        waitDialog.showWaitDialog(POReceiveResultActivity.this);
        POReceivePostingTask task = new POReceivePostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(POReceiveResultActivity.this);
                displayDialog(error, AppConstants.REQUEST_FAILED);
            }

            @Override
            public void bindModel(Object o) {
                Map<String, String> materialDocumentInfo = (Map<String, String>) o;
                // 查询参数校验（物料凭证）
                if (StringUtils.isNotEmpty(materialDocumentInfo.get("materialDocument"))) {
                    displayDialog(getString(R.string.text_to_po_receive_create_success) + materialDocumentInfo.get("materialDocument"), AppConstants.REQUEST_BACK);
//                    startActivityForResult(POStorageHomeActivity.createIntent(app), 10000);
                } else {
                    displayDialog(getString(R.string.text_to_material_doc_error) + materialDocumentInfo.get("error"), AppConstants.REQUEST_FAILED);
                }
                waitDialog.hideWaitDialog(POReceiveResultActivity.this);

            }
        }, list);
        task.execute();
    }

    private void displayDialog(String message, int action) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, 1);
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }

            @Override
            public void callClose() {
                if (AppConstants.REQUEST_BACK == action) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        noticeDialog.create();
    }

    private void hideSoftInput(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}