package com.bidhee.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.bidhee.metadata.DatabaseMetadata;
import com.bidhee.metadata.FAQHelpMetadata;
import com.bidhee.metadata.SessionMetadata;
import com.bidhee.metadata.UserMetadata;

public class DatabaseHelper extends SQLiteOpenHelper {

	Context context;

	public static final String KEY_ID = "id";
	public static final String KEY_CREATED_AT = "created_at";
	
	public DatabaseHelper(Context context) {
		super(context, DatabaseMetadata.DATABASE_NAME, null,
				DatabaseMetadata.DATABASE_VERSION);
		this.context = context;
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDb) {
		
		
		sqliteDb.execSQL(SessionMetadata.SQL_CREATE_TABLE_SESSION);
		sqliteDb.execSQL(UserMetadata.SQL_CREATE_TABLE_USERS);
		sqliteDb.execSQL(FAQHelpMetadata.SQL_CREATE_TABLE_FAQ);
		
		ContentValues cvSession = new ContentValues();
		cvSession.put(SessionMetadata.KEY_SESSION_USER_ID, 0);
		cvSession.put(SessionMetadata.KEY_IS_SESSION_ACTIVE, 0);
		sqliteDb.insert(SessionMetadata.TABLE_SESSION, null, cvSession);
			
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDb, int oldVersion,int newVersion) {
		
		sqliteDb.execSQL("DROP TABLE IF EXISTS " + UserMetadata.TABLE_USERS);
		sqliteDb.execSQL("DROP TABLE IF EXISTS " + SessionMetadata.TABLE_SESSION);
		sqliteDb.execSQL("DROP TABLE IF EXISTS " + FAQHelpMetadata.TABLE_FAQ_HELP);
		
		
		onCreate(sqliteDb);
	}
}
