/**
 * 
 */
package ua.sanya5791.pochemu;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * все для ввода данных необходимых для осуществления нового подключения к БД
 * @author Sanya
 *
 */
public class DFrAddIp extends DialogFragment implements OnClickListener{
	private static final boolean isDebug = true;
	public final static String TAG = "DFrAddIp";
	
	public final static String IP = "ip";
	public final static String DESCR = "desr";
	public final static String LOGIN = "login";
	public final static String PASSW = "passw";

	public static String ip;
	public static String descr;
	public static String login;
	public static String passw;
	
	
	private Activity curActivity;

	private OnLvSelectListener onLvSelectListener;

	private EditText etIP;
	private EditText etDescr;
	private EditText etLogin; 
	private EditText etPassw;
	private Button btAdd; 
	
	public DFrAddIp() {
		ip="";  
		descr="";
		login="";
		passw="";
	}

	@Override
	public void onAttach(Activity activity) {
		curActivity = activity;
		super.onAttach(activity);

		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener = (OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() 
					+ " must implement OnLvSelectListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myLogger("onCreateView():");
		
		View v = inflater.inflate(R.layout.d_fr_add_ip, container, false);
		getDialog().setTitle("Новый IP");
		getDialog().setCanceledOnTouchOutside(false);

		etIP = (EditText)v.findViewById(R.id.etIP);
		etDescr = (EditText)v.findViewById(R.id.etDescr);
		etLogin = (EditText)v.findViewById(R.id.editText3);
		etPassw = (EditText)v.findViewById(R.id.editText4);
		
		btAdd = (Button)v.findViewById(R.id.button1);
		
		etIP.setOnClickListener(this);
		btAdd.setOnClickListener(this);
		
		//if Bundle is not empty get values and put them to views
		Bundle bundle = getArguments();
		if(bundle != null){
			String str;
			str = bundle.getString(IP, "");
			etIP.setText(str);
			myLogger("IP: " + str);

			str = bundle.getString(DESCR, "");
			etDescr.setText(str);
			myLogger("DESCR: " + str);

			str = bundle.getString(LOGIN, "");
			etLogin.setText(str);

			str = bundle.getString(PASSW, "");
			etPassw.setText(str);
		}
		return v;
	}
	

	@Override
	public void onClick(View v) {
		int id = v.getId();
		myLogger("onClick:");
		switch (id) {
		case R.id.button1:
			myLogger("pressed: button1");
			ip=etIP.getText().toString();
			myLogger("ip: "+ip);
			//TODO найти парсер на правильность ip
			if(ip == ""){
				Toast.makeText(curActivity, "ip адрес не может оставаться пустым", Toast.LENGTH_LONG).show();
				break;
			}
			descr=etDescr.getText().toString();
			myLogger("descr: "+descr);
			login=etLogin.getText().toString().trim();
//			login = login.trim();
			myLogger("login: `"+ login +"`");
			//TODO найти парсер для правильности логина
			if(login == ""){
				Toast.makeText(curActivity, "login не может оставаться пустым", Toast.LENGTH_LONG).show();
				break;
			}
			passw=etPassw.getText().toString().trim();
			myLogger("passw: '"+passw + "'");
			//TODO найти парсер для правильности пароля
			if(passw == ""){
				Toast.makeText(curActivity, "password не может оставаться пустым", Toast.LENGTH_LONG).show();
				break;
			}
			
			// так мы передаем через метод onLvSelected интерфеса onLvSelectListener SKILL_ID НАЖАТОГО элемента списка lv
			onLvSelectListener.onLvSelected(this, 0, "");

			dismiss();
			
			break;
		case R.id.etIP :
			myLogger("Edit Text etAdd is clicked.");

			//etIP - mask to enter only ip-address simbols
			etIP.setKeyListener(new NumberKeyListener() {
				@Override
				public int getInputType() {
					return InputType.TYPE_CLASS_NUMBER;
				}
				@Override
				protected char[] getAcceptedChars() {
					return new char []{'1', '2', '3', '4', '5', '6', 
							'7', '8', '9', '0', '.'};
				}
			});

			break;
			
		default:
			break;
		}
		
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
