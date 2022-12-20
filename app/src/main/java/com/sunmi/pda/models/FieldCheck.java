package com.sunmi.pda.models;

public class FieldCheck {
    private String value;
    private Integer message;

    public FieldCheck(String value, Integer message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public Integer getMessage() {
        return message;
    }
}
