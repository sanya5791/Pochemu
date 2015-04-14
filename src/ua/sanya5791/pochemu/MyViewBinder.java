package ua.sanya5791.pochemu;

import ua.sanya5791.dbsqlite.PersonDetails;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

/**
 * 
 *I use it to make visible/unvisible CheckBoxes during inflating "PersonDetails" ListView items 
 * @author sanya5791
 */

class ViewBinderPersonDetails implements ViewBinder{
	static String TAG = "ViewBinderPersonDetails";
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		ImageView iv;
		CheckBox checkBox;
		String columnName, str;
		
		columnName = cursor.getColumnName(columnIndex);
		switch (columnName) {
			
		case PersonDetails.COLUMN_ICON:
			iv = (ImageView)view;
			int resId = cursor.getInt(columnIndex);
			//if item without picture
			if(resId == 0){
				iv.setVisibility(View.INVISIBLE);
			}else{
				iv.setVisibility(View.VISIBLE);
				iv.setImageResource(resId);
			}
			return true;
			
		case PersonDetails.COLUMN_CHBOX_SHOW:
			checkBox = (CheckBox)view;
			if(cursor.getInt(columnIndex) == View.VISIBLE)
				checkBox.setVisibility(View.VISIBLE);
			else
				checkBox.setVisibility(View.INVISIBLE);
			return true;
			
		case PersonDetails.COLUMN_CHBOX_STATE:
			str = cursor.getString(columnIndex);
			if(str == null)
				return true;
			
			checkBox = (CheckBox)view;
			if(str.equals(PersonDetails.STATE_YES))
				checkBox.setChecked(true);
			else if(str.equals(PersonDetails.STATE_NO))
				checkBox.setChecked(false);
	
			return true;
			
		default:
			break;
		}
		
		return false;
	}

}

/**
 * @deprecated from 25.03.15 because of substitution on SimpleCursorAdapter (ViewBinderPersonDetails)
 *I use it to make visible/unvisible CheckBoxes during inflating "PersonDetails" ListView items 
 * @author sanya5791
 */
public class MyViewBinder implements SimpleAdapter.ViewBinder, ViewBinder{
	private static final boolean isDebug = true;
	static String TAG = "MyViewBinder";

	@Override
	public boolean setViewValue(View view, Object data,
			String textRepresentation) {
		int id = view.getId();
		//
		if (id == R.id.checkBox1 && textRepresentation == String.valueOf(View.VISIBLE)){
			view.setVisibility(View.VISIBLE);
			return true;
		}
		if (id == R.id.checkBox1 && textRepresentation == String.valueOf(View.INVISIBLE)){
			view.setVisibility(View.INVISIBLE);
			return true;
		}
		
		//if the ImageView binds "false" (instead of id of picture), then hide the ImageView
		if (id == R.id.imageView1 && textRepresentation.equals("false")){
			view.setVisibility(View.INVISIBLE);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// TODO Auto-generated method stub
		//TODO code for case of no icon 
		MyLogger("view.getId(): " + Integer.toHexString(view.getId())
		+ "\n cursor.getColumnName(columnIndex): " + cursor.getColumnName(columnIndex) 
		+ "\n-------------");
		return true;
//		return false;
	}

	private void MyLogger(String statement){
		if (isDebug) {
			Log.v(TAG, statement);
		}
	}
	
}

/**
 * 
 *I use it to make custom ListView during inflating "FrSkillsSelected" 
 * @author sanya5791
 */
class ViewBinderSkillsSelected implements ViewBinder, SimpleAdapter.ViewBinder{
	static String TAG = "ViewBinderSkillsSelected";

	@Override
	public boolean setViewValue(View view, Object data,
			String textRepresentation) {
		
		int id = view.getId();
		if(id == R.id.ratingBar1 ){
			if(textRepresentation == "0")
				view.setVisibility(View.GONE);
			else{
				view.setVisibility(View.VISIBLE);
				int rating = Integer.parseInt(textRepresentation);
				RatingBar rb = (RatingBar)view;
				rb.setRating(rating);
				//despite the fact that focusable is set false in xml, i must do it here to really set it false
				rb.setFocusable(false);
				rb.setEnabled(false);
			}
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		RatingBar rb;
		TextView tv;
		
		int iValue;
		
		int id = view.getId();
		if(id == R.id.ratingBar1 ){
			iValue = cursor.getInt(columnIndex);
			if(iValue == 0)
				view.setVisibility(View.GONE);
			else{
				view.setVisibility(View.VISIBLE);
				rb = (RatingBar)view;
				rb.setRating(iValue);
				//despite the fact that focusable is set false in xml, i must do it here to really set it false
				rb.setFocusable(false);
				rb.setEnabled(false);
			}
			return true;
		}else if (id == R.id.tvTitle){
			iValue = cursor.getInt(columnIndex);
			tv = (TextView) view;
			if(iValue == 0)
				tv.setText("");
			else{
				tv.setText("Расширенный");
			}
			return true;
		}
		
		 
		return false;
	}

}
