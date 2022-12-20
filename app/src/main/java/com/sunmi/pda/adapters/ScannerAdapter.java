package com.sunmi.pda.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.models.PurchaseOrderResult;

import java.util.List;

public class ScannerAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<String> sn;
    private Context context;
    private boolean isCancel;
    private String materialNumber;
    private String batch;
    public ScannerAdapter(Context context, List<String> sn, String materialNumber, String batch){
        this.sn = sn;
        this.context = context;
        this.materialNumber = materialNumber;
        this.batch = batch;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return sn.size();
    }

    public String getItem(int position) {
        return sn.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_scan, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 != 0) {
            if(position > 0){
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        }else{
            convertView.setBackgroundColor(context.getColor(R.color.colorDivider));
        }
        viewHolder.column5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCallback.onCallBack(position);
            }
        });
        viewHolder.column1.setText(materialNumber);
        viewHolder.column2.setText(batch);
        viewHolder.column3.setText(sn.get(position));
        viewHolder.column4.setText("1");
        return convertView;
    }
    public class ViewHolder {
        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;
        TextView column5;
    }

    private DeleteCallback deleteCallback;

    public void setSplitCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public interface DeleteCallback {
        void onCallBack(int position);
    }
}
