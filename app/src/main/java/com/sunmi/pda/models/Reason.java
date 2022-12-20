package com.sunmi.pda.models;

import java.io.Serializable;

public class Reason implements Serializable {
    private String id;
    private String content;


    public Reason() {
    }

    public Reason(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
