package com.android.pda.database;

import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.User;
import com.android.pda.log.LogUtils;

import java.util.List;

public class DatabaseServiceUser implements DatabaseConstants{

    private final DatabaseHelper dbHelper;
    private final static AndroidApplication app = AndroidApplication.getInstance();
    protected static final String TAG = DatabaseServiceUser.class.getSimpleName();

    public DatabaseServiceUser() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<User> users){
        PojoDBObjectHelper.update(dbHelper.user, users);
        LogUtils.d(TAG, "Created user master....");

    }

    public User getDataByKey(String userid){
        return PojoDBObjectHelper.fetchById(dbHelper.user, new String[] { userid});
    }

    public List<User> getAllData(){
        return PojoDBObjectHelper.fetchAllInList(dbHelper.user);
    }

    public void createData(User user){
        PojoDBObjectHelper.createOrUpdate(dbHelper.user, user);
        LogUtils.d(TAG, "Created user ....");
    }

    public void deleteDataByKey(String id){
        PojoDBObjectHelper.delete(
                dbHelper.user,
                USER_COLUMN_ID + " = ?  ",
                new String[] { id }
        );
    }
    public void deleteData(){
        PojoDBObjectHelper.deleteAll(dbHelper.user);
    }
}
