/**
 * Interface helps listent to an AsyncTask is finished
 */
package ua.sanya5791.pochemu;


/**
 * @author Sanya
 *this Interface helps to learn when the task has been finished
 */
public interface MyInterfaces {
//	public interface AsyncTaskListener<T> {
	//you should call it in the onPostExecute method of AsyncTask;
	/**
	 * @param o Class who launched the Task
	 * @param isSuccess Whether the task has been successfully finished
	 */
	void onTaskFinished(Object o, Boolean isSuccess);			//Операция закончилась  Object o - current class; Boolean isSuccess - status of success task 
//	void onError(Throwable t);				//Получили ошибку
}

/**
 * @author Sanya
 * it helps to learn selected position in ListView
 * @param o Class that holds ListView
 * @param position position of selected item 
 * @param value String value of selected string
 */
interface OnLvSelectListener{
	void onLvSelected(Object o, int position, String value);
}
