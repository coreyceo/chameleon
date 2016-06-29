package com.bidhee.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.bidhee.metadata.FAQHelpMetadata;
import com.bidhee.model.HelpFaqModel;

public class FAQHelpDao {
	
	Context context;
	DatabaseHelper dbHelper;
	private SQLiteDatabase ourDatabase;
	
	public FAQHelpDao(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}

	public long createFaqs(HelpFaqModel faq) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(FAQHelpMetadata.KEY_FAQ, faq.getFaq());
		values.put(FAQHelpMetadata.KEY_HELP, faq.getHelp());
		
		
		long userId = db.insert(FAQHelpMetadata.TABLE_FAQ_HELP, null, values);

		db.close();
		return userId;
	}
	
	public long createFaqArray(ArrayList<HelpFaqModel> faqArray) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long faqId = 0;
		ContentValues values = new ContentValues();

		for (HelpFaqModel faqHelp : faqArray) {
			values.put(FAQHelpMetadata.KEY_FAQ, faqHelp.getFaq());
			values.put(FAQHelpMetadata.KEY_HELP, faqHelp.getHelp());
		}
		try{
			faqId= db.insert(FAQHelpMetadata.TABLE_FAQ_HELP, null, values);
		}
		catch(SQLiteException e){
			e.printStackTrace();
			
		}

		db.close();
		return faqId;
	}
	
	public HelpFaqModel getFaqHelp() {

		HelpFaqModel faqHelpModel = new HelpFaqModel();
		String selectQuery = "SELECT * FROM " + FAQHelpMetadata.TABLE_FAQ_HELP;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (!(c.getCount() > 0)) {
			System.err.println("no entries found");
			return null;
		}
		if (c.moveToFirst()) {
			faqHelpModel.setFaq(c.getString((c.getColumnIndex(FAQHelpMetadata.KEY_FAQ))));
			faqHelpModel.setHelp(c.getString((c.getColumnIndex(FAQHelpMetadata.KEY_HELP))));
			
			
		}
		dbHelper.close();
		c.close();
		db.close();
		return faqHelpModel;

	}
	
	
	public void deletAllData(){
		try{
		dbHelper = new DatabaseHelper(context);
		ourDatabase = dbHelper.getWritableDatabase();
		ourDatabase.delete(FAQHelpMetadata.TABLE_FAQ_HELP,null,null);
		
		}
		catch(SQLiteException e){
			e.printStackTrace();
		}
		ourDatabase.close();
		dbHelper.close();
	}
	

}
