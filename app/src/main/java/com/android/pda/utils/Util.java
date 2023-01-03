package com.android.pda.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import com.android.pda.R;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.log.LogUtils;
import com.android.pda.models.ErrorDetail;
import com.android.pda.models.SapError;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static Pattern p = Pattern.compile("\r|\n");
    public static String parseSapError(String responseString) throws GeneralException {
        String errorMessage = "";
        try{
            SapError sapError = JSON.parseObject(responseString, SapError.class);

            if(sapError != null && sapError.getError() != null && sapError.getError().getInnererror() != null){
                if(sapError.getError().getInnererror().getErrordetails() != null && sapError.getError().getInnererror().getErrordetails().size() > 0){
                    for(ErrorDetail errordetail : sapError.getError().getInnererror().getErrordetails()){
                        if(errordetail != null){
                            errorMessage = errorMessage + errordetail.getMessage() + " ";
                        }
                    }
                }else{
                    if(sapError.getError().getMessage() != null){
                        errorMessage = sapError.getError().getMessage().getValue();
                    }
                }
            }
        }catch (Exception e){
            throw new GeneralException();
        }

        return errorMessage;
    }
    public static List<String> splitCode(String code) {
        List<String> codes = new ArrayList<>();
        if (code != null && !code.isEmpty()) {
            LogUtils.i("Scan code--->", code);
            boolean findEnter = p.matcher(code).find();
            if (findEnter) {
                if (code.contains("\r\n")) {
                    String[] names = code.split("\r\n");
                    LogUtils.i("split code with \n--->", JSON.toJSONString(names));
                    codes.addAll(Arrays.asList(names));
                } else if (code.contains("\n")){
                    String[] names = code.split("\n");
                    LogUtils.i("split code with enter--->", JSON.toJSONString(names));
                    codes.addAll(Arrays.asList(names));
                }
            } else if (code.contains(" ")) {
                String[] names = code.split(" ");
                LogUtils.i("split code with space--->", JSON.toJSONString(names));
                codes.addAll(Arrays.asList(names));
            } else if (code.contains(";")) {
                String[] names = code.split(";");
                LogUtils.i("split code with space--->", JSON.toJSONString(names));
                codes.addAll(Arrays.asList(names));
            } else if (code.contains("\n")){
                String[] names = code.split("\n");
                LogUtils.i("split code with enter--->", JSON.toJSONString(names));
                codes.addAll(Arrays.asList(names));
            }else {
                //Single code
                LogUtils.i("split code with Single code--->", code);
                codes.addAll(Arrays.asList(code));
            }
            //mCode.setText(code);
        }
        return codes;
    }

    public static boolean isDoubleInt(Double d)
    {
        return Math.ceil(d) == Math.floor(d);
    }

    public static boolean isStringInt(String s)
    {
        try {
            double number = Double.parseDouble(s);
            if (number  == (int)number) {
                return true;
            }
            return false;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static String toIntString(String s)
    {
        try {
            double number = Double.parseDouble(s);
            return Integer.toString((int)number);
        } catch (NumberFormatException ex) {
            return s;
        }
    }

    public static String addFilter(String filter, String extraFilter) {
        if (StringUtils.isEmpty(extraFilter)){
            return filter;
        }
        if(StringUtils.isEmpty(filter)){
            filter =  "$filter=" + extraFilter;
        } else {
            filter = filter + " and " + extraFilter;
        }
        return filter;
    }

    public static String getDateFilter(String fieldName, String dateFrom, String dateTo) {
        String filter = "";
        if(StringUtils.isNotEmpty(dateFrom) || StringUtils.isNotEmpty(dateFrom)) {
            if(StringUtils.isNotEmpty(dateTo)){
                String sDateFrom = dateFrom + "T00:00:00";
                String sDateTo = dateTo + "T00:00:00";
                filter = "(" + fieldName + " ge datetime'" + sDateFrom
                        + "' and " + fieldName + " le datetime'" + sDateTo + "' ) ";
            }else{
                if(StringUtils.isNotEmpty(dateFrom)){
                    String sDateFrom = dateFrom + "T00:00:00";
                    filter = "(" + fieldName + " ge datetime'" + sDateFrom +"' ) ";
                }
            }
        }
        return filter;
    }

    public static List<String> getPrintLineBreaks(String str){
        List<String> strList = new ArrayList<>();
        int maxLength = 26;
        int nameSize = str.length() / maxLength;
        if(str.length() % maxLength > 0){
            nameSize = nameSize + 1;
        }
        if(nameSize == 1){
            strList.add(str);
        }else {
            for(int i = 0; i < nameSize; i ++){
                if(i == nameSize - 1){
                    strList.add(str.substring(i * maxLength + 1));
                }else{
                    if(i == 0){
                        strList.add(str.substring(0, maxLength));
                    }else{
                        strList.add(str.substring(i * maxLength, (i + 1) * maxLength));
                    }
                }
            }
        }
        return strList;

    }
    public static String rawRead(Context context){
        String ret = "";
        InputStream is = context.getResources().openRawResource(R.raw.my_raw);
        InputStreamReader isr = new InputStreamReader(is);
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(isr);
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }

    public static String removeEnter(String str){
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    public static List<String> testCode(){
        String baseCode = "DB522206";
        List<String> codes = new ArrayList<>();
        for(int i = 30001; i < 40001 ;i++){
            String code = baseCode + i;
            codes.add(code);
            System.out.println("code-------->" + code);
        }
        return codes;
    }
}
