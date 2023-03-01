package com.android.pda.database;

import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.UserInfo;
import com.android.pda.log.LogUtils;
import com.delawareconsulting.libtools.database.PojoDBObjectHelper;

import java.util.List;

public class DatabaseServiceUserInfo implements DatabaseConstants {

    private final DatabaseHelper dbHelper;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    protected static final String TAG = DatabaseServiceUser.class.getSimpleName();

    public DatabaseServiceUserInfo() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<UserInfo> userInfo) {
        PojoDBObjectHelper.update(dbHelper.userInfo, userInfo);
        LogUtils.d(TAG, "Created userInfo....");

    }

    public UserInfo getDataByKey(String userid) {
        return PojoDBObjectHelper.fetchById(dbHelper.userInfo, new String[]{userid});
    }

    public List<UserInfo> getAllData() {
        return PojoDBObjectHelper.fetchAllInList(dbHelper.userInfo);
    }

    public void createData(UserInfo userInfo) {
        PojoDBObjectHelper.createOrUpdate(dbHelper.userInfo, userInfo);
        LogUtils.d(TAG, "Created user ....");
    }

    public void deleteDataByKey(String id) {
        PojoDBObjectHelper.delete(
                dbHelper.userInfo,
                USER_COLUMN_ID + " = ?  ",
                new String[]{id}
        );
    }

    public void deleteData() {
        PojoDBObjectHelper.deleteAll(dbHelper.user);
    }
}
