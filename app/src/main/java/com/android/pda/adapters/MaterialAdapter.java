package com.android.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.database.pojo.Material;


import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MaterialAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Material> objects;
    private Context context;

    public MaterialAdapter(Context context, List<Material> objects){
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
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_material, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.tv_column6);
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
        viewHolder.column2.setText(objects.get(position).getMaterialName());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getUnit()));
        viewHolder.column4.setText(StringUtils.equalsIgnoreCase("1", objects.get(position).getBatchFlag()) ? "X" : "");
        viewHolder.column5.setText(String.valueOf(objects.get(position).getSerialFlag()));
        viewHolder.column6.setText(String.valueOf(objects.get(position).getBarCode()));
        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;
        TextView column5;
        TextView column6;
    }

}
