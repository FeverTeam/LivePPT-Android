package net.cloudslides.app.activity;

import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.utils.MyActivityManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

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
