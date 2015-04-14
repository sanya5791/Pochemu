
package ua.sanya5791.pochemu.control;

import java.util.HashMap;

import ua.sanya5791.pochemu.DFrSkillScore;
import ua.sanya5791.pochemu.FrDateSelect;
import ua.sanya5791.pochemu.FrMaterialsList;
import ua.sanya5791.pochemu.FrPersonDetailList;
import ua.sanya5791.pochemu.FrPersonsList;
import ua.sanya5791.pochemu.FrSkillsList;
import ua.sanya5791.pochemu.FrSkillsSelected;
import ua.sanya5791.pochemu.FrSubZonesList;
import ua.sanya5791.pochemu.FrZonesList;
import ua.sanya5791.pochemu.R;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Управляет отображением фрагментов в MainActivity.<br>
 * Управление происходит с привязкой к показу/скрытию ActionBar.Tabs
 * @author sanya
 *
 */
public class ShowFragment {
	
	public static final String TAG = "ShowFragment";
	private final boolean isDebug = true;
	
	public final static int NEW = 0;
	public final static int REPLACE = 1;
	
	public final static String AB_TITLE_ZONES = "Зоны";
	public final static String AB_TITLE_SUBZONES = "Подзоны";
	public final static String AB_TITLE_MATERIALS = "Материалы";
	public final static String AB_TITLE_SKILLS = "Навыки";

	private Activity curActivity;
	private FragmentTransaction fTrans; 
	
	//visibility state of right pane in tablet mode; 
	public static boolean rPaneVisible;
	
	private FrPersonsList mFrPersonsList;
	private FrPersonDetailList mFrPersonDetailList;
	private FrZonesList mFrZonesList;
	private FrSubZonesList mFrSubZonesList;
	private FrMaterialsList mFrMaterialsList;
	private FrSkillsList mFrSkillsList;
	
	private boolean rotate=false;

	//Tab's title is binded with Fragment
	private HashMap<String, String> bindTabToFragment;
	

	//is it tablet layout
	private static boolean isTablet;
	
	ActionBar actionBar;
	/**
	 * 
	 */
	public ShowFragment(Activity curActivity) {
		this.curActivity=curActivity;
		createTabAndFragmentsBinds();
		
		View v = curActivity.findViewById(R.id.frgmCont2);
		
		actionBar = curActivity.getActionBar();

		//set tablet/phone mode
		if (v != null)
			isTablet = true;
			

		if(isTablet){
			if(v.getVisibility() == View.VISIBLE)
				rPaneVisible=true;
			else
				rPaneVisible=false;
		}
	}
	
	private void createTabAndFragmentsBinds() {
		String[] tabNames = new String[]{
				curActivity.getResources().getString(R.string.ab_zones),
				curActivity.getResources().getString(R.string.ab_subzones),
				curActivity.getResources().getString(R.string.ab_materials),
				curActivity.getResources().getString(R.string.ab_skills),
		};
		
		String[] fragmentList = {
				FrZonesList.class.getName(),
				FrSubZonesList.class.getName(),
				FrMaterialsList.class.getName(),
				FrSkillsList.class.getName(),
		};
		
		bindTabToFragment = new HashMap<String, String>();
		for(int i=0; tabNames.length > i; i++){
			bindTabToFragment.put(tabNames[i], fragmentList[i]);
		}
		
	}

	/**
	 * Замена, показ фрагмента списка детей
	 * @param task создаем новый или заменяем фрагмент. 
	 * Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 */
	public void personsList(int task){

		fTrans = curActivity.getFragmentManager().beginTransaction(); 
		fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

		//this is an initial list, so you don't need right pane yet
		if(rPaneVisible) 
			rightPaneVisibility(false);
			
		mFrPersonsList = new FrPersonsList();

		switch (task) {
		case NEW:
			fTrans.add(R.id.frgmCont1, mFrPersonsList,
					mFrPersonsList.getClass().getSimpleName());
			fTrans.commit();
			
			break;

		case REPLACE :
			//replace fragment
			fTrans.replace(R.id.frgmCont1, mFrPersonsList,
					mFrPersonsList.getClass().getSimpleName());
			
			Fragment paneRight = curActivity.
					getFragmentManager().findFragmentById(R.id.frgmCont2);
			
			if(paneRight != null)
				fTrans.remove(paneRight);
			
			fTrans.commit();
			
			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
	}
	
	/**
	 * Замена, показ фрагмента списка подробнее о ребенке
	 * @param task создаем новый или заменяем фрагмент. Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 * @param person_id 
	 */
	public void personDetailList(int task, int person_id){

		if(!rPaneVisible) 
			rightPaneVisibility(true);
		
		switch (task) {
		case NEW:
			// new fragment
		
			break;

		case REPLACE :

			//now i can replace current fragment FragmentMaterialsList with  FragmentSkillsList
			fTrans = curActivity.getFragmentManager().beginTransaction();
			fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

			Fragment paneLeft =  curActivity
					.getFragmentManager()
					.findFragmentById(R.id.frgmCont1);
			Fragment paneRight =  curActivity
					.getFragmentManager()
					.findFragmentById(R.id.frgmCont2);
			//if this is not a tablet
			if(curActivity.findViewById(R.id.frgmCont2) == null){
				if(mFrPersonDetailList == null)
					mFrPersonDetailList = new FrPersonDetailList();
				fTrans.replace(R.id.frgmCont1, mFrPersonDetailList, 
						mFrPersonDetailList.getClass().getSimpleName());
				
			//	if step forward
			}else if(paneLeft instanceof FrPersonsList){
				mFrPersonDetailList = new FrPersonDetailList();
				fTrans.replace(R.id.frgmCont2, mFrPersonDetailList, 
						mFrPersonDetailList.getClass().getSimpleName());
				
			//if right pane is FrDateSelect, it means the method 
			//was called by OnBackPress. 
			}else if(paneRight instanceof FrDateSelect){
				moveLeftPaneToRight(paneLeft, fTrans);

				if(mFrPersonsList == null){
					mFrPersonsList = new FrPersonsList();
				}
				fTrans.replace(R.id.frgmCont1, mFrPersonsList,
						mFrPersonsList.getClass().getSimpleName());
				
			}
				
			fTrans.commit();

			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}

	/**
	 * Замена/показ фрагмента списка подробнее о ребенке
	 * @param task создаем новый или заменяем фрагмент.
	 * <br />Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 */
	public void dateSelect(int task){
		
		if(!rPaneVisible) 
			rightPaneVisibility(true);
		
		sweepData(ShowFragment.TAG);
		switch (task) {
		case NEW:
			// new fragment
			
			break;
			
		case REPLACE :
			//now i show DatePicker DialogFragment FrdDateSelect
			fTrans = curActivity.getFragmentManager().beginTransaction();
			fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			
			Fragment paneRight =  curActivity
					.getFragmentManager()
					.findFragmentById(R.id.frgmCont2);
			//if this is not a tablet
			if(paneRight == null)
				fTrans.replace(R.id.frgmCont1, 
								new FrDateSelect());
			
			else if(paneRight instanceof FrPersonDetailList){
				moveRightPaneToLeft(paneRight, fTrans);
				
				FrDateSelect frDateSelect = new FrDateSelect();
				fTrans.replace(R.id.frgmCont2, frDateSelect, 
						frDateSelect.getClass().getSimpleName());
				
			//if FrDateSelect is NOT on right pane, it means the method 
			//was called by OnBackPress. 
			}else{
				mFrPersonDetailList = new FrPersonDetailList();
				fTrans.replace(R.id.frgmCont1, mFrPersonDetailList, 
						mFrPersonDetailList.getClass().getSimpleName());

				FrDateSelect frDateSelect = new FrDateSelect();
				fTrans.replace(R.id.frgmCont2, frDateSelect, 
						frDateSelect.getClass().getSimpleName());
			}
				
			fTrans.commit();
			
			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}
	
	/**
	 * Замена, показ фрагмента списка зон
	 * @param task создаем новый или заменяем фрагмент. 
	 * Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 * @param onRotate state. 
	 */
	public void zonesList(int task, boolean onRotate){
		
		rotate = onRotate;

		if(actionBar.getNavigationMode()!=ActionBar.NAVIGATION_MODE_TABS)
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		switch (task) {
		case NEW:
		// new fragment
			
			break;
			
		case REPLACE :										// replace fragment
			//if Zones Tab does exist - replace Fragment 
			for(int i= 0; i<actionBar.getTabCount(); i++){
				Tab tab = actionBar.getTabAt(i);
				if(tab.getText().equals(AB_TITLE_ZONES)){
					MyLogger("ActionBar.Tab already exists");
					int tabPosition = tab.getPosition();
					actionBar.setSelectedNavigationItem(tabPosition);
					return;
				}
			}
			//if you here - add new Tab and replace the fragment
		    Tab tab = actionBar.newTab()
            .setText(AB_TITLE_ZONES)
            .setTabListener(new TabListener() {
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {

					//if rotate screen - do it only once
					if(rotate){
						rotate=false;
						return;
					}
					
					if(!rPaneVisible) 
						rightPaneVisibility(true);
					
					sweepData(FrZonesList.TAG);
					
					//if this is not a tablet
					if(!isTablet){
						if(mFrZonesList == null)
							mFrZonesList = new FrZonesList();
						ft.replace(R.id.frgmCont1, mFrZonesList, 
								mFrZonesList.getClass().getSimpleName());
						
					//if this tab has been just added (user moves forward)
					}else if(actionBar.getTabCount() == tab.getPosition() + 1){

						FrDateSelect frDateSelect = new FrDateSelect();
						ft.replace(R.id.frgmCont1, frDateSelect, 
								frDateSelect.getClass().getSimpleName());
						
						mFrZonesList = new FrZonesList();
						ft.replace(R.id.frgmCont2, mFrZonesList, 
								mFrZonesList.getClass().getSimpleName());
						
					//if the tab is the first (user moves back)
					}else if(actionBar.getTabCount() > tab.getPosition() + 1){

						
						//get prevTab
						MyLogger("Left pane for select a date; " +
								"Left Fragment=" + FrDateSelect.class.getSimpleName());
						
						//to left pane a fragment FrDateSelect
						Fragment f = new FrDateSelect();
						ft.replace(R.id.frgmCont1, f, 
								f.getClass().getSimpleName());

						//place in right pane a fragment for selected tab
						replaceFragmentByTab(tab, R.id.frgmCont2, ft);
					}

					//remove next Tabs
					tabsRemoveAfterPosition(tab.getPosition());
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					MyLogger("onTabReselected():");
					onTabSelected(tab, ft);
					
				}
			});
		    
		    actionBar.addTab(tab, true);

			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}
	
	/**
	 * Замена, показ фрагмента списка подзон
	 * @param task создаем новый или заменяем фрагмент. Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 * @param onRotate state;
	 */
	public void subZonesList(int task, final boolean onRotate){
	
		Tab tab;
		
		rotate = onRotate;
		
		if(!rPaneVisible) 
			rightPaneVisibility(true);
		
		switch (task) {
		case NEW:
		// new fragment
			
			break;
			
		case REPLACE :								// replace fragment
			//if Zones Tab does exist - just replace Fragment 
			for(int i= 0; i<actionBar.getTabCount(); i++){
				tab = actionBar.getTabAt(i);
				if(tab.getText().equals(AB_TITLE_SUBZONES)){
					MyLogger("ActionBar.Tab already exists");
				    actionBar.setSelectedNavigationItem(tab.getPosition());
					return;
				}
			}
			//if you are here - add new Tab and replace the fragment
		    tab = actionBar.newTab()
            .setText(AB_TITLE_SUBZONES)
            .setTabListener(new TabListener() {
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
					
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					
					//if rotate screen - do it only once
					if(rotate){
						rotate=false;
						return;
					}
					
					if(!rPaneVisible) 
						rightPaneVisibility(true);
					
					sweepData(FrSubZonesList.TAG);
					
					MyLogger("Всего табов=" + actionBar.getTabCount()+
							"; текущий таб="+ tab.getPosition());
					//if this is not a tablet
					if(!isTablet){
						if(mFrSubZonesList == null)
							mFrSubZonesList = new FrSubZonesList();
						ft.replace(R.id.frgmCont1, mFrSubZonesList, "Subzones");
						
					//if this tab has been just added (user moves forward)
					}else if(actionBar.getTabCount() == tab.getPosition() + 1){
						Fragment paneRight =  curActivity
								.getFragmentManager()
								.findFragmentById(R.id.frgmCont2);
						//place zones list on the left and subzones on right
						//first save instance of the Fragment
						moveRightPaneToLeft(paneRight, ft);

						mFrSubZonesList = new FrSubZonesList();
						ft.replace(R.id.frgmCont2, mFrSubZonesList, 
								mFrSubZonesList.getClass().getSimpleName());
						
					//if the tab is nether the last nor the first (user moves back)
					}else if(actionBar.getTabCount() > tab.getPosition() + 1){
						
						//place in right pane a fragment for selected tab
						//and to left pane a fragment for previous tab
						//get prevTab
						Tab prevTab = actionBar.getTabAt(tab.getPosition() - 1);
						replaceFragmentByTab(prevTab, R.id.frgmCont1, ft);
						
						replaceFragmentByTab(tab, R.id.frgmCont2, ft);
					}
						
					tabsRemoveAfterPosition(tab.getPosition());
				}
				

				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					MyLogger("onTabReselected():");
					
					Fragment paneRight =  curActivity
							.getFragmentManager()
							.findFragmentById(R.id.frgmCont2);

					//if this is not a tablet
					if(paneRight == null){
						onTabSelected(tab, ft);
						return;
					}

					onTabletTabReselected(tab, ft, paneRight);
				}
			});
		    
		    actionBar.addTab(tab, true);
		    	

			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}
	
	
	/**
	 * Reload the last tab's paneRight;<br>
	 * Notice, this method is supposed 
	 * to use with TabListener, so you mustn't commit transaction.  
	 * @param tab reselected Tab
	 * @param ft used FragmentTransaction
	 * @param paneRight Fragment to reattach
	 */
	protected void onTabletTabReselected(Tab tab, FragmentTransaction ft,
			Fragment paneRight) {

		//if the tab is on last position (user picked lv's item on the left pane)
		if(actionBar.getTabCount() == tab.getPosition() + 1 ){
			MyLogger("tab is on the last position");
			ft.detach(paneRight);
			ft.attach(paneRight);
		}
	}

	/**
	 * Замена/показ фрагмента списка материалов
	 * @param task создаем новый или заменяем фрагмент. Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 * @param onRotate state;
	 */
	public void materialsList(int task, final boolean onRotate){
		Tab tab;

		rotate = onRotate;
		
		if(!rPaneVisible) 
			rightPaneVisibility(true);
		
		switch (task) {
		case NEW:
			// new fragment
			
			break;
			
		case REPLACE :
			//if Materials Tab does exist - just replace Fragment 
			for(int i= 0; i<actionBar.getTabCount(); i++){
				tab = actionBar.getTabAt(i);
				if(tab.getText().equals(AB_TITLE_MATERIALS)){
					MyLogger("ActionBar.Tab already exists");
				    actionBar.setSelectedNavigationItem(tab.getPosition());
					return;
				}
			}
			//if you are here - add new Tab and replace the fragment
		    tab = actionBar.newTab()
            .setText(AB_TITLE_MATERIALS)
            .setTabListener(new TabListener() {
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					
					//if rotate screen - do it only once
					if(rotate){
						rotate=false;
						return;
					}
					
					if(!rPaneVisible) 
						rightPaneVisibility(true);
					
					sweepData(FrMaterialsList.TAG);
					
					MyLogger("Всего табов=" + actionBar.getTabCount()+
							"; текущий таб="+ tab.getPosition());
					//if this is not a tablet
					if(!isTablet){
						if(mFrMaterialsList == null)
							mFrMaterialsList = new FrMaterialsList();
						ft.replace(R.id.frgmCont1, mFrMaterialsList, "Materials");
					//if this tab has been just added (user moves forward)
					}else if(actionBar.getTabCount() == tab.getPosition() + 1){
						Fragment paneRight =  curActivity
								.getFragmentManager()
								.findFragmentById(R.id.frgmCont2);
						//if he moves forward right pane must be  FrSubZonesList
						if(paneRight instanceof FrSubZonesList){
							moveRightPaneToLeft(paneRight, ft);
						//Otherwise subzone was picked on left pane
						}else {
							actionBar.removeTabAt(tab.getPosition()-1);
						}
						
						mFrMaterialsList = new FrMaterialsList();
						ft.replace(R.id.frgmCont2, mFrMaterialsList, 
								FrMaterialsList.class.getSimpleName());
						
					//if the tab is nether the last nor the first (user moves back)
					}else if(actionBar.getTabCount() > tab.getPosition() + 1){
						//place in right pane a fragment for selected tab
						//and to left pane a fragment for previous tab
						String tabName = (String) tab.getText();
						String fragmentRightName = bindTabToFragment.get(tabName);
						MyLogger("Selected Tab=" + tabName +"; " +
								"Right Fragment=" + fragmentRightName);
						
						//get prevTab
						Tab prevTab = actionBar.getTabAt(tab.getPosition() - 1);
						replaceFragmentByTab(prevTab, R.id.frgmCont1, ft);

						replaceFragmentByTab(tab, R.id.frgmCont2, ft);
					}

					tabsRemoveAfterPosition(tab.getPosition());
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {

					MyLogger("onTabReselected():");
					
					Fragment paneRight =  curActivity
							.getFragmentManager()
							.findFragmentById(R.id.frgmCont2);

					//if this is not a tablet
					if(paneRight == null){
						onTabSelected(tab, ft);
						return;
					}

					onTabletTabReselected(tab, ft, paneRight);
					
				}
			});
		    
	    	actionBar.addTab(tab, true);
		    	
			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}
	
	/**
	 * Замена/показ фрагмента списка навыков
	 * @param task создаем новый или заменяем фрагмент. 
	 * Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 * @param onRotate state;
	 */
	public void skillsList(int task, final boolean onRotate){
		Tab tab;
		
		if(actionBar.getNavigationMode()!=ActionBar.NAVIGATION_MODE_TABS)
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		rotate = onRotate;

		if(!rPaneVisible) 
			rightPaneVisibility(true);
		
		switch (task) {
		case NEW:
			// new fragment
			
			break;
			
		case REPLACE :
			//if Materials Tab does exist - just replace Fragment 
			for(int i= 0; i<actionBar.getTabCount(); i++){
				tab = actionBar.getTabAt(i);
				if(tab.getText().equals(AB_TITLE_SKILLS)){
					MyLogger("ActionBar.Tab already exists");
				    actionBar.setSelectedNavigationItem(tab.getPosition());
					return;
				}
			}
			
			//if you are here - add new Tab and replace the fragment
		    tab = actionBar.newTab()
            .setText(AB_TITLE_SKILLS)
            .setTabListener(new TabListener() {
				
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
					
				}
				
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					MyLogger("onTabSelected():");

					//if rotate screen - do it only once
					if(rotate){
						rotate=false;
						return;
					}

					if(!rPaneVisible) 
						rightPaneVisibility(true);
					
					sweepData(FrSkillsList.TAG);
					
					//if this is not a tablet
					if(!isTablet){
						if(mFrSkillsList == null)
							mFrSkillsList = new FrSkillsList();
						ft.replace(R.id.frgmCont1, mFrSkillsList, "Skills");
						
					//if this tab has been just added (user moves forward)
					}else if(actionBar.getTabCount() == tab.getPosition() + 1){
						Fragment paneRight =  curActivity
								.getFragmentManager()
								.findFragmentById(R.id.frgmCont2);

						//place Material list on the left and Skills on right
						moveRightPaneToLeft(paneRight, ft);
						
						replaceFragmentByTab(tab, R.id.frgmCont2, ft);
					}
				}
				
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					MyLogger("onTabReselected():");
					
					Fragment paneRight =  curActivity
							.getFragmentManager()
							.findFragmentById(R.id.frgmCont2);

					//if this is not a tablet
					if(paneRight == null){
						onTabSelected(tab, ft);
						return;
					}

					if(paneRight instanceof FrSkillsList){
						onTabletTabReselected(tab, ft, paneRight);
						
					//this happens when user pushed button "add skill" an returned 
					//from SkillsSelected - so just behave like tab is new added
					}else{
						onTabSelected(tab, ft);
					}
				}
			});

		    actionBar.addTab(tab, true);
			
			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}
	
	/**
	 * Замена/показ фрагмента списка выбранных навыков
	 * @param task создаем новый или заменяем фрагмент. Используй  ShowFragment.NEW или ShowFragment.REPLACE
	 */
	public void skillsSelected(int task){

		storeTabs();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        switch (task) {
		case NEW:
			// new fragment
			
			break;
			
		case REPLACE :
			fTrans = curActivity.getFragmentManager().beginTransaction();
			fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

			//if this is not a tablet
			if(curActivity.findViewById(R.id.frgmCont2) == null){
				fTrans.replace(R.id.frgmCont1, 
						new FrSkillsSelected());
			}else{
				Fragment paneLeft =  curActivity
						.getFragmentManager()
						.findFragmentById(R.id.frgmCont1);
				//do this for correct return from SkillsSelected
				moveLeftPaneToRight(paneLeft, fTrans);
				
				rightPaneVisibility(false);
				
				//покажем фрагмент с выбранными skills
				fTrans.replace(R.id.frgmCont1, 
						new FrSkillsSelected(), 
						FrSkillsSelected.class.getSimpleName());
			}
			
			fTrans.commit();
			
			break;
		default:
			MyLogger("method was called, but neither of args was used");
			break;
		}
		
	}
	
	/**
	 * показ Dialog фрагмента установки оценки
	 */
	public void dSkillScore() {
		
		sweepData(DFrSkillScore.TAG);
		
		//now i can show DialogFragment DFrSkillScore
		fTrans = curActivity.getFragmentManager().beginTransaction();
		fTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// покажем диалог-фрагмент для установки оценки
		DFrSkillScore dFrSkillScore = new DFrSkillScore();
		dFrSkillScore.show(fTrans, DFrSkillScore.TAG);
	}

	/**
	 * очищает данные которые будут внесены внутри nameFragment и которые будут внесены позже
	 * @param nameFragment
	 */
	private void sweepData(String nameFragment) {
		DataSkillRegistration dsr = 
				new DataSkillRegistration();

		dsr.removeItemsFrom(nameFragment);
		SingletoneUI.getInstance().removeItemsFrom(nameFragment);
	}

	/**
	 * removes tab from tabs list by specified tabTitle.
	 * notice: If there are some tabs with the same name, only the first found will be removed
	 * @param tabTitle
	 * @return true if removed the tab, othervise false
	 */
	public Boolean tabRemove(String tabTitle){
		MyLogger("tabRemove:");

		for(int i= 0; i<actionBar.getTabCount(); i++){
			Tab tab = actionBar.getTabAt(i);
			if(tab.getText().equals(tabTitle)){
				actionBar.removeTab(tab);
				MyLogger("ActionBar.Tab: " + tabTitle + " is removed");
				return true;
			}
		}
		return false;
	}

/**
		 * removes all Tabs following the tabPosition; 
		 * @param tabPosition
		 */
		private void tabsRemoveAfterPosition(int tabPosition) {
			MyLogger("tabsRemoveAfterPosition:");
			for(++tabPosition; tabPosition < actionBar.getTabCount(); ){
				Tab tab = actionBar.getTabAt(tabPosition);
				MyLogger("Removing Tab: " + tab.getText() + ", position: " + tabPosition);
				actionBar.removeTabAt(tabPosition);
			}
		}

	private void MyLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

	/**
	 * move given fragment paneRifgt to the left pane with saved state;
	 * 
	 * @param paneRight instance of Fragment to move
	 * @param ft FragmentTransaction is used; Notice, this method is supposed 
	 * to use with TabListener, so you mustn't commit transaction.  
	 */
	private void moveRightPaneToLeft(Fragment paneRight, FragmentTransaction ft) {

		Fragment paneLeft;

		String className = paneRight.getClass().getName();

		MyLogger("paneRight.getName() = " 
				+ paneRight.getClass().getName());

		Fragment.SavedState savedState = curActivity.
				getFragmentManager().
				saveFragmentInstanceState(paneRight);
		
		try {
			paneLeft = (Fragment) 
					Class.forName(className).newInstance();
			paneLeft.setInitialSavedState(savedState);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		ft.replace(R.id.frgmCont1, paneLeft, 
				paneLeft.getClass().getSimpleName());
	}

	/**
	 * move given fragment paneLeft to the right pane with saved state;
	 * 
	 * @param paneLeft instance of Fragment to move
	 * @param ft FragmentTransaction is used; Notice, this method is supposed 
	 * to use with TabListener, so you mustn't commit transaction.  
	 */
	private void moveLeftPaneToRight(Fragment paneLeft, FragmentTransaction ft) {

		Fragment paneRight;
	
		String className = paneLeft.getClass().getName();
	
		MyLogger("paneLeft.getName() = " 
				+ paneLeft.getClass().getName());
	
		Fragment.SavedState savedState = curActivity.
				getFragmentManager().
				saveFragmentInstanceState(paneLeft);
		
		try {
			paneRight = (Fragment) 
					Class.forName(className).newInstance();
			paneRight.setInitialSavedState(savedState);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
	
		ft.replace(R.id.frgmCont2, paneRight, 
				paneRight.getClass().getSimpleName());
	}

	/**
	 * Define binded Fragment by tab and put it to the fragmentContainer 
	 * using FragmentTransaction replace method; 
	 * @param tab 
	 * @param fragmentContainer
	 * @param ft
	 */
	private void replaceFragmentByTab(Tab tab, int fragmentContainer, FragmentTransaction ft){

		String tabName = (String) tab.getText();
		String fragmentName = bindTabToFragment.get(tabName);

		try {
			Fragment f = (Fragment) Class.forName(fragmentName).newInstance();
			ft.replace(fragmentContainer, f, 
					f.getClass().getSimpleName());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * if tablet mode set right pane visible\gone;<br>
	 * this althogh sets field rPaneVisible to mirror current visibility state<br>
	 * it's used on tablet mode, when you need to show only right pane;
	 * @param state
	 */
	public void rightPaneVisibility(boolean state) {
		
		if(!isTablet) return;
		
		FrameLayout frgmCont2 = (FrameLayout)
				curActivity.findViewById(R.id.frgmCont2);
		SurfaceView sv = (SurfaceView)curActivity
				.findViewById(R.id.surfaceView1);
		
		if(state){
			sv.setVisibility(View.VISIBLE);
			frgmCont2.setVisibility(View.VISIBLE);
		}else{
			sv.setVisibility(View.INVISIBLE);
			frgmCont2.setVisibility(View.GONE);
		}
		
		rPaneVisible = state;
	}

	/**
		 * save state about visibility for all Tabs into SingleTone;<br>
		 * notice: when you change state to ActionBar.NAVIGATION_MODE_TABS, 
		 * the last active tab is become selected and call appropriate listener;
		 * @param bundle
		 * @return false if you don't have ActionBar.NAVIGATION_MODE_TABS state
		 */
		public boolean storeTabs() {
			String tabName; 
			
			if(actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS){
				return false;
			}
			
			SingletoneUI.Keys[] keys = new SingletoneUI.Keys[]{
					SingletoneUI.Keys.AB_TAB_ZONE_STATE,	
					SingletoneUI.Keys.AB_TAB_SUBZONE_STATE,	
					SingletoneUI.Keys.AB_TAB_MATERIAL_STATE,	
					SingletoneUI.Keys.AB_TAB_SKILL_STATE,	
			};
			
			String[] tabNames = new String[]{
					curActivity.getResources().getString(R.string.ab_zones),
					curActivity.getResources().getString(R.string.ab_subzones),
					curActivity.getResources().getString(R.string.ab_materials),
					curActivity.getResources().getString(R.string.ab_skills),
			};
			
			//reset in Singletone all tabs invisible 
			for(int i=0; i<keys.length; i++){
				SingletoneUI.getInstance().putKey(keys[i], false);
			}
			
			int count = actionBar.getNavigationItemCount();
			//save state tabs.visible  in Singletone
			for(int i=0; i<count; i++){
				tabName = (String) actionBar.getTabAt(i).getText();
				int index = -1;
				for(String s : tabNames){
					index++;
					if(tabName.equals(s)){
						break;
					}
				}
				if(index > -1){
					SingletoneUI.getInstance().putKey(keys[index], true);
				}
			}
			
			return true;
		}

	/**
		 * восстановить табы после поворота экрана
		 * @param savedInstanceState здесь список табов для восстановления
		 */
		public void restoreTabs() {
			SingletoneUI stUI;
			
			stUI = SingletoneUI.getInstance();
	
			actionBar.removeAllTabs();
			
			if(actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS){
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			}
			
		
			if((boolean) stUI.getItem(Keys.AB_TAB_ZONE_STATE))
					zonesList(REPLACE, true);
			
			if((boolean) stUI.getItem(Keys.AB_TAB_SUBZONE_STATE))
				subZonesList(REPLACE, true);
			
			if((boolean) stUI.getItem(Keys.AB_TAB_MATERIAL_STATE))
				materialsList(REPLACE, true);
			
			if((boolean) stUI.getItem(Keys.AB_TAB_SKILL_STATE))
				skillsList(REPLACE, true);
			
		}

	/**
	 * set the previous tab selected
	 */
	public void prevTab() {

		if(actionBar.getNavigationMode()!=ActionBar.NAVIGATION_MODE_TABS){
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
		
		int position = actionBar.getSelectedNavigationIndex();
		actionBar.setSelectedNavigationItem(position-1);
	}

	/**
	 * set the last tab selected
	 */
	public void lastTab() {
		
		if(isTablet && !rPaneVisible)
			rightPaneVisibility(true);
		
		//notice: last selected tab is set selected when you turn NAVIGATION_MODE_TABS mode on 
		if(actionBar.getNavigationMode()!=ActionBar.NAVIGATION_MODE_TABS){
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}else{
			int position = actionBar.getTabCount();
			MyLogger("Всего табов " + position);
			actionBar.setSelectedNavigationItem(position-1);
		}
	}

	public static boolean isTablet() {
		return isTablet;
	}
}
