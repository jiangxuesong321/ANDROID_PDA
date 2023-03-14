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
import com.android.pda.database.pojo.MaterialDocument;

import java.util.List;

public class ProductionStorageResultAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<MaterialDocument> objects;
    private Context context;

    public ProductionStorageResultAdapter(Context context, List<MaterialDocument> objects) {
        this.objects = objects;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects.size();
    }

    public MaterialDocument getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; //viewholder的作用是因为Android有个recycler的反复循环器，viewholder就是借助他来做到循环利用itemview。
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.li_production_storage_result, null);
            viewHolder = new ViewHolder();
            viewHolder.column1 = convertView.findViewById(R.id.tv_column1);
            viewHolder.column2 = convertView.findViewById(R.id.tv_column2);
            viewHolder.column3 = convertView.findViewById(R.id.tv_column3);
            viewHolder.column4 = convertView.findViewById(R.id.tv_column4);
            viewHolder.column5 = convertView.findViewById(R.id.tv_column5);
            viewHolder.column6 = convertView.findViewById(R.id.et_column6);
            viewHolder.column7 = convertView.findViewById(R.id.et_column7);
            convertView.setTag(viewHolder);
            viewHolder.column6.setTag(position);
            viewHolder.column6.clearFocus();
            viewHolder.column6.addTextChangedListener(new Watcher(viewHolder));
            viewHolder.column7.setTag(position);
            viewHolder.column7.clearFocus();
            viewHolder.column7.addTextChangedListener(new Watcher(viewHolder));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.column7.clearFocus();
            viewHolder.column7.setTag(position);
        }

        if (position % 2 != 0) {
            if (position > 0) {
                convertView.setBackgroundColor(context.getColor(R.color.bg_colorFourthPrimary));
            }
        } else {
            convertView.setBackgroundColor(context.getColor(android.R.color.transparent));
        }

        viewHolder.column1.setText(objects.get(position).getMaterialDocumentItem());
        viewHolder.column2.setText(objects.get(position).getMaterial());
        viewHolder.column3.setText(String.valueOf(objects.get(position).getDescription()));
        viewHolder.column4.setText(objects.get(position).getBatch());
        viewHolder.column5.setText(String.valueOf(objects.get(position).getQuantityInEntryUnit()));

//        viewHolder.column6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    ((EditText) v).setSelection(0);
//                }
//            }
//        });
//        viewHolder.column7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    ((EditText) v).setSelection(0);
//                }
//            }
//        });


        return convertView;
    }

    public class ViewHolder {
        TextView column1;
        TextView column2;
        TextView column3;
        TextView column4;
        TextView column5;
        EditText column6;
        EditText column7;
    }

    private OnItemClickListener itemClickListener;

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onCallBack(int position);
    }

    private SplitCallback splitCallback;

    public void setSplitCallback(SplitCallback splitCallback) {
        this.splitCallback = splitCallback;
    }

    public interface SplitCallback {
        void onSplitCallBack(MaterialDocument materialDocument, int position);
    }


    private class Watcher implements TextWatcher {

        ViewHolder holder = null;
        boolean isBin;

        public Watcher(ViewHolder holder) {
            this.holder = holder;
            this.isBin = isBin;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (holder.column6.hasFocus()) {
                int position = (Integer) holder.column6.getTag();
                objects.get(position).setStorageBin(s.toString().trim());
            }
//            if (holder.column7.hasFocus()) {
//                String inputMaterial = s.toString().trim();
//                int position = (Integer) holder.column7.getTag();
//                if (objects.get(position).getMaterial().length() == inputMaterial.length()) {
//                    objects.get(position).setInputMaterial(s.toString().trim());
//                    // 创建对话框
//                    checkCallback.onCallBack(position, inputMaterial);
//                }
//            }
//            holder.column7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) {
//                        String inputMaterial = s.toString().trim();
//                        int position = (Integer) holder.column7.getTag();
//                        objects.get(position).setInputMaterial(s.toString().trim());
//                        // 创建对话框
//                        checkCallback.onCallBack(position, inputMaterial);
//                    }
//                }
//            });

            if (holder.column7.hasFocus()) {
                String inputMaterial = s.toString().trim();
                int position = (Integer) holder.column7.getTag();
                objects.get(position).setInputMaterial(s.toString().trim());
                // 创建对话框
                checkCallback.onCallBack(position, inputMaterial);
            }
        }
    }

//    private void displayDialog(String message, int action) {
//        NoticeDialog noticeDialog = new NoticeDialog(context, message, 1);
//        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
//            @Override
//            public void callOk() {
//
//            }
//
//            @Override
//            public void callClose() {
//                if (AppConstants.REQUEST_BACK == action) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        noticeDialog.create();
//    }


    private CheckCallback checkCallback;

    public interface CheckCallback {
        void onCallBack(int position, String material);
    }

    public void setCheckCallback(CheckCallback checkCallback) {
        this.checkCallback = checkCallback;
    }
}
