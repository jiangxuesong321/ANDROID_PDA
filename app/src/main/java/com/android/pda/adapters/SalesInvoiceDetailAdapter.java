package com.android.pda.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.models.SalesInvoice;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SalesInvoiceDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SalesInvoice> objects;
    private Context context;
    private boolean isCancel;
    public SalesInvoiceDetailAdapter(Context context, List<SalesInvoice> objects){
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public SalesInvoice getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_sales_invoice_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.tv_column6);
            convertView.setTag(viewHolder);
            viewHolder.column4.setTag(position);
            viewHolder.column4.clearFocus();
            viewHolder.column4.addTextChangedListener(new Watcher(viewHolder));
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.column4.clearFocus();
            viewHolder.column4.setTag(position);
        }

        if (position % 2 != 0) {
            if(position > 0){
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        }else{
            convertView.setBackgroundColor(context.getColor(R.color.colorDivider));
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
                //System.out.println("onClick---->" + position);
            }
        });
        /*viewHolder.column4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                objects.get(position).setBatch(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });*/

        viewHolder.column1.setText(objects.get(position).getDeliveryDocumentItem());
        viewHolder.column2.setText(objects.get(position).getMaterial());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getMaterialDescribe()));
        viewHolder.column4.setText(objects.get(position).getBatch());
        viewHolder.column5.setText(String.valueOf(objects.get(position).getShipmentQuantity()));
        viewHolder.column6.setText(String.valueOf(objects.get(position).getScanCount()));
        if(objects.get(position).isBatchFlag()){
            viewHolder.column4.setEnabled(true);
        }else{
            viewHolder.column4.setEnabled(false);
        }
        if (objects.get(position).getMaterial() != null && objects.get(position).getMaterial().length() > 0) {
            boolean bEditable = StringUtils.equalsIgnoreCase(objects.get(position).getMaterial().substring(0, 1), "A");
            viewHolder.column4.setEnabled(!bEditable);
        }

        return convertView;
    }

    public class ViewHolder {

        TextView column1;
        TextView column2;
        TextView column3;
        EditText column4;
        TextView column5;
        TextView column6;
    }
    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    private class Watcher implements TextWatcher{

        ViewHolder holder = null;
        boolean isBatch;
        public Watcher(ViewHolder holder){
            this.holder = holder;
            this.isBatch = isBatch;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            int position = (Integer) holder.column4.getTag();
            objects.get(position).setBatch(s.toString().trim());

        }
    }

}
