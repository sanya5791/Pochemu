package ua.sanya5791.pochemu;

import java.sql.ResultSet;

import ua.sanya5791.dbsqlite.MyDB;
import ua.sanya5791.dbsqlite.Zones;
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

public class FrZonesList extends Fragment implements 
											MyInterfaces,
											LoaderCallbacks<Cursor>{
	
	
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
			super(context);
			this.db = db;
			this.dbtask_id = dbtask_id;
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
				
				Log.v(TAG, "cursor.moveToFirst()=" + cursor.moveToFirst());
				
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

	public static final String TAG = "FrZonesList";
	private final boolean isDebug = true;

	static int selectedZone_id;
	
	private Activity curActivity;		//base Activity who places the fragment 
	private FrZonesList curFragment;
	
	private ResultSet rs;
	private SimpleCursorAdapter scAdapter;

	private Zones dbZones;
	//use to distinguish DB task in MyCursor loader 
	private static final int DBTASK_GET_DATA_FOR_LV = 0;
	private static final int DBTASK_GET_BY_ID = 1;

	private ListView lv;
	private ProgressDialog progressDialog;
	private boolean pdIsCanceled;

	private OnLvSelectListener onLvSelectListener;

	private DbQueryTask<FrZonesList> getZonesTask;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		curActivity = activity;
		curFragment = this;

		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener=(OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnLvSelectListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		myLogger("onCreateView: ");
		View viewF =  inflater.inflate(R.layout.fragment_zones_list, 
				container, false);
		
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("Список зон");
		
		//prepare progress Dialog
		pdIsCanceled = false;
		progressDialog = new ProgressDialog(curActivity);
		progressDialog.setMessage("Wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// actions if a user cancel dialog
				myLogger("Progress dialog is canceled.");
				pdIsCanceled = true;
				
			}
		});
		progressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// process behind progress dialog is finished
				myLogger("Progress dialog is dismissed.");
				if(pdIsCanceled){
					pdIsCanceled=false;
					return;
				}
				
				// передаем через метод onLvSelected интерфеса onLvSelectListener PERSON_ID НАЖАТОГО элемента списка lv
				onLvSelectListener.onLvSelected(this, 
						selectedZone_id, null);
			}
		});
		
		//open DB
		dbZones = new Zones(curActivity);
		dbZones.open();
		
		lv = (ListView) viewF.findViewById(R.id.lv_out1);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				myLogger("The item N" + position + " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));
				
				progressDialog.show();

				Cursor c = (Cursor) parent.getAdapter().getItem(position);
				//get _id in SQLite
				int iColumn = c.getColumnIndex(Zones.COLUMN_ID);
				Bundle bundle = new Bundle();
				Long _id = c.getLong(iColumn);
				bundle.putLong("_id", _id);
				// создаем лоадер чтения данных for ListView
				getLoaderManager().restartLoader(DBTASK_GET_BY_ID, 
			    		bundle, curFragment);
			}
		});
		
		String[] from = new String[] {
				Zones.COLUMN_ZONE_NAME, 
				Zones.COLUMN_ID 
				};
		int[] to = new int[] {
				android.R.id.text1,	
				android.R.id.text2,};
		
		scAdapter = new SimpleCursorAdapter(curActivity, 
				android.R.layout.simple_list_item_1, 
				null, from, to, 0);
		lv.setAdapter(scAdapter);

		return viewF;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		myLogger("onActivityCreated: ");
		
		//on screen rotate don't call FB DB, but use SQLite DB instead
		if(savedInstanceState != null){
//			// создаем лоадер чтения данных for ListView
		    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
		    		null, this);
			return;
		}
		
		//take list of ZONES from DB and show it in local ListView
//		Connection connection = MainActivity.connectDBTask.getConnection();
		getZonesTask = new DbQueryTask<FrZonesList>(curActivity, this){
			
			// CallableStatement "ZONES_SELECT"
			@Override
			protected Boolean launchCallableStatement() {
				rs=DbStatements.zonesSelect();
				if(rs != null){
					//inflate inner DB with data from remote DB
					dbZones.inflateFromRS(rs);
					return true;
				}else
					return false;
			}
		};
		getZonesTask.execute();
	}

	//do it after AsyncTask has been finished
		@Override
		public void onTaskFinished(Object o, Boolean isSuccess) {

			myLogger("onTaskFinished(): ");

			if (isSuccess) {
				// создаем лоадер чтения данных for ListView
			    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
			    		null, this);
			} else {
				Toast.makeText(curActivity, "Ошибка получения списка зон", 
						Toast.LENGTH_LONG).show();
			}
		}

	@Override
	public void onDestroyView() {
		myLogger("onDestroyView():");
		super.onDestroyView();
		dbZones.close();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int dbtask_id, Bundle args) {
		myLogger("onCreateLoader():");

		switch (dbtask_id) {
		case DBTASK_GET_DATA_FOR_LV:
			return new MyCursorLoader<Zones>(curActivity, dbZones, dbtask_id);

		case DBTASK_GET_BY_ID:
			long _id = args.getLong("_id");
			return new MyCursorLoader<Zones>(curActivity, dbZones, dbtask_id, _id);
			
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
			
			//save picked zones's data to dsr and Singletone
			if(saveZone(data)){
				//consider  progressDialog.onDismissListener callback
				progressDialog.dismiss();
			}else{
				progressDialog.cancel();
				Toast.makeText(curActivity, 
						"Не удалось сохранить данные о выбранной зоне, " +
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

	private boolean saveZone(Cursor data) {
		int columnIndex;
		
		//whether i can move cursor to the first raw
		if( data == null || !data.moveToFirst())
			return false;

		columnIndex = data.getColumnIndex(Zones.COLUMN_ZONE_ID);
		selectedZone_id = data.getInt(columnIndex);
		
		columnIndex = data.getColumnIndex(Zones.COLUMN_ZONE_NAME);
		String selectedZoneName = data.getString(columnIndex);
		
		SingletoneUI.getInstance()
			.putKey(Keys.ZONE_ID, selectedZone_id);

		SingletoneUI.getInstance()
			.putKey(Keys.ZONE_NAME, selectedZoneName);
		
		return true;
	}
	
	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
