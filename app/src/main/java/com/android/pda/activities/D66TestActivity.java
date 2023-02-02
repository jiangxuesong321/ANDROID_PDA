package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.D66TestAdapter;
import com.android.pda.adapters.UserAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.asynctasks.D66TestTask;
import com.android.pda.controllers.PrintController;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.MaterialInfo;
import com.android.pda.utils.AppUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class D66TestActivity extends AppCompatActivity implements ActivityInitialization {

    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final String INTENT_KEY_LabelFlag = "LabelFlag";
    private EditText et_material_value;
    private String labelFlag;
    private WaitDialog waitDialog;
    private final static int REQUESTCODE = 10001;
    private TextView tv_label_material_number, tv_text_material;
    private String number;
    private List<MaterialInfo> materialInfosList = new ArrayList<>();
    private D66TestAdapter d66TestAdapter;
    private ListView lvMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d66_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initIntent();
        initView();
        initListener();
        initData();
    }

    public static Intent createIntent(Context context, String labelFlag) {
        Intent intent = new Intent(context, D66TestActivity.class);
//        intent.putExtra(INTENT_KEY_LabelFlag, labelFlag);
        return intent;
    }

    @Override
    public void initView() {
        //一个是视图的id，一个是值的id
        tv_label_material_number = findViewById(R.id.tv_label_material_number);
        et_material_value = findViewById(R.id.et_material_value);
        waitDialog = new WaitDialog();
    }


    @Override
    public void initData() {

    }

    public void search(View view) {
        if (et_material_value != null && et_material_value.getText() != null) {
            waitDialog.showWaitDialog(D66TestActivity.this);
            D66TestTask task = new D66TestTask(getApplicationContext(), new OnTaskEventListener<String>() {
                @Override
                public void onSuccess(String result) {
                    //Toast.makeText(getApplicationContext(), "SUCCESS: ", Toast.LENGTH_LONG).show();
                    waitDialog.hideWaitDialog(D66TestActivity.this);
                }

                @Override
                public void onFailure(String error) {
                    waitDialog.hideWaitDialog(D66TestActivity.this);
                    displayDialog(error, AppConstants.REQUEST_FAILED, 1);

                }

                @Override
                public void bindModel(Object o) {
                    materialInfosList = (List<MaterialInfo>) o;
                    if (materialInfosList != null && materialInfosList.size() > 0) {
                        showData(materialInfosList);
                    } else {
                        displayDialog(getString(R.string.text_service_on_result), AppConstants.REQUEST_FAILED, 1);
                    }
                }
            }, et_material_value.getText().toString());
            task.execute();
        }
    }

    public void showData(List<MaterialInfo> mList) {
//        d66TestAdapter.notifyDataSetChanged();
        lvMaterial = findViewById(R.id.lv_material);
        d66TestAdapter = new D66TestAdapter(getApplicationContext(), mList);
        this.lvMaterial.setDividerHeight(1);
        this.lvMaterial.setAdapter(d66TestAdapter);
    }


    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }


    @Override
    public void initIntent() {
        labelFlag = getIntent().getStringExtra(INTENT_KEY_LabelFlag);
    }

    public void download(View view) {
        String number = et_material_value.getText().toString();
        if (StringUtils.equalsIgnoreCase(labelFlag, PrintController.SHIPPING_LABEL)) {
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.PROPERTY_LAST_INPUT_SHIPPING_LABEL, number);
        } else {
            AppUtil.saveLastInput(getApplicationContext(), AppUtil.RECEIVE_LABEL_INPUT_RECEIVE_LABEL, number);
        }

        startActivityForResult(PrintResultActivity.createIntent(app, number, labelFlag), REQUESTCODE);
    }

    private void displayDialog(String message, int action, int buttonCount) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        //for offline
        if (AppConstants.REQUEST_OFFLINE_DATA == action) {
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }

        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {

            }

            @Override
            public void callClose() {

            }
        });
        noticeDialog.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE) {
                //Refresh Data

            }
        }
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
        return super.onOptionsItemSelected(item);
    }
}