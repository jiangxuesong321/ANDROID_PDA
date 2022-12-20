package com.sunmi.pda.models;

import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MaterialVoucher {

    private String mblnr;
    private String mjahr;
    private String rsnum;
    private String zzrqty;
    private String bwart;
    private String zzrqdt;
    private String zzrqps;
    private String zzreps;
    private String zzreds;
    private String zzrect;
    private String rspos;
    private String matnr;
    private String maktx;
    private String zzspec;
    private String zzvendor_ty;
    private String charg;
    private String werks;
    private String name1;
    private String lgort;
    private String lgobe;
    private String menge;
    private String meins;
    private String bstmg;  //接收数量
    private String umlgo;
    private String lgobe_js;
    private String serialNumber;
//            toSERNR;

    private boolean batchFlag;
    private String model;
    private String serialFlag;
    private List<String> snList;
    public MaterialVoucher(String mblnr, String mjahr, String rsnum, String zzrqty, String bwart, String zzrqdt, String zzrqps, String zzreps, String zzreds,
                           String zzrect, String rspos, String matnr, String maktx, String zzspec, String zzvendor_ty, String charg, String werks, String name1,
                           String lgort, String lgobe, String menge, String meins, String bstmg, String umlgo, String lgobe_js, String Serialnumber,
                           boolean batchFlag, String model, String serialFlag) {
        this.mblnr = mblnr;
        this.mjahr = mjahr;
        this.rsnum = rsnum;
        this.zzrqty = zzrqty;
        this.bwart = bwart;
        this.zzrqdt = zzrqdt;
        this.zzrqps = zzrqps;
        this.zzreps = zzreps;
        this.zzreds = zzreds;
        this.zzrect = zzrect;
        this.rspos = rspos;
        this.matnr = matnr;
        this.maktx = maktx;
        this.zzspec = zzspec;
        this.zzvendor_ty = zzvendor_ty;
        this.charg = charg;
        this.werks = werks;
        this.name1 = name1;
        this.lgort = lgort;
        this.lgobe = lgobe;
        this.menge = menge;
        this.meins = meins;
        this.bstmg = bstmg;
        this.umlgo = umlgo;
        this.lgobe_js = lgobe_js;
        this.serialNumber = Serialnumber;
        this.batchFlag = batchFlag;
        this.model = model;
        this.serialFlag = serialFlag;
    }

    public MaterialVoucher() {
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public String getMblnr() {
        return mblnr;
    }

    public String getMjahr() {
        return mjahr;
    }

    public String getRsnum() {
        return rsnum;
    }

    public String getZzrqty() {
        return zzrqty;
    }

    public String getBwart() {
        return bwart;
    }

    public String getZzrqdt() {
        return zzrqdt;
    }

    public String getZzrqps() {
        return zzrqps;
    }

    public String getZzreps() {
        return zzreps;
    }

    public String getZzreds() {
        return zzreds;
    }

    public String getZzrect() {
        return zzrect;
    }

    public String getRspos() {
        return rspos;
    }

    public String getMatnr() {
        return matnr;
    }

    public String getMaktx() {
        return maktx;
    }

    public String getZzspec() {
        return zzspec;
    }

    public String getZzvendor_ty() {
        return zzvendor_ty;
    }

    public String getCharg() {
        return charg;
    }

    public String getWerks() {
        return werks;
    }

    public String getName1() {
        return name1;
    }

    public String getLgort() {
        return lgort;
    }

    public String getLgobe() {
        return lgobe;
    }

    public String getMenge() {
        if (Util.isStringInt(menge)) {
            return Util.toIntString(menge);
        }
        return menge;
    }

    public double getMengeDouble(){
        if(StringUtils.isEmpty(menge)){
            return 0;
        }else{
            return Double.valueOf(menge);
        }
    }

    public String getMeins() {
        return meins;
    }

    public String getUmlgo() {
        return umlgo;
    }

    public String getLgobe_js() {
        return lgobe_js;
    }

    public String getBstmg() {
        if (Util.isStringInt(bstmg)) {
            return Util.toIntString(bstmg);
        }
        return bstmg;
    }
    public double getBstmgDouble(){
        if(StringUtils.isEmpty(bstmg)){
            return 0;
        }else{
            return Double.valueOf(bstmg);
        }
    }
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setMblnr(String mblnr) {
        this.mblnr = mblnr;
    }

    public void setMjahr(String mjahr) {
        this.mjahr = mjahr;
    }

    public void setRsnum(String rsnum) {
        this.rsnum = rsnum;
    }

    public void setZzrqty(String zzrqty) {
        this.zzrqty = zzrqty;
    }

    public void setBwart(String bwart) {
        this.bwart = bwart;
    }

    public void setZzrqdt(String zzrqdt) {
        this.zzrqdt = zzrqdt;
    }

    public void setZzrqps(String zzrqps) {
        this.zzrqps = zzrqps;
    }

    public void setZzreps(String zzreps) {
        this.zzreps = zzreps;
    }

    public void setZzreds(String zzreds) {
        this.zzreds = zzreds;
    }

    public void setZzrect(String zzrect) {
        this.zzrect = zzrect;
    }

    public void setRspos(String rspos) {
        this.rspos = rspos;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public void setMaktx(String maktx) {
        this.maktx = maktx;
    }

    public void setZzspec(String zzspec) {
        this.zzspec = zzspec;
    }

    public void setZzvendor_ty(String zzvendor_ty) {
        this.zzvendor_ty = zzvendor_ty;
    }

    public void setCharg(String charg) {
        this.charg = charg;
    }

    public void setWerks(String werks) {
        this.werks = werks;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public void setLgort(String lgort) {
        this.lgort = lgort;
    }

    public void setLgobe(String lgobe) {
        this.lgobe = lgobe;
    }

    public void setMenge(String menge) {
        this.menge = menge;
    }

    public void setMeins(String meins) {
        this.meins = meins;
    }

    public void setBstmg(String bstmg) {
        this.bstmg = bstmg;
    }

    public void setUmlgo(String umlgo) {
        this.umlgo = umlgo;
    }

    public void setLgobe_js(String lgobe_js) {
        this.lgobe_js = lgobe_js;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(boolean batchFlag) {
        this.batchFlag = batchFlag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<String> getSnList() {
        return snList;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;
    }
}
