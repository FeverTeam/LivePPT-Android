package com.fever.liveppt;
import org.json.JSONException;
import org.json.JSONObject;

import com.fever.model.User;
import com.fever.utils.HttpRequest;
import com.fever.utils.MyApp;
import com.fever.utils.MySharedPreferences;
import com.fever.utils.MyToast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LoginActivity extends Activity
{
	private Button mLoginButton;
	private TextView mRegistText;
	private EditText emailInput;
	private EditText passwordInput;
	private ProgressBar progressBar;	
	private static String loginUrl =HttpRequest.httpProtocol+HttpRequest.hostName+"/app/login";
	private static String loginNameField="email";
	private static String loginPasswordField="password";
	/**
	 * 登陆对话框
	 * @author Felix
	 * 
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_login_new);		
		init();
		
	}	
	
	
	/**
	 * 登陆框控件初始化，事件触发
	 * @author Felix
	 */
	private void init()
	{		
		mLoginButton=(Button)findViewById(R.id.login_go_button);
		mRegistText=(TextView)findViewById(R.id.login_register_text);
		emailInput=(EditText)findViewById(R.id.email_input);
		passwordInput=(EditText)findViewById(R.id.password_input);
		progressBar =(ProgressBar)findViewById(R.id.loginProgressBar);		
		
		emailInput.setText(MySharedPreferences.getShared(LoginActivity.this, "loginInfo", "email", false));		
		passwordInput.setText(MySharedPreferences.getShared(LoginActivity.this,"loginInfo", "password",true));	
		
		mLoginButton.setOnClickListener(new OnClickListener() 
		{			
			/**
			 * 登陆监听
			 */
			@Override
			public void onClick(View v) 
			{							
				new LoginTask().execute();			
			}
		});				
		
		mRegistText.setOnClickListener(new OnClickListener() 
		{		
			/**
			 * 注册监听
			 */
			@Override
			public void onClick(View v) 
			{	
				 Intent intent=new Intent(LoginActivity.this,RegisterActivity.class );
				 startActivity(intent);
			}
		});		
	}
	
	/**
	 * 登陆操作异步线程
	 * @author Felix
	 *
	 */
	
	class LoginTask extends AsyncTask<Void,String,String >
	{

		/**
		 * 预加载忙状态显示
		 */
		@Override
		protected void onPreExecute()
		{					
			progressBar.setVisibility(View.VISIBLE);	
			
		}
		/**
		 * 线程任务
		 */
		@Override
		protected String doInBackground(Void ...params) {
			String strResult;
			String url;
			String email;
			String password;
			JSONObject resInfo;			
			MyApp app =(MyApp)getApplication();
			HttpRequest httpRequest =new HttpRequest();	
			
			/**
			 * 参数初始化
			 */
			email=emailInput.getText().toString().trim();
			password=passwordInput.getText().toString().trim();
			url=loginUrl+"?"+loginNameField+"="+email+"&"+loginPasswordField+"="+password;
			
			/**
			 * 执行请求
			 */
		    strResult=httpRequest.HttpGetRequest(app.getHttpClient(), url);				 
			Log.i("登陆返回：",strResult);				 
				try 
				{
					resInfo = new JSONObject(strResult);						
					if(resInfo.getBoolean("isSuccess"))
					{
						String displayName=resInfo.getJSONObject("data").getString("displayName");
						Long    userId=Long.parseLong(resInfo.getJSONObject("data").getString("userId"));
					    User user =new User(displayName, userId);
					    app.setLocalUser(user);		
					    MySharedPreferences.SaveShared(LoginActivity.this, "loginInfo", "email", email, false);
					    MySharedPreferences.SaveShared(LoginActivity.this, "loginInfo", "password", password, true);
						Intent intent = new Intent(LoginActivity.this,HomeActivity.class);				
					    startActivity(intent);	
					    finish();
					}
					else
					{
						publishProgress(resInfo.getString("message"));
					}
				} 
				catch (JSONException e) 
				{					
					e.printStackTrace();					
				}
		
				return null;
		}
		/**
		 * 处理出错信息
		 */
		 @Override
		  protected void onProgressUpdate(String ...message)
		  {
			 new MyToast().alert(getApplicationContext(),message[0]);			  
		  }
		 /**
		  * 取消忙状态
		  */
		  protected void onPostExecute(String s)
		  {
			  progressBar.setVisibility(View.INVISIBLE);
		  }
		
	}
}
