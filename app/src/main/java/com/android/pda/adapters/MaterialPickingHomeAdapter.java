package com.android.pda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.MaterialDocument;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MaterialPickingHomeAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Material> objects;
    private Context context;
    private String serialNumber;

    public MaterialPickingHomeAdapter(Context context, List<Material> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public Material getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MaterialPickingHomeAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_material_list, null);
            viewHolder = new MaterialPickingHomeAdapter.ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MaterialPickingHomeAdapter.ViewHolder) convertView.getTag();
        }

        if (position % 2 != 0) {
            if (position > 0) {
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        } else {
            convertView.setBackgroundColor(context.getColor(android.R.color.transparent));
        }

        serialNumber = Integer.toString(position + 1);

        viewHolder.column1.setText(serialNumber);
        viewHolder.column2.setText(objects.get(position).getMaterial());

        viewHolder.column3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCallback.onCallBack(position);
            }
        });

        return convertView;
    }

//    private MaterialPickingHomeAdapter.OnItemClickListener itemClickListener;
//
//    public void setClickListener(MaterialPickingHomeAdapter.OnItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void onCallBack(int position);
//    }
//
//    private MaterialPickingHomeAdapter.SplitCallback splitCallback;
//
//    public void setSplitCallback(MaterialPickingHomeAdapter.SplitCallback splitCallback) {
//        this.splitCallback = splitCallback;
//    }
//
//    public interface SplitCallback {
//        void onSplitCallBack(Material material, int position);
//    }

    public class ViewHolder {
        TextView column1;
        TextView column2;
        TextView column3;
    }

    private DeleteCallback deleteCallback;

    public interface DeleteCallback {
        void onCallBack(int position);
    }

    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }
}
