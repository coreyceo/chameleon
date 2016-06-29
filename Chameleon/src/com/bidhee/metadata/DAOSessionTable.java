package com.bidhee.metadata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bidhee.dao.DatabaseHelper;
import com.bidhee.model.DTOSession;

public class DAOSessionTable {
	
	private Context context;
	DatabaseHelper dbHelper;
	private SQLiteDatabase ourDatabase;
	public DAOSessionTable(Context context) {
		this.context = context;
	}
		
	public long createSession(DTOSession session) {
		dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(SessionMetadata.KEY_SESSION_ID, session.getSessionId());
		values.put(SessionMetadata.KEY_SESSION_USER_ID, session.getSessionUserId());
		values.put(SessionMetadata.KEY_IS_SESSION_ACTIVE, session.isSessionActive());
		long sessionId = db.insert(SessionMetadata.TABLE_SESSION, null, values);
		db.close();
		dbHelper.close();
		return sessionId;
	}

	public long getSessionUserId() {
		String selectQuery = "SELECT " + SessionMetadata.KEY_SESSION_USER_ID + " FROM " + SessionMetadata.TABLE_SESSION;
		long temp = 0;
		dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				temp = c.getInt(c.getColumnIndex(SessionMetadata.KEY_SESSION_USER_ID));
			} while (c.moveToNext());
		} else {
			System.err.println("SessionTableDAO getSessionUserId() NO ENTRY!!");
		}
		
		db.close();
		dbHelper.close();
		return temp;
		
	}
	
	public int getSessionState() {
		String selectQuery = "SELECT " + SessionMetadata.KEY_IS_SESSION_ACTIVE + " FROM " + SessionMetadata.TABLE_SESSION;
		int temp = 0;
		dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			do {
				temp = c.getInt(c.getColumnIndex(SessionMetadata.KEY_IS_SESSION_ACTIVE));
			} while (c.moveToNext());
		} else {
			System.err.println("SessionTableDAO getSessionState() NO ENTRY!!");
		}
		db.close();
		dbHelper.close();
		return temp;
	}
	
	public int updateSession(DTOSession session) {
		dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SessionMetadata.KEY_SESSION_USER_ID, session.getSessionUserId());
		
		if (session.isSessionActive()){
			values.put(SessionMetadata.KEY_IS_SESSION_ACTIVE, 1);	
		} else {
			values.put(SessionMetadata.KEY_IS_SESSION_ACTIVE, 0);
		}
		
		int updateId = db.update(SessionMetadata.TABLE_SESSION, values, SessionMetadata.KEY_SESSION_ID+ " = ?", 
				new String[] { 
				String.valueOf(session.getSessionId()) 
			});
		db.close();
		dbHelper.close();
		return updateId;
	}

	public void deleteSession(DTOSession session) {
		dbHelper = new DatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(SessionMetadata.TABLE_SESSION, DatabaseHelper.KEY_ID + " = ?",
				new String[] { 
					String.valueOf(session.getSessionId()) 
				});
		db.close();
		dbHelper.close();
	}
	
	public void deletAllData(){
		dbHelper = new DatabaseHelper(context);
		ourDatabase = dbHelper.getWritableDatabase();
		ourDatabase.delete(SessionMetadata.TABLE_SESSION,null,null);
		ourDatabase.close();
	}
	
	
	
}
