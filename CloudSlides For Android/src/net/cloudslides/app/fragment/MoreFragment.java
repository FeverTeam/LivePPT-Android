package net.cloudslides.app.fragment;

import net.cloudslides.app.Define;
import net.cloudslides.app.R;
import net.cloudslides.app.activity.HomeActivity;
import net.cloudslides.app.activity.MainActivity;
import net.cloudslides.app.utils.FileUtils;
import net.cloudslides.app.utils.MySharedPreferences;
import net.cloudslides.app.utils.MyToast;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MoreFragment extends Fragment {

	private View layout;
	private Button menu;
	private Button logOut;
	private TextView cacheSize;
	private CheckBox autoLoginBox;
	private MainActivity activity;
	private RelativeLayout clearCache;
	private RelativeLayout upgrade;
	private RelativeLayout feedback;
	private RelativeLayout changePsw;
	
	public MoreFragment(MainActivity a)
	{
		this.activity=a;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout =inflater.inflate(R.layout.more_frag, null);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		setUpView();
		initView();
	}
	
	private void setUpView()
	{
		        menu = (Button)layout.findViewById(R.id.more_top_bar_btn);
		autoLoginBox = (CheckBox)layout.findViewById(R.id.more_auto_login_check);
		      logOut = (Button)layout.findViewById(R.id.more_logout_btn);
		   cacheSize = (TextView)layout.findViewById(R.id.more_cache_size_text);
		  clearCache = (RelativeLayout)layout.findViewById(R.id.more_clear_cache_btn);
		     upgrade = (RelativeLayout)layout.findViewById(R.id.more_check_update_btn);
		    feedback = (RelativeLayout)layout.findViewById(R.id.more_feedback_btn);	
		   changePsw = (RelativeLayout)layout.findViewById(R.id.more_change_psw_btn);
	}
	
	private void initView()
	{
		menu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				activity.toggleMenu();
			}
		});
		
		autoLoginBox.setChecked(getLoginState());
		autoLoginBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setLoginState(isChecked);
			}
		});
		
		logOut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				logout();
				Intent intent =new Intent(activity,HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
		
		
		Log.i("cacheDir", StorageUtils.getCacheDirectory(activity).getAbsolutePath());
		cacheSize.setText(FileUtils.fileLength(FileUtils.getFolderSize(StorageUtils.getCacheDirectory(activity))));
		clearCache.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				showClearCacheDialog();
			}
		});
		changePsw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyToast.alert("coming soon.");
			}
		});
		feedback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyToast.alert("...意见接受态度照旧...");
			}
		});
		
		upgrade.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyToast.alert("正在检测最新版本...");
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						MyToast.alert("已经是最新版本.");
					}
				}, 1500);
			}
		});		
	}
	
	
	/**
	 * 获取自动登录设置状态
	 * @return true/false
	 * @author Felix
	 */
	private boolean getLoginState()
	{
		if(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, false).equals("true"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 设置自动登录状态
	 * @param isChecked 是否设为自动登录
	 * @author Felix
	 */
	private void setLoginState(boolean isChecked)
	{
		if(isChecked)
		{
			MySharedPreferences.SaveShared(Define.CONFINFO,Define.LOCAL_USER_INFO_AUTO_LOGIN, "true",false);
		}
		else
		{
			MySharedPreferences.SaveShared(Define.CONFINFO,Define.LOCAL_USER_INFO_AUTO_LOGIN, "false",false);
		}
	}
	
	/**
	 * 注销用户信息
	 * @author Felix
	 */
	
	private void logout()
	{
		MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, "",false);
		MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_HAS_LOGINED_KEY, "",false);
		MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_IS_REMEMBER_ME, "",false);
	}
	/**
	 * 清理缓存对话框
	 * @author Felix
	 */
	private void showClearCacheDialog()
	{
		final Dialog dialog =new Dialog(activity, R.style.mDialog);
		View layout =LayoutInflater.from(activity).inflate(R.layout.normal_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.normal_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.normal_dialog_confirm_btn);
		TextView title = (TextView)layout.findViewById(R.id.normal_dialog_title);
		TextView   msg = (TextView)layout.findViewById(R.id.normal_dialog_message);
		msg.setText("即将删除所有本地缓存,是否确定清除?");
		title.setText("清理缓存");
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new ClearCacheTask().execute();
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	
	/**
	 * 清理缓存异步线程
	 * @author Felix
	 *
	 */
	class ClearCacheTask extends AsyncTask<Void, Void, Boolean>
	{

		@Override
		protected void onPreExecute() {
			MyToast.alert("正在清除缓存...");
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return FileUtils.DeleteFile(StorageUtils.getCacheDirectory(activity));
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result)
			{
				MyToast.alert("清理完毕");
				cacheSize.setText(FileUtils.fileLength(FileUtils.getFolderSize(StorageUtils.getCacheDirectory(activity))));
			}else
			{
				MyToast.alert("清理失败");				
			}
		}
		
	}
}
