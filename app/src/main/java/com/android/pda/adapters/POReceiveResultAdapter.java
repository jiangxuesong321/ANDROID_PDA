package com.android.pda.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.database.pojo.StorageLocation;

import java.util.List;

public class POReceiveResultAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<PurchaseOrder> objects;
    private Context context;
    private Spinner sp_dropdown;  //下拉列表展示
    //    private String[] storageLocations;    // 环境下拉列表
    private Spinner etSpinner;
    private List<StorageLocation> storageLocations;
    private StorageLocationAdapter adapter;
    private EditText tvColumn6;

    public POReceiveResultAdapter(Context context, List<PurchaseOrder> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public PurchaseOrder getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; //viewholder的作用是因为Android有个recycler的反复循环器，viewholder就是借助他来做到循环利用itemview。
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_poreceive_result, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.sp_column4);
            viewHolder.column5 = convertView.findViewById(R.id.et_column5);
            viewHolder.column6 = convertView.findViewById(R.id.et_column6);
            viewHolder.column7 = convertView.findViewById(R.id.et_column7);
            convertView.setTag(viewHolder);
            viewHolder.column5.setTag(position);
            viewHolder.column5.clearFocus();
            viewHolder.column5.addTextChangedListener(new Watcher(viewHolder));
            viewHolder.column4.setTag(position);
            viewHolder.column4.clearFocus();
            viewHolder.column6.setTag(position);
            viewHolder.column6.clearFocus();
            viewHolder.column6.addTextChangedListener(new Watcher(viewHolder));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.column5.clearFocus();
            viewHolder.column5.setTag(position);
            viewHolder.column4.clearFocus();
            viewHolder.column4.setTag(position);
            viewHolder.column6.clearFocus();
            viewHolder.column6.setTag(position);
        }

        if (position % 2 != 0) {
            if (position > 0) {
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        } else {
            convertView.setBackgroundColor(context.getColor(android.R.color.transparent));
        }
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MaterialDocument materialDocument = objects.get(position);
//                itemClickListener.onItemClicked(position);
//                //System.out.println("onClick---->" + position);
//            }
//        });
        /*viewHolder.column4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                objects.get(position).setBatch(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });*/

        viewHolder.column1.setText(objects.get(position).getPurchaseOrderItem());
        viewHolder.column2.setText(objects.get(position).getMaterial());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getDescription()));
//        viewHolder.column4.setAdapter(adapter);
        viewHolder.column5.setText(String.valueOf(objects.get(position).getOrderQuantity()));
        viewHolder.column4.setEnabled(true);
        viewHolder.column5.setEnabled(true);
//        if(StringUtils.isNotEmpty(objects.get(position).getSerialFlag())){
//            viewHolder.column8.setText(context.getString(R.string.text_scan));
//        }else{
//            if(objects.get(position).isSub()){
//                viewHolder.column8.setText(context.getString(R.string.text_delete));
//            }
//            viewHolder.column8.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    splitCallback.onSplitCallBack(objects.get(position), position);
//                }
//            });
//        }

        viewHolder.column5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setSelection(0);
                }
            }
        });

        viewHolder.column6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setSelection(0);
                }
            }
        });

        viewHolder.column4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String storageLocation = adapterView.getSelectedItem().toString();
                objects.get(position).setStorageLocation(storageLocation.toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return convertView;
    }

    public class ViewHolder {
        TextView column1;
        TextView column2;
        TextView column3;
        Spinner column4;
        EditText column5;
        EditText column6;
        EditText column7;
    }

    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onCallBack(int position);
    }

    private SplitCallback splitCallback;

    public void setSplitCallback(SplitCallback splitCallback) {
        this.splitCallback = splitCallback;
    }

    public interface SplitCallback {
        void onSplitCallBack(MaterialDocument materialDocument, int position);
    }


    private class Watcher implements TextWatcher {

        ViewHolder holder = null;
        boolean isBatch;

        public Watcher(ViewHolder holder) {
            this.holder = holder;
//            this.isBatch = isBatch;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (holder.column5.hasFocus()) {
                int position = (Integer) holder.column5.getTag();
                objects.get(position).setOrderQuantity(s.toString().trim());
            }

            if (holder.column6.hasFocus()) {
                int position = (Integer) holder.column6.getTag();
                objects.get(position).setSupplierBatch(s.toString().trim());
            }
            if (holder.column7.hasFocus()) {
                int position = (Integer) holder.column7.getTag();
                objects.get(position).setShelfLifeExpirationDate(s.toString().trim());
            }
        }
    }


}
