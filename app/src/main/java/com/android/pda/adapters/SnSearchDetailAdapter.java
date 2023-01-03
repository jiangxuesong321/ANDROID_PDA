package com.android.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.models.SerialInfo;

import java.util.List;

public class SnSearchDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SerialInfo> objects;
    private Context context;

    public SnSearchDetailAdapter(Context context, List<SerialInfo> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public SerialInfo getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_sn_search_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.tv_column6);
            viewHolder.column7 = convertView.findViewById(R.id.tv_column7);
            viewHolder.column8 = convertView.findViewById(R.id.tv_column8);

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

        viewHolder.column1.setText(objects.get(position).getMaterial());
        viewHolder.column2.setText(objects.get(position).getMaterialDesc());
        viewHolder.column3.setText(objects.get(position).getBatch());
        viewHolder.column4.setText(objects.get(position).getSerialnumber());
        viewHolder.column5.setText(objects.get(position).getPlantdes());
        viewHolder.column6.setText(objects.get(position).getStorageLocation());
        viewHolder.column7.setText(objects.get(position).getStoragelocdes());
        viewHolder.column8.setText(objects.get(position).getQuantityinentryunit());
        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;
        TextView column5;
        TextView column6;
        TextView column7;
        TextView column8;
    }

}
