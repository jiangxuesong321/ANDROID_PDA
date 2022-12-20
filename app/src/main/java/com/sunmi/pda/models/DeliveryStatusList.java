package com.sunmi.pda.models;

import java.util.ArrayList;
import java.util.List;

public class DeliveryStatusList {
    private List<DeliveryStatus> list = new ArrayList<DeliveryStatus>();

    public DeliveryStatusList() {
        this.list.add(new DeliveryStatus("", ""));
        this.list.add(new DeliveryStatus("A", "尚未发货"));
        this.list.add(new DeliveryStatus("B", "部分发货"));
        //this.list.add(new DeliveryStatus("C", "完全发货"));
    }

    public List<DeliveryStatus> getDeliveryStatusList() {
        return list;
    }
}
