package com.android.pda.models;

import java.io.Serializable;
import java.util.List;

public class OrderBody implements Serializable {
    List<Object> dataList;
    public OrderBody() {
    }

    public OrderBody(List<Object> dataList) {
        this.dataList = dataList;
    }

}
