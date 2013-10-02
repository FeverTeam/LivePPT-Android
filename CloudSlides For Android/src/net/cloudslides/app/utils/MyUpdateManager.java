package net.cloudslides.app.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.View.OnClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyUpdateManager {

	private Context mContext;
	
	//提示语
	private String updateMsg = "您当前版本有点旧，快升级最新版吧~";
	
	//返回的安装包url
	private String apkUrl = "http://cloudslides.net/android_upgrade/cloudslides_android_latest.apk";
	
	 /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory().getPath()+"/CloudSlidesUpdate/";
    
    private static final String saveFileName = savePath + "CloudSlidesNewVersion.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    
    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private int progress;
    
    private Thread downLoadThread;
    
    private boolean interceptFlag = false;
    
    public int currVersionCode;
    
	public int  newVersionCode;
	
	private TextView updatingProgressMsg;
	
	private Dialog updateNoticeDialog;
	
	private Dialog downLoadingDialog;
	
	private boolean cancelUpdateCheck=false;//终止更新检查标记
    
    @SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				updatingProgressMsg.setText("正在更新..."+progress+"%");
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
    			updatingProgressMsg.setText("下载完成!");
    			downLoadingDialog.dismiss();
				installApk();
				break;
			default:
				break;
			}
    	};
    };
    
	public MyUpdateManager(Context context) {
		this.mContext = context;
	}
	/**
	 * 更新操作
	 */
	public void doUpdate()
	{
		showNoticeDialog();
	}
	
	/**
	 * 弹出版本更新提示窗口
	 * @author Felix
	 */
	private void showNoticeDialog(){
		updateNoticeDialog =new Dialog(mContext, R.style.mDialog);
		View layout =LayoutInflater.from(mContext).inflate(R.layout.normal_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.normal_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.normal_dialog_confirm_btn);
		TextView title = (TextView)layout.findViewById(R.id.normal_dialog_title);
		TextView   msg = (TextView)layout.findViewById(R.id.normal_dialog_message);
		msg.setText(updateMsg);
		title.setText("版本更新");
		cancel.setText("以后再说");
		confirm.setText("现在更新");
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateNoticeDialog.dismiss();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				updateNoticeDialog.dismiss();
				showDownloadDialog();
			}
		});
		updateNoticeDialog.setContentView(layout);
		updateNoticeDialog.show();		
	}
	
	/**
	 * 弹出下载进度框
	 * @author Felix
	 */
	private void showDownloadDialog(){
		downLoadingDialog =new Dialog(mContext, R.style.mDialog);
		View layout =LayoutInflater.from(mContext).inflate(R.layout.update_progress,null);
		
		      Button cancel = (Button)layout.findViewById(R.id.update_dialog_cancel_btn);
		updatingProgressMsg = (TextView)layout.findViewById(R.id.update_dialog_message);
		          mProgress = (ProgressBar)layout.findViewById(R.id.update_dialog_progressbar);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				downLoadingDialog.dismiss();
				interceptFlag = true;
			}
		});
		downLoadingDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {

				interceptFlag = true;
			}
		});
		downLoadingDialog.setContentView(layout);
		downLoadingDialog.show();
		downloadApk();		
	}
	
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);
			
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度
		    	    
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//下载完成通知安装
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载.
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}			
		}
	};
	
	 /**
     * 下载apk
     * @param url
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	 /**
     * 安装apk
     * @param url
     */
	private void installApk(){
		File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(i);
	
	}
	
	
	/**
	 * 检查版本升级
	 * @param isSilence 是否静默升级，即不产生任何toast（应用打开时检查）
	 * 
	 */
	public void checkUpdate(final boolean isSilence)
	{		
		String url = "/android_upgrade/latest_version.json";
		try 
		{
		       PackageManager pm = HomeApp.getMyApplication().getPackageManager();
		        PackageInfo info = pm.getPackageInfo(HomeApp.getMyApplication().getPackageName(), 0);
		        currVersionCode=info.versionCode;
		        MyHttpClient.get(url, null,new AsyncHttpResponseHandler()
		        {
					@Override
					public void onFailure(Throwable e, String response) 
					{
						if(!isSilence)
						{
							MyToast.alert("获取最新版本信息失败...");
						}
					}

					@Override
					public void onSuccess(String response) 
					{
						Log.i("检查升级返回:",response+"");
						try 
						{
							JSONObject jso = new JSONObject(response);
							newVersionCode = jso.getInt(Param.VERSIONCODE);
							if(newVersionCode>currVersionCode)
							{
								if(!cancelUpdateCheck)
								{
									doUpdate();
								}
							}
							else
							{
								if(!isSilence)
								{
									MyToast.alert("当前已经是最新版");
								}
							}
						} catch (JSONException e) 
						{
							e.printStackTrace();
						} 
					}		        	
		        });
		} 
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止检查更新，主要考虑进入app时检查更新过程中出现页面跳转导致生命周期结束产生的窗口token miss问题
	 * @author Felix
	 */
	public void stopUpdateCheck()
	{
		cancelUpdateCheck=true;
	}
}
