package com.android.pda.controllers;


import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;

public class ScanController {

//    protected static final String TAG = PurchaseOrderController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    public final static int ERROR_REPEAT_SCAN = 0;
    public final static int ERROR_MAX_COUNT = 1;
    public final static int ERROR_NOT_MATCH_MATERIAL = 2;
    public final static int ERROR_SN_COUNT_NOTMATCH = 3;
    public final static int ERROR_SN_EMPTY = 4;
    public final static int ERROR_SN_INVALID = 5;
    public final static int ERROR_SN_INVALID_LENGTH = 6;
    public final static int ERROR_SN_LENGTH_COMPANY = 7;

    private static final MaterialController materialController = app.getMaterialController();

    /**
     * 对本次扫描的条码，查重、code 前 n 位与 material 的 barCode 比对
     * @param snList 已验证过的条码数组
     * @param codes 本次扫描出的条码，1 个或多个
     * @param maxCount
     * @param matBarCode material 的 barCode
     * @param group Company A/Company B，Company A 比对前 4 位，Company B 比对前 9 位
     * @return
     */
    public int verifyScanData(List<String> snList, List<String> codes, double maxCount, String matBarCode, String group){

        HashSet<String> set = new HashSet<>(snList);
        set.retainAll(codes);
        // 判断重复扫描
        if(set.size() > 0){
            return ERROR_REPEAT_SCAN;
        }
        // 判断是否超出允许最大扫描数
        int scanCount = snList.size() + codes.size();
        if(scanCount > maxCount){
            return ERROR_MAX_COUNT;
        }
        // 校验 组织 A 物料匹配
        if(StringUtils.equalsIgnoreCase(group, app.getString(R.string.text_company_name_a))){
            for(String code : codes){
                if(code.length() != 13){
                    return ERROR_SN_LENGTH_COMPANY;
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
            // 校验 组织 B 条码长度
        } else if(StringUtils.equalsIgnoreCase(group, app.getString(R.string.text_company_name_b))){
            for(String code : codes){
                if(code.length() != 13){
                    return ERROR_SN_LENGTH_COMPANY;
                }
            }
            return -1;
            // 校验 组织 C 物料匹配
        }else if(StringUtils.equalsIgnoreCase(group, app.getString(R.string.text_company_name_c))){

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

    /**
     * 校验是否重复扫描
     * @param snList
     * @param codes
     * @return
     */
    public int verifyDupScanData(List<String> snList, List<String> codes){

        HashSet<String> set = new HashSet<>(snList);
        set.retainAll(codes);
        if(set.size() > 0){
            return ERROR_REPEAT_SCAN;
        }
        int scanCount = snList.size() + codes.size();

        return -1;
    }

    /**
     * 校验手工输入内容
     * @param sn
     * @param maxCount
     * @param sCount
     * @return
     */
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
}
