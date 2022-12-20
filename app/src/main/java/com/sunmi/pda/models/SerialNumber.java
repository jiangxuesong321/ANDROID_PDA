package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class SerialNumber {

    @JSONField(name="results")
    private List<SerialNumberResults> results = new ArrayList<>();

    public SerialNumber() {
    }

    public SerialNumber(List<SerialNumberResults> results) {
        this.results = results;
    }

    public List<SerialNumberResults> getResults() {
        return results;
    }

    public void setResults(List<SerialNumberResults> results) {
        this.results = results;
    }
}
