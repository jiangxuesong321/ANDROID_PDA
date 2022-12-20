package com.sunmi.pda.controllers;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.MenuList;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.Algorithm;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserController {
    protected static final String TAG = UserController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private final static LoginController loginController = app.getLoginController();
    private User user;


    public HttpResponse syncData() throws Exception {
        String filter = "&$filter=" + app.getString(R.string.filter_language);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_user) + app.getString(R.string.sap_url_client);

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        if(jsonArray != null && jsonArray.size() > 0){
            String now = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
            AppUtil.saveLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_USER, now);
        }
        List<User> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String zuid = objectI.getString("zuid");
                String zuname = objectI.getString("zuname");
                String zgroup = objectI.getString("zgroup");
                String zdepart = objectI.getString("zdepart");

                User user = new User(zuid, zuname, zgroup, zdepart);
                all.add(user);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "sync UserData Error ------> " + e.getMessage());
            }
        }
        app.getDBService().getDatabaseServiceUser().createData(all);
        return httpResponse;
    }

    public List<User> getUsers(){
        System.out.println("User---->" + JSON.toJSONString(app.getDBService().getDatabaseServiceUser().getAllData()));
        return app.getDBService().getDatabaseServiceUser().getAllData();
    }

    public User getUserById(String userId){
        return app.getDBService().getDatabaseServiceUser().getDataByKey(userId);
    }

    public boolean isLocationManager() {
        Login login = loginController.getLoginUser();
        if (StringUtils.containsIgnoreCase(login.getZfunc(), app.getString(R.string.function_all)) && StringUtils.equalsIgnoreCase(login.getZstore_loc(), app.getString(R.string.storage_location_all))) {
            return true;
        }
        return false;
    }

    public User getLoginUser(){
        Login login = loginController.getLoginUser();
        if(login != null) {
            user = getUserById(login.getZuid());
        }
        return user;
    }

    public List<StorageLocation> getUserLocation() {
        Login login = loginController.getLoginUser();
        if (login != null) {
            if (this.userHasAllLocation() && this.userHasAllPlant()){
                return app.getDBService().databaseServiceStorageLocation.getAllData();
            } else {
                String plant = login.getZfactory();
                return app.getDBService().databaseServiceStorageLocation.getLocationBy(login.getZstore_loc(),
                        plant, login.getZujson());
            }
        }
        return null;
    }

    public String getUserLocationString() {
        Login login = loginController.getLoginUser();
        if (login == null){
            login = loginController.getLoginUser();
        }
        if (login != null) {
            return login.getZstore_loc();
        }
        return "";
    }

    public List<StorageLocation> getUserPlants() {
        Login login = loginController.getLoginUser();
        if (login != null) {
            if (this.userHasAllPlant()){
                return app.getDBService().databaseServiceStorageLocation.getPlantsBy("*");
            } else {
                return app.getDBService().databaseServiceStorageLocation.getPlantsBy(login.getZfactory());
            }
        }
        return null;
    }

    public MenuList getUserMenuList(Context mContext) {
        Login login = loginController.getLoginUser();
        if (login != null) {
            if (this.userHasAllFunction() ){
                List<String> funcList = Util.splitCode("01;02;03;04;05;06;07;08;09;10;11;12;13;14;15;16;17;18;19;20;21;22");
                return new MenuList(funcList, mContext);
            } else {
                List<String> funcList = Util.splitCode(login.getZfunc());
                return new MenuList(funcList, mContext);
            }
        }
        return null;
    }

    /**
     * @param plantFieldName is the field name in url
     * @return (plantFieldName eq 'xxxx') or (plantFieldName eq 'xxxx')
     */
    public String getUserPlantsFilter(String plantFieldName) {
        Login login = loginController.getLoginUser();
        if (login == null){
            login = loginController.getLoginUser();
        }
        String filter = "";
        if (login != null) {
            if (this.userHasAllPlant()){
                return "";
            } else if (login.getZfactory().length() > 0) {
                List<String> plants = Util.splitCode(login.getZfactory());
                if (plants.size() > 1) {
                    filter = "(";
                }
                for(int i=0; i<plants.size(); i++){
                    filter += "(" + plantFieldName +" eq '" + plants.get(i) + "')";
                    if (i < (plants.size() - 1)) {
                        filter += " or ";
                    }
                }
                if (plants.size() > 1) {
                    filter += ")";
                }
            }
        }
        return filter;
    }

    /**
     * return true if user function contains 99
     * @return
     */
    public boolean userHasAllFunction (){
        Login login = loginController.getLoginUser();
        return StringUtils.containsIgnoreCase(login.getZfunc(), app.getString(R.string.function_all));
    }

    /**
     * return true if user location contains 1001
     * @return
     */
    public boolean userHasAllLocation (){
        Login login = loginController.getLoginUser();
        return StringUtils.containsIgnoreCase(login.getZstore_loc(), app.getString(R.string.storage_location_all));
    }

    /**
     * return true if user function contains 1001
     * @return
     */
    public boolean userHasAllPlant(){
        Login login = loginController.getLoginUser();
        return StringUtils.containsIgnoreCase(login.getZfactory(), app.getString(R.string.plant_all));
    }
}
