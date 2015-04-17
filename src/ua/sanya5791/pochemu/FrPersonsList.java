 package ua.sanya5791.pochemu;
/**
 * Здесь есть Пример использования анимации элемента в списке ListView
 * он находится в onCreate
 */
import java.sql.ResultSet;

import ua.sanya5791.dbsqlite.Persons;
import ua.sanya5791.pochemu.control.DataSkillRegistration;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.DbQueryTask;
import ua.sanya5791.pochemu.dbfb.DbStatements;
import ua.sanya5791.pochemu.dbfb.DbStatements.SkillRegistrationInsert;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class FrPersonsList extends Fragment 
						   implements 
						   MyInterfaces, 
						   LoaderCallbacks<Cursor>{

	/**
	 * To load SQLite DB data in background and return data with Cursor
	 * @author sanya
	 *
	 */
	public static class MyCursorLoader extends CursorLoader {

		Persons db;
		Cursor cursor;
		int dbtask_id;
		long _id;
		
		/**
		 * 
		 * @param context 
		 * @param dbtask_db database
		 * @param id id of task for db
		 */
		public MyCursorLoader(Context context, Persons dbtask_db, int id) {
			super(context);
			this.db = dbtask_db;
			this.dbtask_id = id;
		}

		/**
		 * 
		 * @param context 
		 * @param db database
		 * @param dbtask_id id of task for db
		 * @param _id _id of raw in db
		 */
		public MyCursorLoader(Context context, Persons db, int dbtask_id, 
											long _id) {
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

	public static final String TAG = "FrPersonsList";
	private final boolean isDebug = true;
	
	private Activity curActivity;		//base Activity who places the fragment 
	private FrPersonsList curFragment;
	
	private OnLvSelectListener onLvSelectListener;

	private DbQueryTask<FrPersonsList> getPersonsTask;
	private static final String ASYNCTASK_WAS_NOT_FINISHED = "AsyncTaskNotFinished"; 
	private SimpleCursorAdapter scAdapter;
	private ResultSet rs;
	
	//use to distinguish DB task in MyCursor loader 
	private static final int DBTASK_GET_DATA_FOR_LV = 0;
	private static final int DBTASK_GET_BY_ID = 1;

	private Persons dbPersons;

	private ListView lv;
	private ProgressDialog progressDialog;
	
	private static int personSelectedId;

	
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
		View view = inflater.inflate(R.layout.fragment_persons_list, container, false);

		myLogger("onCreateView: ");

		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("Список детей");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
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
				
			}
		});
		progressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				myLogger("Progress dialog is dismissed.");
				
				// передаем через метод onLvSelected интерфеса onLvSelectListener PERSON_ID НАЖАТОГО элемента списка lv
				onLvSelectListener.onLvSelected(this, 
						personSelectedId, null);
			}
		});
		
		//open DB
		dbPersons = new Persons(curActivity);
		dbPersons.open();

		lv = (ListView) view.findViewById(R.id.lv_out1);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				myLogger("The item N" + position + " with id=" + id
						+ " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));
				
				progressDialog.show();
				
				Cursor c = (Cursor) parent.getAdapter().getItem(position);
				//get _id in SQLite
				int iColumn = c.getColumnIndex(Persons.COLUMN_ID);
				Bundle bundle = new Bundle();
				Long _id = c.getLong(iColumn);
				bundle.putLong("_id", _id);
				// создаем лоадер чтения данных for ListView
				getLoaderManager().restartLoader(DBTASK_GET_BY_ID, 
			    		bundle, curFragment);
				
/*      Пример использования анимации элемента в списке ListView				
				final String item = parent.getItemAtPosition(position).toString();
				MyLogger("(String) parent.getItemAtPosition(position): " 
						+ item);
				view.animate().setDuration(1000).alpha(0)
					.withEndAction(new Runnable() {
						
						@Override
						public void run() {
//							myArrayList.remove(item);
							myArrayList.remove(position);
							adapter.notifyDataSetChanged();
							view.setAlpha(1);
						}
					});
				*/
			}
		});
		
		String[] from = new String[] {
				Persons.COLUMN_LAST_NAME, 
				Persons.COLUMN_FIRST_AND_SURNAME_NAME,
				};
		int[] to = new int[] {
				android.R.id.text1,	
				android.R.id.text2,};
		
		scAdapter = new SimpleCursorAdapter(curActivity, 
				android.R.layout.simple_list_item_2, 
				null, from, to, 0);
		lv.setAdapter(scAdapter);

		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myLogger("onActivityCreated: ");
		
		if(savedInstanceState != null &&
				! savedInstanceState.getBoolean(ASYNCTASK_WAS_NOT_FINISHED)){
			// создаем лоадер чтения данных for ListView
		    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
		    		null, this);
			return;
		}

		getPersonsTask = new DbQueryTask<FrPersonsList>(curActivity, this) {

			@Override
			protected Boolean launchCallableStatement() {
				rs = DbStatements.personsSelect();
				if(rs != null){
					//inflate inner DB with data from remote DB
					dbPersons.inflateFromRS(rs);
					return true;
				}
				else
					return false;
			}
			
		};
		getPersonsTask.execute();
		
	}

	@Override
	public void onTaskFinished(Object o, Boolean isSuccess) {
		
		myLogger("onTaskFinished: ");
		
		if (o instanceof DbQueryTask) {
			if (isSuccess) {
				// создаем лоадер чтения данных for ListView
			    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
			    		null, this);

			} else {
				Toast.makeText(curActivity, "Ошибка получения списка детей", 
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbPersons.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int dbtask_id, Bundle args) {

		switch (dbtask_id) {
		case DBTASK_GET_DATA_FOR_LV:
			return new MyCursorLoader(curActivity, dbPersons, dbtask_id);

		case DBTASK_GET_BY_ID:
			long _id = args.getLong("_id");
			return new MyCursorLoader(curActivity, dbPersons, dbtask_id, _id);
			
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

			scAdapter.swapCursor(data);
			break;

		case DBTASK_GET_BY_ID:
			
			//save picked person's data to dsr and Singletone
			if(savePerson(data)){
				progressDialog.dismiss();
			}else{
				Toast.makeText(curActivity, 
						"Не удалось сохранить данные о ребенке, попробуйте еще раз!", 
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
	private boolean savePerson(Cursor data) {
		int iColumn;
		DataSkillRegistration dsr = new DataSkillRegistration();

		dsr.clear();
		
		//print all Cursor data
		if (data != null) {
			if (data.moveToFirst()) {
				String str;
				do {
					str="";
					for(String cn : data.getColumnNames()){
						str = str.concat(cn + " = " 
								+ data.getString(data.getColumnIndex(cn)) + "; ");
					}
					myLogger(str);
				} while (data.moveToNext());
			}
		}
			
		//get column number for PERSON_ID
		iColumn = data.getColumnIndex(Persons.COLUMN_PERSON_ID);
		if(!data.moveToFirst())
			return false;
		//get value of column PERSON_ID
		personSelectedId = data.getInt(iColumn);

		dsr.putToHashMap(SkillRegistrationInsert.I_PERSON_ID, 
				personSelectedId);
		
		//get string LAST_NAME + FIRST_NAME
		iColumn = data.getColumnIndex(Persons.COLUMN_LAST_NAME);
		String selectedPersonFullName = data.getString(iColumn);
		selectedPersonFullName = selectedPersonFullName.concat(" ");

		iColumn = data.getColumnIndex(Persons.COLUMN_FIRST_NAME);
		selectedPersonFullName = selectedPersonFullName.
				concat(data.getString(iColumn));
				
		//save to singletone
		SingletoneUI stUI = SingletoneUI.getInstance();
		stUI.putKey(Keys.PERSON_ID, personSelectedId);
		
		stUI.putKey(Keys.PERSON_NAME , selectedPersonFullName);
		return true;
	}

	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		//if a user rotate screen during asynctask working  
		if(getPersonsTask != null && 
				getPersonsTask.getStatus() != AsyncTask.Status.FINISHED){

			getPersonsTask.cancelTask();
			outState.putBoolean(ASYNCTASK_WAS_NOT_FINISHED, true);
		}
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
