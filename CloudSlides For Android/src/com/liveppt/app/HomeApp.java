package com.liveppt.app;

import android.app.Application;
import android.content.SharedPreferences;

public class HomeApp extends Application {	
	
	private static HomeApp ApplicationInstance = null;

	/**
	 * 检查程序是否第一次启动。
	 * @author Felix
	 * @return 是否第一次运行
	 */
	public boolean isFirstLaunch() {
		
		SharedPreferences sharedPreferences = getSharedPreferences(Define.APP_SHARED_PREFERENCES_NAME,MODE_PRIVATE);
		return sharedPreferences.getBoolean(Define.APP_IS_FIRST_LAUNCH_KEY, true);
	}


	/**
	 * 标记程序已经启动过。
	 * @author Felix
	 */
	public void markLaunched() {
		
		SharedPreferences sharedPreferences = getSharedPreferences(	Define.APP_SHARED_PREFERENCES_NAME,	MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(Define.APP_IS_FIRST_LAUNCH_KEY, false);
		editor.commit();
	}


	/**
	 * 获取当前应用程序的 Application 对象。
	 * @author Felix
	 * @return 全局Application单类对象
	 */
	public static HomeApp getMyApplication() {
		return ApplicationInstance;
	}	

	@Override
	public void onCreate() {
		super.onCreate();
		ApplicationInstance = this;
	}
}
