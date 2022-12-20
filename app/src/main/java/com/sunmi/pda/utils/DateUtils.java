package com.sunmi.pda.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.util.TimeZone;

public class DateUtils {
    public static String FormatFullDate = "yyyy-MM-dd HH:mm:ss";
    public static String FormatY_M_D = "yyyy-MM-dd";
    public static String FormatYMD = "yyyyMMdd";
    public static String FormatHHMMSS = "HH:mm:ss";
    public static String FormatY = "yyyy";
    public static String FormatYMDHMS = "yyyyMMddHHmmss";
    /**
     * Get timestamp from Date object
     *
     * @param jsonDate "\/Date(1626825600000)\/"
     * @param jsonTime  "PT16H39M07S"
     * @return long timestamp
     */
    public static long jsonDateTimeToTimeStamp(String jsonDate, String jsonTime) {
        String sDate = jsonDateToString(jsonDate);
        String sTime = jsonTimeToString(jsonTime);
        String myDate = sDate + " " + sTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date utilDate =  null;
        try {
            utilDate = sdf.parse(myDate);
        } catch (ParseException pe){
            pe.printStackTrace();
        }

        return utilDate.getTime();
    }

    /**
     * Get timestamp from Date object
     *
     * @param jsonDate "\/Date(1626825600000)\/"
     * @return  string date in format FormatY_M_D
     */
    public static String jsonDateToString(String jsonDate) {
        //System.out.println("jsonDate---->" + jsonDate);
        Date date = new Date(jsonDateToTimeStamp(jsonDate));
        //System.out.println("date---->" + date);
        String sDate = dateToString(date, FormatY_M_D);
        //System.out.println("sDate---->" + sDate);
        return sDate;
    }

    /**
     * Get timestamp from Date object
     *
     * @param sDate "\/Date(1626825600000)\/"
     * @return  long timestamp
     */
    public static long jsonDateToTimeStamp(String sDate) {
        if (sDate != null && sDate.indexOf("(") != -1 && sDate.indexOf(")") != -1) {
            String temp = sDate.substring(sDate.indexOf("(") + 1, sDate.indexOf(")"));
            return Long.parseLong(temp);
        }
        return Long.parseLong("0");
    }

    /**
     * Get string from jsonTime
     *
     * @param jsonTime "PT16H39M07S"
     * @return  string HH:mm:ss
     */
    public static String jsonTimeToString(String jsonTime) {
        if (jsonTime != null && jsonTime.indexOf("PT") != -1 && jsonTime.indexOf("H") != -1) {
            String hour = jsonTime.substring(jsonTime.indexOf("PT") + 2, jsonTime.indexOf("H"));
            String min = jsonTime.substring(jsonTime.indexOf("H") + 1, jsonTime.indexOf("M"));
            String sec = jsonTime.substring(jsonTime.indexOf("M") + 1, jsonTime.indexOf("S"));
            return hour + ":" + min + ":" + sec;
        }
        return "";
    }

    /**
     * Format a date to string that is according with user's date format
     *
     * @param date
     * @return date string in format
     */
    public static String dateToString(Date date, String format) {

        String dateStr = "";

        if (date != null) {
            SimpleDateFormat sdfDateToStr = new SimpleDateFormat(format);
            sdfDateToStr.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            dateStr = sdfDateToStr.format(date);
        }

        return dateStr;
    }

    public static String getMonthOrDate(int number){
        if(number < 10){
            return "0" + number;
        }
        return "" + number;
    }

    public static String getDateString() {
        return DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D);
    }

    public static int getYear(){
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return year;
    }
}
