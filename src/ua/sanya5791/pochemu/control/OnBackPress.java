package ua.sanya5791.pochemu.control;

import ua.sanya5791.pochemu.DFrSkillScore;
import ua.sanya5791.pochemu.FrDateSelect;
import ua.sanya5791.pochemu.FrMaterialsList;
import ua.sanya5791.pochemu.FrPersonDetailList;
import ua.sanya5791.pochemu.FrPersonsList;
import ua.sanya5791.pochemu.FrSkillsList;
import ua.sanya5791.pochemu.FrSkillsSelected;
import ua.sanya5791.pochemu.FrSubZonesList;
import ua.sanya5791.pochemu.FrZonesList;
import ua.sanya5791.pochemu.MainActivity;
import ua.sanya5791.pochemu.R;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

public class OnBackPress {
	private static final boolean isDebug = true;
	public final static String TAG = "OnBackPress";

	private  Activity curActivity;
	private ShowFragment showFragment;

	public OnBackPress(Activity activity) {
		myLogger("Creating instance");
		curActivity = activity;
		showFragment = new ShowFragment(curActivity);
	}

	/**
	 * здесь обработаем нажатие кн. назад в активности
	 */
	public void process(){
		Fragment fr;
		
		if(ShowFragment.isTablet()){
			fr = curActivity.
					getFragmentManager().findFragmentById(R.id.frgmCont2);
		}else{
		//находим в контейнере текущий фрагмент
			fr = curActivity.
					getFragmentManager().findFragmentById(R.id.frgmCont1);
		}
		
		if(fr instanceof FrPersonsList || 
						 fr == null){
			myLogger("FrPersonsList");
			
			AlertDialog.Builder ad = new AlertDialog.Builder(curActivity)
			.setTitle("Внимание!")
			.setMessage("Хотите выйти из программы?")
			.setNegativeButton("Нет", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setPositiveButton("Да", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//maybe to clear data and close connection before exit 
					//finish app. 
					curActivity.finish();
				}
			});
			ad.show();
		}else if(fr instanceof FrPersonDetailList){
			myLogger("FrPersonDetailList");
			showFragment.personsList(ShowFragment.REPLACE);

		}else if(fr instanceof FrDateSelect){
			myLogger("FrDateSelect");
			int person_id = (Integer)
					SingletoneUI.getInstance().getItem(Keys.PERSON_ID);
			showFragment.personDetailList(ShowFragment.REPLACE,	person_id);

		}else if(fr instanceof FrZonesList){
			myLogger("FrZonesList");
			
			DataSkillRegistration dsr = 
					new DataSkillRegistration();
			ActionBar ab = curActivity.getActionBar();
			
			//if lesson is opened 
			if(dsr.getSkillsCount() > 0){
				askCancelLesson();
			}else{
				ab.removeAllTabs();
		        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				showFragment.dateSelect(ShowFragment.REPLACE);
			}
		}else if(fr instanceof FrSubZonesList){
			myLogger("FrSubZonesList");
			showFragment.zonesList(ShowFragment.REPLACE, false);
		}else if(fr instanceof FrMaterialsList){
			myLogger("FrMaterialsList");
			showFragment.prevTab();
		}else if(fr instanceof FrSkillsList){
			myLogger("FrSkillsList");
			showFragment.prevTab();

		}else if(fr instanceof DFrSkillScore){
			myLogger("DFrSkillScore");

			showFragment.skillsList(ShowFragment.REPLACE, false);
		}else if(fr instanceof FrSkillsSelected){
			myLogger("FrSkillsSelected");
			Toast.makeText(curActivity, "Выберите записать урок или добавить навык.", 
					Toast.LENGTH_LONG).show();
		}
	}


	/**
		 * shows AlertDialog "Урок уже открыт, Вы хотите его отменить?". И если отменяем,
		 * то очистить данные об уроке и вернуться к списку детей.
		 */
		private void askCancelLesson() {
			
			myLogger("toCancelLesson()");
			//диалог: есть открытый урок, хочешь его отменить?
			AlertDialog.Builder ad = new AlertDialog.Builder(curActivity)
				.setTitle("Внимание!")
				.setMessage("Урок уже открыт, Вы хотите его отменить?")
				.setNegativeButton("Нет", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
	
					}
				})
				.setPositiveButton("Да", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeLesson();
						
						showFragment.personsList(ShowFragment.REPLACE);
					}
				});
			ad.show();
	
			return;
		}

	/**
	 * remove the lesson in case user wants it. Happens:<br>
	 * clear DataSkillRegistration;<br>
	 * clear DataLessonInsert;<br>
	 * makes connectDBTask.rollback & connectDBTask.setAutoCommit(true)
	 */
	private void removeLesson(){
		DataSkillRegistration dsr = new DataSkillRegistration();
		dsr.clear();
		
		SingletoneUI.getInstance().clear();
		
		MainActivity.connectDBTask.rollback();
		MainActivity.connectDBTask.setAutoCommit(true);
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
