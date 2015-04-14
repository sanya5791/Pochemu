/*
 * 
 ************************* --------- TODOs: --------------- **********************
 *
 *-------------- DB Queries: last is DBQ19
 *DBQ19 08.04.15 - Action Bar button "connect" must check connection state every time 
 *	itself's refreshing in onPrepareOptionsMenu;
 *DBQ7 07.12.14 - public class DbStatements; ��� ������� ���������� � ���� �������������; ���� �� � ��������� ������ �������� � ��������� ����-�������� ����������.
 *
 *-------------- User Interface: last is UI10
 *UI9 27.02.15 - enter settings only from MainActiviti or PersonsList
 *UI6 04.02.15 - allow user remove items �� ������ ��������� ������� 
 *UI5 04.02.15 - ������� �� ������ ������� ��, ������� ������ � skillsSelected 
 *UI2 02.01.15 -  ��������� ������ ���� �����. ������ ������ ������ ��� ����� ��������� ���������� ������� ���������.
 *
 *-------------- Java Experience: last is JE10 
 *JE6 1.2. 25.11.2014 - ������� �������� ���������, ���� �� ����������� ������ Fragment_main_list ������ 
 *implements AsyncTaskListener. ��� ����� ��� ������������� ���� ������???
 *
 *-------------- BUGs: last is BU17
 *BU17 08.04.15 FrSkillsList; bug appears if search EditText is filled and rotate screen
 *BU13 30.03.15 FrSkillsList search make case insensetive; This SQLite limitation for not english letters
 *-------------- Without section: last is WS7
 * 
 *
 ************************* --------- Done: --------------- **********************
 *
 *JE7 24.02.15-12.04.15 ������������ ACRA bug report service 
 *JE5 02.12.14 - 07.04.15 �����������. � �� �����, ������ ����� ��������� DbQueryTask �� Fragment_main_list, �� ������ DbQueryTask �������� this=Fragment_main_list$2
 *in UI thread. So you should use it in AsyncTask.doInBackground to learn 
 *whether the Connection is valid
 *UI7 04.02.15 - 07.04.15 � ����������, ��� ������� �� ip �� ������, ����� ��� ������� � ������� activity.
 *UI10 09,03,15 - 07.04.15 ������� ��������� ��� Tablet;
 *BU7 26.02.15 - 07.04.15DFrSkillScore press back and return to FrSkillsList 
 *		(the ResultSet rs_skills is closed)
 *		07.04.15: it's fixed as a result of use Result Set an Loaders
 *BU9 11.03.15 07.04.15 - click left tab and it must appear fragment not in Fragment manager;
 *	cursor is lost, because 2 queries are made in async; 1-st not completed and
 *	2-nd is started;
 *	07.04.15: it's fixed as a result of use Result Set an Loaders
 *BU11 12.03.15 - 07.04.15 DSkillScore. If Back pressed on tablet as well on phone
 *BU12 12.03.15-07.04.15 ��������� ����������; �������� ������ �� �������� �����. 
 *BU15 03.04.15 - 07.04.15 ActionBar icon "�������� ������" appeared in skills pane; 
 *DBQ9 30.12.14 - 07.04.15 ������������ ResultSet.close() ��� �������� ���������� � �� � ������������ �����; 
 *					� ��� ���������� � ���� ����� ������. ����� ��������� �� �������� ��� rs.
 *					07,04,15 � ���� ������������ ResultSet, ������� ��� ������ ��� ������ �� ���������
 *DBQ17 13.03.15 - 07.04.15 �������� SQLite, ����� ������� ������ ���������� �� FireBird �������
 *DBQ2 03.12.14 - 07.04.15 ���������� �������� ������ ProgressDialog ��� ��������.
 *JE8 01.04.15 - 05.04.15 ShowFragment: use field isOnTablet instead of many findViwById(container) checks
 *JE10 02.04.15-03.04.15 ShowFragment: all onTabReselected are  almost the same; write one method;
 *JE9 02.04.15-03.04.15 ShowFragment:  think, maybe you don't need this global variables; answer: i need them for phone layout
 *BU14 01.04.15-03.03.15  ShowFragment: onRotate bug with using final field;
 *BU8 11.03.15-11.03.15 tablet. left persons list, right zones list; problem appears when click the left panel
 *UI8 26.02.15 - 27.02.15 add icons on buttons "Save lesson" and "More skill"
 *BU2 04.01.15 25.02.15 - ActionBar.tabs the bag during screeen rotate. 04.02 - �����, ��� ���� ��������, 
 *	������ � mainactivity ��� ������������� ��� �� ������������. ������� ����� ��������� ��������� ����� 
 *	��� �������� ������
 *WS5 06.02.15 - 20.02.15 ��������� ��������� �� MainActivity to FrPersonDetails - 
 *	ActionBar button "add" was pressed in FragmentPersonDetailList
 *WS6 17.02.15 - 24.02.15 ������ ����� Bundle. ������ ���� ������������ SingletoneUI
 *BU3 06.02.15 - 24.02.15 ����� �������� �� ��. ��� ����� �� ������ ��������� �������, �� 
 *	skillsList ���������� ������
 *DBQ15 29.01.15 - 20.02.15 use SingleTone pattern to keep data in DataLessonInsert and DataSkillRegistration
 *BU4 10.02.15 - 20.02.15 ���� �������� ���, ��� �� ������ ����� ��������� ��������� ����;
 *	����� � FrSkillsList.getLessonID() ���������� �������: ���� ����� ������� ��� ��,
 *	�� ����� ���� �� �������.
 *	� ��, � ������, ������� ����� �� ��������� ����� �����, � ��������� ��, ����� 
 *	����� ������ ��. �������� ����!!! � ����� ��������� ��� ��������� 
 *	20.02.15 ���!!! � ��� � ������
 *WS7 17.02.15 - 19.02.15 ShowFragment.sweepData() sweep SingletoneUI
 *BU6 10.02.15 - 20.02.15 �������� ����������� buttonBack()
 *BU5 13.02.15 - 17.02.15 zones, then skills list. Then button back - NullPointer
 *DBQ16 07.01.15 - 17.02.15 ���� �� ������������� ������� ConnectDBTask.setAutoCommit() ������, ���� fault
 *BU1 28.01.15 - 06.02.15 FrSkillsList ListView items with stars are not clickable, as well as in the FrSkillsSelected
 *				as well as items with checkbox in FrPersonDetails. 06.02.15 resolving: you must set 
 *				any items (����� TextView, ImageView) to setFocusable(false);
 *				Better do it not in xml; 
 *DBQ3 04.12.14 - 05.02.15 Debug DbQueryTask. If Coneection enabled, but later WiFi is switched to EDGE - appears lots of untreated Exceptions
 *					����������, ����� �� ���� wifi, �������� � ������� ������ � �� - �������� ������. 
 *					05.02.15 �������: 
 * 					 - ConnectDBTask<T> ������� �������� �� mConnection.isValid(5)
 * 					 - DbQueryTask<T> launchCallableStatement() ������� return Boolean; 
 * 						�� � �������������� ���� ��������� �� ����� ���� � launchCallableStatement
 *DBQ4 28.11.2014 - 05.02.15 ������� Connection .isValid. - It's impossible to use this method
 *DBQ13 28.01.15 - 05.02.15 ������������ � FrSkillsList � ���� ������ ����, � ������������ ����� ���� �� �����: 1. �������� ���� � ���������� �������� 2. ������ ��������� ����� ����
 *DBQ14 28.01.15 - 01.02.15 ���������� ������ SKILL_REGISTRATION_SELECT_LAST ��� ������ ������ �������
 *UI4 26.01.15 - 02.02.15 ���������� ActionBar.tabs ��� ���������
 *WS4.4 26.12.14 - 05.01.15 CallableStatement for SKILL_REGISTRATION
 *UI3 21.1.15 - 22.01.15 � ��������� �������� �����-������ � �� 
 *WS4 22.12.14 - 21.01.15 Add Lesson and Skill for the the person is chosen;
 *		�������� �� ������� UI �����: ��� �������-���� �����-����-�������-��������-������. 
 *�������� ������-������(DialogFragment) � ������������ � ������ ������. 
 *��� ����, �������� ����������� ������: 
 * - ����������� � ActionBar ������ "�������� � ���� ����";
 * - ���� �������� �� ������� ������ ���������� �������� "����" 
 * 		�� ������� ��������� ������.
 * ����� ��������� ��� ������, ������� �� ��� � ������ ������, � ��������� �����
 * ���� ������ �� �� ActionBar.�������� ���� - �� ���� � ������ ��������� � ����.
 * � ���� ������ �� ��. �����, �� ���� ����������.
 * ������������� ����������� �������� ������ �� ������ ������ �����.
 * 		�������� �� ������� ���������:
 * ������� �� ������ ������, �������� �� ���� �� ���������, ��������� DialogFragment "��� ���������".
 * ��. ������ � ������ �����. �������� ��. �������� � ����.
 * ���������� (below fragment ������ ������) �������������� �������� "����" �� ������� ������ � � ActionBar ��. ��������� ����.
 * ��� ������ �������� � ArrayList<HashMap<String, Object>>. 
 * ArrayList �������� ������ ������. HashMap �������� ��������� ���� ��� ������� ������,
 *  ������� ����� ������������ � ������� LESSON and SKILL_REGISTRATION;
 * 
 *DBQ8 24.12.14 - 15.01.15 �������� ����� ������ � ���� "LESSON_INSERT"
 *DBQ11 02.01.15 - 15.01.15 start to use "enum" Ennumerated types to describe field-names of DB procedures&tables. ���� � � ��������
 *						���� � ���, ��� ����� ������������� enum �� ��������������� � �������, �� ������ ��� ������������ �� ���������. 
 *BUG 09,01,15 - 19.01.15 ��� �������� ������ ����������� ������ � ArrayList
 *DBQ12 06.01.15 - 15.01.15 ���� �������� ������ ��� ������� �� ����������
 *DBQ10 29.12.14 - 02.01.15 ��� ������, ���� ������ ������ �� �������?
 *WS4.1 22.12.14 - 23.12.14 Start is ActionBar "add" button;
 *WS4.2 22.12.14 - 24.12.14 ����������� � ������������ ��� ����� ����
 *WS4.3 24.12.14 - 25.12.14 CallableStatement for LESSON_INSERT
 *WS3 19.12.14 - 22.12.14 ActionBar - implement icon to add a skill in PersonDetail
 *WS1 05,12,14 - 18.12.14 ���������� �������� "��������� � �������". ������� ListView same as in ExDialer - with icons
 * 			Note: I used a custom ItemListView; 
 * 				i used SimpleAdapter.ViewBinder (string of my code: adapter.setViewBinder(new MyViewBinder());)
 * 				http://www.startandroid.ru/ru/uroki/vse-uroki-spiskom/109-urok-50-simpleadapter-ispolzuem-viewbinder.html
 * WS0 05,12,14 - 18.12.14 ���������� ������ �����
 * WS2 10.12.14 - 17.12.14 ActionBar; ������ ��� ����������� ����� ��������� �� ���������� - ����� ������ ��������� action bar
 *DBQ6 06.12.14 - 07.12.14 �� ��� ������� ������ ��������; ����-�� ����� ���������� �����.
 *		if (connection == null){
			MyLogger("Connection to DB is NULL");
			return myArrayList;
		}
 *DBQ5 05.12.14 - 07.12.14 ������� ������ �������� �� ������� ����������� (Fragment....) � ������ ������ (������� ���, ��� ������ ���� ��������� ����� � �������� static)
 * WS2 05,12,14 - 06,12,14 ���������� ActionBar; the action bar is useful for all activities to inform users about where they are
 * http://developer.android.com/training/basics/actionbar/setting-up.html
 *N7 25.11.2014 - 04.12.2014 ������, ��� ����� ����� �������. ����� ��������� � Actitvity, Fragments, ASyncTask; - ����� ����� ��������
 *01.12.14 - 04.12.14 Check Connect button behavior if selected wrong ip; 04.12.14 ����� � AsyncTask ��������� ����� cancel(), �� ������ ��������� ��� ����� ������ � �� ����������� onPostExecute(Boolean). ������� � �� ���� ������������ cancel(), � ������ ����� ��������� return Boolean doInBackground(...)
 * N1. 25.11.2014 - 04.12.2014 ������� �������� ip ������� ���� wifi �� ������������ � �������� �� �������� ���������; I'd done it when debugged behavior of ConnectDBTask 
 *03.12.14 - 03.12.14 choose an icon for Pochemu application
 *28.11.14 01.12.14 �������� ���������� OnSelectedZoneListener � OnSelectedSubzoneListener �� ���� ������������� OnLvSelectListener
 *28.11.14 - 29.11.14 ������� ������ ������. �������� ������������, �� ������-�� �� �����������.
 * 25.11.2014 - 28.11.14 �����������, ������ ����� ���������� 	GetZonesTask ����������� ����� OnCancelListener. � � ��� ��������� GET_ZONES task is canceled. 28.11.14 - ��-�� ������ ������ ProgressDialog.cancel. Use ProgressDialog.dismiss instead 
 *25.11.2014 - 28.11.14 �������� �� ������ ���������� � ������������ � ��. � ����� ��� �� ��. ������� ������������ ���������� ���������� 
 *25.11.2014 - 26.11.2014 ������: ������ 2 ���� ���������� onFinished ����� ����������� � ��? - use progressDialog.dismiss() instead progressDialog.cancel()
 *
 *23.11.2014 - 26.11.2014 ������� ������ ������ ������ � ��������� ����� - �.�. AsyncTask
 *25.11.2014 - 26.11.2014 �������� �� ������������� ������ � ���� �� Connection ��� ���� �������� � 
 * ��������� ��� ������ � ��������� ���������. - ������ ������ �����������/������������� � ��
 *
 ************************* --------- Failed: --------------- **********************
 *
 *26.11.2014 - 26.11.14 ToggleButton: use red and green colors to indicate status - �� ����� �������� �������
 * */
/**
 * 07.04.2015 ��� ���� �������:
 * Beta v.2
 * 1. ��������� SQLite, ����� ������� ��� ������� � �.�. ������� ���������� ���������
 * 		� ��������� �� Firebird
 * 2. ��������� ���������. ��� ��������� �� ��� ������ ��� ��������. 
 * 
 * 24.02.2015 ��� ���� �������:
 * Beta v.1
 * 1. �.2 �� 17,02,15 ���!!!
 * 2. ��� ������ ���� �������, ������� � ���� ������ ������.
 * 3. ����� ����� ����� ���, ��� ������������ ACRA bug report service 
 * 
 * 17.02.2015 ��� ���� �������:
 * 1. ��������� � ��������� � ������ � ������, ��� ����� ������������� �������� UI,
 * 		� �������� ���������� ������ � ��. � ���������� ����� UI �� ������� �����������.
 * 2. ������ � �������, ������� ������� �����������:
 * 		- ����� ���������� �� ���������� ������ ������ � ArrayList DataLessonInsert;
 * 		- ������ ����������� ������ ������� (�� �������� ����� ����� ������� ��������)
 * 		- �� ������� "��������� ����" ������������ ������ ������� � ������� ����� 
 * 			�� ������� ������� ��������������� �� ������ � ���� �� ���������.
 * 		- ������� Singletone ��� �������� ������ ��� UI  
 * 
 * 05.02.2015 ��� ���� �������:
 * 1. ����� ������� � ������� ������ ����� �������� ������ � �����
 * 2. �������� ������ �������� ������ ������
 * 3. ������ ����� ��������� ��� ������ ������� ����� ��������� ������ � ������ ����������� �������.
 * 4. ����� �������� � ����������� � ��. ������� �������� �� ���������� ����������.
 * 5. ������� � �������, ����� ����� ������ DbQueryTask<T>.launchCallableStatement().
 *      - ����� �������� ������������ �������� boolean, ����� ����� ��� ���������� ������.
 *      - � ��� ����� ����� ������ ���� ������� �� ���� ���������, ������� � ����� ������� ����� �����.
 * 
 * 29.01.2015 ��� ���� �������:
 * 1. ����� ������: ��������� �������� ����� � � ��� ������ �������. 
 * 	- �� ������� ������ ����� �������� ���� � ����� AutoCommit false, �� ��� ������,
 * 		���� ��������� ������, ����� ����� ���� ������� rollback.
 *  - ������ ���������� ������� back.
 *  
 * 2. ��������� �������� � ����������� - ������� ������� ��� ����� ��� ip, description, 
 *     login, passw.
 * 3. ActionBar.Tabs ��������� ������������ ���������� � ��������. �������� �������� ��� �������� ������.
 * 4. ������ ������������� ��� ������: DB and control. ��� ��������� �� �����������, 
 * 		���� �������� ���������� � ��� ��������� ������, �� ����� ����� �������� 
 * 		��� � ������������ � �������.  
 * 5. ���������� � �������, ��� �������� ������:
 *  - ������ ��� ����� ��������� �� ������ �� �������� ���������, � ������ 
 *  	����� ��������� ��������� ��� ������������ ��������� ������.
 *  - � ������ ������� ��������(��������) ������, ������� ��� ���� � �������� �������. 
 *  - ����� ��������� ��� ��������.
 *  - ����������� ������ ������� �� ������ �� ����������, � �� ������ ����� (����, �������)
 * 6. ����� ��� ������ ����� ������ � �� ��� ������ ������� �� ������ ����� � �������
 * 		� ��������� ������. ������ � ��� ���������, �� ���� ������ ��� ������ ���������.
 * ����� ����� ���������, �.�. ������ ����� ������������ ���������.     
 * 
 * 
 *  07.01.2015 ��� ���� �������:
 *  �������� ������� ���� � ����������� ������. � ���������:
 *   - � ���������� � ������� ������ "��������� � �������". ��� ������ ������ � ����������
 *   - ���� ������������ enum ��� ���� ����� ������;
 *   - ��������� ActionBar. � ��������� ��� ��� ���������� � ���� ������ ���� ��. "�������� ����"
 *   - ������ ������ � �� ��� ������; ������ ������ �� ���������� ������ ��� "��������� � �������"
 *   - �������� Connection � ����� AutoCommit true\false
 *   - ���������������� �������� ������� ��� ���������� ��������.
 *  ����������� �� ���������� �����. � ������ ������ ��� ������ ������ � ��� ������ 
 *    ������ � ����. ������� UI ��� �����, �� ����� ... ��. ���� �������:
 *  ����� �����, ����� ������ ������������ ������. �����, ��� �������� ��������� 
 *    ���� ����� DbStatements �� ������������ �������� ��� ���� ������� � ��.
 *    ������ ���� ������� ���, ��� ������� � �� � ��������� ����� � ��������� 
 *    ������� �� ������.  
 *   
 *   
 */

package ua.sanya5791.pochemu;

import ua.sanya5791.pochemu.control.OnBackPress;
import ua.sanya5791.pochemu.control.ShowFragment;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.ConnectDBTask;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;


public class MainActivity extends Activity implements 
					OnLvSelectListener, MyInterfaces{

	private static final boolean isDebug = true;
	public final static String TAG = "MainActivity";

	public static ConnectDBTask<MainActivity> connectDBTask;
	
	private static final String ACTION_BAR_SHOW = "ab_show";
	private static final String AB_CONNECT_STATE = "ab_connect_state";

	private static final String RIGHT_PANE_VISIBILITY = "righPaneVisibility";

	private MenuItem menuConnect;

	private boolean abIconConnectState;
	
	private ShowFragment showFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		myLogger("onCreate():");
        super.onCreate(savedInstanceState);
        //���. ���. ���� ��� ����, ����� ����� ���� �����. �������� ��������� �������
        if(!requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)){
        	myLogger("Window.FEATURE_INDETERMINATE_PROGRESS = false");
        }else{
        	myLogger("Window.FEATURE_INDETERMINATE_PROGRESS = true");
        }
        setContentView(R.layout.activity_main);
        
        //instantianalize classes i need here
        showFragment = new ShowFragment(this);

        if(savedInstanceState != null){
        	if(savedInstanceState.getBoolean(ACTION_BAR_SHOW)){
        		showFragment.restoreTabs();
        	}
        	
        	abIconConnectState = savedInstanceState.getBoolean(AB_CONNECT_STATE);
        	boolean state = savedInstanceState.getBoolean(RIGHT_PANE_VISIBILITY);
        	ShowFragment.rPaneVisible = state;
        	showFragment.rightPaneVisibility(state);
        }

		//TODO ������ ��� ������ �������� ����������, ����� ����� ����������� � �����
		SingletoneUI.getInstance().putKey(Keys.WORKER_ID, 1);
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate(R.menu.main, menu);
		
		//inflate action bar menu only if it's abscent
		inflater.inflate(R.menu.ab_main, menu);

		return true;
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(abIconConnectState){
			MenuItem item = menu.findItem(R.id.ab_connect);
			item.setTitle(R.string.ab_disconnect);
			item.setIcon(R.drawable.ic_action_connected2);
		}

		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		myLogger("Pressed item: " + item.getTitle());

		if (id == R.id.action_settings) {
			// ������� ����������, ������� � ������ ��� ��������
			Intent intent = new Intent(MainActivity.this,
					SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		//the ActionBar button "add" was pressed in FrPersonDetailList
		if (id == R.id.ab_add) {
			//now i show DatePicker DialogFragment FrdDateSelect
			showFragment.dateSelect(ShowFragment.REPLACE);

			return true;
		}

		//the ActionBar button "connect/disconnect" was pressed
		if (id == R.id.ab_connect) {
			menuConnect = item;
			String title = menuConnect.getTitle().toString();
			String connect = getResources().getString(R.string.ab_connect);
			
			if(title.equals(connect)){
				//make connection to DB in Async mode
				connectDBTask = 
						new ConnectDBTask<MainActivity>(this, this);
				//pass task to connectDBTask.doInBackground
				connectDBTask.execute("connectOpen");
				
				//show persons list only if frgmCon1 is empty
				if(getFragmentManager().findFragmentById(R.id.frgmCont1) == null)
					showFragment.personsList(ShowFragment.REPLACE);
			}else{
				//make connection to DB in Async mode
				connectDBTask = 
						new ConnectDBTask<MainActivity>(this, this);
				//pass task to connectDBTask.doInBackground
				connectDBTask.execute("closeConnect");
			}
			return true;
		}
		
		if (id == R.id.ab_show_skills) {
			showFragment.skillsList(ShowFragment.REPLACE, false);
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		myLogger("onBackPressed():");
		OnBackPress obp = new OnBackPress(this);

		obp.process();
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		myLogger("onSaveInstanceState():");
		
		ActionBar ab = getActionBar();
		if(ab.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS){
			outState.putBoolean(ACTION_BAR_SHOW, true);
			showFragment.storeTabs();
		}
		
		outState.putBoolean(AB_CONNECT_STATE, abIconConnectState);
		
		//save visibility state of right pane
		if(ShowFragment.isTablet()){
			outState.putBoolean(RIGHT_PANE_VISIBILITY, ShowFragment.rPaneVisible);
		}
		
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onTaskFinished(Object o, Boolean isSuccess) {
		
		String nameObject = o.getClass().getName();
		// if the ConnectDBTask
		if(nameObject.contains("ConnectDBTask")){
			//if the task was succeed
			if(isSuccess){
				myLogger("DB connected");
				String title = menuConnect.getTitle().toString();
				String connect = getResources().getString(R.string.ab_connect);
				if(title.equals(connect)){
					menuConnect.setIcon(R.drawable.ic_action_connected2);
					menuConnect.setTitle(R.string.ab_disconnect);
					abIconConnectState=true;
				}else{
					menuConnect.setIcon(R.drawable.ic_action_disonnected);
					menuConnect.setTitle(R.string.ab_connect);
					abIconConnectState=false;
				}
				
			}else{					
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("������ ����������")
					.setMessage("��������� ��������� � ����������� �����")
					.setIcon(R.drawable.ic_plug_disconnect)
					.setNegativeButton("Ok", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
				AlertDialog ad = builder.create();
				ad.show();
			}
		}
	}



	/**���� ����� ���������� ����������, ����� �������� �� ������� ������ ListView
	 * ��� ������ �� ��������� Object o
	 * */
	@Override
	public void onLvSelected(Object o, int position, String value) {
		myLogger("onLvSelected() for: "  + o.getClass().getSimpleName());

		String objectName = o.getClass().getName();
		
		// ����� �� ��������� "FragmentZonesList" ������ �� ������ ���, 
		//		���� ���������� id ������� ���� (zone_id)
		if(objectName.contains("FrPersonsList")){	//pressed lv in FragmentPersonsList
			
			int person_id = position;		
			showFragment.personDetailList(ShowFragment.REPLACE, 
					person_id);
			
		}else if(objectName.contains("FrDateSelect")){	//if picked a Date in FrDateSelect
			//now i can replace current fragment FrDateSelect with  FragmentZonesList
			showFragment.zonesList(ShowFragment.REPLACE, false);
			
		}else if(objectName.contains("FrZonesList")){
			showFragment.subZonesList(ShowFragment.REPLACE, false);
			
		}else if(objectName.contains("FrSubZonesList")){
			showFragment.materialsList(ShowFragment.REPLACE, false);
			
		}else if(objectName.contains("FrMaterialsList")){
			
			showFragment.skillsList(ShowFragment.REPLACE, false);
			
		}else if(objectName.contains("FrSkillsList")){
			//now i can show DialogFragment DFrSkillScore
			showFragment.dSkillScore();
			
		}else if(objectName.contains("DFrSkillScore")){	//pressed button in SkillScore
			showFragment.skillsSelected(ShowFragment.REPLACE);
			
		}else if(objectName.contains("FrSkillsSelected")){
			int chosenAction = position;
			
			//add skill or write lesson		
			switch (chosenAction) {
			case FrSkillsSelected.ADD_SKILL:
				//in this way i restore pane/s and tabs
				showFragment.restoreTabs();
				showFragment.rightPaneVisibility(true);
				showFragment.skillsList(ShowFragment.REPLACE, false);
				
				break;

			case FrSkillsSelected.WRITE_LESSON:
				
				//remove all tabs
				ActionBar ab = getActionBar();
				ab.removeAllTabs();

				//������ �������� � ���������� ������ �����
				showFragment.personsList(ShowFragment.REPLACE);

				break;
				
			default:
				break;
			}
		}
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}