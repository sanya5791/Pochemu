/**
 * 
 */
package ua.sanya5791.pochemu;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

/**
 * @author Sanya
 *The class shows calendar to choose a date
 */
public class FrDateSelect extends Fragment {
	static String datePicked;

	public static final String TAG = "FrDateSelect";
	private final boolean isDebug = true;
	

	private Activity curActivity;
	private OnLvSelectListener onLvSelectListener;
	private DatePicker datePicker;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		curActivity=activity;
		
		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener=(OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnLvSelectListener");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fr_date_select, container, false);
		
		//change title of ActionBar
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle("¬ыберите дату");

		datePicker = 
				(DatePicker) v.findViewById(R.id.datePicker1);
		
		Calendar today = Calendar.getInstance();
		
//TODO BU16 07.04.15 FrDateSelect. In Tablet mode, selected date isn't set on right pane; 
//You can check set date in Singletone before init datePicker  
		datePicker.init(today.get(Calendar.YEAR), 
				today.get(Calendar.MONTH), 
				today.get(Calendar.DAY_OF_MONTH), 
				new OnDateChangedListener() {
					
					@Override
					public void onDateChanged(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						// по нажатию на дату в календарике вызвать список зон
						// ћес€ц отсчитываетс€ с 0, поэтому добавл€ем 1
						monthOfYear++;
						myLogger("picked date is: " + dayOfMonth + "."+monthOfYear+"."+year);

						datePicked = new StringBuilder()
							.append(datePicker.getYear()).append("-")
							.append(datePicker.getMonth()+1).append("-")
							.append(datePicker.getDayOfMonth())
						.toString();

						SingletoneUI.getInstance()
							.putKey(Keys.DATE, datePicked);
					
						//через интерфейс передадим управление в MainActivity
						onLvSelectListener.onLvSelected(this, 0, "");
					}
				});
		Button btToday = (Button)v.findViewById(R.id.button1);
		btToday.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// pressed key "Today"  - сформируем дату на сегодн€ и сохраним ее в datePicked
				myLogger("Pressed key: " + getString(R.string.bt_today));
				
				Calendar c = new GregorianCalendar();
				myLogger("picked date is "+ c.getTime());
				// ћес€ц отсчитываетс€ с 0, поэтому добавл€ем 1
				datePicked= new StringBuilder()
					.append(c.get(Calendar.YEAR)).append("-")
					.append(c.get(Calendar.MONTH)+1).append("-")
					.append(c.get(Calendar.DAY_OF_MONTH))
					.toString();
				myLogger(datePicked);
				
				SingletoneUI.getInstance()
					.putKey(Keys.DATE, datePicked);
				
				//через интерфейс передадим управление в MainActivity
				onLvSelectListener.onLvSelected(this, 0, "");
			}
		});
		Button btTomorrow = (Button)v.findViewById(R.id.button2);
		btTomorrow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// pressed key "tomorrow" - сформируем дату на завтра и сохраним ее в datePicked
				myLogger("Pressed key: " + getString(R.string.bt_tomorrow));
				
				Calendar c = new GregorianCalendar();
				//add 1 day to current date
				c.add(Calendar.DAY_OF_MONTH, 1);
				myLogger("picked date is "+ c.getTime());
				// ћес€ц отсчитываетс€ с 0, поэтому добавл€ем 1
				datePicked= new StringBuilder()
				.append(c.get(Calendar.YEAR)).append("-")
				.append(c.get(Calendar.MONTH)+1).append("-")
				.append(c.get(Calendar.DAY_OF_MONTH))
				.toString();
				myLogger(datePicked);
				
				SingletoneUI.getInstance()
					.putKey(Keys.DATE, datePicked);
			
				//через интерфейс передадим управление в MainActivity
				onLvSelectListener.onLvSelected(this, 0, "");
			}
		});
		
				
		return v;
	}
	
	
	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
