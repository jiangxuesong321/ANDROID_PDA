package com.sunmi.pda.activities.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.models.FieldCheck;

import java.util.ArrayList;
import java.util.List;

public class DialogNoValueInput extends Dialog {
    private static final AndroidApplication _app = AndroidApplication.getInstance();
    private static final MaterialController _materialCtrl = _app.getMaterialController();

    private String _material = "";
    private String _qty = "";
    private String _batch = "";
    private String _serial = "";
    private int _position = -1;

    private EditText etMaterial, etQty, etBatch, etSerial;
    private TextView tvError;
    private Button btnOk;

    public DialogNoValueInput(@NonNull Context context, String material, String qty,String batch, String serial, int position) {
        super(context, R.style.DialogTheme);
        this._material = material;
        this._qty = qty;
        this._batch = batch;
        this._serial = serial;
        this._position = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_value_input);
        _bindViews();
        _initData();
        _bindListeners();
    }

    private void _bindViews() {
        etMaterial = findViewById(R.id.et_dnvi_material);
        etQty = findViewById(R.id.et_dnvi_qty);
        etBatch = findViewById(R.id.et_dnvi_batch);
        etSerial = findViewById(R.id.et_dnvi_serial);
        tvError = findViewById(R.id.tv_dnvi_error_msg);
        btnOk = findViewById(R.id.btn_dnvi_ok);
    }

    private void _initData() {
        etMaterial.setText(_material);
        etQty.setText(_qty);
        etBatch.setText(_batch);
        etSerial.setText(_serial);
    }

    private void _bindListeners() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _material = etMaterial.getText().toString(),
                        _qty = etQty.getText().toString(),
                        _batch = etBatch.getText().toString(),
                        _serial = etSerial.getText().toString();

                List<FieldCheck> _items = new ArrayList<>();
                _items.add(new FieldCheck(_material, R.string.error_no_value_input_material_required));
                _items.add(new FieldCheck(_qty, R.string.error_no_value_input_qty_required));
                _items.add(new FieldCheck(_batch, R.string.error_no_value_input_batch_required));

                Integer _checkResult = _inputCheck(_items);
                if (_checkResult != -1) {
                    tvError.setText(_checkResult);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }
                ;

                Material _oMtrl = _materialCtrl.getMaterialByKey(_material);
                if (_oMtrl == null) {
                    tvError.setText(R.string.error_no_value_input_no_material);
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                tvError.setVisibility(View.INVISIBLE);
                inputCallback.setInput(_material, _batch, _qty, _oMtrl.getMaterialName(), _serial, _position);
                _hideSoftInput(etMaterial);
                _hideSoftInput(etQty);
                _hideSoftInput(etBatch);
                _hideSoftInput(etSerial);
                dismiss();
            }
        });
    }

    private void _hideSoftInput(EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private Integer _inputCheck(List<FieldCheck> items) {
        for (FieldCheck item : items) {
            if (item.getValue() == null || item.getValue().isEmpty()) {
                return item.getMessage();
            }
        }
        return -1;
    }

    public interface InputCallback {
        public void setInput(String material, String batch, String qty, String desc, String serial, int position);
    }

    private InputCallback inputCallback;

    public void setInputCallback(InputCallback inputCallback) {
        this.inputCallback = inputCallback;
    }
}