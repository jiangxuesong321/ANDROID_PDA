package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Material;

import java.util.List;

public class LogisticsProviderAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<LogisticsProvider> objects;
    private Context context;

    public LogisticsProviderAdapter(Context context, List<LogisticsProvider> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public LogisticsProvider getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_logistics_provider, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);

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

        viewHolder.column1.setText(objects.get(position).getZtId());
        viewHolder.column2.setText(objects.get(position).getZtName());

        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;

    }

}
