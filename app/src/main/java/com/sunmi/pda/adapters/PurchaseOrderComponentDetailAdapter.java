package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sunmi.pda.R;

import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;

import com.sunmi.pda.models.Component;
import com.sunmi.pda.models.PurchaseOrderSubContract;

import java.util.List;

public class PurchaseOrderComponentDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Component> objects;
    private Context context;

    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();

    public PurchaseOrderComponentDetailAdapter(Context context, List<Component> objects){
        this.objects = objects;
        this.context = context;

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public Component getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_po_order_component_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);

            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.et_column6);
            viewHolder.column4 = convertView.findViewById(R.id.et_column4);
            viewHolder.column7 = convertView.findViewById(R.id.tv_column7);

            viewHolder.spinner = convertView.findViewById(R.id.spinner);
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
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position);

            }
        });


        viewHolder.column1.setText(objects.get(position).getPurchaseOrderItem());
        viewHolder.column2.setText(objects.get(position).getComponentNo());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getComponentDesc()));

        viewHolder.column4.setText(objects.get(position).getBatch());
        viewHolder.column5.setText(String.valueOf(objects.get(position).getOpenQuantity()));

        viewHolder.column6.setText(String.valueOf(objects.get(position).getQuantity()));

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
        Spinner spinner;
    }



    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
