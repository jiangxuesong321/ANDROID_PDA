package com.sunmi.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.activities.view.WaitDialog;
import com.sunmi.pda.adapters.SnSearchDetailAdapter;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.asynctasks.SerialInfoPostingTask;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.ExcelUtils;
import com.sunmi.pda.utils.FileUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class SnSearchDetailActivity extends AppCompatActivity implements ActivityInitialization {
    private final static AndroidApplication app = AndroidApplication.getInstance();

    private static final SerialInfoController serialInfoController = app.getSerialInfoController();

    private static final String INTENT_KEY_SN = "SN";

    private ListView lvSerial;
    private List<String> snList;
    private List<SerialInfo> serialInfos = new ArrayList<>();
    private WaitDialog waitDialog;

    private SnSearchDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sn_search_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initIntent();
        initData();
        bindView();
        searchSerials();
    }

    public static Intent createIntent(Context context, List<String> snList) {
        Intent intent = new Intent(context, SnSearchDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SN, (Serializable) snList);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void initView() {
        this.lvSerial = findViewById(R.id.lv_list);
        waitDialog = new WaitDialog();

    }

    @Override
    public void initData() {
    }

    private void bindView() {

        adapter = new SnSearchDetailAdapter(getApplicationContext(), serialInfos);
        this.lvSerial.setDividerHeight(1);
        this.lvSerial.setAdapter(adapter);
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

    }

    @Override
    public void initIntent() {
        snList = (List<String>) this.getIntent().getSerializableExtra(INTENT_KEY_SN);
    }

    private void displayDialog(String message, int action, int buttonCount) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);

        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }

            @Override
            public void callClose() {
                if (action == AppConstants.REQUEST_BACK) {
                    finish();
                }
            }
        });
        noticeDialog.create();
    }

    private void searchSerials() {
        List<SerialNumberResults> serialNumberResults = serialInfoController.buildSerialNumberResults(snList);
        waitDialog.showWaitDialog(this);
        SerialInfoPostingTask task = new SerialInfoPostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(SnSearchDetailActivity.this);

            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(SnSearchDetailActivity.this);
                displayDialog(error, AppConstants.REQUEST_BACK, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                List<SerialInfo> filteredSerialInfo = serialInfoController.filterSerialInfo(items, snList);
                serialInfos.clear();
                serialInfos.addAll(filteredSerialInfo);
                adapter.notifyDataSetChanged();
            }
        }, serialNumberResults);
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        //导出数据
        if(item != null && StringUtils.equalsIgnoreCase(item.getTitle(), "sn_export")){
                if (serialInfos != null && serialInfos.size() > 0) {
                    try {
                        exportExcel(serialInfos);
                        displayDialog(getString(R.string.text_export_success), AppConstants.REQUEST_STAY,1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        displayDialog(getString(R.string.text_export_failed), AppConstants.REQUEST_BACK, 1);
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void exportExcel(List<SerialInfo> serialInfos) throws IOException, WriteException, BiffException {
        String[] title = {"物料编码", "物料描述", "批次", "序列号", "工厂描述", "库存地点", "库存地点描述", "数量"};
        //8个参数
        File file = new File(FileUtil.getSDPath() + "/Sunmi");
        FileUtil.makeDir(file);
        String fileName = "物料编码" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Sunmi/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, "物料编码");

        ExcelUtils.writeObjListToExcel(getRecordData(serialInfos), filePath);
        FileUtils.notifySystemToScan(fileXls, app);
    }

    private ArrayList<ArrayList<String>> getRecordData(List<SerialInfo> serialInfos) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < serialInfos.size(); i++) {
            SerialInfo item = serialInfos.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(String.valueOf(item.getMaterial()));
            beanList.add(String.valueOf(item.getMaterialdes()));
            beanList.add(item.getBatch());
            beanList.add(item.getSerialnumber());
            beanList.add(item.getPlantdes());
            beanList.add(item.getStorageLocation());
            beanList.add(item.getStoragelocdes());
            beanList.add(item.getQuantityinentryunit());
//                if (item.getShipmentQuantity() > 0) {
            recordList.add(beanList);
//                }
        }
        return recordList;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sn_export, menu);
        //menu.add(userController.getLoginUser().getUserId());
        return super.onCreateOptionsMenu(menu);
    }
}