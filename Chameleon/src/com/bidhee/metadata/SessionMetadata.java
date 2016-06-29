package com.bidhee.metadata;



public class SessionMetadata {
	/*
	 * Singleton Session Metadata
	 */
	private SessionMetadata() { }
	
	/*
	 * Comment table name
	 */
	public static final String TABLE_SESSION = "chameleon_session";
	
	/*
	 * Comment table columns names
	 * Comment can be for picture or product from a user
	 */
	public static final String KEY_SESSION_USER_ID = "session_user_id";
	public static final String KEY_IS_SESSION_ACTIVE = "is_session_active";
	public static final String KEY_SESSION_ID ="session_id";
	/*
	 * Create User Table SQL Query
	 */
	public static final String SQL_CREATE_TABLE_SESSION = " CREATE TABLE " + TABLE_SESSION + "(" 
			+ KEY_SESSION_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " 
			+ KEY_SESSION_USER_ID + " INTEGER, "
			+ KEY_IS_SESSION_ACTIVE + " INTEGER" + ")";//datetime replaced
}
