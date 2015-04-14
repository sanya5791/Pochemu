package ua.sanya5791.dbsqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ua.sanya5791.pochemu.dbfb.DbStatements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Persons extends MyDB {
	public final static String TAG = "DBPersons";
	
	private static final String DATABASE_NAME = "persons.db"; 
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "persons_list";

	public static final String COLUMN_PERSON_ID = "person_id"; 		//integer
	public static final String COLUMN_LAST_NAME = "last_name"; 		//text
	public static final String COLUMN_FIRST_NAME = "first_name"; 	//text
	public static final String COLUMN_SURNAME_NAME = "surname"; 	//text
	public static final String COLUMN_FIRST_AND_SURNAME_NAME = 
									"first_and_surname_name"; 		//text
	
	public Persons(Context context) {
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
		Column column = new Column(COLUMN_PERSON_ID, 
				COLUMN_TYPE_INTEGER,
				"not null");
		columns.add(column);

		column = new Column(COLUMN_LAST_NAME, COLUMN_TYPE_TEXT);
		columns.add(column);
		
		column = new Column(COLUMN_FIRST_NAME, COLUMN_TYPE_TEXT);
		columns.add(column);

		column = new Column(COLUMN_SURNAME_NAME, COLUMN_TYPE_TEXT);
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
		String sqlQuery = 
		//don't remove lines behind - they are actual!!!
			"SELECT PS." + COLUMN_ID 
				+ ", PS." + COLUMN_PERSON_ID
				+ ", PS." + COLUMN_LAST_NAME
				+ ", (PS." + COLUMN_FIRST_NAME 
					+ " || \" \" ||" + " PS."+ COLUMN_SURNAME_NAME
					+ ") as " + COLUMN_FIRST_AND_SURNAME_NAME
			+ " FROM " + TABLE_NAME + " AS PS "
			;
		
		myLogger("sqlQuery = " + sqlQuery);
		
		return getDB().rawQuery(sqlQuery, null);
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

		if(rs == null) return;
		try {
			if(!rs.first()) return;
			getDB().beginTransaction();
			do{
				values.clear();
				
				//put person_id
				sKey = DbStatements.PersonsSelect.PERSON_ID.toString();
				iValue = rs.getInt(sKey);
				values.put(COLUMN_PERSON_ID, iValue);
				
				//put last name
				sKey = DbStatements.PersonsSelect.LAST_NAME.toString();
				sValue = rs.getString(sKey);
				values.put(COLUMN_LAST_NAME, sValue);
				
				//put first name
				sKey = DbStatements.PersonsSelect.FIRST_NAME.toString();
				sValue = rs.getString(sKey);
				values.put(COLUMN_FIRST_NAME, sValue);
				
				//put second name
				sKey = DbStatements.PersonsSelect.PATRONYMIC_NAME.toString();
				sValue = rs.getString(sKey);
				values.put(COLUMN_SURNAME_NAME, sValue);
				
				getDB().insert(TABLE_NAME, null, values);
				
			}while (rs.next());
			getDB().setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			getDB().endTransaction();
		}

	}

}
