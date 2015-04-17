package ua.sanya5791.dbsqlite;

import java.sql.ResultSet;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * my base class to work with SQLite DB as a swap space.
 * 1. Create  
 * @author sanya
 *
 */
public abstract class MyDB {
	private boolean isDebug = true;
	String TAG="MyDB";
	
	public static final String COLUMN_ID = "_id"; 					//long

	static final int COLUMN_TYPE_INTEGER = 1;
	static final int COLUMN_TYPE_TEXT = 2;

	private String tableName;
	
	private String columnsCreate; 
	
	private String DATABASE_NAME; 
	private int DATABASE_VERSION;
	
	private Context mCtx;

	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;
	
	public MyDB (Context context){
		mCtx = context;
	}
	
	/**
	 * open connection
	 */
	public void open(){
		myLogger("open():");
		mDBHelper = new DBHelper(mCtx);
		mDB = mDBHelper.getWritableDatabase();
	}

	/**
	 * close connection
	 */
	public void close(){
		myLogger("close():");
		if(mDBHelper != null){
			mDBHelper.close();
			myLogger(DATABASE_NAME + " closed");
		}
	}
	
	/**
	 * Set TAG. if you left it unset - it's used default ""
	 * @param tag is used in LogCat
	 */
	public void setTag(String tag){
		TAG = tag;
	}

	/**
	 * Whether output debug messages to LogCat.  <br>
	 * if you left it unset - it's used default "".
	 * 
	 * @param 
	 */
	public void setIsDebug(boolean isDebug){
		this.isDebug = isDebug;
	}
	
	/**
	 * Set DB name and version
	 * @param dbName
	 * @param version
	 */
	public void setDbNameAndVersion(String dbName, int version){
		DATABASE_NAME = dbName;
		DATABASE_VERSION = version;
	}
	
	
	/**
	 * Create a part of query string (columnsCreate) to create a table;<br>
	 *  As a result the columnsCreate is going to be like: "(_id integer primary key autoincrement,
	 *  person_name string, person_id integer not null)"<br>
	 * Note: Column "_id integer primary key autoincrement" is added by default;
	 */
	public void setColumnsList(ArrayList<Column> columns){
		myLogger("setColumnsList():");
		StringBuilder builder = new StringBuilder("(");
		builder.append("_id integer primary key autoincrement");
		for(Column column : columns){
			builder.append(", ");
			builder.append(column.name);
			
			switch (column.type) {
			case COLUMN_TYPE_INTEGER:
				builder.append(" integer");
				break;

			case COLUMN_TYPE_TEXT:
				builder.append(" text");
				break;
				
			default:
				break;
			}
			
			if(column.params != null){
				builder.append(" ");
				builder.append(column.params);
			}
		}
		builder.append(");");
		columnsCreate = builder.toString();
//		MyLogger("String for creating columns=" + columnsCreate);
	}
	
	/**
	 * Set table's name. You should't forget about extension ".db";
	 */
	public void setTableName(String tableName){
		this.tableName = tableName;
	}
	
	/**
	 * @return DB link
	 */
	SQLiteDatabase getDB(){
		return mDB;
	}
	
	/**
	 * get all data from the table
	 * @return Cursor for all data from the table
	 */
	public Cursor getAllData(){
		return mDB.query(tableName, null, null, null, null, null, null);
	}

	/**
	 * get Cursor with data for ListView
	 * @return fields: 
	 */
	public abstract Cursor getDataForLv();

	/**
	 * inflate the DB table from rs, but first clear the table;
	 * @param rs
	 */
	abstract void inflateFromRS(ResultSet rs);

	/**
	 * get Cursor to the raw with _id
	 * @param _id  _id for the string
	 * @return Cursor to the raw with _id
	 */
	public Cursor getById(long _id) {
		
		myLogger("fetch raw for _id=" + _id);
		String selection = COLUMN_ID + "=" + String.valueOf(_id);

		Cursor cursor= mDB.query(tableName, null, selection, null, 
				null, null, null);
		
		myLogger("cursor.moveToFirst()=" + cursor.moveToFirst());
		return cursor;
	}

	/**
	 * clear all data in the table
	 */
	public void clearAll(){
		mDB.delete(tableName, null, null);
		
	}
	
	/**
	 * Is there any colName's value is found in the table
	 *  
	 * @param colName
	 * @param value
	 * @return true if there is found more then 0 equel values in the colName
	 */
	public boolean isEquelInt(String colName, int value){

		String selection = colName + " = ?";
		String[] selectionArgs = new String[]{String.valueOf(value)};
		
		Cursor c = mDB.query(tableName, null, selection, selectionArgs, null, null, null);
		if(!c.moveToFirst())
			return false;
		else
			return true;
		
	}
	
	/**
	 * Этот класс я использую для создания списка столбцов ArrayList<`ColumnDescr>; <br>
	 * @author sanya
	 *
	 */
	public class Column{
		String name;
		int type;
		String params;
		
		/**
		 * @param name Column's name
		 * @param type Column's type - you must use constants ether 
		 * MyDB.COLUMN_TYPE_INTEGER or MyDB.COLUMN_TYPE_TEXT
		 * @param params additional column's params 
		 */
		public Column(String name, int type, String params){
			this.name = name;
			this.type = type;
			this.params = params;
		}

		/**
		 * @param name Column's name
		 * @param type Column's type - you must use constants ether 
		 * MyDB.COLUMN_TYPE_INTEGER or MyDB.COLUMN_TYPE_TEXT
		 */
		public Column(String name, int type){
			this.name = name;
			this.type = type;
			params = null;
		}
	}
	public class DBHelper extends SQLiteOpenHelper {
		
		public DBHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			myLogger("onCreate():");
			// Database creation sql statement
			String DbCreate = "create table "
					+ tableName + columnsCreate; 
			myLogger("String for query=" + DbCreate);
			db.execSQL(DbCreate);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			myLogger("Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
			
			//simply drop the table and then create a new one
			db.execSQL("DROP TABLE IF EXISTS  " +  tableName);
			onCreate(db);
		}
	}

	protected void myLogger(String statement){
		if (isDebug) Log.v(TAG, statement);
	}
}

