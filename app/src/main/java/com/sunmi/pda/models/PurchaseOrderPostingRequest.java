package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

public class PurchaseOrderPostingRequest {
    @JSONField(name="DocumentDate")
    private String documentDate;
    @JSONField(name="PostingDate")
    private String postingDate;
    @JSONField(name="GoodsMovementCode")
    private String goodsMovementCode;
    @JSONField(name="DocumentType")
    private String documentType;

    @JSONField(name="MaterialDocumentHeaderText")
    private String materialDocumentHeaderText;

    @JSONField(name="to_MaterialDocumentItem")
    private MaterialDocumentItem to_MaterialDocumentItem;

    public PurchaseOrderPostingRequest() {
    }

    public PurchaseOrderPostingRequest(String documentDate, String postingDate, String goodsMovementCode,
                                       String documentType, String materialDocumentHeaderText,
                                       MaterialDocumentItem to_MaterialDocumentItem) {

        this.documentDate = documentDate;
        this.postingDate = postingDate;
        this.goodsMovementCode = goodsMovementCode;
        this.documentType = documentType;
        this.materialDocumentHeaderText = materialDocumentHeaderText;
        this.to_MaterialDocumentItem = to_MaterialDocumentItem;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public String getGoodsMovementCode() {
        return goodsMovementCode;
    }

    public MaterialDocumentItem getTo_MaterialDocumentItem() {
        return to_MaterialDocumentItem;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getMaterialDocumentHeaderText() {
        return materialDocumentHeaderText;
    }
}
