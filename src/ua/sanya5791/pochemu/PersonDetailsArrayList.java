package ua.sanya5791.pochemu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import android.util.Log;
import android.view.View;

/**
 * 
 * Fill ArrayList<HashMap<String, Object>>;
 * Fill it with the Person Details data to use it by ArrayAdapter.
 *You must pass in the constructor ArrayList<HashMap<String, Object>>;
 *To Receive filled ArrayList use getArrayList getter. 
 *@author Sanya5791 
 */
public class PersonDetailsArrayList{
	private static final boolean isDebug = true;
	public static String TAG = "PersonDetailsArrayList";
	
	final static String LABLE = "lable";			//название элемента
	final static String TEXT = "text";				//текст элемента
	final static String ICON = "icon";				//icon of item		
	final static String CHBOX_STATE = "chbox_state";			//checkbox check/uncheck
	final static String CHBOX_SHOW = "chbox_show";			//checkbox check/uncheck

//	Context context;
	private ResultSet rs_personDetails;
	private ArrayList<HashMap<String, Object>> mArrayList;

	/**
	 * 
	 * @param mArrayList link to ArrayList to be filled with data
	 * @param rs_personDetails link to ResultSet; I need it to fill ArrayList
	 */
	public PersonDetailsArrayList(ArrayList<HashMap<String, Object>> mArrayList,
			ResultSet rs_personDetails) {
		this.mArrayList = mArrayList;
		this.rs_personDetails = rs_personDetails; 
		fillAList();
	}
	
	/******** fill data from DB and other resources to ArrayList **********/
	private void fillAList(){
		String str;
		try {
			MyLogger("fill HashMap with data from DB");
			
			// заполняем объект данных с помощью hmap
//			rs_personDetails.findColumn("MATHER");
//			hmap = new HashMap<String, Object>();

			//полагаю, что будет только одна строка для одного ребенка
			rs_personDetails.first();
			
			HashMap<String, Object> hmap;

			// add item name and age
			hmap = new HashMap<String, Object>();
//			Date bDate = rs_personDetails.getDate("BIRTHDAY");
			str = makeAgeStr(rs_personDetails.getDate("BIRTHDAY"));
			
			hmap.put(LABLE, "Возраст " + str);
			hmap.put(TEXT, SingletoneUI.getInstance().getItem(Keys.PERSON_NAME));
//			hmap.put(ICON, R.drawable.birthday2);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);

			hmap = new HashMap<String, Object>();
//			hmap.put(LABLE, context.getString(R.string.lable_birthday));
			
			hmap.put(LABLE, "день рожденья");
			hmap.put(TEXT, rs_personDetails.getDate("BIRTHDAY"));
			hmap.put(ICON, R.drawable.birthday2);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);
			
			// add item sex
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "пол");
			str=rs_personDetails.getString("SEX");
			switch (str) {
			case "M":
				hmap.put(TEXT, "мальчик");
				hmap.put(ICON, R.drawable.boy);
				break;
			case "F":
				hmap.put(TEXT, "девочка");
				hmap.put(ICON, R.drawable.girl);
				break;
			default:
				hmap.put(TEXT, "неизвестный науке зверь");
				break;
			}
//			hmap.put(TEXT, rs_personDetails.getString("SEX"));
//			hmap.put(ICON, null);
			hmap.put(CHBOX_STATE, true);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);
			
			// add item mother
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "мама");
//			hmap.put(LABLE, R.string.lable_mother);
			hmap.put(TEXT, rs_personDetails.getString("MATHER"));
			hmap.put(ICON, R.drawable.mother);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);
			
			// add item "other relatives"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "другие родственники");
//			hmap.put(LABLE, R.string.lable_relatives);
			hmap.put(TEXT, rs_personDetails.getString("TAKE_HOME"));
			hmap.put(ICON, R.drawable.other_relatives);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);
			
			// add item "phones"
			//но телефонов может быть несколько, поэтому сообразим цикл для курсора
			rs_personDetails.beforeFirst();
//			rs_personDetails.
			while (rs_personDetails.next()) {
				hmap = new HashMap<String, Object>();
				hmap.put(LABLE, rs_personDetails.getString("PHONE_TYPE_NAME"));
//				hmap.put(LABLE, "телефоны");
				hmap.put(TEXT, rs_personDetails.getString("PHONE_NUMBER"));
				hmap.put(ICON, R.drawable.phone);
				hmap.put(CHBOX_STATE, false);
				hmap.put(CHBOX_SHOW, View.INVISIBLE);
				mArrayList.add(hmap);
			}
			//возвращаю курсор откуда взял
			rs_personDetails.first();

			// add item "email"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "электронная почта");
//			hmap.put(LABLE, R.string.lable_email);
			hmap.put(TEXT, rs_personDetails.getString("MAIL"));
			hmap.put(ICON, R.drawable.mail);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);
			
			// add item "Neiborhood"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "район проживания");
//			hmap.put(LABLE, R.string.lable_neiborhood);
			hmap.put(TEXT, rs_personDetails.getString("REGION"));
			hmap.put(ICON, R.drawable.region1);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.INVISIBLE);
			mArrayList.add(hmap);

			// add item "isAnotherChild"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "");
			hmap.put(TEXT, "В семье есть другие дети");
			hmap.put(ICON, R.drawable.achild);
			if (rs_personDetails.getString("IS_ANOTHER_CHILD").equals("Y")) {
				hmap.put(CHBOX_STATE, true);
			} else {
				hmap.put(CHBOX_STATE, false);
			}
			hmap.put(CHBOX_SHOW, View.VISIBLE);
			mArrayList.add(hmap);
			
			// add item "Семинар прослушан"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "");
			hmap.put(TEXT, "Семинар прослушан");
			hmap.put(ICON, R.drawable.seminar);
			if (rs_personDetails.getString("SEMINAR").equals("Y")) {
				hmap.put(CHBOX_STATE, true);
			} else {
				hmap.put(CHBOX_STATE, false);
			}
			hmap.put(CHBOX_SHOW, View.VISIBLE);
			mArrayList.add(hmap);
			
			// add item "Пищевая аллергия"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "");
			hmap.put(TEXT, "Есть пищевая аллергия на:");
			hmap.put(ICON, R.drawable.allergy);
			if (rs_personDetails.getString("HAS_ALLERGY").equals("Y")) {
				hmap.put(CHBOX_STATE, true);
				hmap.put(CHBOX_SHOW, View.VISIBLE);
				mArrayList.add(hmap);
				// add item "Пищевая аллергия на"
				hmap = new HashMap<String, Object>();
				hmap.put(LABLE, "");
				hmap.put(TEXT, rs_personDetails.getString("ALLERGY_ON"));
				hmap.put(ICON, false);
//				hmap.put(ICON, R.drawable.ic_launcher);
				hmap.put(CHBOX_STATE, false);
				hmap.put(CHBOX_SHOW, View.INVISIBLE);
				mArrayList.add(hmap);
			} else {
				hmap.put(CHBOX_STATE, false);
				hmap.put(CHBOX_SHOW, View.VISIBLE);
				mArrayList.add(hmap);
			}
			
			// add item "in Archive"
			hmap = new HashMap<String, Object>();
			hmap.put(LABLE, "");
			hmap.put(TEXT, "В архиве");
			hmap.put(ICON, R.drawable.achive);
			hmap.put(CHBOX_STATE, false);
			hmap.put(CHBOX_SHOW, View.VISIBLE);
			mArrayList.add(hmap);
		} catch (SQLException e) {
			e.printStackTrace();
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
		
		int bYear, bMonth;
		int year, month;
		int diffYear, diffMonth;
		
//		ParsePosition pos = new ParsePosition(0);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date dBirth = sdf.parse(birthday, pos);

		Calendar today = new GregorianCalendar();
		Calendar cBirth = GregorianCalendar.getInstance();
		cBirth.setTime(dBirth);
		

		bYear = cBirth.get(Calendar.YEAR);
		bMonth = cBirth.get(Calendar.MONTH);
		
		year = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH);
		
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

	public ArrayList<HashMap<String, Object>> getArrayList() {
		return mArrayList;
	}

	private void MyLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

}
