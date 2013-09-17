package net.cloudslides.app.utils;
import net.cloudslides.app.HomeApp;

import android.content.Context;
import android.content.SharedPreferences;
public class MySharedPreferences {

	/**
	 * 存入数据至SharedPreferences
	 * @param settingName
	 * @param key
	 * @param val
	 * @param isEncrypt 是否加密数据
	 * @author Felix
	 */
	public static void SaveShared(String settingName,String key,String val,boolean isEncrypt)
	{
		SharedPreferences sp= HomeApp.getMyApplication().getSharedPreferences(settingName, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit =sp.edit();
		if(isEncrypt)
		{
      	   edit.putString(key, encrypt(val));
		}        
        else
        {
      	  edit.putString(key, val);	
        }
        
          edit.commit();
      
	}
	
	/**
	 * 从SharedPreferences取数据
	 * @param settingName
	 * @param key
	 * @param isDecrypt 是否解密数据
	 * @return String 结果
	 * @author Felix
	 */
	
	public static String getShared(String settingName,String key,boolean isDecrypt)
	{
		SharedPreferences sp=HomeApp.getMyApplication().getSharedPreferences(settingName, Context.MODE_PRIVATE);
		if(isDecrypt)
		{			
			return encrypt(sp.getString(key, ""));			
		}
		else
		{			
			return sp.getString(key, "");			
		}
		
		           
		                
	}
	
	/**
	 * 可逆的简单加密算法
	 * @param str
	 * @return
	 */
	
	public static String encrypt(String str) 
	{
        char[] a = str.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 'l');
        }
        String s = new String(a);
        return s;
    }
}
