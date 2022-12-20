package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class InOutboundReportQuery implements Serializable {
    private List<Integer> orderIndices;
    private String deliveryDateFrom;
    private String deliveryDateFromTo;
    private List<String> storageLocations;
    public InOutboundReportQuery() {
    }

    public InOutboundReportQuery(List<Integer> orderIndices, String deliveryDateFrom,
                                 String deliveryDateFromTo, List<String> storageLocations) {
        this.orderIndices = orderIndices;
        this.deliveryDateFrom = deliveryDateFrom;
        this.deliveryDateFromTo = deliveryDateFromTo;
        this.storageLocations = storageLocations;
    }

    public List<Integer> getOrderIndices() {
        return orderIndices;
    }

    public void setOrderIndices(List<Integer> orderIndices) {
        this.orderIndices = orderIndices;
    }

    public String getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(String deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public String getDeliveryDateFromTo() {
        return deliveryDateFromTo;
    }

    public void setDeliveryDateFromTo(String deliveryDateFromTo) {
        this.deliveryDateFromTo = deliveryDateFromTo;
    }

    public List<String> getStorageLocations() {
        return storageLocations;
    }
}

