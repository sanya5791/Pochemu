 package ua.sanya5791.pochemu;

import java.sql.ResultSet;

import ua.sanya5791.dbsqlite.Materials;
import ua.sanya5791.dbsqlite.MyDB;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FrMaterialsList extends Fragment 
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
				
				return cursor;
			}
	
		}

	public static final String SUBZONE_ID = "subzone_id";
	public static final String SUBZONE_NAME = "subZoneName";
	
	public static final String TAG = "FrMaterialsList";
	private final boolean isDebug = true;
	
	static int selectedMaterial_id;
	
	private int subzone_id;

	private Activity curActivity;		//base Activity who places the fragment 
	private FrMaterialsList curFragment;

	private OnLvSelectListener onLvSelectListener;

	private DbQueryTask<FrMaterialsList> getMaterialsTask;
	private static final String ASYNCTASK_WAS_NOT_FINISHED = "AsyncTaskNotFinished"; 

	private SimpleCursorAdapter scAdapter;
	private ResultSet rs;
	
	private Materials dbMaterials;
	//use to distinguish DB task in MyCursor loader 
	private static final int DBTASK_GET_DATA_FOR_LV = 0;
	private static final int DBTASK_GET_BY_ID = 1;
	
	private TextView tv;
	private ListView lv_materials;
	private ProgressDialog progressDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		myLogger("onAttach():");
		curActivity = activity;
		curFragment = this;
		
		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener = (OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() 
					+ " must implement OnLvSelectListener");
		}
		
		selectedMaterial_id=0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		myLogger("onCreateView: ");
		View view = inflater.inflate(R.layout.fragment_materials_list, container, false);

		//change title of ActionBar
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("Список материалов");
		
		setHasOptionsMenu(true);

		tv = (TextView)view.findViewById(R.id.tvTitle);

		//prepare progress Dialog
		progressDialog = new ProgressDialog(curActivity);
		progressDialog.setMessage("Wait...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO actions if a user cancel dialog
				myLogger("Progress dialog is canceled.");
				
			}
		});
		progressDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				myLogger("Progress dialog is dismissed.");
				
				// передаем через метод onLvSelected интерфеса onLvSelectListener PERSON_ID НАЖАТОГО элемента списка lv
				onLvSelectListener.onLvSelected(this, 
						selectedMaterial_id, null);
			}
		});
		
		//open DB
		dbMaterials = new Materials(curActivity);
		dbMaterials.open();

		lv_materials = (ListView) view.findViewById(R.id.lv_out1);
		lv_materials.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				myLogger("The item N" + position + " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));
				
				progressDialog.show();

				Cursor c = (Cursor) parent.getAdapter().getItem(position);
				//get _id in SQLite
				int iColumn = c.getColumnIndex(Materials.COLUMN_ID);
				Bundle bundle = new Bundle();
				Long _id = c.getLong(iColumn);
				bundle.putLong("_id", _id);
				// создаем лоадер чтения данных for ListView
				getLoaderManager().restartLoader(DBTASK_GET_BY_ID, 
			    		bundle, curFragment);
				
			}
		});

		String[] from = new String[] {
				Materials.COLUMN_MATERIAL_NAME, 
				Materials.COLUMN_MATERIAL_ID, 
				};
		int[] to = new int[] {
				android.R.id.text1,	
				android.R.id.text2,};
		
		scAdapter = new SimpleCursorAdapter(curActivity, 
				android.R.layout.simple_list_item_1, 
				null, from, to, 0);
		lv_materials.setAdapter(scAdapter);

		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myLogger("onActivityCreated: ");
		String subZoneName;

		subzone_id = (int) 
				SingletoneUI.getInstance().getItem(Keys.SUBZONE_ID);
		subZoneName = (String) 
				SingletoneUI.getInstance().getItem(Keys.SUBZONE_NAME);

		tv.setText(getResources().getString(R.string.subzone)
				+ subZoneName);

		//on screen rotate don't call FB DB, but use SQLite DB instead
		if(savedInstanceState != null &&
				! savedInstanceState.getBoolean(ASYNCTASK_WAS_NOT_FINISHED)){
			// создаем лоадер чтения данных for ListView
		    getLoaderManager().initLoader(DBTASK_GET_DATA_FOR_LV, 
		    		null, this);
			
			return;
		}
		
		getMaterialsTask = new DbQueryTask<FrMaterialsList>(curActivity, this) {

			@Override
			protected Boolean launchCallableStatement() {
				//if the table already filled with current subzone_id - don't query to remote FB db
				if(dbMaterials.isEquelInt(Materials.COLUMN_SUBZONE_ID, subzone_id))
					return true;
				
				rs = DbStatements.materialsSelect(subzone_id);
				if(rs != null){
					//inflate inner DB with data from remote DB
					dbMaterials.inflateFromRS(rs);
					return true;
				}else
					return false;
			}
			
		};
		getMaterialsTask.execute();
		
	}

	@Override
	/** 
	 * Inflate the menu items for use in the action bar
	 */
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		Fragment f = getFragmentManager().findFragmentByTag(FrSkillsSelected.class.getSimpleName());
		//if the ab menu item is left by FrSubzones - don't duplicate it
		//and don't show with FrSkillsSelected 
		if(menu.findItem(R.id.ab_show_skills) == null &&
				f == null){
			inflater.inflate(R.menu.ab_show_skills, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

//		int id = item.getItemId();
		
		//the ActionBar button "show_skills" was pressed 
//		if (id == R.id.ab_show_skills) {
//			ShowFragment showFragment = new ShowFragment(curActivity);
//			
//			showFragment.skillsList(ShowFragment.REPLACE, false);
//			
//			return true;
//		}

		return super.onOptionsItemSelected(item);
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
				Toast.makeText(curActivity, "Ошибка получения материалов", 
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		myLogger("onSaveInstanceState():");
		super.onSaveInstanceState(outState);

		//if a user rotate screen during asynctask working  
		if(getMaterialsTask != null && 
				getMaterialsTask.getStatus() != AsyncTask.Status.FINISHED){

			getMaterialsTask.cancelTask();
			outState.putBoolean(ASYNCTASK_WAS_NOT_FINISHED, true);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbMaterials.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int dbtask_id, Bundle args) {
		switch (dbtask_id) {
		case DBTASK_GET_DATA_FOR_LV:
			return new MyCursorLoader<Materials>(curActivity, dbMaterials, dbtask_id);
	
		case DBTASK_GET_BY_ID:
			long _id = args.getLong("_id");
			return new MyCursorLoader<Materials>(curActivity, dbMaterials, dbtask_id, _id);
			
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
				
				//save picked subzone's data to dsr and Singletone
				if(saveMaterial(data)){
					//consider  progressDialog.onDismissListener callback
					progressDialog.dismiss();
				}else{
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
	
	private boolean saveMaterial(Cursor data) {
		
		//whether i can move cursor to the first raw
		if( data == null || !data.moveToFirst()){
			return false;
		}
		
		int columnIndex;
		
		columnIndex = data.getColumnIndex(Materials.COLUMN_MATERIAL_ID);
		selectedMaterial_id = data.getInt(columnIndex);
		
		columnIndex = data.getColumnIndex(Materials.COLUMN_MATERIAL_NAME);
		String selectedMaterial = data.getString(columnIndex);
		
		SingletoneUI.getInstance().putKey(
				Keys.MATERIAL_ID, selectedMaterial_id);
		
		SingletoneUI.getInstance().putKey(
				Keys.MATERIAL_NAME, selectedMaterial);

		return true;
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
