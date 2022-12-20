package com.sunmi.pda.models;

import java.io.Serializable;

public class DeliveryStatus implements Serializable {
    private String code;
    private String name;

    public DeliveryStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
