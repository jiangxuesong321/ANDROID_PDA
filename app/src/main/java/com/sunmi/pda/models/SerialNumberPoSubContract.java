package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

public class SerialNumberPoSubContract {

    @JSONField(name="SerialNumber")
    private String serialNumber;

    public SerialNumberPoSubContract() {
    }

    public SerialNumberPoSubContract(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
