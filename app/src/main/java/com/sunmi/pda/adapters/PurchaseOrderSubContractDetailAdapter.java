package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.models.PurchaseOrderSubContract;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderSubContractDetailAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<PurchaseOrderSubContract> objects;
    private Context context;
    private boolean isCancel;
    private List<StorageLocation> storageLocations, filterdStorageLocations;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();
    private static final UserController userController = app.getUserController();
    private Login login;
    private String functionId;
    public PurchaseOrderSubContractDetailAdapter(Context context, List<PurchaseOrderSubContract> objects, List<StorageLocation> storageLocations,
                                                 Login login, String functionId){
        this.objects = objects;
        this.context = context;
        this.storageLocations = storageLocations;
        this.filterdStorageLocations = new ArrayList<>();
        this.login = login;
        this.functionId = functionId;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public PurchaseOrderSubContract getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.li_po_order_sub_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);

            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.et_column6);
            viewHolder.column4 = convertView.findViewById(R.id.et_column4);
            viewHolder.column7 = convertView.findViewById(R.id.tv_column7);
            viewHolder.column7Sub = convertView.findViewById(R.id.tv_column7_sub);

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


        viewHolder.column7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitCallback.onCallBack(objects.get(position), position);
            }
        });

        viewHolder.column7Sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subCallback.onSubCallBack(objects.get(position), position);
            }
        });
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_IN)){
            viewHolder.column7Sub.setVisibility(View.VISIBLE);
            viewHolder.column1.setVisibility(View.VISIBLE);
        }else{
            viewHolder.column7Sub.setVisibility(View.GONE);
            viewHolder.column1.setVisibility(View.GONE);
        }
        viewHolder.column1.setText(objects.get(position).getPurchaseOrderItem());
        viewHolder.column2.setText(objects.get(position).getMaterial());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getPurchaseOrderItemText()));
        if(objects.get(position).isSubOrder()){
            //viewHolder.column5.setVisibility(View.INVISIBLE);
            viewHolder.column7.setText(context.getString(R.string.text_delete));
        }else{
            viewHolder.column5.setVisibility(View.VISIBLE);
            viewHolder.column7.setText(context.getString(R.string.text_split));
        }
        viewHolder.column4.setText(objects.get(position).getBatch());
        viewHolder.column5.setText(String.valueOf(objects.get(position).getOpenQuantity()));

        viewHolder.column6.setText(String.valueOf(objects.get(position).getQuantityInEntryUnit()));

        /*if (!userController.userHasAllLocation() && !userController.userHasAllFunction()){
            viewHolder.spinner.setClickable(false);
            viewHolder.spinner.setEnabled(false);
        }*/

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
        TextView column7Sub;
        Spinner spinner;
    }



    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private SplitCallback splitCallback;

    public void setSplitCallback(SplitCallback splitCallback) {
        this.splitCallback = splitCallback;
    }

    public interface SplitCallback {
        void onCallBack(PurchaseOrderSubContract purchaseOrder, int position);
    }

    private SubCallback subCallback;

    public void setSubCallback(SubCallback subCallback) {
        this.subCallback = subCallback;
    }

    public interface SubCallback {
        void onSubCallBack(PurchaseOrderSubContract purchaseOrder, int position);
    }
}
