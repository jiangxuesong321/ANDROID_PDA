package com.sunmi.pda.database;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseServiceStorageLocation implements DatabaseConstants{

    private final DatabaseHelper dbHelper;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    protected static final String TAG = DatabaseService.class.getSimpleName();

    public DatabaseServiceStorageLocation() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<StorageLocation> storageLocations){
        PojoDBObjectHelper.update(dbHelper.plant, storageLocations);
        LogUtils.d(TAG, "Created plant master....");

    }

    public StorageLocation getDataByKey(String plant, String storageLocation){
        return PojoDBObjectHelper.fetchById(dbHelper.plant, new String[] { plant, storageLocation});
    }

    public List<StorageLocation> getAllData(){
        return PojoDBObjectHelper.fetchAllInList(dbHelper.plant);
    }

    public void createData(StorageLocation storageLocation){
        PojoDBObjectHelper.createOrUpdate(dbHelper.plant, storageLocation);
        LogUtils.d(TAG, "Created plant master....");
    }

    public void deleteDataByKey(String plant, String storageLocation){
        PojoDBObjectHelper.delete(
                dbHelper.plant,
                PLANT_MASTER_PLANT + " = ? AND " + PLANT_MASTER_STORAGE_LOCATION + " = ? ",
                new String[] { plant, storageLocation }
        );
    }
    public void deleteData(){
        PojoDBObjectHelper.deleteAll(dbHelper.plant);
    }

    public List<StorageLocation> getLocationBy(String storageLocationId,
                                               String plant, String json){
        //System.out.println("json--->" + json);
        ArrayList<Login> logins = JSON.parseObject(json, new TypeReference<ArrayList<Login>>(){});

        String sql = "select * from " + TABLE_PLANT_MASTER + " where ";
        //需要在工厂代码集合中删除的工厂代码，也是库位是最高权限相对应的工厂
        String removePlant = "";
        if(logins != null){
            for(Login login : logins){
                if(login != null){
                    //System.out.println("login--->" + JSON.toJSONString(login));
                    if(StringUtils.equalsIgnoreCase(login.getZstore_loc(), app.getString(R.string.storage_location_all))){
                        //如果用户库位权限有最高权限，需要再用关联的工厂再次过滤
                        sql += " plant = '" + login.getZfactory() + "' or ";
                        removePlant = login.getZfactory();
                    }
                }
            }
        }
        List<String> storageLocations = Util.splitCode(storageLocationId);
        storageLocations.remove(app.getString(R.string.storage_location_all));
        String idsLocation = storageLocations.stream().map(s -> "\'" + s + "\'").collect(Collectors.joining(", "));

        List<String> plants = Util.splitCode(plant);
        plants.remove(removePlant);
        String idsPlant = plants.stream().map(s -> "\'" + s + "\'").collect(Collectors.joining(", "));

        sql += " (plant in (" + idsPlant + ") and storageLocation in (" + idsLocation + "))";

        System.out.println("sql--->" + sql);
        String[] selectionArg = null;
        List<StorageLocation> locations = PojoDBObjectHelper.fetchByCustomQuery(dbHelper.plant, sql, selectionArg);
        System.out.println("locations--->" + locations.size());
        return locations;
    }

    public List<StorageLocation> getPlantsBy(String sPlants) {
        String sql = "select * from " + TABLE_PLANT_MASTER ;
        if (!StringUtils.equalsIgnoreCase(sPlants, "*")){
            List<String> plantList = Util.splitCode(sPlants);
            String plants = plantList.stream().map(s -> "\'" + s + "\'").collect(Collectors.joining(", "));
            sql += " where "+ PLANT_MASTER_PLANT +" in (" + plants + ")";
        }
        sql += " group by " + PLANT_MASTER_PLANT;
        System.out.println("getPlantsBy ---> " + sql);
        String[] selectionArg = null;
        List<StorageLocation> plants = PojoDBObjectHelper.fetchByCustomQuery(dbHelper.plant, sql, selectionArg);
        return plants;
    }

    public int getStorageLocationCount(){
        String sql = "select * from " + TABLE_PLANT_MASTER + " limit 1 ";
        String[] selectionArg = null;
        List <StorageLocation> list = PojoDBObjectHelper.fetchByCustomQuery(dbHelper.plant, sql, selectionArg);
        int count = list.size();
        return count;
    }
}
