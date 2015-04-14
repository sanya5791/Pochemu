/**
 * 
 */
package ua.sanya5791.pochemu.dbfb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.EnumMap;
import java.util.HashMap;

import ua.sanya5791.pochemu.MainActivity;
import android.util.Log;

/**
 * @author sanya
 * Collection of Firebird DB statements
 */
public class DbStatements {

	/**
	 * 
	 */
	public static ResultSet rs_personsSelect=null;

	static String TAG = "DbStatements";
	private static final boolean isDebug = true;
	
//	static HashMap<LessonInsert, Object> dataLessonInsert;

	
	/**
	 * 
	 *поля запроса PERSON_SELECT
	 * @author sanya5791
	 */
	public enum PersonsSelect{
		PERSON_SELECT,				//name of CallableStatement
		I_WITH_ARCHIVE,
		PERSON_ID,
		LAST_NAME,
		FIRST_NAME,
		PATRONYMIC_NAME,
		IS_ARCHIVED
	}

	/**
	 * 
	 *list of fields of table PERSON 
	 * @author sanya5791
	 */
	public enum Person{
		PERSON,						//table's name
		PERSON_ID,					//INTEGER, NOT NULL
		LAST_NAME,					//VARCHAR(30) 
		FIRST_NAME,					//VARCHAR(30) 
		PATRONYMIC_NAME,			//VARCHAR(30) 
		EDITOR_ID,					//INTEGER, NOT NULL
		EDIT_DTIME,					//TIMESTAMP
		IS_ARCHIVED,				//INTEGER
	}
	
	/**
	 *list of fields of table PERSON_DETAIL 
	 * @author sanya5791
	 */
	public enum PersonDetail{
		PERSON_DETAIL,				//table's name
		PERSON_ID,					//INTEGER, NOT NULL
		BIRTHDAY, 					//DATE
		SEX, 						//CHAR(1)
		MATHER,						//VARCHAR(90) 
		TAKE_HOME,					//VARCHAR(250)
		PHONE,						//VARCHAR(200)
		MAIL,						//VARCHAR(50) 
		REGION,						//VARCHAR(30)
		HAS_ALLERGY,				//CHAR(1) 
		ALLERGY_ON,					//VARCHAR(200)
		IS_ANOTHER_CHILD,			//CHAR(1)
		SEMINAR,					//CHAR(1) 
		EDITOR_ID,					//INTEGER, NOT NULL
		EDIT_DTIME					//TIMESTAMP
	}
	/**
	 * 
	 *list of fields of table PHONE
	 * @author sanya5791
	 */
	public enum Phone{
		PHONE,						//table's name
		PHONE_ID,					//INTEGER
		PERSON_ID,					//INTEGER
		PHONE_TYPE_ID,				//INTEGER
		PHONE_NUMBER,				//VARCHAR(12)
		EDITOR_ID,					//INTEGER
		EDIT_DTIME,					//TIMESTAMP
	}
	
	/**
	 * 
	 *list of fields of table PHONE_TYPE
	 * @author sanya5791
	 */
	public enum PhoneType{
		PHONE_TYPE,					//table's name
		PHONE_TYPE_ID,				//INTEGER
		PHONE_TYPE_NAME,			//VARCHAR(25)
		PHONE_TYPE_ACRONYM,			//VARCHAR(10)
		
	}
	
	/**
	 *There are encounted all the fields of CallableStatement LESSON_INSERT
	 * @author sanya5791
	 */
	public enum LessonInsert{
		LESSON_INSERT,				//name of CallableStatement
		I_PARENT_LESSON_ID,			//INTEGER
		I_PERSON_ID,				//INTEGER
		I_DATE_LESSON,				//String "YYYY-MM-DD"
		I_ZONE_ID,					//INTEGER
		I_SUBZONE_ID,				//INTEGER
		I_MATERIAL_ID,				//INTEGER
		I_MATERIAL_CHOICE_ID,		//INTEGER
		I_WORK_ID,					//INTEGER
		I_WORK_CHOICE_ID,			//INTEGER
		I_PHASE_ID,					//INTEGER
		I_LEVEL_ID,					//INTEGER
		I_BEHAVIOUR_ID,				//INTEGER
		I_CONCENTRATION_ID,			//INTEGER
		I_CYCLE,					//INTEGER не может быть null, поэтому must 0
		I_CLEANING_ID,				//INTEGER
		I_WORKER_ID,				//INTEGER
		O_LESSON_ID,				//INTEGER
	}
	/**
	 *There are encounted all the fields of CallableStatement SKILL_REGISTRATION_INSERT
	 * @author sanya5791
	 */
	public enum SkillRegistrationInsert{
		SKILL_REGISTRATION_INSERT,		//name of CallableStatement
		I_SKILL_ID,						//INTEGER  +
		I_PERSON_ID,					//INTEGER  
		I_HAS_VALUE,					//SMALLINT +
		I_SKILL_VALUE,					//VARCHAR  
		I_RATING,						//INTEGER  
		I_LESSON_ID,					//INTEGER  
		I_WORKER_ID,					//INTEGER  +
		O_LESSON_ID,					//INTEGER  
		SKILL_NAME,						//String я его добавляю из-за того, что мне нужно использовть это поле в адаптере в FrSkillsSelected. Да, коряво, но пока ничего не придумал.  
		LAST_VALUE,						//int последняя оценка; добавляю из-за того, что мне нужно использовть это поле в DFrSkillScore  
		ZONE_ID,						//int    
		SUBZONE_ID,						//int   
		MATERIAL_ID,					//int   
	}

	/**
	 *There are encounted all the fields of Table SKILL_REGISTRATION
	 * @author sanya5791
	 */
	public enum SkillRegistration{
		SKILL_REGISTRATION,				//name of Table
		SKILL_REGISTRATION_ID,			//INTEGER
		SKILL_ID,						//INTEGER  
		PERSON_ID,						//INTEGER  
		LESSON_ID,						//INTEGER  
		EDITOR_ID,						//INTEGER  
		EDIT_DTIME,						//TIMESTAMP
		HAS_VALUE,						//SMALLINT 
		SKILL_VALUE,					//VARCHAR  (200)
		RATING							//INTEGER
	}
	
	/**
	 *There are listed all the fields of CallableStatement SKILLS_SELECT
	 * @author sanya5791
	 */
	public enum SkillsSelect {
		SKILLS_SELECT, 					// name of CallableStatement
		I_MATERIAL_ID, 					//INTEGER
		I_HISTORY, 						// SMALLINT
		SKILL_ID, 						// INTEGER
		MATERIAL_ID, 					// INTEGER
		SKILL_INDEX, 					// INTEGER
		SKILL_NAME, 					// VARCHAR
		HAS_VALUE, 						// SMALLINT
		HISTORY, 						// SMALLINT
		EDIT_DTIME	 					// TIMESTAMP
	}
	
	public DbStatements() {
	}

	/**
	* @return ResulSet after  PERSON_SELECT statement. 
	* You should use it for ArrayAdapter to fill ListView 
	*/
	static public ResultSet personsSelect(){
		CallableStatement stmt;							//вызываемая процедура БД

		final int iIsAchived = 0; 							//Саша, нужно будет читать это из настроек - делать выборку из архива или нет

		myLogger("Prepare CallableStatement for PERSON_SELECT");
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return rs_personsSelect;
		}
		
		try {
				
			stmt = connection.prepareCall(
					"{call PERSON_SELECT(?, ?,?,?,?,?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);


//вариант использования номеров столбцов
			stmt.setInt(1, iIsAchived); // ////////// I_HISTORY - write code later
			
			stmt.registerOutParameter(2, Types.INTEGER); // PERSON_ID
			stmt.registerOutParameter(3, Types.VARCHAR); // LAST_NAME
			stmt.registerOutParameter(4, Types.VARCHAR); // FIRST_NAME
			stmt.registerOutParameter(5, Types.VARCHAR); // PATRONYMIC_NAME
			stmt.registerOutParameter(6, Types.INTEGER); // IS_ARCHIVED
			
			myLogger("ResultSet for query PERSON_SELECT");
			rs_personsSelect = stmt.executeQuery();
		} catch (SQLException e1) {
			myLogger("jdbc.FBDriver: Connection step 2 failed");
			e1.printStackTrace();
		}
		
		return rs_personsSelect;
	}

	/**
	 * @return ResulSet after query to PERSON_DETAIL table. 
	 * You should use it to build an ArrayAdapter to fill ListView 
	 */
	static public ResultSet personDetail(int person_id){

		ResultSet rs_personDetail = null;

		myLogger("Prepare to make query to PERSON_DETAIL table");
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return rs_personDetail;
		}

		try {
			Statement stmt = 
					connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
							ResultSet.CONCUR_READ_ONLY);
			
//создаю строку запроса используя enum и String.concat(String str) для сцепления строк
			String sCon = "select ";
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.BIRTHDAY.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.SEX.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.MATHER.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.TAKE_HOME.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.PHONE.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.MAIL.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.REGION.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.HAS_ALLERGY.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.ALLERGY_ON.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.IS_ANOTHER_CHILD.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PersonDetail.PERSON_DETAIL.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PersonDetail.SEMINAR.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(Phone.PHONE.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(Phone.PHONE_NUMBER.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(Phone.PHONE.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(Phone.PHONE_TYPE_ID.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(PhoneType.PHONE_TYPE.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(PhoneType.PHONE_TYPE_NAME.toString());
				sCon=sCon.concat(", ");
				sCon=sCon.concat(Person.PERSON.toString());
				sCon=sCon.concat(".");
				sCon=sCon.concat(Person.IS_ARCHIVED.toString());
				sCon=sCon.concat(" ");

				sCon+=
				"from " +
				PersonDetail.PERSON_DETAIL.toString() + " " +
				"left join " +
				Phone.PHONE.toString() +
				" on " + PersonDetail.PERSON_DETAIL.toString()+"."+
				PersonDetail.PERSON_ID + " = " +
				Phone.PHONE.toString() +"."+
				Phone.PERSON_ID.toString() +
				" left join " +
				PhoneType.PHONE_TYPE.toString()+
				" on "+Phone.PHONE.toString()+"."+
				Phone.PHONE_TYPE_ID.toString()+"="+
				PhoneType.PHONE_TYPE+"."+PhoneType.PHONE_TYPE_ID+
				" left join "+Person.PERSON.toString()+ 
				" on "+Person.PERSON.toString()+"."+
				Person.PERSON_ID+"="+
				PersonDetail.PERSON_DETAIL+"."+PersonDetail.PERSON_ID+
				
				" where " +
				PersonDetail.PERSON_DETAIL.toString()+"."+
				PersonDetail.PERSON_ID+"="+
				person_id;

			rs_personDetail = stmt.executeQuery(sCon);
		} catch (SQLException e1) {
			myLogger("jdbc.FBDriver: Connection step 2 failed");
			e1.printStackTrace();
		}

		return rs_personDetail;
	}

		
	/**
	 * @return ResulSet after ZONES_SELECT statement has been executed. 
	 * You should use it for ArrayAdapter to fill ListView 
	 */
	static public ResultSet zonesSelect(){
		final int iIsAchived = 0; 							//Саша, нужно будет читать это из настроек - делать выборку из архива или нет
		final String statementName = "ZONES_SELECT";

		CallableStatement stmt;							//вызываемая процедура БД
		ResultSet rs = null;

		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return rs;
		}
		
		try {
			myLogger("Prepare CallableStatement for ZONES_SELECT");
			stmt = connection.prepareCall("{call ZONES_SELECT(?, ?,?,?,?,?)}", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, iIsAchived);						//////////// I_HISTORY - write code later
			stmt.registerOutParameter(2, Types.INTEGER);			//ZONE_ID
			stmt.registerOutParameter(3, Types.INTEGER);			//ZONE_INDEX
			stmt.registerOutParameter(4, Types.VARCHAR);			//ZONE_NAME
			stmt.registerOutParameter(5, Types.INTEGER);		//HISTORY
			stmt.registerOutParameter(6, Types.TIMESTAMP);			//EDIT_DTIME
			
			myLogger("ResultSet for query " + statementName);
			rs = stmt.executeQuery();

		}catch (java.sql.SQLException e){
				myLogger("jdbc.FBDriver: Connection step 2 failed");
				e.printStackTrace();

		} finally {

		}

		myLogger("End of launchCallable statement");

		return rs;

	}

	/**
	 * @return ResulSet after ZONES_SELECT statement has been executed. 
	 * You should use it for ArrayAdapter to fill ListView 
	 * @param zone_id 
	 */
	static public ResultSet subZonesSelect(int zone_id){
		CallableStatement stmt;							//вызываемая процедура БД
	
		final int iIsAchived = 0; 							//////////////////Саша, нужно будет читать это из настроек - делать выборку из архива или нет
	
		ResultSet rs_subzones = null;
		
		myLogger("Prepare CallableStatement for SUBZONES_SELECT");
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return null;
		}
	
		try {
			stmt = connection.prepareCall(
					"{call SUBZONES_SELECT(?,?, ?,?,?,?,?,?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, zone_id); // I_ZONE_ID
			stmt.setInt(2, iIsAchived); // ////////// I_HISTORY - write code
										// later
			stmt.registerOutParameter(3, Types.INTEGER); // SUBZONE_ID
			stmt.registerOutParameter(4, Types.INTEGER); // ZONE_ID
			stmt.registerOutParameter(5, Types.INTEGER); // SUBZONE_INDEX
			stmt.registerOutParameter(6, Types.VARCHAR); // SUBZONE_NAME
			stmt.registerOutParameter(7, Types.SMALLINT); // HISTORY
			stmt.registerOutParameter(8, Types.TIMESTAMP); // EDIT_DTIME
	
			myLogger("ResultSet for query SUBZONES_SELECT");
			rs_subzones = stmt.executeQuery();
	
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return rs_subzones;
	}

	/**
	 * @return ResulSet after MATERIALS_SELECT statement has been executed. 
	 * You should use it for ArrayAdapter to fill ListView 
	 */
	public static ResultSet materialsSelect(int subzone_id){
		
		final int iIsAchived = 0; 							//Саша, нужно будет читать это из настроек - делать выборку из архива или нет

		CallableStatement stmt;							//вызываемая процедура БД
		ResultSet rs_materials = null;

		myLogger("Prepare CallableStatement for MATERIALS_SELECT");
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return rs_materials;
		}

		try {
				
			stmt = connection.prepareCall(
					"{call MATERIALS_SELECT(?,?, ?,?,?,?,?,?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, subzone_id); // I_ZONE_ID
			stmt.setInt(2, iIsAchived); // ////////// I_HISTORY - write code
										// later
			stmt.registerOutParameter(3, Types.INTEGER); // MATERIAL_ID
			stmt.registerOutParameter(4, Types.INTEGER); // SUBZONE_ID
			stmt.registerOutParameter(5, Types.INTEGER); // MATERIAL_INDEX
			stmt.registerOutParameter(6, Types.VARCHAR); // MATERIAL_NAME
			stmt.registerOutParameter(7, Types.SMALLINT); // HISTORY
			stmt.registerOutParameter(8, Types.TIMESTAMP); // EDIT_DTIME

			myLogger("ResultSet for query MATERIALS_SELECT");
			rs_materials = stmt.executeQuery();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return rs_materials;
	}

	/**
	 * я вместо него использую SKILL_REGISTRATION_SELECT_LAST(skillSelectLast())
	 * @return ResulSet after SKILLS_SELECT statement has been executed. 
	 * You should use it for ArrayAdapter to fill ListView 
	 */
	static public ResultSet skillsSelect(int material_id){
		CallableStatement stmt;							//вызываемая процедура БД

		final int iIsAchived = 0; 							//Саша, нужно будет читать это из настроек - делать выборку из архива или нет

		ResultSet rs_skills = null;

		myLogger("Prepare CallableStatement for SKILLS_SELECT");
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return rs_skills;
		}

		try {
				
			stmt = connection.prepareCall(
					"{call SKILLS_SELECT(?,?, ?,?,?,?,?,?,?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, material_id); // I_MATERIAL_ID
			stmt.setInt(2, iIsAchived); // ////////// I_HISTORY - write code
										// later
			stmt.registerOutParameter(3, Types.INTEGER); // SKILL_ID
			stmt.registerOutParameter(4, Types.INTEGER); // MATERIAL_ID
			stmt.registerOutParameter(5, Types.INTEGER); // SKILL_INDEX
			stmt.registerOutParameter(6, Types.VARCHAR); // SKILL_NAME
			stmt.registerOutParameter(7, Types.SMALLINT); // HAS_VALUE
			stmt.registerOutParameter(8, Types.SMALLINT); // HISTORY
			stmt.registerOutParameter(9, Types.TIMESTAMP); // EDIT_DTIME

			myLogger("ResultSet for query SKILLS_SELECT");
			rs_skills = stmt.executeQuery();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return rs_skills;
	}
	
	/**
	 * вызов возвращает список навыков, с/без последней оценкой для клиента 
	 * и переданной Зоны/подзоны/материала.
	 * @param zone_id - обязательный параметр
	 * @param subzone_id
	 * @param material_id
	 * @param person_id - обязательный параметр
	 * @return ResulSet after SKILL_REGISTRATION_SELECT_LAST statement has been executed. 
	 */
	static public ResultSet skillSelectLast(int zone_id, 
											int subzone_id, 
											int material_id,
											int person_id){
		CallableStatement stmt;							//вызываемая процедура БД
		
		ResultSet rs_skillLast = null;
		
		myLogger("Prepare CallableStatement for SKILL_REGISTRATION_SELECT_LAST");
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return rs_skillLast;
		}
		
		try {
			
			stmt = connection.prepareCall(
					"{call SKILL_REGISTRATION_SELECT_LAST(?,?,?,?, ?,?,?,?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, zone_id); // I_ZONE_ID
			stmt.setInt(2, subzone_id);  // I_SUBZONE_ID
			stmt.setInt(3, material_id);  //I_MATERIAL_ID
			stmt.setInt(4, person_id);  // I_PERSON_ID
			// later
			stmt.registerOutParameter(5, Types.INTEGER); // SKILL_ID
			stmt.registerOutParameter(6, Types.VARCHAR); // SKILL_NAME
			stmt.registerOutParameter(7, Types.SMALLINT); // HAS_VALUE
			stmt.registerOutParameter(8, Types.INTEGER); // LAST_VALUE
			
			myLogger("ResultSet for query SKILLS_SELECT");
			rs_skillLast = stmt.executeQuery();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return rs_skillLast;
	}
	
	/**
	 * метод записи в базу; выполнение CallableStatemtnt "LESSON_INSERT"
	 * @param dbFields an array with field-value values to make 
	 * callable statement LESSON_INSERT; If you dont set any field, it will be set by its default value. Лучше бы спросить Русика о значениях поумолчанию
	 * @return lesson_id as a result of query LESSON_INSERT. Or 0 in case of fault;
	 */
	public static int lessonInsert(EnumMap<LessonInsert,Object> emap){
		
		int lesson_id;
		CallableStatement stmt;							//вызываемая процедура БД
		ResultSet rs = null;
		
		lesson_id = 0;									//set by defauln in the state of fault 
		
		myLogger("Prepare CallableStatement for LESSON_INSERT");
		
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return lesson_id;
		}
		try {
			String s;
			
			stmt = connection.prepareCall(
					"{call LESSON_INSERT(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			//input parameters
			if(emap.get(LessonInsert.I_PARENT_LESSON_ID) == null)
				stmt.setInt(1, 0); // value by default
			else
				stmt.setInt(1, (int) emap.get(LessonInsert.I_PARENT_LESSON_ID)); // I_PARENT_LESSON_ID

			if(emap.get(LessonInsert.I_PERSON_ID) == null)
				stmt.setInt(2, 0); // value by default
			else
				stmt.setInt(2, (int) emap.get(LessonInsert.I_PERSON_ID)); // I_PERSON_ID
			
			// make current date by default
			if(emap.get(LessonInsert.I_DATE_LESSON)==null){
				java.util.Date date = new java.util.Date();
				java.sql.Date sqlDate=new java.sql.Date(date.getTime());
				stmt.setDate(3, sqlDate); // value by default
			}else{
				s = (String) emap.get(LessonInsert.I_DATE_LESSON);
				stmt.setDate(3, Date.valueOf(s)); // I_ZONE_ID
			}
			
			if(emap.get(LessonInsert.I_ZONE_ID) == null)
				stmt.setInt(4, 0); // value by default
			else
				stmt.setInt(4, (int) emap.get(LessonInsert.I_ZONE_ID)); // I_ZONE_ID

			if(emap.get(LessonInsert.I_SUBZONE_ID) == null)
				stmt.setInt(5, 0); // value by default
			else
				stmt.setInt(5, (int) emap.get(LessonInsert.I_SUBZONE_ID)); // I_SUBZONE_ID
			
			if(emap.get(LessonInsert.I_MATERIAL_ID) == null)
				stmt.setInt(6, 0); // value by default
			else
				stmt.setInt(6, (int) emap.get(LessonInsert.I_MATERIAL_ID)); // I_MATERIAL_ID

			if(emap.get(LessonInsert.I_MATERIAL_CHOICE_ID) == null)
				stmt.setNull(7, Types.NULL); // value by default
			else
				stmt.setNull(7, (int) emap.get(LessonInsert.I_MATERIAL_CHOICE_ID)); // I_MATERIAL_CHOICE_ID

			if(emap.get(LessonInsert.I_WORK_ID) == null)
				stmt.setNull(8, Types.NULL); // value by default
			else
				stmt.setInt(8, (int) emap.get(LessonInsert.I_WORK_ID)); // I_WORK_ID

			if(emap.get(LessonInsert.I_WORK_CHOICE_ID) == null)
				stmt.setNull(9, Types.NULL); // value by default
			else
				stmt.setInt(9, (int) emap.get(LessonInsert.I_WORK_CHOICE_ID)); // I_WORK_CHOICE_ID
			
			if(emap.get(LessonInsert.I_PHASE_ID) == null)
				stmt.setNull(10, Types.NULL); // value by default
			else
				stmt.setInt(10, (int) emap.get(LessonInsert.I_PHASE_ID)); // I_PHASE_ID
			
			if(emap.get(LessonInsert.I_LEVEL_ID) == null)
				stmt.setNull(11, Types.NULL); // value by default
			else
				stmt.setInt(11, (int) emap.get(LessonInsert.I_LEVEL_ID)); // I_LEVEL_ID
			
			if(emap.get(LessonInsert.I_BEHAVIOUR_ID) == null)
				stmt.setNull(12, Types.NULL); // value by default
			else
				stmt.setInt(12, (int) emap.get(LessonInsert.I_BEHAVIOUR_ID)); // I_BEHAVIOUR_ID
			
			if(emap.get(LessonInsert.I_CONCENTRATION_ID) == null)
				stmt.setNull(13, Types.NULL); // value by default
			else
				stmt.setInt(13, (int) emap.get(LessonInsert.I_CONCENTRATION_ID)); // I_CONCENTRATION_ID
			
			if(emap.get(LessonInsert.I_CYCLE) == null)
				stmt.setInt(14, 0); // value by default
			else
				stmt.setInt(14, (int) emap.get(LessonInsert.I_CYCLE)); // I_CYCLE

			if(emap.get(LessonInsert.I_CLEANING_ID) == null)
				stmt.setNull(15, Types.NULL); // value by default
			else
				stmt.setInt(15, (int) emap.get(LessonInsert.I_CLEANING_ID)); // I_CLEANING_ID
			
			if(emap.get(LessonInsert.I_WORKER_ID) == null)
				stmt.setInt(16, 0); // value by default
			else
				stmt.setInt(16, (int) emap.get(LessonInsert.I_WORKER_ID)); // I_WORKER_ID
			//output parameters
			stmt.registerOutParameter(17, Types.INTEGER); // O_LESSON_ID=lesson_id
			
			myLogger("ResultSet for query LESSON_INSERT");
			rs = stmt.executeQuery();
			rs.first();
			lesson_id=rs.getInt(1);
			myLogger("O_LESSON_ID: "+ String.valueOf(lesson_id));

		} catch (SQLException e1) {
			e1.printStackTrace();
			myLogger("Callable statement was failed.");
		} catch (NullPointerException e) {
			e.printStackTrace();
			myLogger("Error of passing fields to Callable statements");
		}
		
		return lesson_id;
	}

	/**
	 * метод записи умений SKILL_REGISTRATION_INSERT в БД
	 * @param dbFields
	 * @return skillRegistration_id or 0 in case of fail
	 */
	public static int skillRegistrationInsert(HashMap<SkillRegistrationInsert, 
			Object> dbFields){
		
		int skillRegistration_id;
		
		CallableStatement stmt;							//вызываемая процедура БД
		ResultSet rs = null;
		
		skillRegistration_id = 0;									//set by default in the state of fault 
		
		myLogger("Prepare CallableStatement for SKILL_REGISTRATION_INSERT");
		
		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			//in case of fault return 0
			return skillRegistration_id;
		}
		
		try {
			
			stmt = connection.prepareCall(
					"{call SKILL_REGISTRATION_INSERT (?,?,?,?,?,?,?, ?)}",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			
			//input parameters
			if(dbFields.get(SkillRegistrationInsert.I_SKILL_ID) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setInt(1, (int) dbFields.get(SkillRegistrationInsert.I_SKILL_ID)); // I_SKILL_ID
			
			if(dbFields.get(SkillRegistrationInsert.I_PERSON_ID) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setInt(2, (int) dbFields.get(SkillRegistrationInsert.I_PERSON_ID)); // I_PERSON_ID
			
			if(dbFields.get(SkillRegistrationInsert.I_HAS_VALUE) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setInt(3, (int) dbFields.get(SkillRegistrationInsert.I_HAS_VALUE)); // I_HAS_VALUE
			
			if(dbFields.get(SkillRegistrationInsert.I_SKILL_VALUE) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setString(4, (String) dbFields.get(SkillRegistrationInsert.I_SKILL_VALUE)); // I_SKILL_VALUE
			
			
			if(dbFields.get(SkillRegistrationInsert.I_RATING) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setInt(5, (int) dbFields.get(SkillRegistrationInsert.I_RATING)); // I_RATING
			
			if(dbFields.get(SkillRegistrationInsert.I_LESSON_ID) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setInt(6, (int) dbFields.get(SkillRegistrationInsert.I_LESSON_ID)); // I_LESSON_ID
			
			if(dbFields.get(SkillRegistrationInsert.I_WORKER_ID) == null)
				//in case of fault return 0
				return skillRegistration_id;
			else
				stmt.setInt(7, (int) dbFields.get(SkillRegistrationInsert.I_WORKER_ID)); // I_WORKER_ID
			

			//output parameters
			stmt.registerOutParameter(8, Types.INTEGER); // O_SKILL_REGISTRATION_ID=skillRegistration_id
			
			myLogger("ResultSet for query SKILL_REGISTRATION_INSERT");
			rs = stmt.executeQuery();
			rs.first();
			skillRegistration_id=rs.getInt(1);
			myLogger("O_SKILL_REGISTRATION_ID: "
						+ String.valueOf(skillRegistration_id));
		} catch (SQLException e1) {
			e1.printStackTrace();
			myLogger("Callable statement was failed.");
		} catch (NullPointerException e) {
			e.printStackTrace();
			myLogger("Error of passing fields to Callable statements");
		}
		
		return skillRegistration_id;
	}
	
	/**
	 * make DB query to get ResultSet with hierarchy for the skill_id
	 * @param skill_id
	 * @return ResultSet with ZONE_ID, SUBZONE_ID, MATERIAL_ID for the skill_id
	 */
	static public ResultSet getSkillHierarchy(int skill_id){

		ResultSet rs = null;

		Connection connection = MainActivity.connectDBTask.getConnection();
		if (connection == null){
			myLogger("Connection to DB is NULL");
			return null;
		}

		try {
			Statement stmt = 
					connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
							ResultSet.CONCUR_READ_ONLY);
			
//создаю строку запроса используя enum и String.concat(String str) для сцепления строк
			String sCon = "select ZS.ZONE_ID, SZ.SUBZONE_ID, MS.MATERIAL_ID"
				+ " from SKILLS SS"
				+ "	left join MATERIALS MS on SS.MATERIAL_ID = MS.MATERIAL_ID"
				+ " left join SUBZONES SZ on MS.SUBZONE_ID = SZ.SUBZONE_ID"
				+ " left join ZONES ZS on SZ.ZONE_ID = ZS.ZONE_ID"
				+ " where SS.SKILL_ID = "
				+ String.valueOf(skill_id);

			rs = stmt.executeQuery(sCon);
		} catch (SQLException e1) {
			myLogger("jdbc.FBDriver: Connection step 2 failed");
			e1.printStackTrace();
		}

		return rs;
	}

	static void myLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
}
