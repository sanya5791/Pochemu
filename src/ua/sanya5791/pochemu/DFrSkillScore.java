package ua.sanya5791.pochemu;
import ua.sanya5791.pochemu.control.DataSkillRegistration;
import ua.sanya5791.pochemu.dbfb.DbStatements.SkillRegistrationInsert;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;


public class DFrSkillScore extends DialogFragment implements OnClickListener{
	public static final String TAG = "DFrSkillScore";
	private final boolean isDebug = true;
	
	private OnLvSelectListener onLvSelectListener;
	
	private DataSkillRegistration dsr;

	private TextView tvHasValue;			//расширенный навык
	private EditText editText;
	private RatingBar ratingBar;
	private Button button;
	
	private int score;						//максимальная оценка за навык

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		dsr = new DataSkillRegistration();
		
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
		// запуск диалога и инициализация элементов
		getDialog().setTitle(R.string.title_skill_score);
		getDialog().setCanceledOnTouchOutside(false);
		View v = inflater.inflate(R.layout.frd_skill_score, container, false);
		
		tvHasValue = (TextView)v.findViewById(R.id.textView1);
		editText = (EditText) v.findViewById(R.id.editText1);
		ratingBar = (RatingBar)v.findViewById(R.id.ratingBar1);
		button = (Button)v.findViewById(R.id.button1);
		
		editText.setOnClickListener(this);
		button.setOnClickListener(this);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				myLogger("ratingBar1 has been clicked with rating: " + rating);
				score=(int) rating;
			}
		});
		
		isHasValue();
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Integer i = (Integer) dsr.getHMapItem(SkillRegistrationInsert.LAST_VALUE);
		if(i != null)
			ratingBar.setRating(i);
	}

	/**
	 * если навык НЕ расширенный - скрыть EditText
	 */
	private void isHasValue() {

		int i = (int) dsr.getHMapItem(SkillRegistrationInsert.I_HAS_VALUE);
		if(i==0){
			tvHasValue.setVisibility(View.GONE);
			editText.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editText1:
			//TODO 05.01.15 скрывать клаву после выхода из редактирования
			myLogger("editText1 is clicked");
			
			break;

		case R.id.button1:
			myLogger("button1 is clicked");
			dsr.putToHashMap(
					SkillRegistrationInsert.I_SKILL_VALUE, 
					editText.getText().toString());
			dsr.putToHashMap(
					SkillRegistrationInsert.I_RATING, 
					score);

			// передадим через интерфейс продолжение выполнения программы в MainActivity
			onLvSelectListener.onLvSelected(this, score, "");
			dismiss();
			break;
			
		default:
			break;
		}
		
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		myLogger("onCancel():");
		super.onCancel(dialog);
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

}
