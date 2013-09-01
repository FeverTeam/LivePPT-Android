package net.cloudslides.app.utils;

import net.cloudslides.app.HomeApp;
import android.widget.Toast;

public class MyToast {
	
	
	public static void alert(String message)
	{
		Toast toast = Toast.makeText(HomeApp.getMyApplication().getApplicationContext(), message, Toast.LENGTH_SHORT);	    
	    toast.show();
	 }

}
