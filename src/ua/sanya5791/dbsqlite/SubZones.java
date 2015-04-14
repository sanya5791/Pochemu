package ua.sanya5791.dbsqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SubZones extends MyDB {
	public final static String TAG = "SubZones";
	
	private static final String DATABASE_NAME = "subzones.db"; 
	private static final int DATABASE_VERSION = 3;
	
	public static final String TABLE_NAME = "subzones_list";

	public static final String COLUMN_SUBZONE_ID = "subzone_id";		//integer
	public static final String COLUMN_ZONE_ID = "zone_id";	 			//integer
	public static final String COLUMN_SUBZONE_NAME = "subzone_name";	//text
	
	public SubZones(Context context) {
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
		Column column = new Column(COLUMN_SUBZONE_ID, 
				COLUMN_TYPE_INTEGER,
				"not null");
		columns.add(column);

		column = new Column(COLUMN_ZONE_ID, 
				COLUMN_TYPE_INTEGER,
				"not null");
		columns.add(column);
		
		column = new Column(COLUMN_SUBZONE_NAME, COLUMN_TYPE_TEXT);
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
				
				//put SUBZONE_ID
				sKey = "SUBZONE_ID";
				iValue = rs.getInt(sKey);
				values.put(COLUMN_SUBZONE_ID, iValue);
				
				//put ZONE_ID
				sKey = "ZONE_ID";
				iValue = rs.getInt(sKey);
				values.put(COLUMN_ZONE_ID, iValue);
				
				//put SUBZONE_NAME
				sKey = "SUBZONE_NAME";
				sValue = rs.getString(sKey);
				values.put(COLUMN_SUBZONE_NAME, sValue);
				
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
