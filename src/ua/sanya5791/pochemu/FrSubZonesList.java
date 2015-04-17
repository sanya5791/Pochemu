 package ua.sanya5791.pochemu;

import java.sql.ResultSet;

import ua.sanya5791.dbsqlite.MyDB;
import ua.sanya5791.dbsqlite.SubZones;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.DbQueryTask;
import ua.sanya5791.pochemu.dbfb.DbStatements;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FrSubZonesList extends Fragment 
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
				
	//			try {
	//				TimeUnit.SECONDS.sleep(3);
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
				
				return cursor;
			}
	
		}

	public static final String TAG = "FrSubZonesList";
	private  final boolean isDebug = true;
	
	public static String ZONE_ID = "zone_id";
	public static String ZONE_NAME = "zoneName";

	static int selectedSubzone_id;

	private Activity curActivity;		//base Activity who places the fragment 
	private FrSubZonesList curFragment;

	private OnLvSelectListener onLvSelectListener;

	private DbQueryTask<FrSubZonesList> getSubZonesTask;
	private static final String ASYNCTASK_WAS_NOT_FINISHED = "AsyncTaskNotFinished"; 

	private SimpleCursorAdapter scAdapter;
	private ResultSet rs;

	private SubZones dbSubZones;
	//use to distinguish DB task in MyCursor loader 
	private static final int DBTASK_GET_DATA_FOR_LV = 0;
	private static final int DBTASK_GET_BY_ID = 1;

	private ListView lv;
	private TextView tv;
	private ProgressDialog progressDialog;
	
	private int zone_id;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		curActivity = activity;
		curFragment = this;
		
		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener = (OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnLvSelectListener");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		myLogger("onCreateView: ");
		View view = inflater.inflate(R.layout.fragment_sub_zones_list, 
				container, false);

		//change title of ActionBar
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("Список подзон");
		
		setHasOptionsMenu(true);

		tv = (TextView)view.findViewById(R.id.textView22);

		//prepare progress Dialog
		progressDialog = new ProgressDialog(curActivity);
		progressDialog.setMessage("Wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//actions if a user cancel dialog
				myLogger("Progress dialog is canceled.");
				
			}
		});
		progressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				//process behind progress dialog is finished
				myLogger("Progress dialog is dismissed.");
				
				// передаем через метод onLvSelected интерфеса onLvSelectListener PERSON_ID НАЖАТОГО элемента списка lv
				onLvSelectListener.onLvSelected(this, 
						selectedSubzone_id, null);
			}
		});
		
		//open DB
		dbSubZones = new SubZones(curActivity);
		dbSubZones.open();

		lv = (ListView) view.findViewById(R.id.lv_out1);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				myLogger("The item N" + position + " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));
				
				progressDialog.show();

				Cursor c = (Cursor) parent.getAdapter().getItem(position);
				//get _id in SQLite
				int iColumn = c.getColumnIndex(SubZones.COLUMN_ID);
				Bundle bundle = new Bundle();
				Long _id = c.getLong(iColumn);
				bundle.putLong("_id", _id);
				// создаем лоадер чтения данных for ListView
				getLoaderManager().restartLoader(DBTASK_GET_BY_ID, 
			    		bundle, curFragment);
				
			}
		});

		String[] from = new String[] {
				SubZones.COLUMN_SUBZONE_NAME, 
				SubZones.COLUMN_SUBZONE_ID, 
				};
		int[] to = new int[] {
				android.R.id.text1,	
				android.R.id.text2,};
		
		scAdapter = new SimpleCursorAdapter(curActivity, 
				android.R.layout.simple_list_item_1, 
				null, from, to, 0);
		lv.setAdapter(scAdapter);

		return view;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		myLogger("onSaveInstanceState():");
		super.onSaveInstanceState(outState);

		//if a user rotate screen during asynctask working  
		if(getSubZonesTask != null && 
				getSubZonesTask.getStatus() != AsyncTask.Status.FINISHED){

			getSubZonesTask.cancelTask();
			outState.putBoolean(ASYNCTASK_WAS_NOT_FINISHED, true);
		}
	}

	/**
	 * @param savedInstanceState
	 */
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myLogger("onActivityCreated: ");
		String zoneName;
		
		zone_id = (Integer) SingletoneUI.getInstance().getItem(
							Keys.ZONE_ID);
		
		zoneName = (String) SingletoneUI.getInstance().getItem(
				Keys.ZONE_NAME);
		
		tv.setText("Зона: " + zoneName);

		//on screen rotate don't call FB DB, but use SQLite DB instead
		if(savedInstanceState != null &&
				! savedInstanceState.getBoolean(ASYNCTASK_WAS_NOT_FINISHED)){

			// создаем лоадер чтения данных for ListView
		    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
		    		null, this);
			return;
		}
		
		getData();
	}


	/**
	 * get data from ether FB DB or SQLite DB;<br>
	 * notice: this method causes callback onTaskFinished();
	 */
	private void getData() {

		getSubZonesTask = new DbQueryTask<FrSubZonesList>(curActivity, this) {

			@Override
			protected Boolean launchCallableStatement() {
				//if the table already filled with current subzone_id - don't query to remote FB db
				if(dbSubZones.isEquelInt(SubZones.COLUMN_ZONE_ID, zone_id))
					return true;
				
				rs = DbStatements.subZonesSelect(zone_id);
				if(rs != null){
					//inflate inner DB with data from remote DB
					dbSubZones.inflateFromRS(rs);
					return true;
				}else
					return false;
			}
		};
		getSubZonesTask.execute();
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		Fragment f = getFragmentManager().findFragmentByTag(FrSkillsSelected.class.getSimpleName());
		if(f == null){
			// Inflate the menu items for use in the action bar
			inflater.inflate(R.menu.ab_show_skills, menu);
		}
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
				Toast.makeText(curActivity, "Ошибка получения списка подзон", 
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbSubZones.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int dbtask_id, Bundle args) {
		myLogger("onCreateLoader():");
		
		switch (dbtask_id) {
		case DBTASK_GET_DATA_FOR_LV:
			return new MyCursorLoader<SubZones>(curActivity, dbSubZones, dbtask_id);
	
		case DBTASK_GET_BY_ID:
			long _id = args.getLong("_id");
			return new MyCursorLoader<SubZones>(curActivity, dbSubZones, dbtask_id, _id);
			
		default:
			break;
		}
		return null;
	}

	@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			myLogger("onLoadFinished():");
			//получаем результат работы лоадера – новый курсор с данными. 
			//Этот курсор мы отдаем адаптеру методом swapCursor.
			switch (loader.getId()) {
			case DBTASK_GET_DATA_FOR_LV:
	
				scAdapter.swapCursor(data);
				break;
	
			case DBTASK_GET_BY_ID:
				
				//save picked subzone's data to dsr and Singletone
				if(saveSubZone(data)){
					//consider  progressDialog.onDismissListener callback
					progressDialog.dismiss();
				}else{
					Toast.makeText(curActivity, 
							"Не удалось сохранить данные о выбранной подзоне, " +
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
	
	private boolean saveSubZone(Cursor data) {
		int columnIndex;
		
		//whether i can move cursor to the first raw
		if( data == null || !data.moveToFirst()){
			return false;
		}
		
		columnIndex = data.getColumnIndex(SubZones.COLUMN_SUBZONE_ID);
		selectedSubzone_id = data.getInt(columnIndex);
		
		columnIndex = data.getColumnIndex(SubZones.COLUMN_SUBZONE_NAME);
		String selectedSubzoneName = data.getString(columnIndex);
		
		SingletoneUI.getInstance()
			.putKey(Keys.SUBZONE_ID, selectedSubzone_id);
		
		SingletoneUI.getInstance()
			.putKey(Keys.SUBZONE_NAME, selectedSubzoneName);

		return true;
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
