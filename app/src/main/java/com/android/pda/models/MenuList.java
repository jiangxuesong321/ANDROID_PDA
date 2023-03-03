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
        this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PO_STORAGE, mContext.getString(R.string.title_po_storage)));
        this.menuItemList.add(new MenuItemData(AppConstants.FUNCTION_ID_PROD_STORAGE, mContext.getString(R.string.title_production_storage)));
    }

    public List<MenuItemData> getMenuItemList() {
        return menuItemList;
    }
}
