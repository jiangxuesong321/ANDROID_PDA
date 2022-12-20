package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.activities.StockTransferActivity;
import com.sunmi.pda.models.SerialInfo;

import java.util.List;

public class StockTransferAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SerialInfo> objects;
    private Context context;

    public StockTransferAdapter(Context context, List<SerialInfo> objects){
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
            convertView = layoutInflater.inflate(R.layout.li_stock_transfer, null);
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
        viewHolder.column6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCallback.onCallBack(position);
            }
        });
        TextView column1 = convertView.findViewById(R.id.tv_column1);
        TextView column2 = convertView.findViewById(R.id.tv_column2);
        TextView column3 = convertView.findViewById(R.id.tv_column3);
        TextView column4 = convertView.findViewById(R.id.tv_column4);
        TextView column5 = convertView.findViewById(R.id.tv_column5);
        TextView column6 = convertView.findViewById(R.id.tv_column6);


        column1.setText(objects.get(position).getMaterial());
        column2.setText(objects.get(position).getMaterialDesc());
        column3.setText(objects.get(position).getCount());
        column4.setText(objects.get(position).getBatch());
        column5.setText(objects.get(position).getSerialnumber());

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
        TextView column6;
    }
}
