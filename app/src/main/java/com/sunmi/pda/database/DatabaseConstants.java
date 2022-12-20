package com.sunmi.pda.database;

interface DatabaseConstants {
	
	// name of the database file for your application
	static final String DATABASE_NAME = "pda.db";
	
	// any time you make changes to your database objects, you may have to
		// increase the database version
	static final int DATABASE_VERSION = 1;
	
	/**
	 * All tables and columns, also create sql statements are
	 * defined below.
	 */

	// Table Login
	static final String TABLE_LOGIN = "Login";
	static final String LOGIN_COLUMN_ID = "userId";
	static final String LOGIN_COLUMN_FUNCTION = "function";
	static final String LOGIN_COLUMN_FACTORY = "factory";
	static final String LOGIN_COLUMN_STORAGE_LOCATION = "storageLocation";
	static final String LOGIN_COLUMN_NAME = "userName";
	static final String LOGIN_COLUMN_JSON = "userJson";

	public static final String LOGIN_CREATE =
			"create table if not exists " + TABLE_LOGIN + "("
		    + LOGIN_COLUMN_ID + " text not null primary key, "
			+ LOGIN_COLUMN_FUNCTION + " text, "
			+ LOGIN_COLUMN_FACTORY + " text, "
			+ LOGIN_COLUMN_STORAGE_LOCATION + " text, "
			+ LOGIN_COLUMN_NAME + " text, "
					+ LOGIN_COLUMN_JSON + " text "
		    + ");";

	// Table User
	static final String TABLE_USER = "UserMaster";
	static final String USER_COLUMN_ID = "userId";
	static final String USER_COLUMN_NAME = "userName";
	static final String USER_COLUMN_GROUP = "userGroup";
	static final String USER_COLUMN_DEPART = "depart";


	public static final String USER_CREATE =
			"create table if not exists " + TABLE_USER  + "("
					+ USER_COLUMN_ID + " text not null primary key, "
					+ USER_COLUMN_NAME + " text, "
					+ USER_COLUMN_GROUP + " text, "
					+ USER_COLUMN_DEPART + " text "
					+ ");";

	static final String TABLE_OFFLINE = "Offline";
	static final String OFFLINE_ID = "Id";
	static final String OFFLINE_ORDER_BODY = "OrderBody";
	static final String OFFLINE_ORDER_NUMBER = "OrderNumber";
	static final String OFFLINE_POST_BODY = "PostBody";
	static final String OFFLINE_URL = "Url";
	static final String OFFLINE_POST_DATE = "PostDate";
	static final String OFFLINE_LOGISTICS_PROVIDER = "LogisticsProvider";
	static final String OFFLINE_LOGISTIC_NUMBER = "LogisticNumber";
	static final String OFFLINE_PLANT = "Plant";
	static final String OFFLINE_SEND_LOCATION = "SendLocation";
	static final String OFFLINE_RECEIVE_LOCATION = "ReceiveLocation";
	static final String OFFLINE_RESERVE1 = "Reserve1";
	static final String OFFLINE_RESERVE2 = "Reserve2";

	public static final String OFFLINE_CREATE =
			"create table if not exists " + TABLE_OFFLINE + "("
					+ OFFLINE_ID + " text not null primary key, "
					+ OFFLINE_ORDER_BODY + " text, "
					+ OFFLINE_ORDER_NUMBER + " text, "
					+ OFFLINE_POST_BODY + " text, "
					+ OFFLINE_URL + " text, "
					+ OFFLINE_POST_DATE + " text, "
					+ OFFLINE_LOGISTICS_PROVIDER + " text, "
					+ OFFLINE_LOGISTIC_NUMBER + " text, "
					+ OFFLINE_PLANT + " text, "
					+ OFFLINE_SEND_LOCATION + " text, "
					+ OFFLINE_RECEIVE_LOCATION + " text, "
					+ OFFLINE_RESERVE1 + " text, "
					+ OFFLINE_RESERVE2 + " text "
					+ ");";

	// Table Material
	static final String TABLE_MATERIAL = "MaterialMaster";
	static final String MATERIAL_MATERIAL = "Material";
	static final String MATERIAL_MATERIAL_NAME = "MaterialName";
	static final String MATERIAL_UNIT = "Unit";
	static final String MATERIAL_BATCH_FLAG = "BatchFlag";
	static final String MATERIAL_SERIAL_FLAG = "SerialFlag";
	static final String MATERIAL_BARCODE = "Barcode";
	static final String MATERIAL_CREATION_DATE = "CreationDate";
	static final String MATERIAL_LAST_CHANGE_DATE = "LastChangeDate";


	public static final String MATERIAL_CREATE =
			"create table if not exists " + TABLE_MATERIAL + "("
					+ MATERIAL_MATERIAL + " text not null primary key, "
					+ MATERIAL_MATERIAL_NAME + " text, "
					+ MATERIAL_UNIT + " text, "
					+ MATERIAL_BATCH_FLAG + " text, "
					+ MATERIAL_SERIAL_FLAG + " text, "
					+ MATERIAL_BARCODE + " text, "
					+ MATERIAL_CREATION_DATE + " long, "
					+ MATERIAL_LAST_CHANGE_DATE + " long "
					+ ");";


	// Table PlantMaster
	static final String TABLE_PLANT_MASTER = "PlantMaster";
	static final String PLANT_MASTER_PLANT = "plant";
	static final String PLANT_MASTER_STORAGE_LOCATION = "storageLocation";
	static final String PLANT_MASTER_PLANT_NAME = "plantName";
	static final String PLANT_MASTER_STORAGE_LOCATION_NAME = "storageLocationName";

	public static final String PLANT_MASTER_CREATE =
			"create table if not exists " + TABLE_PLANT_MASTER + "("
					+ PLANT_MASTER_PLANT + " text not null, "
					+ PLANT_MASTER_STORAGE_LOCATION + " text not null, "
					+ PLANT_MASTER_PLANT_NAME + " text, "
					+ PLANT_MASTER_STORAGE_LOCATION_NAME + " text, "
					+ " primary key (" + PLANT_MASTER_PLANT	+ "," + PLANT_MASTER_STORAGE_LOCATION + ")"
					+ ");";

	
	// Table LogisticsProvider
	static final String TABLE_LOGISTICS_PROVIDER = "LogisticsProvider";
	static final String LOGISTICS_PROVIDER_COLUMN_ID = "ztId";
	static final String LOGISTICS_PROVIDER_COLUMN_NAME = "ztName";


	public static final String LOGISTICS_PROVIDER_CREATE =
			"create table if not exists " + TABLE_LOGISTICS_PROVIDER + "("
					+ LOGISTICS_PROVIDER_COLUMN_ID + " text not null primary key, "
					+ LOGISTICS_PROVIDER_COLUMN_NAME + " text "
					+ ");";

}