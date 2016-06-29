package com.bidhee.metadata;

public class DatabaseMetadata {
	/*
	 * Database version and name
	 */
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "db_ksf_chameleon";
	
	/*
	 * Common table columns names
	 */
	public static final String KEY_ID = "id";
	public static final String KEY_CREATED_AT = "created_at";
	
	/*
	 * Singleton Database Metadata
	 */
	private DatabaseMetadata() { }

}
