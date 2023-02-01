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
    }

    public List<MenuItemData> getMenuItemList() {
        return menuItemList;
    }
}
