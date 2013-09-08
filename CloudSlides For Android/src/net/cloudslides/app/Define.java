package net.cloudslides.app;


public class Define {
	
	 public static final float DENSITY = HomeApp.getMyApplication().getResources().getDisplayMetrics().density;
	 public static final int WIDTH_PX = HomeApp.getMyApplication().getResources().getDisplayMetrics().widthPixels;
	 public static final int HEIGHT_PX = HomeApp.getMyApplication().getResources().getDisplayMetrics().heightPixels;
	
	/**
	 * 本程序 Shared Preferences 文件名。
	 */
	public static final String CONFINFO = "HomeAppSharedPreferences";
	/**
	 * SP中的用户邮箱字段
	 */
	public static final String LOCAL_USER_INFO_EMAIL_KEY ="user_email";
	/**
	 * SP中的用户密码字段
	 */
	public static final String LOCAL_USER_INFO_PASSWORD_KEY ="user_password";

	/**
	 * SP中标记用户是否已经登录
	 */
	public static final String LOCAL_USER_INFO_HAS_LOGINED_KEY ="user_has_logined";
	
	/**
	 * SP中标记是否自动登录
	 */
	public static final String LOCAL_USER_INFO_AUTO_LOGIN = "user_auto_login";
	
	/**
	 * SP中标记用户是否记录登录信息
	 */
	public static final String LOCAL_USER_INFO_IS_REMEMBER_ME ="user_info_save";
	/**
	 * 登录页跳转码
	 * 
	 */
	public static final int LOGIN_JUMP_PPT =0;
	public static final int LOGIN_JUMP_FOUNDING=1;
	public static final int LOGIN_JUMP_ATTENDING=3;
	
	
	/**
	 * 标记程序是否第一次启动。
	 */
	public static final String APP_IS_FIRST_LAUNCH_KEY = "APP_IS_FIRST_LAUNCH_KEY";
	
	public static final String Intent_KEY_PPT_POSITION="pptPosition";
	
	public static final String Intent_KEY_MEETING_POSITION="meetingPosition";

}
