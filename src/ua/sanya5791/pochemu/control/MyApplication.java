package ua.sanya5791.pochemu.control;


import android.app.Application;
import android.util.Log;
import org.acra.*;
import org.acra.annotation.*;
//import static ReportField.*; 

import ua.sanya5791.pochemu.R;


@ReportsCrashes(
//        formKey = "", // This is required for backward compatibility but not used
        mailTo = "sanya5791@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_toast_text,
        resDialogTitle = R.string.crash_dialog_title, //optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,  // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
//        formUri = "https://sanya5791.iriscouch.com/"
    )

//@ReportsCrashes(formKey = "", formUri = "http://www.yourselectedbackend.com/reportpath")
public class MyApplication extends Application {
	public final static String TAG = "MyApplication";
	
	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate()");
		super.onCreate();
		// Initialize the singletons so their instances
	    // are bound to the application process.
		initSingletons();
		
		// The following line triggers the initialization of ACRA
        ACRA.init(this);
	}

	protected void initSingletons() {
		// TODO Auto-generated method stub
		SingletoneUI.initIstance();
//		SingletoneDataSkillRegistration.initIstance();
		
	}
	
	public void customAppMethod()
	  {
	    // Custom application method
	  }
	
}
