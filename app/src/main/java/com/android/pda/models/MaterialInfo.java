package com.android.pda.models;

public class MaterialInfo {

    private String matnr;

    private String mtart;

    private String meins;

    public MaterialInfo() {
    }

    public MaterialInfo(String matnr, String mtart, String meins) {
        this.matnr = matnr;
        this.mtart = mtart;
        this.meins = meins;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getMtart() {
        return mtart;
    }

    public void setMtart(String mtart) {
        this.mtart = mtart;
    }

    public String getMeins() {
        return meins;
    }

    public void setMeins(String meins) {
        this.meins = meins;
    }
}
