package net.cloudslides.app;

import net.cloudslides.app.model.User;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class HomeApp extends Application {	
	
	private static HomeApp ApplicationInstance = null;
	private static User localuser = null;

	/**
	 * 检查程序是否第一次启动。
	 * @author Felix
	 * @return 是否第一次运行
	 */
	public boolean isFirstLaunch() {
		
		SharedPreferences sharedPreferences = getSharedPreferences(Define.CONFINFO,MODE_PRIVATE);
		return sharedPreferences.getBoolean(Define.APP_IS_FIRST_LAUNCH_KEY, true);
	}


	/**
	 * 标记程序已经启动过。
	 * @author Felix
	 */
	public void markLaunched() {
		
		SharedPreferences sharedPreferences = getSharedPreferences(	Define.CONFINFO,MODE_PRIVATE);
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

	/**
	 * 获取本地用户
	 * @author Felix
	 */
	public static User getLocalUser(){
		return localuser;
	}
	
	/**
	 * 配置本地用户
	 * @author Felix
	 */
	public static void setLocalUser(User user)
	{
		localuser=user;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		ApplicationInstance = this;		
		initImageLoader(getApplicationContext());		
	}
	/**
	 * 初始化imageloader
	 * @param context
	 * @author Felix
	 */
	
	public static void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()        
        .cacheInMemory(true) 
        .cacheOnDisc(true)
        .showImageOnFail(R.drawable.ic_error)
        .showStubImage(R.drawable.empty_picture)
        .showImageForEmptyUri(R.drawable.ic_error)
        .build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.tasksProcessingOrder(QueueProcessingType.FIFO)
		.defaultDisplayImageOptions(options)
		.build();
		Log.d("cache存储路径",StorageUtils.getCacheDirectory(getMyApplication()).getAbsolutePath());
		ImageLoader.getInstance().init(config);		
		Log.i("initImageLoader", "初始化uil");
	}
}
