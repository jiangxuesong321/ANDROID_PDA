package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunmi.pda.R;

import com.sunmi.pda.database.pojo.StorageLocation;

import java.util.List;

public class StorageLocationAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<StorageLocation> objects;
    private Context context;

    public StorageLocationAdapter(Context context, List<StorageLocation> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public StorageLocation getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_storge_location, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
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

        viewHolder.column1.setText(objects.get(position).getPlant());
        viewHolder.column2.setText(objects.get(position).getPlantName());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getStorageLocation()));
        viewHolder.column4.setText(objects.get(position).getStorageLocationName());

        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;

    }

}
