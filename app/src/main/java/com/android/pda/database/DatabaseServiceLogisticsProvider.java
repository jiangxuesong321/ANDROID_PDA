package com.android.pda.database;

import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.LogisticsProvider;
import com.android.pda.log.LogUtils;

import java.util.List;

public class DatabaseServiceLogisticsProvider implements DatabaseConstants{

    private final DatabaseHelper dbHelper;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    protected static final String TAG = DatabaseServiceLogisticsProvider.class.getSimpleName();

    public DatabaseServiceLogisticsProvider() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<LogisticsProvider> logisticsProviders){
        PojoDBObjectHelper.update(dbHelper.logisticsProvider, logisticsProviders);
        LogUtils.d(TAG, "Created LogisticsProvider master....");

    }

    public LogisticsProvider getDataByKey(String id){
        return PojoDBObjectHelper.fetchById(dbHelper.logisticsProvider, new String[] { id});
    }

    public List<LogisticsProvider> getAllData(){
        return PojoDBObjectHelper.fetchAllInList(dbHelper.logisticsProvider);
    }

    public void createData(LogisticsProvider logisticsProvider){
        PojoDBObjectHelper.createOrUpdate(dbHelper.logisticsProvider, logisticsProvider);
        LogUtils.d(TAG, "Created LogisticsProvider master....");
    }

    public void deleteDataByKey(String id){
        PojoDBObjectHelper.delete(
                dbHelper.logisticsProvider,
                PLANT_MASTER_PLANT + " = ?  ",
                new String[] { id }
        );
    }
    public void deleteData(){
        PojoDBObjectHelper.deleteAll(dbHelper.logisticsProvider);
    }
}
