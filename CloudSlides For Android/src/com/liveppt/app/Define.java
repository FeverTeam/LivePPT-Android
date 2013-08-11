package com.liveppt.app;


public class Define {
	
	 public static final float DENSITY = HomeApp.getMyApplication().getResources().getDisplayMetrics().density;
	 public static final int WIDTH_PX = HomeApp.getMyApplication().getResources().getDisplayMetrics().widthPixels;
	 public static final int HEIGHT_PX = HomeApp.getMyApplication().getResources().getDisplayMetrics().heightPixels;
	
	/**
	 * 本程序 Shared Preferences 文件名。
	 */
	public static final String APP_SHARED_PREFERENCES_NAME = "HomeAppSharedPreferences";
	
	
	/**
	 * 标记程序是否第一次启动。
	 */
	public static final String APP_IS_FIRST_LAUNCH_KEY = "APP_IS_FIRST_LAUNCH_KEY";

}
