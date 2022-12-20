/**
 * 借机还回
 */
package com.sunmi.pda.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.LendBackAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.asynctasks.SerialInfoPostingTask;
import com.sunmi.pda.asynctasks.StockTransferPostingTask;
import com.sunmi.pda.controllers.LendBackController;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.BusinessOrderQuery;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LendBackActivity extends AppCompatActivity implements ActivityInitialization, LendBackAdapter.DeleteCallback {

    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final LendBackController controller = app.getLendBackController();
    private static final UserController userController = app.getUserController();
    private static final ScanController scanController = app.getScanController();
    private static final LoginController loginController = app.getLoginController();

    private final static int REQUESTCODE_RECEIVE = 20002;
    private static final String INTENT_KEY_STOCK_TRANSFER = "SockTransfer";
    public static final String INTENT_KEY_SCAN_RESULT = "ScanResult";
    private static final int ACTION_ADD = 1;
    private static final int ACTION_DELETE = 2;
    private static final int ACTION_DELETE_ALL_SN = 3;

    private ListView lvSerial;

    private EditText etSnNumberValue;

    private int removePosition;

    private User user;
    private Login loginUser;


    private WaitDialog waitDialog;
    private List<String> snList = new ArrayList<>();
    private List<SerialNumberResults> postSerials = new ArrayList<>();
    private List<SerialInfo> serialInfos = new ArrayList<>();
//    private List<GeneralMaterialDocumentItemResults> postItems;
    private LendBackAdapter adapter;

    private static final OfflineController offlineController = app.getOfflineController();
    private Offline offline;
    private TextView tvMaxCountValue, tvTotalCountValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        initData();
        bindView();
        if(offline != null){
            displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    public static Intent createIntent(Context context ) {
        Intent intent = new Intent(context, LendBackActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        this.lvSerial = findViewById(R.id.lv_list);
        waitDialog = new WaitDialog();
        etSnNumberValue = findViewById(R.id.et_sn_number_value);
        tvMaxCountValue = findViewById(R.id.tv_max_count_value);
        tvTotalCountValue = findViewById(R.id.tv_total_count_value);
    }

    @Override
    public void initData() {
        user = userController.getLoginUser();
        loginUser = loginController.getLoginUser();
        offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_LEND_BACK);
    }

    private void bindView(){

        adapter = new LendBackAdapter(getApplicationContext(), serialInfos);
        this.lvSerial.setDividerHeight(1);
        this.lvSerial.setAdapter(adapter);
        adapter.setDeleteCallback(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_RECEIVE) {
                etSnNumberValue.setText("");
                snList.clear();
                postSerials.clear();
                serialInfos.clear();
                adapter.notifyDataSetChanged();
                setCount();
            }
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

    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if(action == ACTION_DELETE){

                    snList.remove(removePosition);
                    postSerials.remove(removePosition);
                    serialInfos.remove(removePosition);
                    adapter.notifyDataSetChanged();
                    setCount();
                }
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    startActivityForResult(LendBacDetailActivity.createIntent(getApplicationContext(), serialInfos, offline), REQUESTCODE_RECEIVE);
                }
                if(action == ACTION_DELETE_ALL_SN){
                    snList.clear();
                    postSerials.clear();
                    serialInfos.clear();
                    adapter.notifyDataSetChanged();
                    setCount();
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
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_LEND_BACK);
                    /*String number = etPrototypeBorrowValue.getText().toString();
                    BusinessOrderQuery query = new BusinessOrderQuery(number);
                    startActivityForResult(PrototypeBorrowDetailActivity.createIntent(app, query, null), REQUESTCODE);*/
                }
            }
        });
        noticeDialog.create();
    }

    public void checkSerial(View view){
       if (postSerials.size() > 0) {
           postSerials(postSerials);
       } else {
           displayDialog(getString(R.string.error_no_serial), AppConstants.REQUEST_STAY,1);
       }
    }

    private void postSerials(List<SerialNumberResults> serials){
        waitDialog.showWaitDialog(this);
        SerialInfoPostingTask task = new SerialInfoPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(LendBackActivity.this);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(LendBackActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if(items != null && items.size() > 0){
                    serialInfos.clear();
                    serialInfos.addAll(items);
                    adapter.notifyDataSetChanged();
                    lvSerial.setSelection(adapter.getCount() -1 );
                    setCount();
                }else{
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_STAY, 1);
                }
            }
        }, serials);
        task.execute();
    }

    public void ok(View view){
        if (serialInfos.size() > 0) {

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

            offline = offlineController.getOfflineData(AppConstants.FUNCTION_ID_LEND_BACK);
            if(offline != null){
                displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
            }else{
                startActivityForResult(LendBacDetailActivity.createIntent(getApplicationContext(), serialInfos, null), REQUESTCODE_RECEIVE);
            }
            //List<GeneralMaterialDocumentItemResults> itemResults = controller.getItems(serialInfos, null);
            //System.out.println("itemResults--->" + JSON.toJSONString(itemResults));

        } else {
            displayDialog(getString(R.string.error_no_serial), AppConstants.REQUEST_STAY,1);
        }
    }

    private void postData(GeneralPostingRequest request){
        waitDialog.showWaitDialog(this);
        StockTransferPostingTask task = new StockTransferPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(LendBackActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(LendBackActivity.this);
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
                finish();
                break;
            case R.id.action_menu_delete:
                //删除所有条码
                if (snList.size() > 0) {
                    displayDialog(getString(R.string.text_confirm_delete_all_sn), ACTION_DELETE_ALL_SN, 2);
                } else {
                    displayDialog(getString(R.string.error_no_serial_for_delete), -1, 1);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    //******* Scan *******//

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            etSnNumberValue.setText("");
            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);

            fillSn(codes);

            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        }
    };

    private void fillSn(List<String> codes){
        int errorId = scanController.verifyDupScanData(snList, codes);
        if(errorId < 0){
            if(codes != null && codes.size() > 0){
                if(codes.size() == 1){
                    // etSnNumberValue.setText("");
                    //etSnNumberValue.setText(codes.get(0));
                }
                for (String serialNr: codes) {
                    snList.add(serialNr);
                    SerialNumberResults serial = new SerialNumberResults(serialNr);
                    postSerials.add(serial);
                    SerialInfo serialInfo = new SerialInfo(serialNr);
                    serialInfos.add(serialInfo);
                }
            }
            adapter.notifyDataSetChanged();
            lvSerial.setSelection(adapter.getCount() -1 );
            setCount();
            String sn = codes.stream().reduce((first, second) -> second).orElse("");
            etSnNumberValue.setText("");
            etSnNumberValue.setText(sn);
        }else{
            switch (errorId){
                case ScanController.ERROR_REPEAT_SCAN:
                    displayDialog( app.getString(R.string.text_repeat_scan), -1, 1);
                    break;
            }
        }}

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

    private void setCount(){
        if(serialInfos != null){
            int maxCount = serialInfos.size();
            tvMaxCountValue.setText(String.valueOf(maxCount));
            tvTotalCountValue.setText(String.valueOf(maxCount));
        }
    }

    public void addSn(View view){
        String sn = etSnNumberValue.getText().toString();
        if(sn != null){
            sn = sn.toUpperCase(Locale.ROOT);
        }
        List<String> codes = Util.splitCode(sn);
        fillSn(codes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}