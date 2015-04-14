/*
 * 
 ************************* --------- TODOs: --------------- **********************
 *
 *-------------- DB Queries: last is DBQ19
 *DBQ19 08.04.15 - Action Bar button "connect" must check connection state every time 
 *	itself's refreshing in onPrepareOptionsMenu;
 *DBQ7 07.12.14 - public class DbStatements; все запросы переделать в один универсальный; куда бы я передавал массив входящих и исходящих ключ-значение параметров.
 *
 *-------------- User Interface: last is UI10
 *UI9 27.02.15 - enter settings only from MainActiviti or PersonsList
 *UI6 04.02.15 - allow user remove items из списка выбранных навыков 
 *UI5 04.02.15 - убирать из списка навыков те, которые сейчас в skillsSelected 
 *UI2 02.01.15 -  календарь выбора даты урока. Убрать номера недель или найти стороннюю библиотеку легкого календаря.
 *
 *-------------- Java Experience: last is JE10 
 *JE6 1.2. 25.11.2014 - сделать проверку поведения, если из вызывающего класса Fragment_main_list убрать 
 *implements AsyncTaskListener. Что будет при возникновении этой ошибки???
 *
 *-------------- BUGs: last is BU17
 *BU17 08.04.15 FrSkillsList; bug appears if search EditText is filled and rotate screen
 *BU13 30.03.15 FrSkillsList search make case insensetive; This SQLite limitation for not english letters
 *-------------- Without section: last is WS7
 * 
 *
 ************************* --------- Done: --------------- **********************
 *
 *JE7 24.02.15-12.04.15 прикручивать ACRA bug report service 
 *JE5 02.12.14 - 07.04.15 Разобраться. я не понял, почему когда вызываешь DbQueryTask из Fragment_main_list, то внутри DbQueryTask значение this=Fragment_main_list$2
 *in UI thread. So you should use it in AsyncTask.doInBackground to learn 
 *whether the Connection is valid
 *UI7 04.02.15 - 07.04.15 в настройках, при нажатии на ip из списка, сразу его выбрать и закрыть activity.
 *UI10 09,03,15 - 07.04.15 создать интерфейс под Tablet;
 *BU7 26.02.15 - 07.04.15DFrSkillScore press back and return to FrSkillsList 
 *		(the ResultSet rs_skills is closed)
 *		07.04.15: it's fixed as a result of use Result Set an Loaders
 *BU9 11.03.15 07.04.15 - click left tab and it must appear fragment not in Fragment manager;
 *	cursor is lost, because 2 queries are made in async; 1-st not completed and
 *	2-nd is started;
 *	07.04.15: it's fixed as a result of use Result Set an Loaders
 *BU11 12.03.15 - 07.04.15 DSkillScore. If Back pressed on tablet as well on phone
 *BU12 12.03.15-07.04.15 Загаловки фрагментов; особенно навыки на планшете слева. 
 *BU15 03.04.15 - 07.04.15 ActionBar icon "Показать навыки" appeared in skills pane; 
 *DBQ9 30.12.14 - 07.04.15 использовать ResultSet.close() для закрытия соединения с БД и освобождения места; 
 *					и это советуется в инфе этого метода. Нужно проверить на закрытие все rs.
 *					07,04,15 я стал использовать ResultSet, поэтому эта задача уже больше не актуальна
 *DBQ17 13.03.15 - 07.04.15 Внедрить SQLite, чтобы хранить данные полученные из FireBird сервера
 *DBQ2 03.12.14 - 07.04.15 протестить алгоритм отмены ProgressDialog для запросов.
 *JE8 01.04.15 - 05.04.15 ShowFragment: use field isOnTablet instead of many findViwById(container) checks
 *JE10 02.04.15-03.04.15 ShowFragment: all onTabReselected are  almost the same; write one method;
 *JE9 02.04.15-03.04.15 ShowFragment:  think, maybe you don't need this global variables; answer: i need them for phone layout
 *BU14 01.04.15-03.03.15  ShowFragment: onRotate bug with using final field;
 *BU8 11.03.15-11.03.15 tablet. left persons list, right zones list; problem appears when click the left panel
 *UI8 26.02.15 - 27.02.15 add icons on buttons "Save lesson" and "More skill"
 *BU2 04.01.15 25.02.15 - ActionBar.tabs the bag during screeen rotate. 04.02 - думаю, что табы остаются, 
 *	только в mainactivity при инициализации они не показываются. Поэтому нужно проверять видимость табов 
 *	при вращении экрана
 *WS5 06.02.15 - 20.02.15 перенести обработку из MainActivity to FrPersonDetails - 
 *	ActionBar button "add" was pressed in FragmentPersonDetailList
 *WS6 17.02.15 - 24.02.15 убрать везде Bundle. Вместо него используется SingletoneUI
 *BU3 06.02.15 - 24.02.15 когда нажимешь на кн. еще навык из списка выбранныъ навыков, то 
 *	skillsList вызывается дважды
 *DBQ15 29.01.15 - 20.02.15 use SingleTone pattern to keep data in DataLessonInsert and DataSkillRegistration
 *BU4 10.02.15 - 20.02.15 пока оставляю баг, что на каждый навык создается отдельный урок;
 *	Нужно в FrSkillsList.getLessonID() подправить условие: если навык остался тот же,
 *	то новый урок не создаем.
 *	А то, и вообще, подумай может не создавать уроки сразу, а создавать их, когда 
 *	будет нажата кн. записать урок!!! И тогда проводить всю аналитику 
 *	20.02.15 Ура!!! я так и сделал
 *WS7 17.02.15 - 19.02.15 ShowFragment.sweepData() sweep SingletoneUI
 *BU6 10.02.15 - 20.02.15 работает неправильно buttonBack()
 *BU5 13.02.15 - 17.02.15 zones, then skills list. Then button back - NullPointer
 *DBQ16 07.01.15 - 17.02.15 надо бы предусмотреть возврат ConnectDBTask.setAutoCommit() метода, если fault
 *BU1 28.01.15 - 06.02.15 FrSkillsList ListView items with stars are not clickable, as well as in the FrSkillsSelected
 *				as well as items with checkbox in FrPersonDetails. 06.02.15 resolving: you must set 
 *				any items (кроме TextView, ImageView) to setFocusable(false);
 *				Better do it not in xml; 
 *DBQ3 04.12.14 - 05.02.15 Debug DbQueryTask. If Coneection enabled, but later WiFi is switched to EDGE - appears lots of untreated Exceptions
 *					Соединился, вышел за зону wifi, вернулся и делаешь запрос к БД - вылетает ошибка. 
 *					05.02.15 решение: 
 * 					 - ConnectDBTask<T> заменил проверку на mConnection.isValid(5)
 * 					 - DbQueryTask<T> launchCallableStatement() добавил return Boolean; 
 * 						ну и соответственно внес изменения по всему коду в launchCallableStatement
 *DBQ4 28.11.2014 - 05.02.15 Изучить Connection .isValid. - It's impossible to use this method
 *DBQ13 28.01.15 - 05.02.15 пользователь в FrSkillsList и если открыт урок, а пользователь нажал один из табов: 1. записать урок с имеющимися навыками 2. начать создавать новый урок
 *DBQ14 28.01.15 - 01.02.15 прикрутить скрипт SKILL_REGISTRATION_SELECT_LAST при показе списка навыков
 *UI4 26.01.15 - 02.02.15 прикрутить ActionBar.tabs для навигации
 *WS4.4 26.12.14 - 05.01.15 CallableStatement for SKILL_REGISTRATION
 *UI3 21.1.15 - 22.01.15 в настройки добавить логин-пароль к БД 
 *WS4 22.12.14 - 21.01.15 Add Lesson and Skill for the the person is chosen;
 *		Алгоритм со стороны UI такой: Фио ребенка-Дата урока-зона-подзона-материал-умения. 
 *Выбираем умение-оценка(DialogFragment) и возвращаемся к выбору умений. 
 *При этом, меняется отображение умений: 
 * - добавляется в ActionBar иконка "записать в базу урок";
 * - ниже фрагмена со списком умений появляется фрагмент "Урок" 
 * 		со списком выбранных умений.
 * Можно добавлять еще умения, нажимая на них в списке умений, в созданный урокж
 * Если нажать на кн ActionBar.записать урок - то урок и умения заносятся в базу.
 * А если нажать на кн. назад, то урок отменяется.
 * Предусмотреть возможность удаления умения из списка умений урока.
 * 		Алгоритм со стороны программы:
 * доходим до выбора умений, нажимаем на один из элементов, всплывает DialogFragment "Как оценивать".
 * см. пример в Русика проге. Нажимаем кн. добавить в урок.
 * Появляется (below fragment выбора умений) дополнительный фрагмент "Урок" со списком умений и в ActionBar кн. Сохранить урок.
 * Все данные сохраняю в ArrayList<HashMap<String, Object>>. 
 * ArrayList содержит список умений. HashMap содержит подробную инфу для каждого умения,
 *  которые будут записываться в таблицы LESSON and SKILL_REGISTRATION;
 * 
 *DBQ8 24.12.14 - 15.01.15 написать метод записи в базу "LESSON_INSERT"
 *DBQ11 02.01.15 - 15.01.15 start to use "enum" Ennumerated types to describe field-names of DB procedures&tables. Хотя я и встретил
 *						инфу о том, что ранее использование enum не рекомнедовалось в Андроид, но сейчас эта рекомендация не актуальна. 
 *BUG 09,01,15 - 19.01.15 при повороте экрана добавляется запись в ArrayList
 *DBQ12 06.01.15 - 15.01.15 если создание данных для запроса не получилось
 *DBQ10 29.12.14 - 02.01.15 что делать, если запись умения не удалась?
 *WS4.1 22.12.14 - 23.12.14 Start is ActionBar "add" button;
 *WS4.2 22.12.14 - 24.12.14 разобраться с календариком для ввода даты
 *WS4.3 24.12.14 - 25.12.14 CallableStatement for LESSON_INSERT
 *WS3 19.12.14 - 22.12.14 ActionBar - implement icon to add a skill in PersonDetail
 *WS1 05,12,14 - 18.12.14 прикрутить фрагмент "Подробнее о ребенке". Сделать ListView same as in ExDialer - with icons
 * 			Note: I used a custom ItemListView; 
 * 				i used SimpleAdapter.ViewBinder (string of my code: adapter.setViewBinder(new MyViewBinder());)
 * 				http://www.startandroid.ru/ru/uroki/vse-uroki-spiskom/109-urok-50-simpleadapter-ispolzuem-viewbinder.html
 * WS0 05,12,14 - 18.12.14 прикрутить список детей
 * WS2 10.12.14 - 17.12.14 ActionBar; задача при возвращении назад фрагмента на предыдущий - нужно менять заголовок action bar
 *DBQ6 06.12.14 - 07.12.14 во все запросы внести проверку; мало-ли может потерялась связь.
 *		if (connection == null){
			MyLogger("Connection to DB is NULL");
			return myArrayList;
		}
 *DBQ5 05.12.14 - 07.12.14 вынести методы запросов из раздела контроллера (Fragment....) в раздел данных (здается мне, это должен быть отдельный класс с методами static)
 * WS2 05,12,14 - 06,12,14 прикрутить ActionBar; the action bar is useful for all activities to inform users about where they are
 * http://developer.android.com/training/basics/actionbar/setting-up.html
 *N7 25.11.2014 - 04.12.2014 учесть, что экран могут вращать. Будут изменения в Actitvity, Fragments, ASyncTask; - вроде везде работает
 *01.12.14 - 04.12.14 Check Connect button behavior if selected wrong ip; 04.12.14 Когда в AsyncTask вызываешь метод cancel(), то задача снимается как можно скорее и НЕ выполняется onPostExecute(Boolean). Поэтому я не стал использовать cancel(), а вместо этого использую return Boolean doInBackground(...)
 * N1. 25.11.2014 - 04.12.2014 сделать проверку ip текущей сети wifi на соответсвтие с подсетью из настроек программы; I'd done it when debugged behavior of ConnectDBTask 
 *03.12.14 - 03.12.14 choose an icon for Pochemu application
 *28.11.14 01.12.14 заменить интерфейсы OnSelectedZoneListener и OnSelectedSubzoneListener на один универсальный OnLvSelectListener
 *28.11.14 - 29.11.14 вывести список подзон. Фрагмент показывается, но почему-то не заполняется.
 * 25.11.2014 - 28.11.14 Разобраться, почему после выполнения 	GetZonesTask срабатывает метод OnCancelListener. И в лог выводится GET_ZONES task is canceled. 28.11.14 - из-за вызова метода ProgressDialog.cancel. Use ProgressDialog.dismiss instead 
 *25.11.2014 - 28.11.14 Повесить на кнопку соединение и разъединение с БД. А потом уже из др. классов использовать полученное соединение 
 *25.11.2014 - 26.11.2014 Вопрос: почему 2 раза вызывается onFinished после соеднинения с БД? - use progressDialog.dismiss() instead progressDialog.cancel()
 *
 *23.11.2014 - 26.11.2014 вынести логику работы модели в отдельный класс - т.е. AsyncTask
 *25.11.2014 - 26.11.2014 подумать об использовании одного и того же Connection для всех запросов и 
 * закрывать его вместе с закрытием программы. - Сделал кнопку соединиться/разъединиться с БД
 *
 ************************* --------- Failed: --------------- **********************
 *
 *26.11.2014 - 26.11.14 ToggleButton: use red and green colors to indicate status - не нашел простого решения
 * */
/**
 * 07.04.2015 что было сделано:
 * Beta v.2
 * 1. Прикрутил SQLite, чтобы хранить кэш таблицы и т.о. снизить количество обращений
 * 		к удаленной БД Firebird
 * 2. Поддержка планшетов. Это оказалось не так просто как казалось. 
 * 
 * 24.02.2015 что было сделано:
 * Beta v.1
 * 1. п.2 от 17,02,15 Ура!!!
 * 2. это первый бета вариант, который я могу отдать Марине.
 * 3. копию делаю перед тем, как прикручивать ACRA bug report service 
 * 
 * 17.02.2015 что было сделано:
 * 1. запутался в программе и пришел к выводу, что нужно разрабатывать отдельно UI,
 * 		а отдельно сохранение данных в БД. В результате довел UI до нужного функционала.
 * 2. Пришел к выводам, которые намерен реализовать:
 * 		- нужно отказаться от накопления списка уроков в ArrayList DataLessonInsert;
 * 		- просто накапливаем список навыков (из которого можно позже удалять элементы)
 * 		- по нажатию "сохранить урок" обрабатываем список навыков и создаем уроки 
 * 			со списком навыков отфильтрованных по одному и тому же материалу.
 * 		- создаем Singletone для хранения данных для UI  
 * 
 * 05.02.2015 что было сделано:
 * 1. Навел порядок с очистой данных после создания навыка в уроке
 * 2. устранил ошибку создания пустых уроков
 * 3. Теперь можно создавать для одного ребенка сразу несколько уроков с разным количеством навыков.
 * 4. Начал работать с соединением к БД. Поменял проверку на валидность соединения.
 * 5. Подошел к моменту, когда нужно менять DbQueryTask<T>.launchCallableStatement().
 *      - нужно добавить возвращаемое значение boolean, чтобы знать как выполнился запрос.
 *      - а для этого нужно менять кучу методов по всей программе, поэтому и решил сделать копию всего.
 * 
 * 29.01.2015 что было сделано:
 * 1. Самое важное: прикрутил создание урока и в нем список навыков. 
 * 	- до момента записи урока перевожу базу в режим AutoCommit false, на тот случай,
 * 		если возникнут ошибки, чтобы можно было сделать rollback.
 *  - ручное управление кнопкой back.
 *  
 * 2. переделал активити с настройками - добавил фргмент для ввода там ip, description, 
 *     login, passw.
 * 3. ActionBar.Tabs прикрутил динамическое добавление и удаление. Осталась проблема при повороте экрана.
 * 4. Создал дополнительно два пакета: DB and control. Всю программу не переделывал, 
 * 		лишь частично переместил в них некоторые классы, но кладу новые элементы 
 * 		уже в соответствии с пакетом.  
 * 5. Встречался с Мариной, она уточнила задачу:
 *  - навыки она хочет добавлять не только из текущего материала, а значит 
 *  	нужно создавать незаметно для пользователя несколько уроков.
 *  - в списке навыков отмечать(выделять) навыки, которые уже есть у текущего ребенка. 
 *  - хочет программу для планшета.
 *  - возможность выбора навыков не только из материалов, а из любого места (Зоны, подзоны)
 * 6. Русик уже создал новый запрос к БД для вывода навыков из любого места и отметки
 * 		о последней оценке. Запрос я уже прикрутил, но пока только для одного Андросова.
 * Делаю копию программы, т.к. дальше будут значительные изменения.     
 * 
 * 
 *  07.01.2015 что было сделано:
 *  подробно описано выше в выполненных тасках. А глобально:
 *   - я разобрался и внедрил список "подробнее о ребенке". Это кастом список с картинками
 *   - стал использовать enum для имен полей таблиц;
 *   - прикрутил ActionBar. И использую его для заголовков и пока только одна кн. "Добавить урок"
 *   - создал запрос к БД для записи; прямой запрос из нескольких таблиц для "Подробнее о ребенке"
 *   - Перевожу Connection в режим AutoCommit true\false
 *   - попереименовывал названия классов для сокращения названий.
 *  Остановился на добавлении урока. Я создал методы для записи уроков и для записи 
 *    умений в урок. Написал UI для этого, но ввиду ... см. ниже причину:
 *  Бэкап делаю, чтобы начать использовать пакеты. Решил, что ошибочно использую 
 *    один класс DbStatements со статическими методами для всех запросо к БД.
 *    Теперь хочу вынести все, что связано с БД в отдельный пакет и разделить 
 *    запросы на классы.  
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
        //уст. доп. фичу для окна, чтобы можно было показ. кружочек занятости системы
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

		//TODO замени эту строку реальным работником, когда будет авторизация в прогу
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
			// выводим активность, которую я создал для настроек
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
				builder.setTitle("Ошибка соединения")
					.setMessage("Проверьте настройки и попытайтесь снова")
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



	/**этот метод интерфейса вызывается, когда нажимают на элемент списка ListView
	 * или кнопку во фрагменте Object o
	 * */
	@Override
	public void onLvSelected(Object o, int position, String value) {
		myLogger("onLvSelected() for: "  + o.getClass().getSimpleName());

		String objectName = o.getClass().getName();
		
		// когда во фрагменте "FragmentZonesList" нажали на список зон, 
		//		сюда передается id нажатой зоны (zone_id)
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

				//теперь вернемся к начальному списку детей
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