package com.fever.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本类封装和实现了SharedPreferences数据库的功能，缓存登陆界面中的账号和密码
 * @author Felix
 */
public class MySharedPreferences {


	/**
	 * 存入数据至SharedPreferences
	 * @param context
	 * @param settingName
	 * @param key
	 * @param val
	 * @param isEncrypt 是否加密数据
	 * last modified: Frank
	 */
	public static void SaveShared(Context context ,String settingName,String key,String val,boolean isEncrypt){
		SharedPreferences sp= context.getSharedPreferences(settingName, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit =sp.edit();

        if(isEncrypt){
      	    edit.putString(key, encrypt(val));
		}        
        else{
      	    edit.putString(key, val);
        }
        
        edit.commit(); //这句话不懂功能
      
	}


	/**
	 * 从SharedPreferences取数据
	 * @param context
	 * @param settingName
	 * @param key
	 * @param isDecrypt 是否解密数据
	 * @return String 结果
	 * last modified: Frank
	 */
	public static String getShared(Context context ,String settingName,String key,boolean isDecrypt){
		SharedPreferences sp= context.getSharedPreferences(settingName, Context.MODE_PRIVATE);

        if(isDecrypt){
			return encrypt(sp.getString(key, ""));			
		}
		else{
			return sp.getString(key, "");			
		}

	}


	/**
	 * 可逆的加密算法 不懂！
	 * @param str
	 * @return
     * last modified: Frank
	 */
	public static String encrypt(String str) {
        char[] a = str.toCharArray();

        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 'l');
        }

        String s = new String(a);
        return s;

    }

}
