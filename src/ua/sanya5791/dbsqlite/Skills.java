package ua.sanya5791.dbsqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Skills extends MyDB {
	public final static String TAG = "Skills";
	
	private static final String DATABASE_NAME = "skills.db"; 
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "skills_list";

	public static final String COLUMN_SKILL_ID = "skill_id";	 	//integer
	public static final String COLUMN_SKILL_NAME = "skill_name";	//text
	public static final String COLUMN_HAS_VALUE = "has_value";		//integer
	public static final String COLUMN_LAST_VALUE = "last_value";	//integer
	
	public Skills(Context context) {
		super(context);
		setupTable();
	}

	/**
	 * setup all tables parameters
	 */
	private void setupTable() {
		setDbNameAndVersion(DATABASE_NAME, DATABASE_VERSION);
		setTag(TAG);
		setTableName(TABLE_NAME);
		setColumnsList(createColumnsList());
	}

	private ArrayList<Column> createColumnsList() {
		ArrayList<Column> columns = new ArrayList<MyDB.Column>();
		Column column = new Column(COLUMN_SKILL_ID, 
				COLUMN_TYPE_INTEGER,
				"not null");
		columns.add(column);

		column = new Column(COLUMN_SKILL_NAME, 
				COLUMN_TYPE_TEXT);
		columns.add(column);
		
		column = new Column(COLUMN_HAS_VALUE, 
				COLUMN_TYPE_INTEGER);
		columns.add(column);
		
		column = new Column(COLUMN_LAST_VALUE, 
				COLUMN_TYPE_INTEGER);
		columns.add(column);
		
		return columns;
	}

	/**
	 * get Cursor with data for ListView
	 * @return fields: COLUMN_PERSON_ID, COLUMN_LAST_NAME, 
	 * 					COLUMN_FIRST_AND_SURNAME_NAME.
	 */
	@Override
	public Cursor getDataForLv() {
		myLogger("getDataForLv():");

		return getAllData();
	}

	/**
	 * inflate the DB table from rs, but first clear the table;
	 * @param rs
	 */
	@Override
	public void inflateFromRS(ResultSet rs) {
		myLogger("inflateFromRS():");
		String sKey, sValue;
		int iValue;
		ContentValues values = new ContentValues();
		
		clearAll();

		try {
			getDB().beginTransaction();
			if(rs == null || !rs.first()) return;
			do{
				values.clear();
				
				//put SKILL_ID
				sKey = "SKILL_ID";
				iValue = rs.getInt(sKey);
				values.put(COLUMN_SKILL_ID, iValue);
				
				//put SKILL_NAME
				sKey = "SKILL_NAME";
				sValue = rs.getString(sKey);
				values.put(COLUMN_SKILL_NAME, sValue);
				
				//put HAS_VALUE
				sKey = "HAS_VALUE";
				iValue = rs.getInt(sKey);
				values.put(COLUMN_HAS_VALUE, iValue);
				
				//put LAST_VALUE
				sKey = "LAST_VALUE";
				iValue = rs.getInt(sKey);
				values.put(COLUMN_LAST_VALUE, iValue);
				
				getDB().insert(TABLE_NAME, null, values);
				
			}while (rs.next());
			getDB().setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			getDB().endTransaction();
		}
	}
	
	/**
	 * query for all data; But constrained by COLUMN_SKILL_NAME with sFilter applied;
	 * @param sFilter string to constrain  
	 * @return Cursor for all data from the table
	 */
	public Cursor getFilteredDataForLv(String sFilter) {
		myLogger("getFilteredDataForLv():");
		StringBuilder sBuilder = new StringBuilder(COLUMN_SKILL_NAME + " LIKE '%");
		sBuilder.append(sFilter).append("%'");
		
		String selection = sBuilder.toString();
		return getDB().query(TABLE_NAME, null, selection, null, null, null, null);	}
}
