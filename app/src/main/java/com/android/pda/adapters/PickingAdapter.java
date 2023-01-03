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
import com.android.pda.models.Picking;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PickingAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Picking> objects;
    private Context context;

    public PickingAdapter(Context context, List<Picking> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public Picking getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_lend, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.tv_column6);
            viewHolder.column7 = convertView.findViewById(R.id.tv_column7);

            convertView.setTag(viewHolder);
            viewHolder.column4.setTag(position);
            viewHolder.column4.clearFocus();
            viewHolder.column4.addTextChangedListener(new Watcher(viewHolder));
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.column4.clearFocus();
            viewHolder.column4.setTag(position);
        }

        if (position % 2 != 0) {
            if(position > 0){
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        }else{
            convertView.setBackgroundColor(context.getColor(R.color.colorDivider));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position);

            }
        });
        /*if(StringUtils.isEmpty(objects.get(position).getSerialFlag())){
            viewHolder.column7.setVisibility(View.VISIBLE);
        }else {
            viewHolder.column7.setVisibility(View.INVISIBLE);
        }*/
        if(StringUtils.isNotEmpty(objects.get(position).getSerialFlag())){
            viewHolder.column7.setText(context.getString(R.string.text_scan));
        }else{
            if(objects.get(position).isSub()){
                viewHolder.column7.setText(context.getString(R.string.text_delete));
            }
            viewHolder.column7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    splitCallback.onSplitCallBack(objects.get(position), position);
                }
            });
        }
       /* viewHolder.column4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                objects.get(position).setBatch(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });*/


        /*viewHolder.column6.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(StringUtils.isEmpty(s.toString())){
                    objects.get(position).setScanQuantity(0);
                }else{
                    objects.get(position).setScanQuantity(Integer.valueOf(s.toString()));
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });*/

        viewHolder.column1.setText(objects.get(position).getReservedItemNo());
        viewHolder.column2.setText(objects.get(position).getMaterial());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getMaterialDescription()));
        viewHolder.column4.setText(objects.get(position).getBatch());
        if(objects.get(position).isBatchFlag()){
            viewHolder.column4.setEnabled(true);
        }else{
            viewHolder.column4.setEnabled(false);
        }
        viewHolder.column5.setText(String.valueOf(objects.get(position).getQuantity()));
        viewHolder.column6.setText(String.valueOf(objects.get(position).getScanQuantity()));
        /*if(StringUtils.equalsIgnoreCase(objects.get(position).getSerialFlag(), "X")){
            viewHolder.column4.setEnabled(false);
            viewHolder.column6.setEnabled(false);
        }else{
            viewHolder.column4.setEnabled(true);
            viewHolder.column6.setEnabled(true);
        }*/
        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;
        TextView column3;
        EditText column4;
        TextView column5;
        TextView column6;
        TextView column7;
    }
    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    private SplitCallback splitCallback;

    public void setSplitCallback(SplitCallback splitCallback) {
        this.splitCallback = splitCallback;
    }

    public interface SplitCallback {
        void onSplitCallBack(Picking picking, int position);
    }

    private class Watcher implements TextWatcher{

        ViewHolder holder = null;
        boolean isBatch;
        public Watcher(ViewHolder holder){
            this.holder = holder;
            this.isBatch = isBatch;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            int position = (Integer) holder.column4.getTag();
            objects.get(position).setBatch(s.toString().trim());

        }
    }

}
