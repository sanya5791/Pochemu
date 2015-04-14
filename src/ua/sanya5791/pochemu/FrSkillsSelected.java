 package ua.sanya5791.pochemu;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.ListIterator;

import ua.sanya5791.pochemu.control.DataSkillRegistration;
import ua.sanya5791.pochemu.control.SingletoneUI;
import ua.sanya5791.pochemu.control.SingletoneUI.Keys;
import ua.sanya5791.pochemu.dbfb.DbQueryTask;
import ua.sanya5791.pochemu.dbfb.DbStatements;
import ua.sanya5791.pochemu.dbfb.DbStatements.LessonInsert;
import ua.sanya5791.pochemu.dbfb.DbStatements.SkillRegistrationInsert;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FrSkillsSelected extends Fragment 
							  implements OnClickListener, MyInterfaces{
	
	private final boolean isDebug = true;
	public static final String TAG = "FrSkillsSelected";

	static final int ADD_SKILL = 1;
	static final int WRITE_LESSON = 2;
	
	private Activity curActivity;		//base Activity who places the fragment 

	private OnLvSelectListener onLvSelectListener;
	
	private SimpleAdapter adapter;
	private DataSkillRegistration dsr;

	private ListView lv;
	private Button btAddSkill;
	private Button btSaveLesson;
	
	private int writtenLessonsCounter=0;
	private int writtenSkillsCounter=0;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		MyLogger("onAttach()");
		curActivity = activity;
		
		//check whether the calling activity has implemented OnSelectedZoneListener
		try {
			onLvSelectListener = (OnLvSelectListener)activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() 
					+ " must implement OnLvSelectListener");
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_skills_selected, 
				container, false);

		MyLogger("onCreateView: ");

		//change title of ActionBar
		ActionBar actionBar = curActivity.getActionBar();
		actionBar.setTitle(R.string.tv_skills_selected);

		//id выбранной ѕодзоны примем из Bundle
		
		btAddSkill=(Button)view.findViewById(R.id.button1);
		btSaveLesson=(Button)view.findViewById(R.id.button2);
		btAddSkill.setOnClickListener(this);
		btSaveLesson.setOnClickListener(this);
		lv = (ListView) view.findViewById(R.id.lv_out1);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyLogger("The item N" + position + " is selected" +
						"\nIt contains" + parent.getAdapter().getItem(position));

			}
		});
		return view;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyLogger("onActivityCreated: ");
		//вывести в листвью выбранное умение+оценку
		//затем через кастом лист вью прикрутить raw_skill_selected
		dsr = new DataSkillRegistration();
		
		String from[] = new String[]{
				"SKILL_NAME", 
				"I_RATING", 
				"I_SKILL_VALUE",};
		
		int to[] = new int[]{
			R.id.tvText,
			R.id.ratingBar1,
			R.id.tvTitle,
		};
		//создаем новый экземпл€р и передаем туда ArrayList of selected skills for converting.
		ArrayList<HashMap<SkillRegistrationInsert, Object>>  skillsSelected = 
				dsr.getFilled(true);
		if(skillsSelected == null){
			MyLogger("ќшибка получени€ ArrayList выбранных навыков. ");
			return;
		}
		DsrToStr dsrToStr = new DsrToStr(dsr.getFilled(true));
		ArrayList<HashMap<String, Object>> data = dsrToStr.getConverted();
		
		adapter = new SimpleAdapter(
				curActivity, 
				data,
				R.layout.raw_skill_selected,
				from,
				to);

		// назначаем адаптеру свой биндер
		adapter.setViewBinder(new ViewBinderSkillsSelected());

		MyLogger("присваиваем адаптер списку");
		lv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		String keyTitle;
		switch (v.getId()) {
		case R.id.button1:
			keyTitle = getString(R.string.bt_add_skill);
			MyLogger("Pressed key: " + keyTitle);
			
			// так мы передаем управление в MainActivity и добавить еще skill к уроку  
			onLvSelectListener.onLvSelected(this, ADD_SKILL, keyTitle);

			break;

		case R.id.button2:
			keyTitle = getString(R.string.bt_write);
			MyLogger("Pressed key: " + keyTitle);
			//uncomment it for debug purpose
//			dsr.printData();

			writeSkills();
			
			// так мы передаем управление в MainActivity и записать урок  
			onLvSelectListener.onLvSelected(this, WRITE_LESSON, keyTitle);
			break;
			
		default:
			break;
		}
	}

	/**
	 * запишем все навыки в Ѕƒ.
	 * алгоритм: берем первый навык, создаем под него урок; ищем в списке навыков 
	 * еще навыки with same material_id, чтобы записать их в тот же урок; 
	 * каждый записанный урок удал€ем из списка; затем берем следующий навык
	 * и так до тех пор, пока не будут удалены-записаны все навыки
	 */
	private void writeSkills() {
		
		DbQueryTask<FrSkillsSelected> dbTask = 
			new DbQueryTask<FrSkillsSelected>(curActivity, this) {

				@Override
				protected Boolean launchCallableStatement() {
					ArrayList<HashMap<SkillRegistrationInsert, Object>> data;
					
					data = dsr.getFilled(false);
					
					HashMap<SkillRegistrationInsert, Object> curSkill;

					while (!data.isEmpty()) {
						ListIterator<HashMap<SkillRegistrationInsert, Object>> list = 
								data.listIterator();
						int lesson_id = -1;
						int material_id = -1;

						while(list.hasNext()){
							curSkill = list.next();
							
							//if lesson_id is set to initial value - write lesson
							if(lesson_id == -1){
								lesson_id = writeLesson(curSkill);
								if(lesson_id == 0)
									return false;

								++writtenLessonsCounter;
								
								//заберем id материала 
								material_id = (Integer)	curSkill.get(
										SkillRegistrationInsert.MATERIAL_ID);
							}
							
							//если  material_id совпадает - записать навык в текущий урок
							if(material_id == (Integer)	curSkill.get(
									SkillRegistrationInsert.MATERIAL_ID)){
								
								//присвоим текущему навыку lesson_id
								curSkill.put(SkillRegistrationInsert
										.I_LESSON_ID, lesson_id);
								
								if(DbStatements.skillRegistrationInsert(curSkill) == 0)
									return false;
								
								++writtenSkillsCounter;
								
								list.remove();
							}
						}
					}
					return true;
				}


				/**
				 * 
				 * @return lesson_id or 0 in case of fault
				 */
				private int writeLesson(
						HashMap<SkillRegistrationInsert, Object> curSkill) {
					SingletoneUI stUI = SingletoneUI.getInstance();
					EnumMap<LessonInsert, Object> emap = 
							new EnumMap<LessonInsert, Object>(LessonInsert.class);
					emap.put(LessonInsert.I_PERSON_ID, 
							stUI.getItem(Keys.PERSON_ID));
					emap.put(LessonInsert.I_DATE_LESSON, 
							stUI.getItem(Keys.DATE));
					emap.put(LessonInsert.I_ZONE_ID, 
							curSkill.get(SkillRegistrationInsert.ZONE_ID));
					emap.put(LessonInsert.I_SUBZONE_ID, 
							curSkill.get(SkillRegistrationInsert.SUBZONE_ID));
					emap.put(LessonInsert.I_MATERIAL_ID, 
							curSkill.get(SkillRegistrationInsert.MATERIAL_ID));
					emap.put(LessonInsert.I_WORKER_ID, 
							stUI.getItem(Keys.WORKER_ID));

					
					return DbStatements.lessonInsert(emap);
				}
		};
		dbTask.execute();
	}

	private void MyLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

	@Override
	public void onTaskFinished(Object o, Boolean isSuccess) {
		if(! isSuccess){
			Toast.makeText(curActivity, "«апись навыков провалилась", 
					Toast.LENGTH_LONG).show();
		}else
			Toast.makeText(curActivity, 
					"«аписано " + writtenLessonsCounter + " уроков и "
					+ writtenSkillsCounter + " навыков", 
					Toast.LENGTH_LONG).show();
	}

}

/**
 * Ѕерет DataSkillRegistrationArrayList.getFilled()
 * и конвертирует &lt;HashMap&lt;SkillRegistrationInsert, Object>> dataDsr в 
 *ArrayList&lt;HashMap&lt;String, Object>> mDdataStr;
 *Ќужен, чтобы подставить результат в adapter for ListView
 *
 */
class DsrToStr{
	static String TAG = "DsrToStr";
	private static final boolean isDebug = true;

	private ArrayList<HashMap<SkillRegistrationInsert, Object>> dataDsr;
	
	private ArrayList<HashMap<String, Object>> mDataStr;
	private HashMap<String, Object> hmapStr;

	public DsrToStr(ArrayList<HashMap<SkillRegistrationInsert, Object>> dataDsr) {
		this.dataDsr = dataDsr;
		mDataStr = new ArrayList<HashMap<String, Object>>();
	}
	
	
	private void convert(){
		// convert
		myLogger("convert(): ");
		String strKey;
		Object value;
		for(int i=0; i < dataDsr.size(); i++){
			HashMap<SkillRegistrationInsert, Object> source = 
					dataDsr.get(i);
			hmapStr = new HashMap<String, Object>();
			for(SkillRegistrationInsert key : source.keySet()){
				value=source.get(key);
				strKey=key.toString();
				hmapStr.put(strKey, value);
			}
			mDataStr.add(hmapStr);
		}
		
	}
	
	/** 
	 * /
	 * @return отдать сконвертированный ArrayList
	 */
	ArrayList<HashMap<String, Object>> getConverted(){
		convert();
		return mDataStr;
	}
	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}

