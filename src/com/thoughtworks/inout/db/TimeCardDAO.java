package com.thoughtworks.inout.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thoughtworks.inout.PunchType;

public class TimeCardDAO extends SQLiteOpenHelper {

	private static final String DB_NAME = "inout.db";
	private static final int DB_VERSION = 1;

	private static class TimeCardTable {
		private static final String NAME = "timecard";
		private static final String COL_ID = "id";
		private static final String COL_DATE = "date";
		private static final String COL_TYPE = "type";
	}

	private SQLiteDatabase db;

	// Constructor to simplify Business logic access to the repository 
	public TimeCardDAO(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s DATE, %s TEXT)",
				TimeCardTable.NAME, TimeCardTable.COL_ID, TimeCardTable.COL_DATE,
				TimeCardTable.COL_TYPE));
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// Later when you change the DB_VERSION 
		// This code will be invoked to bring your database
		// Upto the correct specification
	}

	public void insertPunch(Date punchDate, PunchType punchType) {		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		ContentValues values = new ContentValues(); 
		values.put("date", dateFormat.format(punchDate));
		values.put("type", punchType.toString().toLowerCase());
		db.insert(TimeCardTable.NAME, null, values);
	}
}

