package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class BusinessOrderQuery implements Serializable {
    String number;

    String dateFrom;
    String dateTo;
    List<String> storageLocations;
    List<DeliveryStatus> deliveryStatuses;

    public BusinessOrderQuery() {
    }

    public BusinessOrderQuery(String number) {
        this.number = number;
    }

    public BusinessOrderQuery(String dateFrom, String dateTo, List<String> storageLocations,
                              List<DeliveryStatus> deliveryStatuses) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.storageLocations = storageLocations;
        this.deliveryStatuses = deliveryStatuses;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public List<String> getStorageLocations() {
        return storageLocations;
    }

    public List<DeliveryStatus> getDeliveryStatuses() {
        return deliveryStatuses;
    }

    public void setDeliveryStatuses(List<DeliveryStatus> deliveryStatuses) {
        this.deliveryStatuses = deliveryStatuses;
    }
}

