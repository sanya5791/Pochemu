package ua.sanya5791.pochemu.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ua.sanya5791.pochemu.dbfb.DbStatements.SkillRegistrationInsert;
import android.util.Log;
/**
 * ���� ����� �������� � ArrayList "data" ��� ����������� ������ ��� ������������� � ������� SKILL_REGISTRATION_INSERT;
 * �����, � ��� ��������� ��� ����������� � ListView in FragmentSkillsSelected;
 * @author sanya
 *
 */
public class DataSkillRegistration {
	
	/**
	 * ���������� ����� ������ ��� ���������� ������� SKILL_REGISTRATION_INSERT, 
	 * �� ���� ����������� ������. 
	 * ���� ���������� ��. �����, �� ������� ���������� ������ ������
	 */
	static final String TAG = "DataSkillRegistration";
	private static final boolean isDebug = true;
	
	private static ArrayList<HashMap<SkillRegistrationInsert, Object>> data =
			new ArrayList<HashMap<SkillRegistrationInsert, Object>>();
	
	private static HashMap<SkillRegistrationInsert, Object> hmap = 
			new HashMap<SkillRegistrationInsert, Object>();


	public DataSkillRegistration() {
	}
	
	/**
	 * get value by key from hmap 
	 * @param key 
	 * @return value or null if no mapping for the specified key is found.
	 */
	public Object getHMapItem(SkillRegistrationInsert key){
		Object value;
		value=hmap.get(key);
		return value;
	}
	
	/**
	 *��������� ������ key-value to HashMap;
	 *  
	 */
	public void putToHashMap(SkillRegistrationInsert key, Object value){
	// ����� ���������� �������� (���� ������ � ��)
		myLogger("putToHashMap():");
		if(hmap.put(key, value) == null)
			myLogger("key: " + key
					+", value: " + value);
		else
			myLogger("��������! ����: " + key 
					+ " ������������ ���������: " + value.toString());
			
	}
	
	
	/**
	 * ��������� ������ HashMap � ArrayList (07.02.15 depricated: � ������� ����� ������ HashMap)
	 * @return true ���� ��; 
	 */
	 public boolean hmapToArrayList(){
		 myLogger("hmapToArrayList():");
		//���������� �������� HashMap � ArrayList
		if (isValidHashMap()) {
			myLogger("adding hmap to ArrayList");
			printHMap(hmap);
			data.add(hmap);
			
			return true;
		} else {
			myLogger("fail to add to ArryList");
			return false;
		}
	}
	
	/**
	 * ������ �������� �� ������� �������� ����������� ��������� � HashMap. 
	 * @param hmap �������� 
	 * @return true; ���� ����-�� ���, �� false
	 */
	private boolean isValidHashMap() {
		myLogger("isValidHashMap:");
		//
		SkillRegistrationInsert[] hmapKeys = {
			SkillRegistrationInsert.I_SKILL_ID, 
			SkillRegistrationInsert.I_PERSON_ID, 
			SkillRegistrationInsert.I_HAS_VALUE, 
			SkillRegistrationInsert.I_SKILL_VALUE, 
			SkillRegistrationInsert.I_RATING, 
			SkillRegistrationInsert.I_WORKER_ID,
			SkillRegistrationInsert.ZONE_ID,
			SkillRegistrationInsert.SUBZONE_ID,
			SkillRegistrationInsert.MATERIAL_ID,
		};
		
		for(SkillRegistrationInsert key : hmapKeys){
			if(! hmap.containsKey(key)){
				myLogger("hmap has not complete item list; the key " 
						+ key + " is missed");
				return false;
			}
		}

		return true;
	}

	/**
	 * get ArrayList of selected skills "data"
	 * @param addHmapToArrrayList whether to add completed hmap to ArrayList"data". 
	 * ������, ���� ��� ������ �� ������ �������, 
	 * �� ����� ���, ��� ������ ArrayList ����� ������ � ���� �������� hmap. 
	 * �� � ������� ����� hmap.  
	 * @return either completed ArrayList or null;
	 * null in case the ArrayList is not completely filled  
	 */
	public ArrayList<HashMap<SkillRegistrationInsert, Object>> 
												getFilled(Boolean addHmapToArrrayList){
		//�����: ������� ����������� ArrayList
		
		if(addHmapToArrrayList){
			hmapToArrayList();
			hmapNew();
		}

		if (isValidArrayList()) {
			return data;
		} else {
			return null;
		}
	}
	
	private void hmapNew() {
		myLogger("hmapNew:");
		// new hashmap
		hmap = new HashMap<SkillRegistrationInsert, Object>();

	}

	/**
	 * remove hmap item 
	 * @param key
	 * @return true if succeed or false if the key wasn't found
	 */
	public Boolean removeItemHMap(SkillRegistrationInsert key){
		if(hmap.remove(key) != null){
			myLogger("remove key: " + key.toString());
			return true;
		}else{
//			MyLogger("key: " + key.toString() + " NOT found for removing");
			return false;
		}
	}
	
	public Boolean removeDataItem(int index){
		data.remove(index);
		return false;
	}
	
	
	/**
	 * �������� ArrayList, ����� �� ��������
	 * @return true if ArrayList exists, otherwise false
	 */
	private boolean isValidArrayList() {
		if(data.size()>0){
			myLogger("Number of selected skill is: " 
					+ String.valueOf(data.size()));
			return true;
		}else{
			myLogger("ArrayList with the data doesn't exist");
			return false;
		}
	}
	
	/**
	 * 
	 * @return ���������� ��������� �������
	 */
	public int getSkillsCount(){
		return data.size();
	}
	
	/**
	 * ������ ������� ������� ���������� �  inLesson_id
	 * @param inLesson_id id of a lesson
	 * @return quantity of skills; 
	 */
	public Integer howManySkills(Integer inLesson_id){
		Integer skillsCount = 0;
		ArrayList<HashMap<SkillRegistrationInsert, Object>> alSkills;
		HashMap<SkillRegistrationInsert, Object> hmSkill;
		
		alSkills = getFilled(false);

		if (alSkills == null)
			return 0;

		//look for existing skills in last lesson
		for(int i=0; i < alSkills.size(); i++){
			hmSkill = alSkills.get(i);
			Integer id = (Integer)hmSkill.get(SkillRegistrationInsert.I_LESSON_ID);
			if(id != null && id.equals(inLesson_id))
				++skillsCount;
		}

		return skillsCount;
	}

	private void printHMap(HashMap<SkillRegistrationInsert, Object> hmapToPrn) {
		for (Map.Entry<SkillRegistrationInsert, Object> entry :  hmapToPrn.entrySet()) {
			myLogger("key: "+entry.getKey()+
					 "; value: " +entry.getValue());
		}
		
	}

	/**
	 * print all data from ArrayList dataForSkillRegistration
	 */
	public void printData() {
		myLogger("printData: ");
		String s;
		s=String.valueOf(data.size());
		myLogger("dataForSkillRegistration.size() is " 
				+ s);
		
		for (int i = 0; i < data.size(); i++) {
			myLogger("Item N" + String.valueOf(i+1) + " of the ArrayList:");
			HashMap<SkillRegistrationInsert, Object> hmap = data.get(i);
			printHMap(hmap);
		}
	}
/**
 * Removes all elements from this ArrayList, leaving it empty.
 */
	public void clear(){
		myLogger("Clearing all data in DataSkillRegistration");
		data.clear();
		hmap.clear();
	}

	/**
	 * remove hmap items are put by fromFragment and the other items are put later; 
	 * (Items ������� ���� ��������� ����� fromFragment ����� ������ �����������) 
	 * @param beginFromFragment �� ������ ��������� ������ �������� ������ hmap
	 */
	public void removeItemsFrom(String beginFromFragment){
		myLogger("removeItemsFrom("+ beginFromFragment +"):");
		
		/**
		 * class to keep the list of keys for removing bind to nameFragment
		 * @author Sanya
		 */
		class DataForClear{
			String nameFragment;
			@SuppressWarnings("unused")
			SkillRegistrationInsert[] listKeys;
		}
		
		//declare a list of nameFragments
		ArrayList<DataForClear> aList;
		aList = new ArrayList<DataForClear>();
		DataForClear dfr;
		dfr = new DataForClear();
		
		//create the list of keys for removing
		dfr.nameFragment = "FrPersonsList";
		dfr.listKeys = new SkillRegistrationInsert[]{
				SkillRegistrationInsert.I_PERSON_ID};
		aList.add(dfr);
		dfr.nameFragment = "FrSkillsList";
		dfr.listKeys = new SkillRegistrationInsert[]{
				SkillRegistrationInsert.I_SKILL_ID,
				SkillRegistrationInsert.SKILL_NAME,
				SkillRegistrationInsert.I_HAS_VALUE,
				SkillRegistrationInsert.I_WORKER_ID,
				};
		aList.add(dfr);
		dfr.nameFragment = "DFrSkillScore";
		dfr.listKeys = new SkillRegistrationInsert[]{
				SkillRegistrationInsert.I_SKILL_VALUE,
				SkillRegistrationInsert.I_RATING,
		};
		aList.add(dfr);
		
		//remove all keys specified by fromFragment and the following keys
		Boolean nameFragmentFound=false;
		for (DataForClear dataForClear : aList) {
			if(dataForClear.nameFragment.contentEquals(beginFromFragment)
					|| nameFragmentFound == true){
				nameFragmentFound=true;
//				//��� ������� - ����� ����� �������
//				for (SkillRegistrationInsert key : dataForClear.listKeys) {
//					if(removeItemHMap(key))
//						myLogger("remove item: " + key.toString());
//				}
			}
		}		
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

	/**
	 * is the skill_id_selected already in the selected list "data" 
	 * @param skill_id_selected skill id for for checking
	 * @return true in case of first meeteng skill_id_selected;
	 *  false if if skill_id_selected is not found
	 */
	public boolean isSkillSelected(int skill_id_selected) {
		myLogger("isSkillSelected(): ");
		int skill_id;
		
		for (int i = 0; i < data.size(); i++) {
			HashMap<SkillRegistrationInsert, Object> hmap = data.get(i);
			skill_id = (int) hmap.get(SkillRegistrationInsert.I_SKILL_ID);
			if(skill_id == skill_id_selected)
				return true;
		}
		
		return false;
	}
}
