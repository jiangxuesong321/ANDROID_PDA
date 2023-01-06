package com.android.pda.models;

import com.android.pda.R;
import com.android.pda.application.AppConstants;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 单据类型 List
 */

public class OrderTypeList {
    private List<DeliveryStatus> list = new ArrayList<DeliveryStatus>();

    public OrderTypeList(Context mContext, int type) {
        // 发货指令
        this.list.add(new DeliveryStatus(AppConstants.FUNCTION_ID_SALES_INVOICE, mContext.getString(R.string.title_sales_invoice)));
//        this.list.add(new DeliveryStatus(AppConstants.FUNCTION_ID_LEND, mContext.getString(R.string.text_prototype_borrow)));
//        this.list.add(new DeliveryStatus(AppConstants.FUNCTION_ID_PICKING_MATERIAL, mContext.getString(R.string.text_picking)));
//        this.list.add(new DeliveryStatus(AppConstants.FUNCTION_ID_PICKING, mContext.getString(R.string.text_picking_material)));
//        this.list.add(new DeliveryStatus(AppConstants.FUNCTION_ID_TRANSFER_OUT, mContext.getString(R.string.title_transfer_order)));
        this.list.add(new DeliveryStatus(AppConstants.FUNCTION_IDS_OTHERS, "其他单据"));

    }

    public List<DeliveryStatus> getOrderTypeList() {
        return list;
    }
}
