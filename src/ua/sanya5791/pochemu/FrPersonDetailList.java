 package ua.sanya5791.pochemu;
 
import java.sql.ResultSet;

import ua.sanya5791.dbsqlite.PersonDetails;
import ua.sanya5791.pochemu.control.ShowFragment;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.DbQueryTask;
import ua.sanya5791.pochemu.dbfb.DbStatements;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
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
import android.widget.Toast;

public class FrPersonDetailList extends Fragment 
								implements MyInterfaces{
									
	public static final String TAG = "FrPersonDetailList";
	private final boolean isDebug = true;
	
	public static String PERSON_ID = "person_id";
	private int person_id;

	private Activity curActivity;		//base Activity who places the fragment 

	private DbQueryTask<FrPersonDetailList> getPersonDetailTask;
	private static final String ASYNCTASK_WAS_NOT_FINISHED = "AsyncTaskNotFinished"; 

	private SimpleCursorAdapter scAdapter;
	private ResultSet rs;
	private Cursor cursor;

	private PersonDetails dbPersonDetails;
	
	private ListView lv;
	
	public static FrPersonDetailList newInstance(Bundle bundle){
		FrPersonDetailList fsl = new FrPersonDetailList();
		fsl.setArguments(bundle);

		return fsl;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		curActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_person_detail_list, 
				container, false);

		myLogger("onCreateView: ");

		myLogger("LayoutInflater inflater=" + inflater
				+ "; ViewGroup container=" + container
				);
		
		
		setHasOptionsMenu(true);
		//change title of ActionBar
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("Подробнее о ребенке");
		
		//open db
		dbPersonDetails = new PersonDetails(curActivity);
		dbPersonDetails.open();

		person_id = (int) 
				SingletoneUI.getInstance().getItem(Keys.PERSON_ID);
		
		lv = (ListView) view.findViewById(R.id.lv_out1);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				myLogger("The item N" + position + " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));
	
			}
		});
		
		// формируем поля сопоставления
		String[] from = new String[]{
				PersonDetails.COLUMN_LABLE, 
				PersonDetails.COLUMN_TEXT, 
				PersonDetails.COLUMN_ICON, 
				PersonDetails.COLUMN_CHBOX_STATE,
				PersonDetails.COLUMN_CHBOX_SHOW, 
				};
		int to[] = new int[]{
				R.id.tvTitle, 
				R.id.tvText, 
				R.id.imageView1, 
				R.id.checkBox1, 
				R.id.checkBox1};
		//create adapter with null cursor
		scAdapter = new SimpleCursorAdapter(curActivity, 
				R.layout.raw_person_layout, null, from, to, 0);
		
		// назначаем адаптеру свой биндер
		scAdapter.setViewBinder(new ViewBinderPersonDetails());
		
		//set adapter, but lv will be shown with  scAdapter.swapCursor(data);
		lv.setAdapter(scAdapter);
		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		myLogger("onActivityCreated: ");

		//on screen rotate load data from SQLite DB
		if(savedInstanceState != null &&
				! savedInstanceState.getBoolean(ASYNCTASK_WAS_NOT_FINISHED)){
			restoreLocalList();
			return;
		}
		
		getPersonDetailTask = 
				new DbQueryTask<FrPersonDetailList>(curActivity, this) {

			@Override
			protected Boolean launchCallableStatement() {
				
				rs=DbStatements.personDetail(person_id);
				if(rs != null){
					dbPersonDetails.inflateFromRS(rs);
					cursor = dbPersonDetails.getDataForLv();
					return true;
				}else
					return false;
			}
			
		};
		getPersonDetailTask.execute();
	}
	
/**
 *restore data from local DB to ListView 
 */
	private void restoreLocalList() {
		getPersonDetailTask = 
				new DbQueryTask<FrPersonDetailList>(curActivity, this) {
	
			@Override
			protected Boolean launchCallableStatement() {
				cursor = dbPersonDetails.getDataForLv();
				return true;
			}
		};
		getPersonDetailTask.execute();
	}

@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		myLogger("onCreateOptionsMenu():");
		
		//if device is tablet and the fragment on left pane 
		if(ShowFragment.isTablet() &&
				R.id.frgmCont1 == ((ViewGroup)getView().getParent()).getId()){
		}else{
			// Inflate the menu items for use in the action bar
			inflater.inflate(R.menu.fr_person_detail_list, menu);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.ab_add:
			myLogger("Pressed ActionBar item: " + item.getTitle());
			
			break;
	
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTaskFinished(Object o, Boolean isSuccess) {
		myLogger("onTaskFinished: ");

		if (o instanceof DbQueryTask) {
			if (isSuccess) {
				scAdapter.swapCursor(cursor);

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
		if(getPersonDetailTask != null && 
				getPersonDetailTask.getStatus() != AsyncTask.Status.FINISHED){

			getPersonDetailTask.cancelTask();
			outState.putBoolean(ASYNCTASK_WAS_NOT_FINISHED, true);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		dbPersonDetails.close();
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

}
