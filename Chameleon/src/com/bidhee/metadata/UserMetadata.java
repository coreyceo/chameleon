package com.bidhee.metadata;




public class UserMetadata {
	
	private UserMetadata() { }
	
	
	public static final String TABLE_USERS = "tb_chameleon_users";
	
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_USER_AUTH = "user_auth";
	public static final String KEY_USER_NAME = "user_name";
	public static final String KEY_USER_EMAIL = "user_email";
	public static final String KEY_USER_GENDER = "user_sex";
	public static final String KEY_USER_IMAGE = "user_image";
	public static final String KEY_SOCIAL_MEDIA_TYPE = "user_social_media_type";
	public static final String KEY_SOCIAL_MEDIA_ID = "user_social_id";
	public static final String KEY_USER_DOB = "user_dob";
	
	
	/*
	 * Create User Table SQL Query
	 */
	
	public static final String SQL_CREATE_TABLE_USERS = " CREATE TABLE " + TABLE_USERS + " ( " 
			+ KEY_USER_ID + " INTEGER UNIQUE,"
			+ KEY_USER_AUTH + " VARCHAR,"
			+ KEY_USER_NAME + " VARCHAR, "
			+ KEY_USER_EMAIL + " VARCHAR, "
			+ KEY_USER_GENDER + " VARCHAR, "
			+ KEY_SOCIAL_MEDIA_ID + " VARCHAR, "
			+ KEY_SOCIAL_MEDIA_TYPE + " VARCHAR, "
			+ KEY_USER_DOB + " VARCHAR, "
			+ KEY_USER_IMAGE + " VARCHAR "
			+ " ) ";
}
