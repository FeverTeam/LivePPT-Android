package net.cloudslides.app.activity;

import net.cloudslides.app.Define;
import net.cloudslides.app.R;
import net.cloudslides.app.utils.MyActivityManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HomeActivity extends Activity {
	private Button signUpBtn;
	private Button attendingBtn;
	private Button foundingBtn;
	private Button pptBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		MyActivityManager.getInstance().add(this);
		setupView();
		initView();		
	}
	
	private void setupView()
	{
		attendingBtn =(Button)findViewById(R.id.splash_attending_button);
		signUpBtn =(Button)findViewById(R.id.splash_join_button);
		foundingBtn =(Button)findViewById(R.id.splash_foudning_button);
		pptBtn  =(Button)findViewById(R.id.splash_ppt_button);  		
	}
 
	private void initView()
	{
		//-------------加入会议---------------------------------
		attendingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
				intent.putExtra("Goto", Define.LOGIN_JUMP_ATTENDING);
				startActivity(intent);
			}
		});         
		
		//-------------发起会议---------------------------------
		foundingBtn.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
 				intent.putExtra("Goto", Define.LOGIN_JUMP_FOUNDING);
 				startActivity(intent);
 			}
 		});	
		
		//-------------我的文稿--------------------------------- 
		pptBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(HomeActivity.this, LoginActivity.class);
				intent.putExtra("Goto", Define.LOGIN_JUMP_PPT);
				startActivity(intent);
			}
		});
		
		//-------------注册---------------------------------
       signUpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(HomeActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});			
	}	
	
}
