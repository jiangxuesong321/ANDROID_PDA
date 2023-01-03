package com.android.pda.models;

import android.content.Context;

import com.android.pda.R;
import com.android.pda.application.AppConstants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuList {
    private List<MenuItemData> menuItemList = new ArrayList<MenuItemData>();
    private List<String> funcList;
    public MenuList(List<String> funcList, Context mContext) {
        this.funcList = funcList;
//TODO: test data
        this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_BASE_DATA, mContext.getString(R.string.text_master_data)));
        if(funcList.contains("11")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_SHIP_ORDER, mContext.getString(R.string.title_order_query)));
        }
        if(funcList.contains("12")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_SN_REPORT, mContext.getString(R.string.title_storage_report) ));
        }
        if(funcList.contains("01")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_SALES_INVOICE, mContext.getString(R.string.title_sales_invoice) ));
        }
        if(funcList.contains("02")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PURCHASE_ORDER, mContext.getString(R.string.text_po_receiving)));
        }
        if(funcList.contains("03")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_LEND, mContext.getString(R.string.text_prototype_borrow) ));
        }
        if(funcList.contains("04")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PICKING_MATERIAL,  mContext.getString(R.string.text_picking)));
        }
        if(funcList.contains("05")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PICKING, mContext.getString(R.string.text_picking_material) ));
        }
        if(funcList.contains("06")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_LEND_BACK,  mContext.getString(R.string.text_returning_material)  ));
        }
        if(funcList.contains("07")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_STOCK_MOVE, mContext.getString(R.string.title_stock_transfer) ));
        }
        if(funcList.contains("08")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_TRANSFER_OUT, mContext.getString(R.string.title_transfer_order)));
        }
        if(funcList.contains("09")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_TRANSFER_IN, mContext.getString(R.string.title_transfer_order_detail_receive)));
        }
        if(funcList.contains("10")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_SN_SEARCH, mContext.getString(R.string.title_serial_query)));
        }
        if(funcList.contains("13")){
            this.menuItemList.add(new MenuItemData("13", mContext.getString(R.string.text_print_manual)));
        }
        if(funcList.contains("14")){
            this.menuItemList.add(new MenuItemData("14", mContext.getString(R.string.text_shipping_label)));
        }
        if(funcList.contains("15")){
            this.menuItemList.add(new MenuItemData("15", mContext.getString(R.string.text_receive_label)));
        }

        if(funcList.contains("16")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_NO_VALUE_IN_BOUND, mContext.getString(R.string.text_no_value_in_bound)));
        }

        if(funcList.contains("17")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_NO_VALUE_OUT_BOUND, mContext.getString(R.string.text_no_value_out_bound)));
        }

        if(funcList.contains("18")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_RETURN, mContext.getString(R.string.text_po_return)));
        }
        if(StringUtils.equalsIgnoreCase(mContext.getString(R.string.default_environment), "Q")){
            if(funcList.contains("19")){
                this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_OUT, mContext.getString(R.string.text_po_sub_contract_out)));
            }
            if(funcList.contains("20")){
                this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_SUBCONTRACT_IN, mContext.getString(R.string.text_po_sub_contract_in)));
            }
        }
        if(funcList.contains("21")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI, mContext.getString(R.string.text_po_gi)));
        }
        if(funcList.contains("22")){
            this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR, mContext.getString(R.string.text_po_pgr)));
        }
    }

    public List<MenuItemData> getMenuItemList() {
        return menuItemList;
    }
}
