package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.models.SerialInfo;

import java.util.List;

public class LendBackAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SerialInfo> objects;
    private Context context;

    public LendBackAdapter(Context context, List<SerialInfo> objects){
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
            convertView = layoutInflater.inflate(R.layout.li_lend_back, null);
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


        viewHolder.column1.setText(objects.get(position).getMaterial());
        viewHolder.column2.setText(objects.get(position).getBatch());
        viewHolder.column3.setText(objects.get(position).getSerialnumber());
        viewHolder.column4.setText("1");

        return convertView;
    }

    private DeleteCallback deleteCallback;

    public interface DeleteCallback {
        void onCallBack(int position);
    }

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public class ViewHolder {
        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;
        TextView column5;
    }
}
