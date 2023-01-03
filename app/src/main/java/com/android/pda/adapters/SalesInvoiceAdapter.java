package com.android.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.models.SalesInvoiceResult;

import java.util.List;

public class SalesInvoiceAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SalesInvoiceResult> objects;
    private Context context;
    private boolean isCancel;
    public SalesInvoiceAdapter(Context context, List<SalesInvoiceResult> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public SalesInvoiceResult getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_po_order, null);
        if (position % 2 != 0) {
            if(position > 0){
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        }else{
            convertView.setBackgroundColor(context.getColor(R.color.colorDivider));
        }
        TextView column1 = convertView.findViewById(R.id.tv_column1);
        TextView column2 = convertView.findViewById(R.id.tv_column2);
        TextView column3 = convertView.findViewById(R.id.tv_column3);


        column1.setText(objects.get(position).getDeliveryDocument());
        column2.setText(objects.get(position).getDownloadDocumentTime());
        column3.setText(String.valueOf(objects.get(position).getDeliveryDone()));


        return convertView;
    }
}
