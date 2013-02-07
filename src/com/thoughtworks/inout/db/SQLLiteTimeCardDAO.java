package com.thoughtworks.inout.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thoughtworks.inout.Punch;
import com.thoughtworks.inout.PunchType;
import com.thoughtworks.inout.exception.DataRetrieveException;

public class SQLLiteTimeCardDAO extends SQLiteOpenHelper implements TimeCardDAO {
	
	private final DateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final DateFormat sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

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
	public SQLLiteTimeCardDAO(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.db = this.getWritableDatabase();
	}
	
	public SQLLiteTimeCardDAO(Context context, SQLiteDatabase sqlLiteDatabase) {
		super(context, DB_NAME, null, DB_VERSION);
		this.db = sqlLiteDatabase;
	}
	
	public String getTableName() {
		return TimeCardTable.NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s DATE, %s TEXT)",
				TimeCardTable.NAME, TimeCardTable.COL_ID, TimeCardTable.COL_DATE,
				TimeCardTable.COL_TYPE));
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

	/* (non-Javadoc)
	 * @see com.thoughtworks.inout.db.TimeCardDAO#insertPunch(java.util.Date, com.thoughtworks.inout.PunchType)
	 */
	public void insertPunch(Punch punch) {		 
		ContentValues values = new ContentValues(); 
		System.out.println("NO INSERT ################## "+punch.getDate().getYear() );
		values.put("date", sqlDateFormat.format(punch.getDate()));
		values.put("type", punch.getType().toString().toLowerCase());
		db.insert(TimeCardTable.NAME, null, values);
	}
	
	public Date[] getAllRegisterDates() throws DataRetrieveException {
		List<Date> data = new ArrayList<Date>();
		Cursor c = this.db.rawQuery("select strftime('%Y-%m-%d', " +
				TimeCardTable.COL_DATE + ") as f_date from " +
				TimeCardTable.NAME + " group by f_date order by " +
				TimeCardTable.COL_DATE + " DESC", null);
		if (c != null) {
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {					
					try {
						data.add(sqlDateTimeFormat.parse(c.getString(0)));
					} catch (ParseException e) {
						Log.e("SQLLiteTimeCardDAO", "Error while trying to parse \"" + c.getString(0) + "\".");
						throw new DataRetrieveException("There was an error while trying to retrieve the data.");
					}
					c.moveToNext();
				}
			}
		}
		return data.toArray(new Date[0]);
	}
	
	@Override
	public Punch[] getAllPunchesFor(Date d) throws DataRetrieveException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DATE, 1);
		String formattedDate = sqlDateFormat.format(d);
		String formattedAfterDate = sqlDateFormat.format(cal.getTime());
		List<Punch> data = new ArrayList<Punch>();
		String query = String.format("select %s, %s from %s where %s > '%s " +
					"00:00:00' and %s < '%s 00:00:00' order by %s ASC",
				TimeCardTable.COL_DATE, TimeCardTable.COL_TYPE,
				TimeCardTable.NAME, TimeCardTable.COL_DATE,
				formattedDate, TimeCardTable.COL_DATE,
				formattedAfterDate, TimeCardTable.COL_DATE);
		Cursor c = this.db.rawQuery(query, null);
		if (c != null) {
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					try {
						if (!c.isNull(0)) {
							data.add(new Punch(PunchType.valueOf(c.getString(1).toUpperCase()),
									sqlDateTimeFormat.parse(c.getString(0))));
						}
					} catch (ParseException e) {
						Log.e("SQLLiteTimeCardDAO", "Error while trying to parse \"" + c.getString(0) + "\".");
						throw new DataRetrieveException("There was an error while trying to retrieve the data.");
					}
					c.moveToNext();
				}
			}
		}
		return data.toArray(new Punch[0]);
	}

	@Override
	public Punch getLastPunch() throws DataRetrieveException {
		String sql = String.format("select max(%s), %s from %s", TimeCardTable.COL_DATE,
				TimeCardTable.COL_TYPE, TimeCardTable.NAME);
	    Cursor c = this.db.rawQuery(sql, null);
	    Punch punch = null;
	    if (c != null) {
			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					try {
						String punchType = c.getString(1);
						String date = c.getString(0);
						if (punchType != null && date != null) {
							punch = new Punch(
									PunchType.valueOf(punchType.toUpperCase()),
									sqlDateFormat.parse(date));
						}
					} catch (ParseException e) {
						Log.e("SQLLiteTimeCardDAO", "Error while trying to parse \"" + c.getString(0) + "\".");
						throw new DataRetrieveException("There was an error while trying to retrieve the data.");
					}
					c.moveToNext();
				}
			}
		}
	    return punch;
	}

	public void clearDatabase() {
		db.execSQL(String.format("DELETE * FROM %s", TimeCardTable.NAME));
	}
	
}