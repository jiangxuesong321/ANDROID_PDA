package com.sunmi.pda.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.models.DeliveryStatus;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeliveryStatusSpinnerAdapter extends ArrayAdapter<DeliveryStatus> {
    private LayoutInflater layoutInflater;
    private List<DeliveryStatus> objects;
    int textViewResourceId;
    final static class ViewHolder {
        public TextView textView;
    }
    public DeliveryStatusSpinnerAdapter(Context context, int textViewResourceId, List<DeliveryStatus> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.textViewResourceId = textViewResourceId;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {


        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(textViewResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.alert_dialog_data_adapter_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(StringUtils.isNotEmpty(objects.get(position).getCode())) {
            viewHolder.textView.setText(objects.get(position).getCode() + " - " + objects.get(position).getName());
        } else {
            viewHolder.textView.setText("");
        }
        return convertView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(textViewResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.alert_dialog_data_adapter_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(StringUtils.isNotEmpty(objects.get(position).getCode())) {
            viewHolder.textView.setText(objects.get(position).getCode() + " - " + objects.get(position).getName());
        }
        return convertView;
    }
}
