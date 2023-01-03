package com.android.pda.database;

import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Login;
import com.android.pda.log.LogUtils;

import java.util.List;

public class DatabaseServiceLogin implements DatabaseConstants{

    private final DatabaseHelper dbHelper;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    protected static final String TAG = DatabaseServiceLogin.class.getSimpleName();

    public DatabaseServiceLogin() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<Login>  logins){
        PojoDBObjectHelper.update(dbHelper.login, logins);
        LogUtils.d(TAG, "Created Login data....");

    }

    public List<Login> getAllData(){
        return PojoDBObjectHelper.fetchAllInList(dbHelper.login);
    }

    public Login getDataByKey(String userid){
        return PojoDBObjectHelper.fetchById(dbHelper.login, new String[] { userid});
    }

    public void deleteDataByKey(String id){
        PojoDBObjectHelper.delete(
                dbHelper.login,
                LOGIN_COLUMN_ID + " = ?  ",
                new String[] { id }
        );
    }
    public void deleteData(){
        PojoDBObjectHelper.deleteAll(dbHelper.login);
    }
}
