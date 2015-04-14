package ua.sanya5791.dbsqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ua.sanya5791.pochemu.R;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.DbStatements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;

/**
 * class to work with local SQLite DB.<br>
 * Table with current Person's detailed information;<br>
 * Notice: the table is rotated according to original FB table. 
 * It's made to have the appropriate pattern to ScimpleCursorAdapter  
 * @author sanya
 *
 */
public class PersonDetails extends MyDB {
	public final static String TAG = "PersonDetails";
	
	private static final String DATABASE_NAME = "person_details.db"; 
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "person_details";

	public static final String COLUMN_LABLE = "lable";				//text; название элемента
	public static final String COLUMN_TEXT = "text";				//text; основной текст элемента
	public static final String COLUMN_CHBOX_STATE = "chbox_state";	//text; checked/unchecked=(y/n)
	public static final String COLUMN_CHBOX_SHOW = "chbox_show";	//integer; checkbox View.VISIBLE/View.INVISIBLE
	public static final String COLUMN_ICON = "icon";				//integer; icon of item		
	
	public static final String STATE_YES = "y"; 					//text (y/n)
	public static final String STATE_NO = "n"; 						//text (y/n)
	
	
	public PersonDetails(Context context) {
		super(context);
		setupTable();
	}

	/**
	 * setup all tables parameters
	 */
	private void setupTable() {
		setDbNameAndVersion(DATABASE_NAME, DATABASE_VERSION);
		setTableName(TABLE_NAME);
		setTag(TAG);
		setColumnsList(createColumnsList());
	}

	private ArrayList<Column> createColumnsList() {
		
		Column column;
		ArrayList<Column> columns = new ArrayList<MyDB.Column>();

		column = new Column(COLUMN_LABLE, COLUMN_TYPE_TEXT);
		columns.add(column);
		
		column = new Column(COLUMN_TEXT, COLUMN_TYPE_TEXT);
		columns.add(column);

		column = new Column(COLUMN_CHBOX_STATE, COLUMN_TYPE_TEXT);
		columns.add(column);
		
		column = new Column(COLUMN_CHBOX_SHOW, COLUMN_TYPE_INTEGER);
		columns.add(column);
		
		column = new Column(COLUMN_ICON, COLUMN_TYPE_INTEGER);
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
			"SELECT PS." + COLUMN_ID 
				+ ", PS." + COLUMN_LABLE
				+ ", PS." + COLUMN_TEXT
				+ ", PS." + COLUMN_CHBOX_STATE
				+ ", PS." + COLUMN_CHBOX_SHOW
				+ ", PS." + COLUMN_ICON
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

		String sKey, sValue, colName;
		DbStatements.PersonDetail key;					//enum-key appropriate to FB table's field 
		ContentValues values = new ContentValues();
		
		//clear all data in the table
		clearAll();

		if(rs == null) return;
		try {
			if(!rs.first()) return;
			getDB().beginTransaction();
				
			values.clear();
			
			sKey= DbStatements.PersonDetail.BIRTHDAY.toString();
			sValue = makeAgeStr(rs.getDate(sKey));
			values.put(COLUMN_LABLE, "Возраст " + sValue);
			
			sValue = (String) 
					SingletoneUI.getInstance().getItem(Keys.PERSON_NAME);
			values.put(COLUMN_TEXT, sValue);
			
			values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);

			getDB().insert(TABLE_NAME, null, values);
				
				
			myLogger("List of column names:");
			int count = rs.getMetaData().getColumnCount();
			for(int i=1; i<=count; i++){
				values.clear();
				myLogger("Column name: " + rs.getMetaData().getColumnName(i));
				colName = rs.getMetaData().getColumnName(i);
				//if enum DbStatements.PersonDetail doesn't have colName - skip it
				try{
					key = DbStatements.PersonDetail.valueOf(colName);
				}catch (IllegalArgumentException e){
					myLogger("...skiped");
					continue;
				}
				switch (key) {
				case BIRTHDAY:
					values.put(COLUMN_LABLE, "день рожденья");
					
					sKey=key.toString();
					sValue = rs.getDate(sKey).toString();
					values.put(COLUMN_TEXT, sValue);
					
					values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					values.put(COLUMN_ICON, R.drawable.birthday2);
					
					break;

				case SEX:
					values.put(COLUMN_LABLE, "пол");
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					switch (sValue) {
					case "M":
						values.put(COLUMN_TEXT, "мальчик");
						values.put(COLUMN_ICON, R.drawable.boy);
						break;

					case "F":
						values.put(COLUMN_TEXT, "девочка");
						values.put(COLUMN_ICON, R.drawable.girl);
						break;
						
					default:
						values.put(COLUMN_TEXT, "неизвестный науке зверь");
						values.put(COLUMN_ICON, R.drawable.girl);
						break;
					}
					
					values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					
					break;
					
				case MATHER:
					values.put(COLUMN_LABLE, "мама");
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					values.put(COLUMN_TEXT, sValue);
					
					values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					values.put(COLUMN_ICON, R.drawable.mother);
					
					break;

				case TAKE_HOME:
					values.put(COLUMN_LABLE, "другие родственники");
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					values.put(COLUMN_TEXT, sValue);
					
					values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					values.put(COLUMN_ICON, R.drawable.other_relatives);
					
					break;
					
				case PHONE:
					//но телефонов может быть несколько, поэтому сообразим цикл для курсора
					do{
						values.clear();
						sValue = rs.getString("PHONE_NUMBER");
						values.put(COLUMN_TEXT, sValue);
						
						sValue = rs.getString("PHONE_TYPE_NAME");
						values.put(COLUMN_LABLE, sValue);
						
						values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
						values.put(COLUMN_ICON, R.drawable.phone);
						
						getDB().insert(TABLE_NAME, null, values);
						
					}while (rs.next());
					//return cursor to the first raw
					rs.first();
					//return to the start of cycle
					continue;
					
				case MAIL:
					values.put(COLUMN_LABLE, "электронная почта");
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					values.put(COLUMN_TEXT, sValue);
					
					values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					values.put(COLUMN_ICON, R.drawable.mail);
					
					break;

				case REGION:
					values.put(COLUMN_LABLE, "район проживания");
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					values.put(COLUMN_TEXT, sValue);
					
					values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					values.put(COLUMN_ICON, R.drawable.region1);
					
					break;
					
				case IS_ANOTHER_CHILD:
					values.put(COLUMN_LABLE, "");
					
					values.put(COLUMN_TEXT, "В семье есть другие дети");
					values.put(COLUMN_CHBOX_SHOW, View.VISIBLE);
					values.put(COLUMN_ICON, R.drawable.achild);

					sKey=key.toString();
					sValue = rs.getString(sKey);
					if(sKey.equals("Y"))
						values.put(COLUMN_CHBOX_STATE, STATE_YES);
					else
						values.put(COLUMN_CHBOX_STATE, STATE_NO);
					
					break;
					
				case SEMINAR:
					values.put(COLUMN_LABLE, "");
					
					values.put(COLUMN_TEXT, "Семинар прослушан");
					values.put(COLUMN_CHBOX_SHOW, View.VISIBLE);
					values.put(COLUMN_ICON, R.drawable.seminar);
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					if(sKey.equals("Y"))
						values.put(COLUMN_CHBOX_STATE, STATE_YES);
					else
						values.put(COLUMN_CHBOX_STATE, STATE_NO);
					
					break;
					
				case HAS_ALLERGY:
					values.put(COLUMN_LABLE, "");
					
					values.put(COLUMN_TEXT, "Есть пищевая аллергия на:");
					values.put(COLUMN_CHBOX_SHOW, View.VISIBLE);
					values.put(COLUMN_ICON, R.drawable.allergy);
					
					sKey=key.toString();
					sValue = rs.getString(sKey);
					if(sValue.equals("Y"))
						values.put(COLUMN_CHBOX_STATE, STATE_YES);
					else
						values.put(COLUMN_CHBOX_STATE, STATE_NO);
					
					break;
					
				case ALLERGY_ON:
					//If a child doesn't have allergy - just miss this part
					sKey=DbStatements.PersonDetail.HAS_ALLERGY.toString();
					sValue = rs.getString(sKey);
					if(sValue.equals("Y")){
						values.put(COLUMN_LABLE, "");
						
						sKey=key.toString();
						sValue = rs.getString(sKey);
						values.put(COLUMN_TEXT, sValue);
						
						values.put(COLUMN_CHBOX_SHOW, View.INVISIBLE);
					}
					
					break;
					
				default:
					break;
				}

				if (values.size() == 0)
					myLogger("is skiped, because of lack of use");
				else
					getDB().insert(TABLE_NAME, null, values);
			}
			myLogger("--------- end of list ----------");
				
			getDB().setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			getDB().endTransaction();
		}

	}
	
	/**
	 * вычисляет возраст ребенка и возвращает в его в виде строки типа 
	 * "YY лет, MM месяцев"	  
	 * @param dBirth дата рождения
	 * @return строка с возрастом ребенка
	 */
	private String makeAgeStr(java.sql.Date dBirth) {
		
		String age; 
		
		int bYear, bMonth, bDay;
		int year, month, day;
		int diffYear, diffMonth, diffDay;
		
		Calendar today = new GregorianCalendar();
		Calendar cBirth = Calendar.getInstance();
		cBirth.setTime(dBirth);
		

		bYear = cBirth.get(Calendar.YEAR);
		bMonth = cBirth.get(Calendar.MONTH);
		bDay = cBirth.get(Calendar.DAY_OF_MONTH);
		
		year = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH);
		day = today.get(Calendar.DAY_OF_MONTH);
		
		diffDay = day-bDay;
		if(diffDay < 0){
			month--;
		}

		diffMonth = month-bMonth;
		if(diffMonth < 0){
			year--;
			diffMonth=12+diffMonth;
		}

		diffYear = year - bYear;
		
		age = String.valueOf(diffYear);
		if (diffYear == 1){
			age += " год";
		}else if(diffYear == 2 || diffYear == 3 || diffYear == 4){
			age += " года";
		}else{
			age += " лет";
		}
		
		age += " и " + String.valueOf(diffMonth);
		
		if (diffMonth == 1){
			age += " месяц";
		}else if(diffMonth == 2 || diffMonth == 3 || diffMonth == 4){
			age += " месяца";
		}else
			age += " месяцев";
		
		return age;
	}


}
