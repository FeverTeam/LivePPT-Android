package com.liveppt.app.utils;

import com.liveppt.app.HomeApp;
import android.widget.Toast;

public class MyToast {
	
	
	public void alert(String message)
	{
		Toast toast = Toast.makeText(HomeApp.getMyApplication().getApplicationContext(), message, Toast.LENGTH_SHORT);	    
	    toast.show();
	 }

}
