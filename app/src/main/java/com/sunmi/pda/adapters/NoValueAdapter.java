package com.sunmi.pda.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunmi.pda.R;
import com.sunmi.pda.models.SerialInfo;

import java.util.List;

public class NoValueAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SerialInfo> serials;
    private Context context;
    private CallbackDelete cbDel;

    public NoValueAdapter(Context context, List<SerialInfo> serials) {
        this.context = context;
        this.serials = serials;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return serials.size();
    }

    @Override
    public SerialInfo getItem(int position) {
        return serials.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        ViewHolder vh;
        if (cv == null) {
            cv = layoutInflater.inflate(R.layout.li_stock_transfer, null);
            vh = new ViewHolder();
            vh.col1 = cv.findViewById(R.id.tv_column1);
            vh.col2 = cv.findViewById(R.id.tv_column2);
            vh.col3 = cv.findViewById(R.id.tv_column3);
            vh.col4 = cv.findViewById(R.id.tv_column4);
            vh.col5 = cv.findViewById(R.id.tv_column5);
            vh.col6 = cv.findViewById(R.id.tv_column6);
            cv.setTag(vh);
        } else {
            vh = (ViewHolder) cv.getTag();
        }

        if (position %2 !=0) {
            if (position > 0) {
                cv.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        } else {
            cv.setBackgroundColor(context.getColor(R.color.colorDivider));
        }

        vh.col6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbDel.onDeleteListItem(position);
            }
        });

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position);
            }
        });

        TextView col1 = cv.findViewById(R.id.tv_column1);
        TextView col2 = cv.findViewById(R.id.tv_column2);
        TextView col3 = cv.findViewById(R.id.tv_column3);
        TextView col4 = cv.findViewById(R.id.tv_column4);
        TextView col5 = cv.findViewById(R.id.tv_column5);


        col1.setText(serials.get(position).getMaterial());
        col2.setText(serials.get(position).getMaterialDesc());
        col3.setText(serials.get(position).getCount());
        col4.setText(serials.get(position).getBatch());
        col5.setText(serials.get(position).getSerialnumber());

        return cv;
    }

    public void setDeleteItem(CallbackDelete cbDel) {
        this.cbDel = cbDel;
    }

    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

//    internal class and interface
    public class ViewHolder {
        TextView col1;
        TextView col2;
        TextView col3;
        TextView col4;
        TextView col5;
        TextView col6;
    }

    public interface CallbackDelete {
        void onDeleteListItem(int position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
