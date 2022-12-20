package com.sunmi.pda.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.models.PrototypeBorrow;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PrototypeBorrowDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<PrototypeBorrow> objects;
    private Context context;

    public PrototypeBorrowDetailAdapter(Context context, List<PrototypeBorrow> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public PrototypeBorrow getItem(int position) {
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
                //System.out.println("onClick---->" + position);
            }
        });

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
        void onSplitCallBack(PrototypeBorrow prototypeBorrow, int position);
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
