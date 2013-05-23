package com.app.liveppt;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.base.User;
import com.app.liveppt.R;
import com.app.utils.HttpRequest;
import com.app.utils.MyToast;
import com.app.utils.myApp;
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


public class LoginActivity extends Activity
{
	private Button mLoginButton;
	private Button mRegistButton;
	private EditText emailInput;
	private EditText passwordInput;
	private ProgressBar progressBar;
	private static String loginUrl ="http://live-ppt.com/app/login";
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
		setContentView(R.layout.activity_login);		
		init();
	}	
	
	
	/**
	 * 登陆框控件初始化，事件触发
	 * @author Felix
	 */
	private void init()
	{		
		mLoginButton=(Button)findViewById(R.id.login_go_button);
		mRegistButton=(Button)findViewById(R.id.login_register_button);
		emailInput=(EditText)findViewById(R.id.email_input);
		passwordInput=(EditText)findViewById(R.id.password_input);
		progressBar =(ProgressBar)findViewById(R.id.loginProgressBar);
		
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
		
		mRegistButton.setOnClickListener(new OnClickListener() 
		{		
			/**
			 * 注册监听
			 */
			@Override
			public void onClick(View v) 
			{	
				 new MyToast().alert(getApplicationContext(),"--Coming soon--");								
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
			String strResult="";
			String url="";
			String email="";
			String password="";			
			JSONObject resInfo;			
			myApp app =(myApp)getApplication();	
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
						Intent intent = new Intent(LoginActivity.this,HomeActivity.class);				
					    startActivity(intent);		
					}
					else
					{
						publishProgress(resInfo.getJSONObject("data").getString("message"));
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
