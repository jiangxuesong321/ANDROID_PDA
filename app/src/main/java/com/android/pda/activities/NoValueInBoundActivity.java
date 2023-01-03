
package com.android.pda.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.android.pda.R;
import com.android.pda.activities.view.DialogNoValueInput;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.activities.view.WaitDialog;
import com.android.pda.adapters.NoValueAdapter;
import com.android.pda.adapters.SpinnerAdapter;
import com.android.pda.adapters.SpinnerAdapterPlant;
import com.android.pda.application.AppConstants;
import com.android.pda.application.AndroidApplication;
import com.android.pda.asynctasks.NoValuePostingTask;
import com.android.pda.asynctasks.SerialInfoPostingTask;
import com.android.pda.controllers.MaterialController;
import com.android.pda.controllers.NoValueController;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.ScanController;
import com.android.pda.controllers.StorageLocationController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.Offline;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.NoValueRequest;
import com.android.pda.models.SerialInfo;
import com.android.pda.models.SerialNumberResults;
import com.android.pda.utils.DateUtils;
import com.android.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoValueInBoundActivity extends AppCompatActivity implements ActivityInitialization, DialogNoValueInput.InputCallback, NoValueAdapter.CallbackDelete, NoValueAdapter.OnItemClickListener {
    private static final AndroidApplication app = AndroidApplication.getInstance();

    //    controllers
    private static final UserController userCtrl = app.getUserController();
    private static final StorageLocationController storageLocationCtrl = app.getStorageLocationController();
    private static final ScanController scanCtrl = app.getScanController();
    private static final OfflineController offlineCtrl = app.getOfflineController();
    private static final NoValueController noValueCtrl = app.getNoValueController();
    private static final MaterialController matlCtrl = app.getMaterialController();

    // constant
    private static final Integer ACTION_DELETE_ITEM = 2;
    private static final int ACTION_DELETE_ALL_SN = 3;

    //    adapters
    private SpinnerAdapterPlant spAdapterPlant;
    private SpinnerAdapter spAdapterStorageTo;
    private NoValueAdapter noValueAdapter;

    //    components
    private Spinner spPlant, spStorageTo;
    private EditText etConfirmDate;
    private ListView lvSerial;
    private WaitDialog waitDialog;

    //    data
    private List<StorageLocation> plants, storages;

    //
    private boolean dialogIsOpen;
    private List<String> snList = new ArrayList<>();
    private List<SerialInfo> serialInfos = new ArrayList<>();
    private int itemToRemove = -1;
    private List<SerialNumberResults> postSerials = new ArrayList<>();
    private Offline offline;
    private StorageLocation _emptySL= new StorageLocation(" ", "", "", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_value_in_bound);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initData();
        initListener();
        initIntent();
        _bindPlantView();
        _bindListView();

        if (offline != null) {
            displayDialog(getString(R.string.text_offline_warning), AppConstants.OFFLINE_DATE_EXISTING, 2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    public void initView() {
        waitDialog = new WaitDialog();

        spPlant = findViewById(R.id.sp_nvi_plant);
        spStorageTo = findViewById(R.id.sp_nvi_storage_to);
        etConfirmDate = findViewById(R.id.et_nvi_confirm_date);
        _hideSoftInput(etConfirmDate);
        lvSerial = findViewById(R.id.lv_nvi_material_voucher);
    }

    @Override
    public void initData() {
        plants = userCtrl.getUserPlants();
        storages = userCtrl.getUserLocation();
        etConfirmDate.setText(DateUtils.getDateString());
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {
        etConfirmDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    _showDatePicker();
                }
            }
        });
        etConfirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDatePicker();
            }
        });
    }

    @Override
    public void initIntent() {
        offline = offlineCtrl.getOfflineData(AppConstants.FUNCTION_ID_NO_VALUE_IN_BOUND);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                displayDialog(getString(R.string.text_offline_not_finished), AppConstants.REQUEST_OFFLINE_DATA, 2);
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
    public void setInput(String material, String batch, String qty, String desc, String serial, int position) {
        if (position == -1) {
            SerialInfo _serial = new SerialInfo("", material, desc, batch, serial, "");
            _serial.setCount(qty);
            serialInfos.add(_serial);
            snList.add("");
        } else {
            SerialInfo _si = serialInfos.get(position);
            _si.setMaterial(material);
            _si.setMaterialDesc(desc);
            _si.setBatch(batch);
            _si.setCount(qty);
            _si.setSerialnumber(serial);
        }
        noValueAdapter.notifyDataSetChanged();
        dialogIsOpen = false;
    }

    @Override
    public void onItemClick(int position) {
        SerialInfo _si = serialInfos.get(position);
        _displayDialogInput(_si.getMaterial(), _si.getCount(), _si.getBatch(), _si.getSerialnumber(), position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            displayDialog(getString(R.string.text_offline_not_finished), AppConstants.REQUEST_OFFLINE_DATA, 2);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDeleteListItem(int position) {
        itemToRemove = position;
        displayDialog(app.getString(R.string.text_confirm_delete), ACTION_DELETE_ITEM, 2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, NoValueInBoundActivity.class);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstants.ACTION_DATA_CODE_RECEIVED);
        registerReceiver(receiver, filter);
    }

    public void add(View view) {
        _displayDialogInput("", "", "", "", -1);
    }

    private void _displayDialogInput(String material, String qty, String batch, String serial, int position) {
        DialogNoValueInput _dialog = new DialogNoValueInput(this, material, qty, batch, serial, position);
        _dialog.setInputCallback(this);
        _dialog.show();
        _dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogIsOpen = false;
            }
        });
        dialogIsOpen = true;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (dialogIsOpen == true) {
                return;
            }

            String code = intent.getStringExtra(AppConstants.DATA);
            List<String> codes = Util.splitCode(code);
            Integer errorId = scanCtrl.verifyDupScanData(snList, codes);
            if (errorId < 0) {
                for (String serial : codes) {
                    snList.add(serial);
                    String _barCode = StringUtils.substring(serial, 0, 4);
                    Material _material = matlCtrl.getMaterialByBarCode(_barCode);
                    SerialInfo info;
                    if (_material != null) {
                        info = new SerialInfo(_material.getMaterial(), _material.getMaterialName(), serial);
                    } else {
                        info = new SerialInfo(serial);
                    }
                    info.setCount("1");
                    serialInfos.add(info);
                }
                noValueAdapter.notifyDataSetChanged();
            } else {
                switch (errorId) {
                    case ScanController.ERROR_REPEAT_SCAN:
                        displayDialog(app.getString(R.string.text_repeat_scan), -1, 1);
                        break;
                }
            }
        }
    };

    public void check(View view) {
        postSerials.clear();
        for (String sn : snList) {
            if (sn.length() > 0) {
                SerialNumberResults _serial = new SerialNumberResults(sn);
                postSerials.add(_serial);
            }
        }

        if (postSerials.size() == 0) {
            displayDialog(getString(R.string.error_no_serial), AppConstants.REQUEST_STAY, 1);
            return;
        }

        _checkSerials(postSerials);
    }

    public void post(View view) {
        if (serialInfos == null || serialInfos.size() == 0) {
            displayDialog(getString(R.string.error_no_serial), AppConstants.REQUEST_STAY, 1);
            return;
        }

        //verify if serial has been checked
        StringBuilder sb = new StringBuilder();
        for (SerialInfo serialInfo : serialInfos) {
            if (serialInfo.getMaterial() == null || serialInfo.getMaterial().isEmpty()) {
                sb.append(serialInfo.getSerialnumber()).append("; ");
            }
        }
        if (sb.toString().length() > 0) {
            displayDialog(getString(R.string.error_check_serial_first) + ": " + sb.toString(), AppConstants.REQUEST_STAY, 1);
            return;
        }

        StorageLocation _sl = (StorageLocation) spStorageTo.getSelectedItem();
        StorageLocation _plant = (StorageLocation) spPlant.getSelectedItem();

        NoValueRequest request = noValueCtrl.generateRequest(serialInfos, _sl.getStorageLocation(), "", _plant.getPlant(), etConfirmDate.getText().toString(), "11", "511");
        _postAction(request);
    }

    private void displayDialog(String msg, Integer action, Integer btnCount) {
        NoticeDialog nDialog = new NoticeDialog(this, msg, btnCount);
        if (action == AppConstants.REQUEST_OFFLINE_DATA) {
            nDialog.setPositiveButtonText(getString(R.string.text_yes));
            nDialog.setNegativeButtonText(getString(R.string.text_no));
        }

        if (action == AppConstants.OFFLINE_DATE_EXISTING) {
            nDialog.setPositiveButtonText(getString(R.string.text_continue));
            nDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }

        nDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                if (action == ACTION_DELETE_ITEM) {
                    snList.remove(itemToRemove);
                    serialInfos.remove(itemToRemove);
                    noValueAdapter.notifyDataSetChanged();
                }

                if (action == AppConstants.REQUEST_OFFLINE_DATA) {
                    StorageLocation _plant = (StorageLocation) spPlant.getSelectedItem();
                    StorageLocation _storageTo = (StorageLocation) spStorageTo.getSelectedItem();
                    offlineCtrl.saveOfflineData(AppConstants.FUNCTION_ID_NO_VALUE_IN_BOUND, JSON.toJSONString(serialInfos), "", null, "", _plant.getPlant(), _storageTo, null);
                    finish();
                }

                if (action == AppConstants.OFFLINE_DATE_EXISTING) {
                    if (offline != null) {
                        _bindPlantView();
                        List<SerialInfo> _tempSerials = (List<SerialInfo>) JSON.parseArray(offline.getOrderBody(), SerialInfo.class);
                        for (SerialInfo serialInfo : _tempSerials) {
                            snList.add(serialInfo.getSerialnumber());
                            serialInfos.add(serialInfo);
                        }
                        noValueAdapter.notifyDataSetChanged();
                    }
                }
                if (action == ACTION_DELETE_ALL_SN) {
                    snList.clear();
                    serialInfos.clear();
                    noValueAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void callClose() {
                switch (action) {
                    case AppConstants.REQUEST_SUCCEED:
                        clearData();
                        break;
                    case AppConstants.REQUEST_BACK:
                    case AppConstants.REQUEST_OFFLINE_DATA:
                        finish();
                        break;
                    case AppConstants.OFFLINE_DATE_EXISTING:
                        offline = null;
                        offlineCtrl.clearOfflineData(AppConstants.FUNCTION_ID_NO_VALUE_IN_BOUND);
                        _bindPlantView();
                        break;
                    default:
                        break;
                }
            }
        });
        nDialog.create();
    }

    private void clearData() {
        snList.clear();
        serialInfos.clear();
        noValueAdapter.notifyDataSetChanged();
    }

    private void _hideSoftInput(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void _bindPlantView() {
        spAdapterPlant = new SpinnerAdapterPlant(getApplicationContext(), R.layout.li_spinner_adapter, plants);
        spAdapterPlant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlant.setAdapter(spAdapterPlant);
        spPlant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                _bindStorageView((StorageLocation) parent.getSelectedItem());
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int index = 0;
        StorageLocation _plant = null;
//        String _defaultPlant = offline == null ? "110Z" : offline.getPlant();
        String _defaultPlant = "110Z";
        for (int i = 0; i < plants.size(); i++) {
            if (StringUtils.equalsIgnoreCase(plants.get(i).getPlant(), _defaultPlant)) {
                _plant = plants.get(i);
                index = i;
                break;
            }
        }

        spPlant.setEnabled(false);
        if(_plant != null) {
            spPlant.setSelection(index);
            _bindStorageView(_plant);
        } else {
            List<StorageLocation> _empty = new ArrayList<>();
            _empty.add(_emptySL);
            spPlant.setAdapter(new SpinnerAdapterPlant(getApplicationContext(), R.layout.li_spinner_adapter, _empty));
            _bindStorageView(_emptySL);
            displayDialog(getString(R.string.error_no_value_no_authorization), AppConstants.REQUEST_BACK, 1);
        }
    }

    private void _bindStorageView(StorageLocation plant) {
        if(!plant.getPlant().equalsIgnoreCase(" ")) {
            List<StorageLocation> _storages = storageLocationCtrl.filterStorageLocationByPlant(storages, plant.getPlant());
            spAdapterStorageTo = new SpinnerAdapter(getApplicationContext(), R.layout.li_spinner_adapter, _storages);
            spAdapterStorageTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStorageTo.setAdapter(spAdapterStorageTo);
            if (offline != null) {
                int index = storageLocationCtrl.getStorageLocationPosition(offline.getSendLocation(), _storages);
                spStorageTo.setSelection(index);
                spAdapterStorageTo.notifyDataSetChanged();
            }
        }else {
            List<StorageLocation> _empty = new ArrayList<>();
            _empty.add(_emptySL);
            spStorageTo.setAdapter(new SpinnerAdapterPlant(getApplicationContext(), R.layout.li_spinner_adapter, _empty));
        }

    }

    private void _bindListView() {
        noValueAdapter = new NoValueAdapter(getApplicationContext(), serialInfos);
        lvSerial.setDividerHeight(1);
        lvSerial.setAdapter(noValueAdapter);
        noValueAdapter.setDeleteItem(this);
        noValueAdapter.setItemClickListener(this);
    }

    private void _showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "-" + DateUtils.getMonthOrDate(monthOfYear + 1) + "-" + DateUtils.getMonthOrDate(dayOfMonth);
                etConfirmDate.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void _checkSerials(List<SerialNumberResults> serials) {
        waitDialog.showWaitDialog(this);
        SerialInfoPostingTask task = new SerialInfoPostingTask(getApplicationContext(), new OnTaskEventListener() {
            @Override
            public void onSuccess(Object object) {
                waitDialog.hideWaitDialog(NoValueInBoundActivity.this);
                offlineCtrl.clearOfflineData(AppConstants.FUNCTION_ID_NO_VALUE_IN_BOUND);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(NoValueInBoundActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                List<SerialInfo> items = (List<SerialInfo>) o;
                if (items != null && items.size() > 0) {
                    for (SerialInfo si : serialInfos) {
                        for (int i = 0; i < items.size(); i++) {
                            SerialInfo _item = items.get(i);
                            if (StringUtils.equalsIgnoreCase(si.getSerialnumber(), _item.getSerialnumber())) {
                                si.setBatch(_item.getBatch());
                                si.setMaterial(_item.getMaterial());
                                si.setMaterialDesc(_item.getMaterialDesc());
                                si.setBatch(_item.getBatch());
                                si.setPlant(_item.getPlant());
                                si.setCount("1");
                                items.remove(i);
                            }
                        }
                    }
                    noValueAdapter.notifyDataSetChanged();
                } else {
                    displayDialog(getString(R.string.text_service_no_result), AppConstants.REQUEST_STAY, 1);
                }
            }
        }, serials);
        task.execute();
    }

    private void _postAction(NoValueRequest request) {
        waitDialog.showWaitDialog(this);
        NoValuePostingTask task = new NoValuePostingTask(getApplicationContext(), new OnTaskEventListener<String>() {
            @Override
            public void onSuccess(String result) {
                waitDialog.hideWaitDialog(NoValueInBoundActivity.this);
            }

            @Override
            public void onFailure(String error) {
                waitDialog.hideWaitDialog(NoValueInBoundActivity.this);
                displayDialog(error, AppConstants.REQUEST_STAY, 1);
            }

            @Override
            public void bindModel(Object o) {
                displayDialog(getString(R.string.text_material_document) + ": " + (String) o, AppConstants.REQUEST_SUCCEED, 1);
            }
        }, request);
        task.execute();
    }

    /**
     * 删除菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


}