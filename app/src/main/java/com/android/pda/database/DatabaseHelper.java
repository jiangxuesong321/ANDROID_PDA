package com.android.pda.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;

import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Login;
import com.android.pda.database.pojo.LogisticsProvider;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.Offline;
import com.android.pda.database.pojo.StorageLocation;
import com.android.pda.database.pojo.User;
import com.android.pda.database.pojo.UserInfo;
import com.delawareconsulting.libtools.database.PojoDBObject;
import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.delawareconsulting.libtools.database.SimplePojoDBObject;

/**
 * Used for communication with the database
 */
class DatabaseHelper implements DatabaseConstants {
    private final static AndroidApplication app = AndroidApplication.getInstance();

    PojoDBObject<Login> login;
    PojoDBObject<StorageLocation> plant;
    PojoDBObject<LogisticsProvider> logisticsProvider;
    PojoDBObject<Material> material;
    PojoDBObject<User> user;
    PojoDBObject<Offline> offline;
    PojoDBObject<UserInfo> userInfo;

    public DatabaseHelper() {
        login = createLoginDB();
        plant = createPlantDB();
        logisticsProvider = createLogisticsProviderDB();
        material = createMaterialDB();
        user = createUserDB();
        offline = createOfflineDB();
        userInfo = createUserInfoDB();
    }

    public void deleteAll() {
        PojoDBObjectHelper.deleteAll(login);
    }

    /**
     * Login DB Helper
     *
     * @return Login
     */
    private SimplePojoDBObject<Login> createLoginDB() {
        return new SimplePojoDBObject<Login>(app, TABLE_LOGIN, DatabaseOpenHelper.class) {
            @Override
            public Login convertToPojo(Cursor cursor) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_ID));
                String func = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_FUNCTION));
                String factory = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_FACTORY));
                String storageLocation = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_STORAGE_LOCATION));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_NAME));
                String json = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_JSON));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(LOGIN_COLUMN_CITY));
                return new Login(id, func, factory, storageLocation, name, json, city);
            }

            @Override
            public ContentValues createContentValues(Login pojo) {
                ContentValues cv = new ContentValues(7);
                cv.put(LOGIN_COLUMN_ID, pojo.getZuid());
                cv.put(LOGIN_COLUMN_FUNCTION, pojo.getZfunc());
                cv.put(LOGIN_COLUMN_FACTORY, pojo.getZfactory());
                cv.put(LOGIN_COLUMN_STORAGE_LOCATION, pojo.getZstore_loc());
                cv.put(LOGIN_COLUMN_NAME, pojo.getZuname());
                cv.put(LOGIN_COLUMN_JSON, pojo.getZujson());
                cv.put(LOGIN_COLUMN_CITY, pojo.getZcity());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return LOGIN_COLUMN_ID + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(Login pojo) {
                return new String[]{pojo.getZuid()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, Login pojo) {
                final int colId = helper.getColumnIndex(LOGIN_COLUMN_ID);
                final int colName = helper.getColumnIndex(LOGIN_COLUMN_NAME);
                final int colFunc = helper.getColumnIndex(LOGIN_COLUMN_FUNCTION);
                final int colFactory = helper.getColumnIndex(LOGIN_COLUMN_FACTORY);
                final int colStorageLocation = helper.getColumnIndex(LOGIN_COLUMN_STORAGE_LOCATION);
                final int colJson = helper.getColumnIndex(LOGIN_COLUMN_JSON);
                final int colCity = helper.getColumnIndex(LOGIN_COLUMN_CITY);
                //helper.bind(colNumber, pojo.materialNumber);
                helper.bind(colId, pojo.getZuid());
                helper.bind(colName, pojo.getZuname());
                helper.bind(colFunc, pojo.getZfunc());
                helper.bind(colFactory, pojo.getZfactory());
                helper.bind(colStorageLocation, pojo.getZstore_loc());
                helper.bind(colJson, pojo.getZujson());
                helper.bind(colCity, pojo.getZcity());
            }
        };
    }

    /**
     * User DB Helper
     *
     * @return User
     */
    private SimplePojoDBObject<User> createUserDB() {
        return new SimplePojoDBObject<User>(app, TABLE_USER, DatabaseOpenHelper.class) {
            @Override
            public User convertToPojo(Cursor cursor) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_NAME));
                String group = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_GROUP));
                String depart = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_DEPART));
                return new User(id, name, group, depart);
            }

            @Override
            public ContentValues createContentValues(User pojo) {
                ContentValues cv = new ContentValues(4);
                cv.put(USER_COLUMN_ID, pojo.getUserId());
                cv.put(USER_COLUMN_NAME, pojo.getUserName());
                cv.put(USER_COLUMN_GROUP, pojo.getGroup());
                cv.put(USER_COLUMN_DEPART, pojo.getDepart());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return USER_COLUMN_ID + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(User pojo) {
                return new String[]{pojo.getUserId()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, User pojo) {
                final int colId = helper.getColumnIndex(USER_COLUMN_ID);
                final int colName = helper.getColumnIndex(USER_COLUMN_NAME);
                final int colGroup = helper.getColumnIndex(USER_COLUMN_GROUP);
                final int colDepart = helper.getColumnIndex(USER_COLUMN_DEPART);

                helper.bind(colId, pojo.getUserId());
                helper.bind(colName, pojo.getUserName());
                helper.bind(colGroup, pojo.getGroup());
                helper.bind(colDepart, pojo.getDepart());
            }
        };
    }

    /**
     * UserInfo DB Helper
     *
     * @return User
     */
    private SimplePojoDBObject<UserInfo> createUserInfoDB() {
        return new SimplePojoDBObject<UserInfo>(app, TABLE_USER_INFO, DatabaseOpenHelper.class) {
            @Override
            public UserInfo convertToPojo(Cursor cursor) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(USER_INFO_COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(USER_INFO_COLUMN_NAME));
                String apiKey = cursor.getString(cursor.getColumnIndexOrThrow(USER_INFO_COLUMN_APIKEY));
                return new UserInfo(id, name, apiKey);
            }

            @Override
            public ContentValues createContentValues(UserInfo pojo) {
                ContentValues cv = new ContentValues(3);
                cv.put(USER_INFO_COLUMN_ID, pojo.getUserId());
                cv.put(USER_INFO_COLUMN_NAME, pojo.getUserName());
                cv.put(USER_INFO_COLUMN_APIKEY, pojo.getApiKey());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return USER_INFO_COLUMN_ID + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(UserInfo pojo) {
                return new String[]{pojo.getUserId()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, UserInfo pojo) {
                final int colId = helper.getColumnIndex(USER_INFO_COLUMN_ID);
                final int colName = helper.getColumnIndex(USER_INFO_COLUMN_NAME);
                final int colApiKey = helper.getColumnIndex(USER_INFO_COLUMN_APIKEY);

                helper.bind(colId, pojo.getUserId());
                helper.bind(colName, pojo.getUserName());
                helper.bind(colApiKey, pojo.getApiKey());
            }
        };
    }

    /**
     * Plant Master DB Helper
     *
     * @return StorageLocation
     */
    private SimplePojoDBObject<StorageLocation> createPlantDB() {
        return new SimplePojoDBObject<StorageLocation>(app, TABLE_PLANT_MASTER, DatabaseOpenHelper.class) {
            @Override
            public StorageLocation convertToPojo(Cursor cursor) {
                String plant = cursor.getString(cursor.getColumnIndexOrThrow(PLANT_MASTER_PLANT));
                String storageLocation = cursor.getString(cursor.getColumnIndexOrThrow(PLANT_MASTER_STORAGE_LOCATION));
                String plantName = cursor.getString(cursor.getColumnIndexOrThrow(PLANT_MASTER_PLANT_NAME));
                String storageLocationName = cursor.getString(cursor.getColumnIndexOrThrow(PLANT_MASTER_STORAGE_LOCATION_NAME));

                return new StorageLocation(plant, storageLocation, plantName, storageLocationName);
            }

            @Override
            public ContentValues createContentValues(StorageLocation pojo) {
                ContentValues cv = new ContentValues(4);
                cv.put(PLANT_MASTER_PLANT, pojo.getPlant());
                cv.put(PLANT_MASTER_STORAGE_LOCATION, pojo.getStorageLocation());
                cv.put(PLANT_MASTER_PLANT_NAME, pojo.getPlantName());
                cv.put(PLANT_MASTER_STORAGE_LOCATION_NAME, pojo.getStorageLocationName());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return PLANT_MASTER_PLANT + " = ? and " + PLANT_MASTER_STORAGE_LOCATION + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(StorageLocation pojo) {
                return new String[]{pojo.getPlant(), pojo.getStorageLocation()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, StorageLocation pojo) {
                final int colId = helper.getColumnIndex(PLANT_MASTER_PLANT);
                final int colName = helper.getColumnIndex(PLANT_MASTER_STORAGE_LOCATION);
                final int colBusiness = helper.getColumnIndex(PLANT_MASTER_PLANT_NAME);
                final int colPlant = helper.getColumnIndex(PLANT_MASTER_STORAGE_LOCATION_NAME);

                helper.bind(colId, pojo.getPlant());
                helper.bind(colName, pojo.getStorageLocation());
                helper.bind(colBusiness, pojo.getPlantName());
                helper.bind(colPlant, pojo.getStorageLocationName());
            }
        };
    }

    /**
     * Logistics DB Helper
     *
     * @return LogisticsProvider
     */
    private SimplePojoDBObject<LogisticsProvider> createLogisticsProviderDB() {
        return new SimplePojoDBObject<LogisticsProvider>(app, TABLE_LOGISTICS_PROVIDER, DatabaseOpenHelper.class) {
            @Override
            public LogisticsProvider convertToPojo(Cursor cursor) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(LOGISTICS_PROVIDER_COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(LOGISTICS_PROVIDER_COLUMN_NAME));
                return new LogisticsProvider(id, name);
            }

            @Override
            public ContentValues createContentValues(LogisticsProvider pojo) {
                ContentValues cv = new ContentValues(5);
                cv.put(LOGISTICS_PROVIDER_COLUMN_ID, pojo.getZtId());
                cv.put(LOGISTICS_PROVIDER_COLUMN_NAME, pojo.getZtName());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return LOGISTICS_PROVIDER_COLUMN_ID + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(LogisticsProvider pojo) {
                return new String[]{pojo.getZtId()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, LogisticsProvider pojo) {
                final int colId = helper.getColumnIndex(LOGISTICS_PROVIDER_COLUMN_ID);
                final int colName = helper.getColumnIndex(LOGISTICS_PROVIDER_COLUMN_NAME);

                helper.bind(colId, pojo.getZtId());
                helper.bind(colName, pojo.getZtName());
            }
        };
    }

    /**
     * Material Info DB Helper
     *
     * @return Material
     */
    private SimplePojoDBObject<Material> createMaterialDB() {
        return new SimplePojoDBObject<Material>(app, TABLE_MATERIAL, DatabaseOpenHelper.class) {
            @Override
            public Material convertToPojo(Cursor cursor) {
                String material = cursor.getString(cursor.getColumnIndexOrThrow(MATERIAL_MATERIAL));
                String materialName = cursor.getString(cursor.getColumnIndexOrThrow(MATERIAL_MATERIAL_NAME));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(MATERIAL_UNIT));
                String batchFlag = cursor.getString(cursor.getColumnIndexOrThrow(MATERIAL_BATCH_FLAG));
                String serialFlag = cursor.getString(cursor.getColumnIndexOrThrow(MATERIAL_SERIAL_FLAG));
                String barcode = cursor.getString(cursor.getColumnIndexOrThrow(MATERIAL_BARCODE));
                long creationDate = cursor.getLong(cursor.getColumnIndexOrThrow(MATERIAL_CREATION_DATE));
                long lastChangeDate = cursor.getLong(cursor.getColumnIndexOrThrow(MATERIAL_LAST_CHANGE_DATE));
                return new Material(material, materialName, unit, batchFlag, serialFlag, barcode, creationDate, lastChangeDate);
            }

            @Override
            public ContentValues createContentValues(Material pojo) {
                ContentValues cv = new ContentValues(8);
                cv.put(MATERIAL_MATERIAL, pojo.getMaterial());
                cv.put(MATERIAL_MATERIAL_NAME, pojo.getMaterialName());
                cv.put(MATERIAL_UNIT, pojo.getUnit());
                cv.put(MATERIAL_BATCH_FLAG, pojo.getBatchFlag());
                cv.put(MATERIAL_SERIAL_FLAG, pojo.getSerialFlag());
                cv.put(MATERIAL_BARCODE, pojo.getBarCode());
                cv.put(MATERIAL_CREATION_DATE, pojo.getCreationDate());
                cv.put(MATERIAL_LAST_CHANGE_DATE, pojo.getLastChangeDate());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return MATERIAL_MATERIAL + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(Material pojo) {
                return new String[]{pojo.getMaterial()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, Material pojo) {
                final int colMaterial = helper.getColumnIndex(MATERIAL_MATERIAL);
                final int colMaterialName = helper.getColumnIndex(MATERIAL_MATERIAL_NAME);
                final int colUnit = helper.getColumnIndex(MATERIAL_UNIT);
                final int colBatchFlag = helper.getColumnIndex(MATERIAL_BATCH_FLAG);
                final int colSerialFlag = helper.getColumnIndex(MATERIAL_SERIAL_FLAG);
                final int colBarcode = helper.getColumnIndex(MATERIAL_BARCODE);
                final int colCreationDate = helper.getColumnIndex(MATERIAL_CREATION_DATE);
                final int colLastDate = helper.getColumnIndex(MATERIAL_LAST_CHANGE_DATE);
                helper.bind(colMaterial, pojo.getMaterial());
                helper.bind(colMaterialName, pojo.getMaterialName());
                helper.bind(colUnit, pojo.getUnit());
                helper.bind(colBatchFlag, pojo.getBatchFlag());
                helper.bind(colSerialFlag, pojo.getSerialFlag());
                helper.bind(colBarcode, pojo.getBarCode());
                helper.bind(colCreationDate, pojo.getCreationDate());
                helper.bind(colLastDate, pojo.getLastChangeDate());
            }
        };
    }

    /**
     * 暂存数据 DB Helper
     *
     * @return Offline
     */
    private SimplePojoDBObject<Offline> createOfflineDB() {
        return new SimplePojoDBObject<Offline>(app, TABLE_OFFLINE, DatabaseOpenHelper.class) {
            @Override
            public Offline convertToPojo(Cursor cursor) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_ID));
                String orderBody = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_ORDER_BODY));
                String orderNumber = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_ORDER_NUMBER));
                String postBody = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_POST_BODY));
                String url = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_URL));
                String postDate = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_POST_DATE));
                String logisticsProvider = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_LOGISTICS_PROVIDER));
                String logisticNumber = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_LOGISTIC_NUMBER));
                String plant = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_PLANT));
                String sendLocation = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_SEND_LOCATION));
                String receiveLocation = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_RECEIVE_LOCATION));
                String reserve1 = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_RESERVE1));
                String reserve2 = cursor.getString(cursor.getColumnIndexOrThrow(OFFLINE_RESERVE2));
                return new Offline(id, orderBody, orderNumber, postBody, url, postDate,
                        logisticsProvider, logisticNumber, plant, sendLocation, receiveLocation, reserve1, reserve2);
            }

            @Override
            public ContentValues createContentValues(Offline pojo) {
                ContentValues cv = new ContentValues(13);
                cv.put(OFFLINE_ID, pojo.getId());
                cv.put(OFFLINE_ORDER_BODY, pojo.getOrderBody());
                cv.put(OFFLINE_ORDER_NUMBER, pojo.getOrderNumber());
                cv.put(OFFLINE_POST_BODY, pojo.getPostBody());
                cv.put(OFFLINE_URL, pojo.getUrl());
                cv.put(OFFLINE_POST_DATE, pojo.getPostDate());
                cv.put(OFFLINE_LOGISTICS_PROVIDER, pojo.getLogisticsProvider());
                cv.put(OFFLINE_LOGISTIC_NUMBER, pojo.getLogisticNumber());
                cv.put(OFFLINE_PLANT, pojo.getPlant());
                cv.put(OFFLINE_SEND_LOCATION, pojo.getSendLocation());
                cv.put(OFFLINE_RECEIVE_LOCATION, pojo.getReceiveLocation());
                cv.put(OFFLINE_RESERVE1, pojo.getReserve1());
                cv.put(OFFLINE_RESERVE2, pojo.getReserve2());
                return cv;
            }

            @Override
            public String getIdWhereClause() {
                return OFFLINE_ID + " = ?";
            }

            @Override
            public String[] getIdWhereArgs(Offline pojo) {
                return new String[]{pojo.getId()};
            }

            @Override
            public void prepareInsertHelper(InsertHelper helper, Offline pojo) {
                final int colId = helper.getColumnIndex(OFFLINE_ID);
                final int colOrderBody = helper.getColumnIndex(OFFLINE_ORDER_BODY);
                final int colOrderNumber = helper.getColumnIndex(OFFLINE_ORDER_NUMBER);
                final int colPostBody = helper.getColumnIndex(OFFLINE_POST_BODY);
                final int colUrl = helper.getColumnIndex(OFFLINE_URL);
                final int colPostDate = helper.getColumnIndex(OFFLINE_POST_DATE);
                final int colLogisticsProvider = helper.getColumnIndex(OFFLINE_LOGISTICS_PROVIDER);
                final int colLogisticNumber = helper.getColumnIndex(OFFLINE_LOGISTIC_NUMBER);
                final int colPlant = helper.getColumnIndex(OFFLINE_PLANT);
                final int colSendLocation = helper.getColumnIndex(OFFLINE_SEND_LOCATION);
                final int colReceiveLocation = helper.getColumnIndex(OFFLINE_RECEIVE_LOCATION);
                final int colReserve1 = helper.getColumnIndex(OFFLINE_RESERVE1);
                final int colReserve2 = helper.getColumnIndex(OFFLINE_RESERVE2);
                helper.bind(colId, pojo.getId());
                helper.bind(colOrderBody, pojo.getOrderBody());
                helper.bind(colOrderNumber, pojo.getOrderNumber());
                helper.bind(colPostBody, pojo.getPostBody());
                helper.bind(colUrl, pojo.getUrl());
                helper.bind(colPostDate, pojo.getPostDate());
                helper.bind(colLogisticsProvider, pojo.getLogisticsProvider());
                helper.bind(colLogisticNumber, pojo.getLogisticNumber());
                helper.bind(colPlant, pojo.getPlant());
                helper.bind(colSendLocation, pojo.getSendLocation());
                helper.bind(colReceiveLocation, pojo.getReceiveLocation());
                helper.bind(colReserve1, pojo.getReserve1());
                helper.bind(colReserve2, pojo.getReserve2());
            }
        };
    }
}