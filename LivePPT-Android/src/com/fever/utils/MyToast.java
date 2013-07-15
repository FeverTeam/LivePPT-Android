package com.fever.utils;

import android.content.Context;
import android.widget.Toast;

public class MyToast {
	
	
	public void alert(Context context,String message)
	{
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);	    
	    toast.show();
	 }

}
