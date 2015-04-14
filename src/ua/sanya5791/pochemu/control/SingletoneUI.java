package ua.sanya5791.pochemu.control;

import java.util.ArrayList;
import java.util.EnumMap;

import android.util.Log;

/**
 * Синглтон данных, которые нужны для пользовательского интерфейса.
 * Данные храняться  в EnumMap data
 * Список доступных полей в  enum Keys;
 * @author sanya
 *
 */
public class SingletoneUI {
	private static final boolean isDebug = true;
	static String TAG = "SingletoneUI";
	
	//list of keys for EnumMap
	public enum Keys{
		PERSON_ID,				//Integer
		PERSON_NAME,			//String
		DATE,					//String
		ZONE_ID,				//Integer
		ZONE_NAME, 				//String
		SUBZONE_ID,				//Integer
		SUBZONE_NAME, 			//String
		MATERIAL_ID,			//Integer
		MATERIAL_NAME, 			//String
		WORKER_ID, 				//String
		AB_TAB_ZONE_STATE, 		//Boolean
		AB_TAB_SUBZONE_STATE,	//Boolean
		AB_TAB_MATERIAL_STATE, 	//Boolean
		AB_TAB_SKILL_STATE, 	//Boolean
//		AB_TAB_VISIBILITY_STATE,//Boolean
	}
	
	//save here data i need to use between fragment transactions
	private EnumMap<Keys, Object> data
		= new EnumMap<>(Keys.class);
	
	private static SingletoneUI instance;
	
	private SingletoneUI() {
		// Constructor hidden because this is a singleton
	}
	
	/**
	 * get instance 
	 * @return instance SingletoneUI
	 */
	public static SingletoneUI getInstance(){
		// Return the instance
		return instance;
	}
	
	public static void initIstance(){
		if (instance == null){
			instance = new SingletoneUI();
		}
	}

	private void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}

	/**
	 *добавляет связку key-value to EnumMap;
	 *  
	 */
	public void putKey(Keys key, Object value){
	// метод добавления элемента
		myLogger("putKey:");
		Object valueOld = data.put(key, value);
		if(valueOld != null)
			myLogger("Внимание! Перезаписано поле: " + key 
					+ " было: " 	+ valueOld 
					+ ", а стало: " + value.toString());
	}

	/**
	 * get value by key from EnumMap 
	 * @param key 
	 * @return value or null if no mapping for the specified key is found.
	 */
	public Object getItem(Keys key){
		Object value;
		value=data.get(key);
		return value;
	}

	/**
	 *удаляет связку key-value from EnumMap;
	 *  @return true on success; false if there was npthing to remove/
	 */
	public Boolean removeItem(Keys key){
		myLogger("removeItem():");
		//remove emap item 
		if(data.remove(key) != null){
			myLogger("remove key: " + key.toString());
			return true;
		}else{
			myLogger("key: " + key.toString() + " NOT found for removing");
			return false;
		}
	}

	/**
	 * remove EnumMap items are put by "beginFromFragment" and the other items are put later; 
	 * (Items которые были добавлены после "beginFromFragment" также должны бытьудалены) 
	 * @param beginFromFragment от какого фрагмента начать удаление items EnumMap
	 */
	public void removeItemsFrom(String beginFromFragment){
		myLogger("removeItemsFrom("+ beginFromFragment +"):");
		
		/**
		 * class to keep the list of keys for removing bind to nameFragment
		 * @author Sanya
		 */
		class DataForClear{
			String nameFragment;
			Keys listKeys[];
		}
		
		//declare a list of nameFragments
		ArrayList<DataForClear> aList;
		aList = new ArrayList<DataForClear>();
		DataForClear dfr;
		
		//create the list of keys for removing
		dfr = new DataForClear();
		dfr.nameFragment = "FrPersonsList";
		dfr.listKeys = new Keys[]{
				Keys.PERSON_ID,
				Keys.PERSON_NAME,
				};
		aList.add(dfr);
		
		dfr = new DataForClear();
		dfr.nameFragment = "FrDateSelect";
		dfr.listKeys = new Keys[]{
				Keys.DATE,
				};
		aList.add(dfr);
		
		dfr = new DataForClear();
		dfr.nameFragment = "FrZonesList";
		dfr.listKeys = new Keys[]{
				Keys.ZONE_ID,
				Keys.ZONE_NAME,
		};
		aList.add(dfr);
		
		dfr = new DataForClear();
		dfr.nameFragment = "FrSubZonesList";
		dfr.listKeys = new Keys[]{
				Keys.SUBZONE_ID,
				Keys.SUBZONE_NAME,
		};
		aList.add(dfr);
		
		dfr = new DataForClear();
		dfr.nameFragment = "FrMaterialsList";
		dfr.listKeys = new Keys[]{
				Keys.MATERIAL_ID,
				Keys.MATERIAL_NAME,
		};
		aList.add(dfr);
		
		//remove all keys specified by fromFragment and the following keys
		Boolean nameFragmentFound=false;
		for (DataForClear dataForClear : aList) {
			if(dataForClear.nameFragment.contentEquals(beginFromFragment)
					|| nameFragmentFound == true){
				nameFragmentFound=true;
				for (Keys key : dataForClear.listKeys) {
					//TODO для отладки - позже можно удалить
					if(removeItem(key));
				}
			}
		}		
	}
	

	/**
	 * полностью очистить data и оставить его пустым
	 */
	public void clear() {
		data.clear();
		
	}
	
}
