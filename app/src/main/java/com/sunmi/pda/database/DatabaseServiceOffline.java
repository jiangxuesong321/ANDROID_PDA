package com.sunmi.pda.database;

import android.database.sqlite.SQLiteBlobTooBigException;

import com.alibaba.fastjson.JSON;
import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.sunmi.pda.application.SunmiApplication;

import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.log.LogUtils;

import java.util.List;

public class DatabaseServiceOffline implements DatabaseConstants{

    private final DatabaseHelper dbHelper;
    private final static SunmiApplication app = SunmiApplication.getInstance();
    protected static final String TAG = DatabaseServiceOffline.class.getSimpleName();

    public DatabaseServiceOffline() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<Offline> offlineList){
        PojoDBObjectHelper.update(dbHelper.offline, offlineList);
        LogUtils.d(TAG, "Created Offline....");

    }

    public Offline getDataByKey(String id){

        try{
            Offline offline = PojoDBObjectHelper.fetchById(dbHelper.offline, new String[] { id});
            LogUtils.d(TAG, "Offline...." + JSON.toJSONString(offline));
            return offline;
        }catch (SQLiteBlobTooBigException expected){
            LogUtils.e(TAG, "SQLiteBlobTooBigException...." + expected.getMessage());
        }
        return null;
    }

    public List<Offline> getAllData(){
        return PojoDBObjectHelper.fetchAllInList(dbHelper.offline);
    }

    public void createData(Offline offline){
        PojoDBObjectHelper.createOrUpdate(dbHelper.offline, offline);
        LogUtils.d(TAG, "Created Offline ....");
    }
    public void deleteData(){
        PojoDBObjectHelper.deleteAll(dbHelper.offline);
    }

    public void deleteDataByKey(String id){
        PojoDBObjectHelper.delete(
                dbHelper.offline,
                OFFLINE_ID + " = ?  ",
                new String[] { id }
        );
    }
}
