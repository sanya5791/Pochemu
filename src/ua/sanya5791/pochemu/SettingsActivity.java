package ua.sanya5791.pochemu;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity 
							  implements OnLvSelectListener{
	public static final boolean isDebug = true;
	public final String TAG = "sanya5791.pochemuchka.ShowResultActivity";
	
	public static final String APP_PREFERENCES = "mysettings";
	public static final String APP_PREFERENCES_CURR_IP = "Current IP to Connect DB ";
	public static final String APP_PREFERENCES_CURR_IP_DESCR = "Current IP's description ";
	public static final String APP_PREFERENCES_CURR_LOGIN = "Current login to Connect DB ";
	public static final String APP_PREFERENCES_CURR_PASSW = "Current password to Connect DB ";
	public static final String APP_PREFERENCES_LIST_IPS = "IP to Connect DB item ";
	public static final String APP_PREFERENCES_LIST_DESCR = "IP's descriptions of item ";
	public static final String APP_PREFERENCES_LIST_LOGIN = "Current login to Connect DB ";
	public static final String APP_PREFERENCES_LIST_PASSW = "Current password to Connect DB ";
	public static final String APP_PREFERENCES_LIST_COUNT = "Counter of Items in IP List";
	
	public static final String KEY_IP = "ip"; 
	public static final String KEY_DESCR = "descr"; 
	public static final String KEY_LOGIN = "login"; 
	public static final String KEY_PASSW = "passw"; 

	public static final String ARG_ADD = "descr"; 
	
	final int MENU_REMOVE = 1;
	final int MENU_EDIT=2;

	DialogFragment dFrAddIp;
	
	SharedPreferences mySettings;
	ArrayList<HashMap<String, String>> myArrayList;
	HashMap<String, String> hmap;
	SimpleAdapter adapter;
	ListView lv;
	Button btAdd;

	//TODO 22.01.15 после того, как доавбил пароль я не проверил контекстное меню edit
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		btAdd = (Button)findViewById(R.id.bt_add_ip);
		
		//Внутри метода onCreate() вы инициализируете эту переменную
		mySettings=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		//наш listview с которым мы будем работать
		lv = (ListView)findViewById(R.id.lv_out1);
		// для lv необходимо создавать контекстное меню
		registerForContextMenu(lv);

		prefReadIPsList();
		showIPsToConnectFB();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (v.getId()) {
		case R.id.lv_out1:
			menu.add(0, MENU_EDIT, 0, "edit");
			menu.add(0, MENU_REMOVE, 0, "remove");
			menu.add(0, 3, 0, "Clear Prefs & myArrayList");
			break;

		default:
			break;
		}
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//объект для получения инфы из меню
		AdapterContextMenuInfo acmi;
		String str;
		switch (item.getItemId()) {
		case MENU_EDIT:
			Bundle bundle = new Bundle();
			 // получаем инфу о пункте списка
			acmi = (AdapterContextMenuInfo)item.getMenuInfo();
			
			//get ip and put to Bundle
			str = myArrayList.get(acmi.position).get(KEY_IP);
			bundle.putString(DFrAddIp.IP, str);

			//get description and put to Bundle
			str = myArrayList.get(acmi.position).get(KEY_DESCR);
			bundle.putString(DFrAddIp.DESCR, str);

			//get login and put to Bundle
			str = myArrayList.get(acmi.position).get(KEY_LOGIN);
			bundle.putString(DFrAddIp.LOGIN, str);

			//get passw and put to Bundle
			str = myArrayList.get(acmi.position).get(KEY_PASSW);
			bundle.putString(DFrAddIp.PASSW, str);

			// удаляем Map из коллекции, используя позицию пункта в списке
			myArrayList.remove(acmi.position);
			// уведомляем, что данные изменились
			adapter.notifyDataSetChanged();
			
			prefsSave(APP_PREFERENCES_LIST_IPS, "");

			//show dFrAddIp and pass params with Bundle
			FragmentTransaction fTrans = getFragmentManager().beginTransaction();
			fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			
			dFrAddIp= new DFrAddIp();
			dFrAddIp.setArguments(bundle);
			dFrAddIp.show(fTrans, DFrAddIp.TAG);			
			
			//Меняем название кнопки на Add IP
//			btAdd.setText(R.string.bt_settings_add);
			
			break;
		case MENU_REMOVE:
			 // получаем инфу о пункте списка
			acmi = (AdapterContextMenuInfo) item.getMenuInfo();
			 // удаляем Map из коллекции, используя позицию пункта в списке
			myArrayList.remove(acmi.position);
			// уведомляем, что данные изменились
			adapter.notifyDataSetChanged();

			prefsSave(APP_PREFERENCES_LIST_IPS, "");
			break;
		case 3:					//Clear Preferences file and List View
			Editor editor = mySettings.edit();
			editor.clear(); editor.commit();                           //clear all Preferences
			myArrayList.clear();
			adapter.notifyDataSetChanged();
			
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

	public void onMyClick(View v){
		switch (v.getId()) {
		case R.id.button1 :
			FragmentTransaction fTrans = getFragmentManager().beginTransaction();
			fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			
			dFrAddIp= new DFrAddIp();
			dFrAddIp.show(fTrans, DFrAddIp.TAG);			
			break;

		default:
			break;
		}
	}
	

	/**
	 * Write data to mySettings (SharedPreferences).\n
	 * - You can use argument "String key" as a key of SharedPreferences 
	 * file and argument "String value" as a value you need to write.
	 * - If you use argument "String key == APP_PREFERENCES_LIST_IPS" and "value == """ 
	 * the whole list of IPs to connect FB data base will be
	 * written from myArrayList
	 * - If you use argument "String key" 
	 * and argument "String value" then
	 * it's added new IP to myArrayList as well as to mySettings*/
	void prefsSave(String key, String value) {
		int iListCount;
		String str;
		String sIP = DFrAddIp.ip;
		String sDescr = DFrAddIp.descr;
		String login = DFrAddIp.login;
		String passw = DFrAddIp.passw;

		Editor editor = mySettings.edit();
		switch (key) {
		case APP_PREFERENCES_LIST_IPS :
			iListCount=mySettings.getInt(APP_PREFERENCES_LIST_COUNT, 0);	//берем количество элементов списка ip адресов   
			if (value.equals(ARG_ADD)) {
				++iListCount;
				str = APP_PREFERENCES_LIST_IPS + Integer.toString(iListCount);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, sIP);
				str = APP_PREFERENCES_LIST_DESCR + Integer.toString(iListCount);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, sDescr);
				
				str = APP_PREFERENCES_LIST_LOGIN + Integer.toString(iListCount);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, login);
				
				str = APP_PREFERENCES_LIST_PASSW + Integer.toString(iListCount);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, passw);
				
				editor.putInt(APP_PREFERENCES_LIST_COUNT, iListCount);		//save to Preferences counter of ip list
				editor.apply();
				prefReadIPsList();
				break;														//break "case"
			}
			//*write all data from myArray to preferences. But first clear old data */
			for (int i = 0; i < iListCount; i++) {
				editor.remove(APP_PREFERENCES_LIST_IPS + Integer.toString(i+1));
				myLogger("removing from Preferences: " + APP_PREFERENCES_LIST_IPS + Integer.toString(i+1));
				editor.remove(APP_PREFERENCES_LIST_DESCR + Integer.toString(i+1));
				myLogger("removing from Preferences: " + APP_PREFERENCES_LIST_DESCR + Integer.toString(i+1));
				editor.remove(APP_PREFERENCES_LIST_LOGIN + Integer.toString(i+1));
				myLogger("removing from Preferences: " + APP_PREFERENCES_LIST_LOGIN + Integer.toString(i+1));
				editor.remove(APP_PREFERENCES_LIST_PASSW + Integer.toString(i+1));
				myLogger("removing from Preferences: " + APP_PREFERENCES_LIST_PASSW + Integer.toString(i+1));
			}
			//set counter of ip's list to zero 
			editor.putInt(APP_PREFERENCES_LIST_COUNT, 0);
			//fill preferences with new ips & descr list
			for (int i = 0; i < myArrayList.size(); i++) {
				str = APP_PREFERENCES_LIST_IPS + Integer.toString(i+1);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, myArrayList.get(i).get(KEY_IP));
				str = APP_PREFERENCES_LIST_DESCR + Integer.toString(i+1);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, myArrayList.get(i).get(KEY_DESCR));

				str = APP_PREFERENCES_LIST_LOGIN + Integer.toString(i+1);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, myArrayList.get(i).get(KEY_LOGIN));
				str = APP_PREFERENCES_LIST_PASSW + Integer.toString(i+1);
				myLogger("Creating Arg in Prefs:" + str);
				editor.putString(str, myArrayList.get(i).get(KEY_PASSW));
			}
			editor.putInt(APP_PREFERENCES_LIST_COUNT, myArrayList.size());
			myLogger("ALL Prefs:" + mySettings.getAll());
			break;
		case APP_PREFERENCES_CURR_IP:
			editor.putString(key, value);
			break;
		case APP_PREFERENCES_CURR_IP_DESCR:
			editor.putString(key, value);
			break;
		case APP_PREFERENCES_CURR_LOGIN:
			editor.putString(key, value);
			break;
		case APP_PREFERENCES_CURR_PASSW:
			editor.putString(key, value);
			break;
		default:
			break;
		}
		editor.apply();
	}
	
	/**
	 * читаем из конфига список возможных ip to connect в глобальный myArrayList 
	 * и его помещаем в адаптер adapter
	 * */
	public void prefReadIPsList(){

		//создаем объект данных 
		myArrayList = new ArrayList<HashMap<String,String>>();
		//заполняем объект данных с помощью hmap
		int iRowsCout = mySettings.getInt(APP_PREFERENCES_LIST_COUNT, 0);
		for (int i = 1; i <= iRowsCout; i++) {
			hmap=new HashMap<String, String>();
			hmap.put("ip", mySettings.getString(APP_PREFERENCES_LIST_IPS + Integer.toString(i), ""));
			hmap.put("descr", mySettings.getString(APP_PREFERENCES_LIST_DESCR + Integer.toString(i), ""));
			hmap.put("login", mySettings.getString(APP_PREFERENCES_LIST_LOGIN + Integer.toString(i), ""));
			hmap.put("passw", mySettings.getString(APP_PREFERENCES_LIST_PASSW + Integer.toString(i), ""));
			myArrayList.add(hmap);
		}
		//create adapter
		adapter = new SimpleAdapter(this, myArrayList, 
				android.R.layout.simple_list_item_2, 
				new String[]{"ip", "descr"}, 
				new int[]{android.R.id.text1, android.R.id.text2});
		//apply adapter
		lv.setAdapter(adapter);
	}

	public void showIPsToConnectFB(){
/*отображаем текущий IP to conntect FireBird*/
		final TextView tv = (TextView)findViewById(R.id.tv_settings_curr_ip);

		//отображаем текущий ip to connect
		tv.setText(mySettings.getString(APP_PREFERENCES_CURR_IP, "")
				+ ", " + mySettings.getString(APP_PREFERENCES_CURR_IP_DESCR, ""));
		//при нажатии на один из элементов списка,  сделать его текущим ip to connect
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int arg2,
					long arg3) {
				myLogger("The item N" + arg2 + " is selected" +
						"\nIt's name" + parent.getAdapter().getItem(arg2));

				//получить данные списка через адаптер
				myLogger("You've chosen: " + myArrayList.get(arg2).get("ip"));

				Toast toast = Toast.makeText(getApplicationContext(), 
						"Вы выбрали для соединения ip: " 
								+ myArrayList.get(arg2).get(KEY_IP), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
						
				tv.setText(myArrayList.get(arg2).get(KEY_IP) + ", " + myArrayList.get(arg2).get(KEY_DESCR));
				//save current to connect ip and its description to preferences
				prefsSave(APP_PREFERENCES_CURR_IP, 
						myArrayList.get(arg2).get(KEY_IP));
				prefsSave(APP_PREFERENCES_CURR_IP_DESCR, 
						myArrayList.get(arg2).get(KEY_DESCR));
				prefsSave(APP_PREFERENCES_CURR_LOGIN, 
						myArrayList.get(arg2).get(KEY_LOGIN));
				prefsSave(APP_PREFERENCES_CURR_PASSW, 
						myArrayList.get(arg2).get(KEY_PASSW));
				finish();
			}
		});
	}

	@Override
	public void onLvSelected(Object o, int position, String value) {
		prefsSave(
				APP_PREFERENCES_LIST_IPS, 
				ARG_ADD);
	}
}
