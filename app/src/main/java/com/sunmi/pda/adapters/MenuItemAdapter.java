package com.sunmi.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sunmi.pda.R;
import com.sunmi.pda.models.MenuItemData;

import java.util.List;

public class MenuItemAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<MenuItemData> menuItems;
    private Context context;
    public MenuItemAdapter(Context context, List<MenuItemData> menuItems){
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.menuItems = menuItems;
    }

    public int getCount() {
        return menuItems.size();
    }

    public MenuItemData getItem(int position) {
        return menuItems.get(position);
    }

    public long getItemId(int position) {
        return Long.valueOf(menuItems.get(position).getId());
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.li_menu, null);
        RelativeLayout rlMenu = convertView.findViewById(R.id.rl_menu);
        TextView tvSerialNr = convertView.findViewById(R.id.tv_menu_label);
        if(position == 0){
            rlMenu.setBackground(context.getDrawable(R.drawable.view_menu_base_data));
            tvSerialNr.setTextColor(context.getColor(R.color.bg_colorFourthPrimary));
        }else{
            rlMenu.setBackground(context.getDrawable(R.drawable.view_menu_data));
            tvSerialNr.setTextColor(context.getColor(R.color.colorPrimary));
        }

        tvSerialNr.setText(menuItems.get(position).getMenuName());
        return convertView;
    }
}
