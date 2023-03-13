package com.android.pda.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.database.pojo.Material;

import java.util.List;

public class MaterialPickingResultAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Material> objects;
    private Context context;

    public MaterialPickingResultAdapter(Context context, List<Material> objects) {
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
        ViewHolder viewHolder; //viewholder的作用是因为Android有个recycler的反复循环器，viewholder就是借助他来做到循环利用itemview。
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_material_picking_result, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.cb_column6);
            convertView.setTag(viewHolder);
            viewHolder.column6.setTag(position);
            viewHolder.column6.clearFocus();
            viewHolder.column6.addTextChangedListener(new Watcher(viewHolder));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
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

//        viewHolder.column6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    ((EditText) v).setSelection(0);
//                }
//            }
//        });
        viewHolder.column6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    objects.get(position).setBatchFlag("choose");
                } else {
                    objects.get(position).setBatchFlag("");
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
        CheckBox column6;
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
        void onSplitCallBack(Material material, int position);
    }


    private class Watcher implements TextWatcher {

        ViewHolder holder = null;
        boolean isBin;

        public Watcher(ViewHolder holder) {
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
            if (holder.column6.hasFocus()) {
                int position = (Integer) holder.column6.getTag();
//                objects.get(position).setStorageBin(s.toString().trim());
            }
        }
    }


}
