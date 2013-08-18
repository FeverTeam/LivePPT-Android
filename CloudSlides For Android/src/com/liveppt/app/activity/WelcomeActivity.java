package com.liveppt.app.activity;

import com.liveppt.app.HomeApp;
import com.liveppt.app.R;
import com.liveppt.app.utils.MyActivityManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.content.Intent;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		MyActivityManager.getInstance().add(this);
		
		//2秒后离开启动画面。
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				if (HomeApp.getMyApplication().isFirstLaunch()) 
				{
					Intent introIntent = new Intent(WelcomeActivity.this, IntroActivity.class);
					startActivity(introIntent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				} 
				else
				{
					Intent mainIntent = new Intent(WelcomeActivity.this, HomeActivity.class);
					startActivity(mainIntent);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

				}
				finish();
			}
		}, 2000);		
	}



}
