package com.sunmi.pda.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sunmi.pda.application.SunmiApplication;

public class DatabaseOpenHelper extends SQLiteOpenHelper implements DatabaseConstants {

	private static final SunmiApplication app = SunmiApplication.getInstance();

	private static final String TAG = DatabaseOpenHelper.class.getSimpleName();

	public DatabaseOpenHelper() {
		super(app, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(LOGIN_CREATE);
		database.execSQL(PLANT_MASTER_CREATE);
		database.execSQL(LOGISTICS_PROVIDER_CREATE);
		database.execSQL(MATERIAL_CREATE);
		database.execSQL(USER_CREATE);
		database.execSQL(OFFLINE_CREATE);
		Log.i(TAG, "Create tabls is succeed...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (oldVersion == 9  && newVersion == 10) {
			Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
			database.execSQL(LOGIN_CREATE);
			database.execSQL(PLANT_MASTER_CREATE);
			database.execSQL(LOGISTICS_PROVIDER_CREATE);
			database.execSQL(MATERIAL_CREATE);
			database.execSQL(USER_CREATE);
			database.execSQL(OFFLINE_CREATE);
			database.execSQL("ALTER TABLE TABLE_LOGIN ADD userJson TEXT");
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
}