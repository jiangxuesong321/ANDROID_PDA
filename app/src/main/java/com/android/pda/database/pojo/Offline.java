package com.android.pda.database.pojo;

import java.io.Serializable;

public class Offline implements Serializable {
    private String id;
    private String orderBody;
    private String orderNumber;
    private String postBody;
    private String url;
    private String postDate;
    private String reserve1;
    private String reserve2;

    private String logisticsProvider;
    private String logisticNumber;
    private String plant;
    private String sendLocation;
    private String receiveLocation;

    public Offline() {
    }

    public Offline(String id, String orderBody, String orderNumber, String postBody, String url,
                   String postDate, String logisticsProvider, String logisticNumber, String plant,
                   String sendLocation, String receiveLocation, String reserve1, String reserve2) {
        this.id = id;
        this.orderBody = orderBody;
        this.orderNumber = orderNumber;
        this.postBody = postBody;
        this.url = url;
        this.postDate = postDate;
        this.logisticsProvider = logisticsProvider;
        this.logisticNumber = logisticNumber;
        this.plant = plant;
        this.sendLocation = sendLocation;
        this.receiveLocation = receiveLocation;
        this.reserve1 = reserve1;
        this.reserve2 = reserve2;
    }

    public String getId() {
        return id;
    }

    public String getOrderBody() {
        return orderBody;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getPostBody() {
        return postBody;
    }

    public String getUrl() {
        return url;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getReserve1() {
        return reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOrderBody(String orderBody) {
        this.orderBody = orderBody;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }

    public String getLogisticNumber() {
        return logisticNumber;
    }

    public void setLogisticNumber(String logisticNumber) {
        this.logisticNumber = logisticNumber;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getSendLocation() {
        return sendLocation;
    }

    public void setSendLocation(String sendLocation) {
        this.sendLocation = sendLocation;
    }

    public String getReceiveLocation() {
        return receiveLocation;
    }

    public void setReceiveLocation(String receiveLocation) {
        this.receiveLocation = receiveLocation;
    }
}
