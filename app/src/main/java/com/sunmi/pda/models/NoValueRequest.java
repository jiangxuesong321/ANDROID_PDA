package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class NoValueRequest {
    @JSONField(name = "GoodsMovementCode")
    private String goodsMovementCode;
    @JSONField(name="PostingDate")
    private String postingDate;
    @JSONField(name="DocumentDate")
    private String documentDate;
    @JSONField(name="MaterialDocumentHeaderText")
    private String materialDocumentHeaderText;
    @JSONField(name="DocumentType")
    private String documentType;
    @JSONField(name="to_MaterialDocumentItem")
    private DocumentItem to_MaterialDocumentItem;

    public NoValueRequest(String goodsMovementCode, String postingDate, String documentDate, String materialDocumentHeaderText, String documentType, List<NoValueItem> results) {
        this.goodsMovementCode = goodsMovementCode;
        this.postingDate = postingDate;
        this.documentDate = documentDate;
        this.materialDocumentHeaderText = materialDocumentHeaderText;
        this.documentType = documentType;
        this.to_MaterialDocumentItem = new DocumentItem(results);
    }

    public String getGoodsMovementCode() {
        return goodsMovementCode;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public String getMaterialDocumentHeaderText() {
        return materialDocumentHeaderText;
    }

    public String getDocumentType() {
        return documentType;
    }

    public DocumentItem getTo_MaterialDocumentItem() {
        return to_MaterialDocumentItem;
    }

    public class DocumentItem {
        List<NoValueItem> results;

        public DocumentItem(List<NoValueItem> results) {
            this.results = results;
        }

        public List<NoValueItem> getResults() {
            return results;
        }
    }
}
