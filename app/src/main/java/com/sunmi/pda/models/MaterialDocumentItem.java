package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MaterialDocumentItem {

    @JSONField(name="results")
    private List<MaterialDocumentItemResults> results;

    public MaterialDocumentItem() {
    }

    public MaterialDocumentItem(List<MaterialDocumentItemResults> results) {
        this.results = results;
    }

    public List<MaterialDocumentItemResults> getResults() {
        return results;
    }
}
