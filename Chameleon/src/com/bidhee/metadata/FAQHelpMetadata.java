package com.bidhee.metadata;

public class FAQHelpMetadata {
	
	public FAQHelpMetadata() {
	
	}
	
	public static final String TABLE_FAQ_HELP = "tb_help_faq";
	public static final String KEY_FAQ = "col_faq";
	public static final String KEY_HELP = "col_help";
	
	public static final String SQL_CREATE_TABLE_FAQ = " CREATE TABLE " + TABLE_FAQ_HELP+ " ( " 
			+ KEY_FAQ + " VARCHAR,"
			+ KEY_HELP + " VARCHAR "
			+ " ) ";

}
