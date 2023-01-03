package com.android.pda.controllers;


import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ScanController {

    protected static final String TAG = PurchaseOrderController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    public final static int ERROR_REPEAT_SCAN = 0;
    public final static int ERROR_MAX_COUNT = 1;
    public final static int ERROR_NOT_MATCH_MATERIAL = 2;
    public final static int ERROR_SN_COUNT_NOTMATCH = 3;
    public final static int ERROR_SN_EMPTY = 4;
    public final static int ERROR_SN_INVALID = 5;
    public final static int ERROR_SN_INVALID_LENGTH = 6;
    public final static int ERROR_SN_LENGTH_SHANGMI = 7;

    private static final MaterialController materialController = app.getMaterialController();

    /**
     * 对本次扫描的条码，查重、code前n位与material的barCode比对
     * @param snList 已验证过的条码数组
     * @param codes 本次扫描出的条码，1个或多个
     * @param maxCount
     * @param matBarCode material的barCode
     * @param group 商米/川田，商米比对前4位，川田比对前9位
     * @return
     */
    public int verifyScanData(List<String> snList, List<String> codes, double maxCount, String matBarCode, String group){

        HashSet<String> set = new HashSet<>(snList);
        set.retainAll(codes);
        if(set.size() > 0){
            return ERROR_REPEAT_SCAN;
        }
        int scanCount = snList.size() + codes.size();
        if(scanCount > maxCount){
            return ERROR_MAX_COUNT;
        }
        if(StringUtils.equalsIgnoreCase(group, app.getString(R.string.text_sunmi))){
            for(String code : codes){
                if(code.length() != 13){
                    return ERROR_SN_LENGTH_SHANGMI;
                }
                if(code.length() > 4){
                    String matFromCode = code.substring(0,4);
                    if(matBarCode.length() > 4){
                        matBarCode = matBarCode.substring(0,4);
                    }
                    if (!StringUtils.equalsIgnoreCase(matFromCode, matBarCode)) {
                        return ERROR_NOT_MATCH_MATERIAL;
                    }
                }else{
                    return ERROR_NOT_MATCH_MATERIAL;
                }
            }
        } else if(StringUtils.equalsIgnoreCase(group, app.getString(R.string.text_shanghai_sunmi))){
            for(String code : codes){
                if(code.length() != 13){
                    return ERROR_SN_LENGTH_SHANGMI;
                }
            }
            return -1;
        }else if(StringUtils.equalsIgnoreCase(group, app.getString(R.string.text_chuantian))){

            for(String code : codes){
                if (code.length() < 9 || matBarCode.length() < 9) {
                    return ERROR_NOT_MATCH_MATERIAL;
                }
                int len = code.length();
                String matFromCode = codes.get(0).substring(0, 9);
                if (!StringUtils.equalsIgnoreCase(matFromCode, matBarCode.substring(0, 9))) {
                    return ERROR_NOT_MATCH_MATERIAL;
                }
            }
        }
        return -1;
    }

    public int verifyDupScanData(List<String> snList, List<String> codes){

        HashSet<String> set = new HashSet<>(snList);
        set.retainAll(codes);
        if(set.size() > 0){
            return ERROR_REPEAT_SCAN;
        }
        int scanCount = snList.size() + codes.size();

        return -1;
    }


    public int verifyManualSN(String sn, double maxCount, String sCount){
        int count = 0;
        if (Util.isStringInt(sCount)) {
            count = Integer.parseInt(sCount);
        } else {
            return ERROR_SN_COUNT_NOTMATCH;
        }
        if (count != maxCount) {
            return ERROR_SN_COUNT_NOTMATCH;
        } else if (StringUtils.isEmpty(sn) ){
            return ERROR_SN_EMPTY;
        } else if (sn.length() != 16) {
            return ERROR_SN_INVALID_LENGTH;
        } else {
            String sn_code = sn.substring(12, 16);
            if (!Util.isStringInt(sn_code)) {
                return ERROR_SN_INVALID;
            }
        }
        return -1;
    }

    public List<String> testScan(String materialNumber, int maxCount, String s){

        List<String> snList = new ArrayList<>();
        String materialBarCode = "";
        materialBarCode = "KA17250" + s;
        for(int i = 1; i <= maxCount; i++){
            String str = String.format("%0" + 5 + "d", i);
            System.out.println("str---->" + materialBarCode + str);
            snList.add(materialBarCode + str);

        }
        System.out.println("TEST snList----" + snList);
        System.out.println("TEST snList----" + snList.size());
        return snList;
    }
}
