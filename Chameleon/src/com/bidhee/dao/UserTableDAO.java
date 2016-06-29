package com.bidhee.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bidhee.metadata.UserMetadata;
import com.bidhee.model.UserModel;

public class UserTableDAO {

	Context context;
	DatabaseHelper dbHelper;
	private SQLiteDatabase ourDatabase;
	public UserTableDAO(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}

	public long createUser(UserModel user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(UserMetadata.KEY_USER_ID, user.getUserId());
		values.put(UserMetadata.KEY_USER_NAME, user.getUserName());
		values.put(UserMetadata.KEY_USER_AUTH, user.getSecretKey());
		values.put(UserMetadata.KEY_USER_EMAIL, user.getUserEmail());
		values.put(UserMetadata.KEY_USER_GENDER, user.getUserGender());
		values.put(UserMetadata.KEY_USER_IMAGE, user.getUserImage());
		values.put(UserMetadata.KEY_SOCIAL_MEDIA_ID, user.getSocialID());
		values.put(UserMetadata.KEY_SOCIAL_MEDIA_TYPE, user.getSocialMediaType());
		values.put(UserMetadata.KEY_USER_DOB, user.getUserDob());
		
		long userId = db.insert(UserMetadata.TABLE_USERS, null, values);

		db.close();
		return userId;
	}
	
	public long createUserArray(ArrayList<UserModel> userArray) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		for (UserModel user : userArray) {
			values.put(UserMetadata.KEY_USER_ID, user.getUserId());
			values.put(UserMetadata.KEY_USER_NAME, user.getUserName());
			values.put(UserMetadata.KEY_USER_AUTH, user.getSecretKey());
			values.put(UserMetadata.KEY_USER_EMAIL, user.getUserEmail());
			values.put(UserMetadata.KEY_USER_GENDER, user.getUserGender());
			values.put(UserMetadata.KEY_USER_IMAGE, user.getUserImage());
			values.put(UserMetadata.KEY_SOCIAL_MEDIA_ID, user.getSocialID());
			values.put(UserMetadata.KEY_SOCIAL_MEDIA_TYPE, user.getSocialMediaType());
			values.put(UserMetadata.KEY_USER_DOB, user.getUserDob());
			
			
		}
		
		long userId = db.insert(UserMetadata.TABLE_USERS, null, values);

		db.close();
		return userId;
	}
	
	public UserModel getDataProfile(long userId) {

		UserModel userDetails = new UserModel();
		String selectQuery = "SELECT * FROM " + UserMetadata.TABLE_USERS;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (!(c.getCount() > 0)) {
			System.err.println("no entries found");
			return null;
		}
		if (c.moveToFirst()) {
			userDetails.setUserId((c.getInt((c.getColumnIndex(UserMetadata.KEY_USER_ID)))));
			userDetails.setSecretKey(c.getString(c.getColumnIndex(UserMetadata.KEY_USER_AUTH)));
			userDetails.setUserName(c.getString(c.getColumnIndex(UserMetadata.KEY_USER_NAME)));
			userDetails.setUserEmail(c.getString(c.getColumnIndex(UserMetadata.KEY_USER_EMAIL)));
			userDetails.setUserGender(c.getString(c.getColumnIndex(UserMetadata.KEY_USER_GENDER)));
			userDetails.setUserImage(c.getString(c.getColumnIndex(UserMetadata.KEY_USER_IMAGE)));
			userDetails.setSocialID(c.getString(c.getColumnIndex(UserMetadata.KEY_SOCIAL_MEDIA_ID)));
			userDetails.setSocialMediaType(c.getString(c.getColumnIndex(UserMetadata.KEY_SOCIAL_MEDIA_TYPE)));
			userDetails.setUserDob(c.getString(c.getColumnIndex(UserMetadata.KEY_USER_DOB)));
			
		}
		db.close();
		return userDetails;

	}
	
	public String getSecretKey() {

		String userAuth = "";
		String selectQuery = "SELECT * FROM " + UserMetadata.TABLE_USERS;
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.moveToFirst()) {
			userAuth = (c.getString(c.getColumnIndex(UserMetadata.KEY_USER_AUTH)));
		}
		db.close();
		return userAuth;

	}

	public long getUserIdFromEmail(String userEmail) {
		String selectQuery = "SELECT " + DatabaseHelper.KEY_ID + " FROM "
				+ UserMetadata.TABLE_USERS + " WHERE "
				+ UserMetadata.KEY_USER_EMAIL + " = " + "'" + userEmail + "'";
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			System.out.println("Returning User Id for Session");
			long userId = c.getLong(c.getColumnIndex(DatabaseHelper.KEY_ID));
			db.close();
			return userId;
		} else {
			System.out
					.println("Could not return user id for Session. Hence, returning 0");
			db.close();
			return 0;
		}
	}

	public int updateUser(UserModel user) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int updateId  = 0;
		ContentValues values = new ContentValues();
		values.put(UserMetadata.KEY_USER_NAME, user.getUserName());
		values.put(UserMetadata.KEY_USER_GENDER, user.getUserGender());
		values.put(UserMetadata.KEY_USER_IMAGE, user.getUserImage());
		values.put(UserMetadata.KEY_USER_DOB, user.getUserDob());
		
		try{
			updateId = db.update(UserMetadata.TABLE_USERS, values,null,null);

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		db.close();
		
		return updateId;
	}



	public void deleteUser(UserModel user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(UserMetadata.TABLE_USERS, DatabaseHelper.KEY_ID + " = ?",
				new String[] { String.valueOf(user.getUserId()) });
		db.close();
	}
	
	public void deletAllData(){
		dbHelper = new DatabaseHelper(context);
		ourDatabase = dbHelper.getWritableDatabase();
		ourDatabase.delete(UserMetadata.TABLE_USERS,
				null,null);
	}
	
}
