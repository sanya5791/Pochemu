package ua.sanya5791.pochemu.dbfb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import ua.sanya5791.pochemu.MyInterfaces;
import ua.sanya5791.pochemu.SettingsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Этот класс соединяет и разъединяет соединение с БД в отдельном потоке AsyncTask*/
public class ConnectDBTask<T> extends AsyncTask<String, String, Boolean> {
	
	private static final boolean isDebug = true;
	public static String TAG = "ConnectDBTask";

	public static Boolean autoCommitState = true;
	private Activity curActivity;
	
	private MyInterfaces asyncTaskListener;

	private static final String APP_PREFERENCES = "mysettings";
	private static final String APP_PREFERENCES_CURR_IP = "Current IP to Connect DB ";
	private Connection mConnection;							//объект для соединения с БД
	private ProgressDialog progressDialog;
	
	private int mWasFaults=0;
	
	private String ip, login, passw;
	
	public ConnectDBTask(Activity activity, T interfaceLink){
		myLogger("Creating a new instance of ConnectDBTask");
		curActivity = activity;
		//check whether the calling activity has implemented OnSelectedZoneListener		
		try{
			asyncTaskListener = (MyInterfaces) interfaceLink;
		} catch (ClassCastException e) {
			myLogger(interfaceLink.toString() + " must implement OnConnectDBTaskListener");
			cancel(true);
			throw new ClassCastException(activity.toString() + " must implement OnConnectDBTaskListener");
		}
	}

	/**
	 * @return return link to Connection or null if connection is not alive
	 */
	public Connection getConnection() {
		myLogger("getConnection():");
		if(isConnectionValid())
			return mConnection;
		else
			return null;
	}
	
	private boolean isConnectionValid(){
		
		//check if the connection alive?
		try {
			if( ! mConnection.isValid(5)){
				myLogger("Connection to DB is unValid");
				return false;
			}
		} catch (SQLException e) {
			myLogger("SQLException: " + e);
			e.printStackTrace();
			return false;
		} catch (NullPointerException e) {
			myLogger("NullPointerException: " + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		myLogger("ConnectDBTask.onPreExecute");
		
		ip = readIPFromConfig();
		//if noIP - interrupt the task
		if (ip.contentEquals("noIP")){
			myLogger("There is NO ip-address to connect DB");
			asyncTaskListener.onTaskFinished(this, false);
			cancel(true);
			return;
		}
		
		myLogger("Create new instance of ProgressDialog");
		progressDialog = new ProgressDialog(curActivity);
		progressDialog.setMessage("Connecting...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		//if the button "back" has been pressed
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				myLogger("Connection task is canceled.");
				//tell the task is failed throurgh onTaskFinished 
				cancel(true);
				asyncTaskListener.onTaskFinished(this, false);
			}
		});
		
		progressDialog.show();
	}

	@Override
	protected Boolean doInBackground(String... task) {
		
		myLogger("ConnectDBTask.doInBackground");
		if (isCancelled()){
			return false;
		}

		switch (task[0]) {
		case "connectOpen":
			resetWasFaults();
			//Сначала проверим есть ли драйвер
			myLogger("Сначала проверим есть ли драйвер");
			try {
				if (isCancelled()) return false;
				
				myLogger("Заходим в первый try - look for Jadbird driver");
				Class.forName("org.firebirdsql.jdbc.FBDriver");
			} catch (ClassNotFoundException e) {
				myLogger("Произошло исключение: мjdbc.FBDriver: Connection step 1 failed");
				e.printStackTrace();
				publishProgress("Connection step 1 failed");
				setWasFaults();
				return false;
			}
			
			myLogger("Драйвер найден");
			Properties ParamConnections = new Properties();
			ParamConnections.setProperty("user", login);
			ParamConnections.setProperty("password", passw);
			ParamConnections.setProperty("encoding", "WIN1251");
			String sCon = "jdbc:firebirdsql:" + ip + "/3050:pochemuchka";           // string for connection DB

			//Connection to DB
			try {
				if(isCancelled()) return false;

				myLogger("Пытаемся соединиться строкой: " + sCon 
						+ "login:"+login + ":passw: *****;");
				mConnection = DriverManager.getConnection(sCon,
						ParamConnections);
				
				if(isCancelled()) return false;
				
				myLogger("Connection to DB is successfull");
			}catch (java.sql.SQLException e){

				if (e.getErrorCode() == 335544721) {
					myLogger("Network is unreachable." 
							+ "\nThis error is thrown when Java application "
							+ "cannot establish connection to the"
							+ "database server due to a network issues,"
							+ "e.g. host name is specified incorrectly,"
							+ "Firebird had not been started on the remote"
							+ "host, firewall configuration prevents client");
					publishProgress("Network is unreachable.");
				}else{
					myLogger("jdbc.FBDriver: Connection step 2 failed");
					e.printStackTrace();
					publishProgress("Connection step 2 failed");
				}
				setWasFaults();
				return false;
			} 
			break;
			
		case "closeConnect":
			myLogger("Closing connection to DB");
			try {
				if (mConnection != null){
					mConnection.close();
				}
			} catch (SQLException e) {
				myLogger("Не могу закрыть соединение с БД");
				e.printStackTrace();
				publishProgress("Connection closing failed");
				return false;
			}
			break;
		default:
			myLogger("no task was executed!");
			break;
		}
		return true;
	}
	
	/**здесь выводим в activity  Toast о возникающих ошибках*/
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		
		//т.к. сюда выводится только инфа об ошибках, то значит отменяем всю задачу
		
		switch (values[0]) {
		case "Connection step 1 failed":
			Toast.makeText(curActivity,
			"jdbc.FBDriver: Connection step 1 failed - Jadbird driver not found",
			Toast.LENGTH_LONG).show();
			break;
		case "Connection step 2 failed":
			Toast.makeText(curActivity,
					"jdbc.FBDriver: Connection step 2 failed",
					Toast.LENGTH_LONG).show();
			break;
		case "Network is unreachable.":
			Toast.makeText(curActivity,
					"jdbc.FBDriver: Network is unreachable.",
					Toast.LENGTH_LONG).show();
			break;

		case "Connection closing failed":
			Toast.makeText(curActivity,
					"Не могу закрыть соединение с БД.",
					Toast.LENGTH_LONG).show();
			break;
			
		default:
			break;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		myLogger("ConnectDBTask.onPostExecute:");
		super.onPostExecute(result);
		//уберем диалог ожидания
		progressDialog.dismiss();
		
		//if the task failed
		if (result){
			asyncTaskListener.onTaskFinished(this, true);
		}else{
			asyncTaskListener.onTaskFinished(this, false);
		}
			
	}
	
	/*прочитаем из конфига текущий ip, login, passw для подключения к DB и вернем его */
	private String readIPFromConfig(){
		
		SharedPreferences mySettings=curActivity.getSharedPreferences(APP_PREFERENCES, 
				Context.MODE_PRIVATE);					//нужно проинициализировать mySettings (looks like a bug)
		String sIPtoDB = mySettings.getString(APP_PREFERENCES_CURR_IP, "noIP");		//get ip to connect from settings
		login = mySettings.getString(
				SettingsActivity.APP_PREFERENCES_CURR_LOGIN, "noIP");		
		passw = mySettings.getString(
				SettingsActivity.APP_PREFERENCES_CURR_PASSW, "noIP");		
		myLogger("ip to connect DB is: " + sIPtoDB);
		/// if 	No current IP address for connection in config;
		if (sIPtoDB  == "noIP") {	
			AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
			builder.setTitle("Connection error")
				.setMessage("No IP address for connection.\n"
						+"Go to Settings and setup it there.")
				.setCancelable(true)
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
			AlertDialog alert = builder.create();
			alert.show();
			return sIPtoDB;
		}

		return sIPtoDB;
	}

	/**
	 * 
	 * SetAutoCommit of the mConnection and reset WasFalts counter
	 * @author sanya 
	 * @param autoCommit - use true or false
	 * @return true on succeed
	 */
	public Boolean setAutoCommit(final Boolean autoCommit){

		AsyncTask<Void, Void, Boolean> task = 
				new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				Connection connect = getConnection();

				//if connection unvalid
				if(connect == null)
					return false;
				
				try {
					myLogger("setAutoCommit current state is: " + connect.getAutoCommit()
							+ "; want to set: " + autoCommit);
					if(connect.getAutoCommit() != autoCommit){
						if(autoCommit == false)
							resetWasFaults();
						connect.setAutoCommit(autoCommit);
					}
					myLogger("setAutoCommit is set: " + connect.getAutoCommit());
					autoCommitState = autoCommit;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Log.v(TAG, "Закончилась задача по переводу в автокоммит!");
				return true;
			}
		}.execute();

		try {
			if(!task.get())
				return false;
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v(TAG, "дождались окончания задачи по переводу в автокоммит!");
		return true;
	}
	
	/**
	 * Commits all of the changes made since the last commit or rollback of the associated transaction. 
	 * @author sanya 
	 */
	public void commit(){
		//TODO надо бы предусмотреть возврат метода, если fault
		//TODO maybe should show spining circle?
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				try {
					mConnection.commit();
					myLogger("Statement is commited");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		}.execute();
	}

	/**
	 * Rolls back all updates made so far in this transaction and relinquishes all acquired database locks. 
	 * It is an error to invoke this operation when in auto-commit mode.
	 * @author sanya
	 */
	public void rollback(){
		new AsyncTask<Void, Void, Void>() {
			//TODO надо бы предусмотреть возврат метода, если fault
			//TODO maybe should show spining circle?

			@Override
			protected Void doInBackground(Void... params) {
				try {
					mConnection.rollback();
					myLogger("Statement is rolled back");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
		
	}
	
	/**
	 * add one more fault to counter
	 */
	public void setWasFaults() {
		myLogger("Возникла ошибка при работе с БД");
		++mWasFaults;
	}

	/**
	 * 
	 * @return 0, если всё ОК или количество возникших ошибок;
	 */
	public int getWasFaults() {
		return mWasFaults;
	}

	public void resetWasFaults() {
		myLogger("resetWasFaults():");
		mWasFaults = 0;
	}
	
	public void cancelTask() {
		progressDialog.dismiss();
		cancel(true);
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
