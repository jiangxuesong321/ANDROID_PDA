package com.android.pda.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.database.pojo.Material;

import java.util.List;

public class MaterialPickingPostAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Material> objects;
    private Context context;

    public MaterialPickingPostAdapter(Context context, List<Material> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public Material getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MaterialPickingPostAdapter.ViewHolder viewHolder; //viewholder的作用是因为Android有个recycler的反复循环器，viewholder就是借助他来做到循环利用itemview。
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_material_picking_post, null);
            viewHolder = new MaterialPickingPostAdapter.ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.tv_column6);

            viewHolder.column7 = convertView.findViewById(R.id.et_column7);
            viewHolder.column8 = convertView.findViewById(R.id.et_column8);
            convertView.setTag(viewHolder);
            viewHolder.column7.setTag(position);
            viewHolder.column7.clearFocus();
            viewHolder.column7.addTextChangedListener(new MaterialPickingPostAdapter.Watcher(viewHolder));

            viewHolder.column8.setTag(position);
            viewHolder.column8.clearFocus();
            viewHolder.column8.addTextChangedListener(new MaterialPickingPostAdapter.Watcher(viewHolder));
        } else {
            viewHolder = (MaterialPickingPostAdapter.ViewHolder) convertView.getTag();
        }

        if (position % 2 != 0) {
            if (position > 0) {
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        } else {
            convertView.setBackgroundColor(context.getColor(android.R.color.transparent));
        }

        viewHolder.column1.setText(Integer.toString((position + 1) * 10));
        viewHolder.column2.setText(objects.get(position).getMaterial());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getMaterialName()));
        viewHolder.column4.setText(objects.get(position).getBatch());
        viewHolder.column5.setText(String.valueOf(objects.get(position).getMatlWrhsStkQtyInMatlBaseUnit()));
        viewHolder.column6.setText(String.valueOf(objects.get(position).getStorageBin()));
        viewHolder.column7.setEnabled(true);
        viewHolder.column8.setEnabled(true);


        viewHolder.column7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setSelection(0);
                }
            }
        });
        viewHolder.column8.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setSelection(0);
                }
            }
        });


        return convertView;
    }

    public class ViewHolder {
        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;
        TextView column5;
        TextView column6;
        EditText column7;
        EditText column8;
    }

    private MaterialPickingPostAdapter.OnItemClickListener itemClickListener;

    public void setClickListener(MaterialPickingPostAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onCallBack(int position);
    }

    private MaterialPickingPostAdapter.SplitCallback splitCallback;

    public void setSplitCallback(MaterialPickingPostAdapter.SplitCallback splitCallback) {
        this.splitCallback = splitCallback;
    }

    public interface SplitCallback {
        void onSplitCallBack(Material material, int position);
    }


    private class Watcher implements TextWatcher {

        MaterialPickingPostAdapter.ViewHolder holder = null;
        boolean isBin;

        public Watcher(MaterialPickingPostAdapter.ViewHolder holder) {
            this.holder = holder;
            this.isBin = isBin;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (holder.column7.hasFocus()) {
                String storageBin = s.toString().trim();
                int position = (Integer) holder.column7.getTag();
                objects.get(position).setConfirmStorageBin(s.toString().trim());
                // 创建对话框
                checkCallback.onCallBack(position, storageBin, "storageBin");
            }
            if (holder.column8.hasFocus()) {
                String inputMaterial = s.toString().trim();
                int position = (Integer) holder.column8.getTag();
                objects.get(position).setInputMaterial(s.toString().trim());
                // 创建对话框
                checkCallback.onCallBack(position, inputMaterial, "material");
            }
        }
    }

    private CheckCallback checkCallback;

    public interface CheckCallback {
        void onCallBack(int position, String material, String type);
    }

    public void setCheckCallback(CheckCallback checkCallback) {
        this.checkCallback = checkCallback;
    }
}
