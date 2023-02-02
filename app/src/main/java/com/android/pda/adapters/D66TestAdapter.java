package com.android.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.models.MaterialInfo;

import java.util.List;

public class D66TestAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<MaterialInfo> objects;
    private Context context;

    public D66TestAdapter(Context context, List<MaterialInfo> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public MaterialInfo getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        D66TestAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_user, null);
            viewHolder = new D66TestAdapter.ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (D66TestAdapter.ViewHolder) convertView.getTag();
        }

        if (position % 2 != 0) {
            if (position > 0) {
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        } else {
            convertView.setBackgroundColor(context.getColor(R.color.colorDivider));
        }

        viewHolder.column1.setText(objects.get(position).getMatnr());
        viewHolder.column2.setText(objects.get(position).getMtart());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getMeins()));
        viewHolder.column4.setText(objects.get(position).getMeins());

        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;

    }
}
