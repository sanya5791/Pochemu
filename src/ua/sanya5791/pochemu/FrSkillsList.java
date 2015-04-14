 package ua.sanya5791.pochemu;

import java.sql.ResultSet;
import java.sql.SQLException;

import ua.sanya5791.dbsqlite.MyDB;
import ua.sanya5791.dbsqlite.Skills;
import ua.sanya5791.pochemu.control.DataSkillRegistration;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.DbQueryTask;
import ua.sanya5791.pochemu.dbfb.DbStatements;
import ua.sanya5791.pochemu.dbfb.DbStatements.SkillRegistrationInsert;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class FrSkillsList extends Fragment 
						  implements MyInterfaces, LoaderCallbacks<Cursor>{

	/**
	 * To load SQLite DB data in background and return data with Cursor
	 * @author sanya
	 */
	public static class MyCursorLoader<T extends MyDB> 
									extends CursorLoader {

		T  db;
		Cursor cursor;
		int dbtask_id;
		long _id;
//		String filter;
		
		/**
		 * 
		 * @param context 
		 * @param dbtask_db database
		 * @param dbtask_id id of task for db
		 */
		public MyCursorLoader(Context context, T db, int dbtask_id) {
			super(context);
			this.db = db;
			this.dbtask_id = dbtask_id;
		}

		/**
		 * 
		 * @param context 
		 * @param db database
		 * @param dbtask_id id of task for db
		 * @param _id _id of raw in db
		 */
		public MyCursorLoader(Context context, T db, int dbtask_id, 
											long _id) {
			//call upper constructor
			this(context, db, dbtask_id);
			this._id = _id;
		}
		
		@Override
		public Cursor loadInBackground() {
			switch (dbtask_id) {
			case DBTASK_GET_DATA_FOR_LV:
				cursor = db.getDataForLv();
				break;
				
			case DBTASK_GET_BY_ID:
				cursor = db.getById(_id);
				break;

			default:
				break;
			}
			
			return cursor;
		}

	}

	public static final String TAG = "FrSkillsList";
	private final boolean isDebug = true;

	public static String MATERIAL_ID = "material_id";
	public static String MATERIAL_NAME = "materialName";
	
	private final static String SEARCH_OPENED = "searchOpened";		//on rotate in saved Instance - state of search
	private static String SEARCH_QUERY;								//on rotate in saved Instance - string for filter
	
	public static final int ALL = 0;

	static int history_selected;					// 
	static String skill_name_selected;				//выбранное пользователем умение 
	
	int skill_id_selected;
	int has_value_selected;	

	private Activity curActivity;		//base Activity who places the fragment 
	private FrSkillsList curFragment;

	private OnLvSelectListener onLvSelectListener;

	private DbQueryTask<FrSkillsList> getSkillsTask;

	private DataSkillRegistration dsr;
	private SingletoneUI stUI; 

	private SimpleCursorAdapter scAdapter;
	private ResultSet rs_skills;

	private Skills dbSkills;
	//use to distinguish DB task in MyCursor loader 
	private static final int DBTASK_GET_DATA_FOR_LV = 0;
	private static final int DBTASK_GET_BY_ID = 2;

	private ListView lv;
	private TextView tv;
	private ProgressDialog progressDialog;
	private boolean isProgressDialogCanceled;

	private Drawable mIconOpenSearch;
	private Drawable mIconCloseSearch;
	private MenuItem mSearchAction;
	private boolean mSearchOpened;							//keeps track if the search bar is opened
	private String mSearchQuery;							//holds current text in the search bar
	private EditText mSearchEt;								//
	
	
	public static FrSkillsList newInstance(Bundle bundle){
		FrSkillsList fsl = new FrSkillsList();
		fsl.setArguments(bundle);
		
		return fsl;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		curActivity = activity;
		curFragment = this;
		
		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener = (OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() 
					+ " must implement OnLvSelectListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		myLogger("onCreateView: ");
		
		View view = inflater.inflate(R.layout.fragment_skills_list, container, false);


		if(savedInstanceState == null){
			mSearchQuery="";
			mSearchOpened = false;
			
		}else{
			mSearchOpened = savedInstanceState.getBoolean(SEARCH_OPENED);
			mSearchQuery = savedInstanceState.getString(SEARCH_QUERY);
		}
		//change title of ActionBar
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("Список умений");
		
		setHasOptionsMenu(true);

		stUI = SingletoneUI.getInstance();
		dsr = new DataSkillRegistration();

		mIconOpenSearch = curActivity.
				getResources().getDrawable(R.drawable.ic_action_search);
		mIconCloseSearch = curActivity.
				getResources().getDrawable(R.drawable.ic_action_remove);

		tv = (TextView)view.findViewById(R.id.tvTitle);
		
		//prepare progress Dialog
		progressDialog = new ProgressDialog(curActivity);
		progressDialog.setMessage("Wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// actions if a user cancel dialog
				myLogger("Progress dialog is canceled.");
				isProgressDialogCanceled = true;
			}
		});
		progressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				//process behind progress dialog is finished
				myLogger("Progress dialog is dismissed.");
				if(!isProgressDialogCanceled)
					// передаем через метод onLvSelected интерфеса onLvSelectListener PERSON_ID НАЖАТОГО элемента списка lv
					onLvSelectListener.onLvSelected(this, 
							skill_id_selected, null);
			}
		});
		
		//open DB
		dbSkills = new Skills(curActivity);
		dbSkills.open();

		lv = (ListView) view.findViewById(R.id.lv_out1);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				myLogger("The item N" + position + " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));

				//reset cancel state of pd and show the pd
				isProgressDialogCanceled = false;
				progressDialog.show();

				Cursor c = (Cursor) parent.getAdapter().getItem(position);
				//get _id in SQLite
				int iColumn = c.getColumnIndex(Skills.COLUMN_ID);
				Bundle bundle = new Bundle();
				Long _id = c.getLong(iColumn);
				bundle.putLong("_id", _id);
				// создаем лоадер чтения данных for ListView
				getLoaderManager().restartLoader(DBTASK_GET_BY_ID, 
			    		bundle, curFragment);
			}
		});

		String[] from = new String[] {
				Skills.COLUMN_SKILL_NAME, 
				Skills.COLUMN_LAST_VALUE, 
				Skills.COLUMN_HAS_VALUE, 
				};
		int[] to = new int[] {
				R.id.tvText,
				R.id.ratingBar1,
				R.id.tvTitle,
			};
		
		scAdapter = new SimpleCursorAdapter(curActivity, 
				R.layout.raw_skill_selected,
				null, from, to, 0);

		// назначаем адаптеру свой биндер
		scAdapter.setViewBinder(new ViewBinderSkillsSelected());
		
		lv.setAdapter(scAdapter);
		
		scAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			
			@Override
			public Cursor runQuery(CharSequence constraint) {
				
				return dbSkills.getFilteredDataForLv(constraint.toString());
			}
		});

		return view;
	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(SEARCH_OPENED, mSearchOpened);
		outState.putString(SEARCH_QUERY, mSearchQuery);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myLogger("onActivityCreated: ");

		final String prevTabName = prevTabName();
		
		String titleName = getLastFilterTitle();
		tv.setText(titleName);

		//on screen rotate don't call DB
		if(savedInstanceState != null){
			// создаем лоадер чтения данных for ListView
		    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
		    		null, this);
			return;
		}
		
		getSkillsTask = new DbQueryTask<FrSkillsList>(curActivity, this) {

			@Override
			protected Boolean launchCallableStatement() {
				//TODO DBQ18 29.03.15 добавить работу с кэшированием в SQLite
				rs_skills = getRsSkills();	
				if(rs_skills != null){
					//inflate inner DB with data from remote DB
					dbSkills.inflateFromRS(rs_skills);
					return true;
				}else
					return false;
			}

			private ResultSet getRsSkills() {
				
				int zone_id = 0, 
					subzone_id = 0, 
					material_id = 0, 
					person_id;

				//если фильтр установлен по материалам/материалУ
				if(prevTabName.equals(curActivity.getResources().
						getString(R.string.ab_materials))){
					zone_id = (Integer)
							stUI.getItem(Keys.ZONE_ID);
					subzone_id = (int)
							stUI.getItem(Keys.SUBZONE_ID);
					String s = (String) 
							stUI.getItem(Keys.MATERIAL_NAME);
					if(s != null)
						material_id = (int)
							SingletoneUI.getInstance().getItem(Keys.MATERIAL_ID);
					else
						material_id = ALL;
				//если фильтр установлен по подзоне
				}else if(prevTabName.equals(curActivity.getResources().
						getString(R.string.ab_subzones))){
					zone_id = (int)
							stUI.getItem(Keys.ZONE_ID);
					subzone_id = ALL;
					material_id = ALL;
				}
				myLogger("I_ZONE_ID: " + zone_id);
				myLogger("I_SUBZONE_ID: " + subzone_id);
				myLogger("I_MATERIAL_ID: " + material_id);
					
				person_id = (int)
						stUI.getItem(Keys.PERSON_ID);
				
				return DbStatements.skillSelectLast(zone_id, 
														subzone_id,
														material_id, 
														person_id);
			}

		};
		getSkillsTask.execute();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.ab_search, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		//clear menu item created by previous fragments
		menu.removeItem(R.id.ab_show_skills);
		
		mSearchAction = menu.findItem(R.id.ab_search);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menu_id = item.getItemId();
		switch (menu_id) {
		case R.id.ab_search:
			if(mSearchOpened){
				closeSearchBar();
			}else{
				openSearchBar(mSearchQuery);
			}
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void openSearchBar(String searchText){
		ActionBar ab = curActivity.getActionBar();
		ab.setDisplayShowCustomEnabled(true);
		ab.setCustomView(R.layout.search_bar);
		
		mSearchEt = (EditText)ab.getCustomView()
				.findViewById(R.id.et_search);
		mSearchEt.addTextChangedListener(new SearchWatcher());
		mSearchEt.setText(mSearchQuery);
		mSearchEt.requestFocus();
		
		//show keyboard
		InputMethodManager imm = (InputMethodManager) curActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mSearchEt, InputMethodManager.SHOW_IMPLICIT);
		
		//set icon "Close search"
		mSearchAction.setIcon(mIconCloseSearch);
		
		//set global variable about state of search
		mSearchOpened = true;
	}
	
	private void closeSearchBar(){
		ActionBar ab = curActivity.getActionBar();
		// Remove custom view.
		ab.setDisplayShowCustomEnabled(false);

		try{
			mSearchEt.setText("");
			mSearchAction.setIcon(mIconOpenSearch);
		}catch (NullPointerException e){
			myLogger("mSearchEt is NULL");
		}
		
		
		//hide keyboard
		curActivity.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
			);
//TODO 07.04.15 hide keyboard
//		InputMethodManager imm = (InputMethodManager) curActivity
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(mSearchEt.getWindowToken(), 0);
		
		mSearchOpened = false;
	}

	private String getLastFilterTitle() {
		String title = null;
		
		String prevTabName = prevTabName();

		//если фильтр установлен по материалу
		if(prevTabName.equals(curActivity.getResources().
				getString(R.string.ab_materials))){
			if(SingletoneUI.getInstance().getItem(Keys.MATERIAL_NAME) != null){
				title = curActivity.getResources().getString(R.string.material)
						+ " " 
						+ stUI.getItem(Keys.MATERIAL_NAME);
			}else
				title = curActivity.getResources().getString(R.string.subzone)
				+ " " 
				+ stUI.getItem(Keys.SUBZONE_NAME);
			
		//если фильтр установлен по зоне
		}else if(prevTabName.equals(curActivity.getResources().
				getString(R.string.ab_subzones))){
			title = curActivity.getResources().getString(R.string.zone)
					+ " " 
					+ stUI.getItem(Keys.ZONE_NAME);
		}

		return title;
	}

	/**
	 * @return имя предпоследнего таба
	 */
	private String prevTabName() {
		//узнаем имя таба до "НАВЫКИ"
		ActionBar ab = curActivity.getActionBar();
		int index = ab.getSelectedNavigationIndex();
		
		myLogger("index = " + index);	//строчкой ниже было исключение, после длинного перерыва
		
		//if Navigation tabs are hidden 
		if(index == -1){
			return "без табов";
		}
			
		Tab prevTab = ab.getTabAt(index-1);
		
		return (String) prevTab.getText();
	}

	/**
	 * выполнить запрос к БД чтобы получить иерархию zone_id, subZone_id, material_id 
	 * и записать их в dsr и SingleTone;
	 * @param skill_id skill_id для которого получить иерархию
	 */
	private void fillSkillHierarchy(final int skill_id) {
		myLogger("fillSkillHierarchy(): ");
		new AsyncTask<Void, Void, Void>() {
	
			@Override
			protected Void doInBackground(Void... params) {
				ResultSet rs = DbStatements.getSkillHierarchy(skill_id);
	
				try {
					rs.beforeFirst();
					rs.next();
					int id = rs.getInt("ZONE_ID");
					dsr.putToHashMap(
							SkillRegistrationInsert.ZONE_ID, id);
					stUI.putKey(Keys.ZONE_ID, id);
					
					id = rs.getInt("SUBZONE_ID");
					dsr.putToHashMap(
							SkillRegistrationInsert.SUBZONE_ID, id);
					stUI.putKey(Keys.SUBZONE_ID, id);
	
					id = rs.getInt("MATERIAL_ID");
					dsr.putToHashMap(
							SkillRegistrationInsert.MATERIAL_ID, id);
					stUI.putKey(Keys.MATERIAL_ID, id);
				} catch (SQLException e) {
					e.printStackTrace();
				}
	
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
			}
			
		}.execute();
		
	}

	@Override
	public void onTaskFinished(Object o, Boolean isSuccess) {
		myLogger("onTaskFinished: ");

		if (o instanceof DbQueryTask) {
			if (isSuccess) {

				// создаем лоадер чтения данных for ListView
			    getLoaderManager().restartLoader(DBTASK_GET_DATA_FOR_LV, 
			    		null, this);
			} else {
				Toast.makeText(curActivity, "Ошибка получения навыков", 
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int dbtask_id, Bundle args) {
		switch (dbtask_id) {
		case DBTASK_GET_DATA_FOR_LV:
			return new MyCursorLoader<Skills>(curActivity, dbSkills, dbtask_id);
	
		case DBTASK_GET_BY_ID:
			long _id = args.getLong("_id");
			return new MyCursorLoader<Skills>(curActivity, dbSkills, 
														dbtask_id, _id);
			
		default:
			break;
		}
		return null;
	}

	@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			//получаем результат работы лоадера – новый курсор с данными. 
			//Этот курсор мы отдаем адаптеру методом swapCursor.
			switch (loader.getId()) {
			case DBTASK_GET_DATA_FOR_LV:
	
	//			cursor = data;
				scAdapter.swapCursor(data);
				break;
	
			case DBTASK_GET_BY_ID:
				
				//save picked subzone's data to dsr and Singletone
				if(saveSkill(data)){
					//consider  progressDialog.onDismissListener callback
					progressDialog.dismiss();
				}else{
					progressDialog.cancel();
					Toast.makeText(curActivity, 
							"Не удалось сохранить данные о выбранном Материле, " +
							"попробуйте еще раз!", 
							Toast.LENGTH_LONG).show();
				}
				break;
	
			default:
				break;
			}
			
		}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	/**
	 * save data associated with picked lv item to DataSkillRegistration
	 * and SingleTone
	 * @param data
	 * @return true on sucess
	 */
	
	private boolean saveSkill(Cursor data) {
		
		//whether i can move cursor to the first raw
		if( data == null || !data.moveToFirst()){
			return false;
		}
		
		int columnIndex;
		
		columnIndex = data.getColumnIndex(Skills.COLUMN_SKILL_ID);
		skill_id_selected = data.getInt(columnIndex);
		
		if(dsr.isSkillSelected(skill_id_selected)){
			AlertDialog.Builder builder 
			= new AlertDialog.Builder(curActivity);
			builder.setTitle("Внимание!")
				.setMessage("Этот навык уже добавлен, выберите другой.")
				.setNegativeButton("Ok", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
			AlertDialog ad = builder.create();
			ad.show();
			return false;
		}
		
		dsr.putToHashMap(SkillRegistrationInsert.I_SKILL_ID, 
				skill_id_selected);
		
		columnIndex = data.getColumnIndex(Skills.COLUMN_SKILL_NAME);
		skill_name_selected = data.getString(columnIndex);
		dsr.putToHashMap(SkillRegistrationInsert.SKILL_NAME, 
		skill_name_selected);
		
		columnIndex = data.getColumnIndex(Skills.COLUMN_HAS_VALUE);
		dsr.putToHashMap(SkillRegistrationInsert.I_HAS_VALUE, 
				data.getInt(columnIndex));
		
		//put last score
		columnIndex = data.getColumnIndex(Skills.COLUMN_LAST_VALUE);
		dsr.putToHashMap(SkillRegistrationInsert.LAST_VALUE, 
				data.getInt(columnIndex));
		
		dsr.putToHashMap(SkillRegistrationInsert.I_PERSON_ID, 
						 stUI.getItem(Keys.PERSON_ID));

		//TODO замени позже, когда будешь знать айди реального работника					
		dsr.putToHashMap(SkillRegistrationInsert.I_WORKER_ID, 
				1);

		fillSkillHierarchy(skill_id_selected);

		return true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbSkills.close();
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
	
	
	public class SearchWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			myLogger("afterTextChanged(): ");
			mSearchQuery = mSearchEt.getText().toString();
			myLogger("mSearchQuery: " + mSearchQuery);
			scAdapter.getFilter().filter(mSearchQuery);
		}
	}



	@Override
	public void onPause() {
		closeSearchBar();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}

