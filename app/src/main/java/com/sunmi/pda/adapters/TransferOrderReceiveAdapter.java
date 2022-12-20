package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.models.MaterialVoucher;

import java.util.List;

public class TransferOrderReceiveAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<MaterialVoucher> objects;
    private Context context;

    public TransferOrderReceiveAdapter(Context context, List<MaterialVoucher> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public MaterialVoucher getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_transfer_order_receive_detail, null);

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
        if (position % 2 != 0) {
            if(position > 0){
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }else{
                convertView.setBackgroundColor(context.getColor(R.color.text_colorSecondPrimary));
            }
        }else{
            convertView.setBackgroundColor(context.getColor(R.color.text_colorSecondPrimary));
        }

        viewHolder.column1.setText(objects.get(position).getRspos());
        viewHolder.column2.setText(objects.get(position).getMatnr());
        viewHolder.column3.setText(objects.get(position).getMaktx());
        viewHolder.column4.setText(String.valueOf(objects.get(position).getMenge()));
        viewHolder.column5.setText(String.valueOf(objects.get(position).getBstmg()));
        viewHolder.column6.setText(String.valueOf(objects.get(position).getLgort()));
        viewHolder.column7.setText(String.valueOf(objects.get(position).getCharg()));
        viewHolder.column8.setText(String.valueOf(objects.get(position).getSerialNumber()));

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
