package ua.sanya5791.pochemu.dbfb;

import ua.sanya5791.pochemu.MyInterfaces;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

/**
 *  Этот класс выполняет асинхронные запросы к БД в отдельном потоке AsyncTask;
 *  На время выполнения запроса вывешивается ProgressDialog;
 *  обязательно Override abstract void launchCallableStatement() - 
 *  это тот метод, кот и будет выполняться в Async потоке
 *  
 */
public abstract class DbQueryTask<T> extends AsyncTask<String, String, Boolean> {

	private static final boolean isDebug = true;
	public static String TAG = "DbQueryTask";

	private Activity curActivity;	
	
	private ProgressDialog progressDialog;
	
	private MyInterfaces asyncTaskListener;
		
	//в конструкторе передаем текущую активность, Соединение к БД и интерфейс, который реализован вызывающим классом 
	public DbQueryTask(Activity curActivity, T interfaceLink) {
		super();
		this.curActivity = curActivity;
		
		try{
			asyncTaskListener = (MyInterfaces) interfaceLink;
		}catch(ClassCastException e){
			myLogger(interfaceLink.toString() + " must implement OnGetZonesTaskListener");
			throw new ClassCastException(interfaceLink.toString() + " must implement OnGetZonesTaskListener");
		}
	}
	
	@Override
	protected void onCancelled() {
		myLogger("onCancelled(Boolean result):");
		super.onCancelled();
		progressDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		myLogger("onPreExecute:");
		super.onPreExecute();
		myLogger("Create new instance of ProgressDialog");
		progressDialog = new ProgressDialog(curActivity);
		progressDialog.setMessage("Loading...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		//if the button "back" has been pressed
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				myLogger("task is canceled.");
				cancel(true);
			}
		});
		
		progressDialog.show();
	}

	protected abstract Boolean launchCallableStatement();
	
	@Override
	protected Boolean doInBackground(String... params) {
		myLogger("doInBackground:");
		if(isCancelled()){
			return false;
		}
		if (launchCallableStatement())
			return true;
		else
			return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		myLogger("onPostExecute: ");

		progressDialog.dismiss();
		
		if(result){
			myLogger("asyncTaskListener.onTaskFinished(this, true)");
			asyncTaskListener.onTaskFinished(this, true);
		}else{
			myLogger("asyncTaskListener.onTaskFinished(this, false)");
			asyncTaskListener.onTaskFinished(this, false);
		}
	}
	
	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

}

