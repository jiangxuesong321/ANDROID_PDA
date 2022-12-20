package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class SerialNumberPoReturn {

    @JSONField(name="SERIALNUMBER")
    private String serialNumber;

    public SerialNumberPoReturn() {
    }

    public SerialNumberPoReturn(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
