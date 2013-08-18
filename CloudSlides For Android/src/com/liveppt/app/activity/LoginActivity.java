package com.liveppt.app.activity;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.liveppt.app.Define;
import com.liveppt.app.HomeApp;
import com.liveppt.app.R;
import com.liveppt.app.model.User;
import com.liveppt.app.utils.MyActivityManager;
import com.liveppt.app.utils.MyHttpClient;
import com.liveppt.app.utils.MySharedPreferences;
import com.liveppt.app.utils.MyToast;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 登录界面
 * @author Felix
 *
 */
public class LoginActivity extends Activity {
	private EditText emailEdt;
	private EditText passwordEdt;
	private Button   loginBtn;
	private CheckBox rememberCheck;
	private CheckBox autoLoginCheck;
	private ProgressDialog progressDialog;
	private String email;
	private String password;
	private int jumpFlag;//标记登录成功后跳转到哪
	private boolean ignoreCheck=false;//是否无视checkbox

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		MyActivityManager.getInstance().add(this);
		jumpFlag=getIntent().getIntExtra("Goto", 0);
		setupView();
		initView();
		//已经登录并且设置了自动登录时，自动登录
		if(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_HAS_LOGINED_KEY, false).equals("true")
		   &&MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, false).equals("true"))			
		{
			loginBtn.performClick();
			autoLoginCheck.setChecked(true);
		}
	}
	/**
	 * 绑定视图
	 * @author Felix
	 */
	private void setupView()
	{
		emailEdt=(EditText)findViewById(R.id.login_email_edit_text);
		passwordEdt=(EditText)findViewById(R.id.login_password_edit_text);
		loginBtn=(Button)findViewById(R.id.login_button);
		rememberCheck=(CheckBox)findViewById(R.id.remember_checkbox);
		autoLoginCheck=(CheckBox)findViewById(R.id.auto_login_checkBox);
		
	}
	/**
	 * 初始化视图逻辑
	 * @author Felix
	 */
	private void initView()
	{
		if(MySharedPreferences.getShared(Define.CONFINFO,Define.LOCAL_USER_INFO_IS_REMEMBER_ME, false).equals("true"))
		{
			emailEdt.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_EMAIL_KEY, false));
			passwordEdt.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_PASSWORD_KEY, true));
			rememberCheck.setChecked(true);			
		}
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_HAS_LOGINED_KEY, false).equals("true")
				   &&MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, false).equals("true"))
				{
					email=MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_EMAIL_KEY, false);
					password=MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_PASSWORD_KEY, true);
					emailEdt.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_EMAIL_KEY, false));
					passwordEdt.setText(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_PASSWORD_KEY, true));
					ignoreCheck=true;
				}
				else
				{
					email=emailEdt.getText().toString().trim();				
				    password=passwordEdt.getText().toString().trim();					
				}
				doLogin();
			}
		});
	}
	/**
	 * 登录操作
	 * @author Felix
	 */
	private void doLogin()
	{   
		String url = "/app/login?"+"email="+email+"&"+"password="+password;
		MyHttpClient.get(url, null, new AsyncHttpResponseHandler(){
			@Override
			public void onStart()
			{
				progressDialog=ProgressDialog.show(LoginActivity.this,"","登录中...", true, false);
			}
			@Override
			public void onSuccess(String response)//请求成功
			{
				progressDialog.dismiss();
				try
				{
					JSONObject jso=new JSONObject(response);
					if(!jso.getBoolean("isSuccess"))//登录失败
					{
						MyToast.alert(jso.getString("message"));
					}
					else//登录成功
					{
						parseJsonToUser(response);
						MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_HAS_LOGINED_KEY, "true", false);
						MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_EMAIL_KEY, email, false);
						MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_PASSWORD_KEY, password, true);
						
						if(!rememberCheck.isChecked()&&!ignoreCheck)
						{
							MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_IS_REMEMBER_ME, "false", false);				
						}
						else
							if(rememberCheck.isChecked()&&!ignoreCheck)
							{
								MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_IS_REMEMBER_ME, "true", false);				
							}
						
						
						
						if(autoLoginCheck.isChecked())
						{
							MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, "true", false);
							
						}
						else
						{
							MySharedPreferences.SaveShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, "false", false);
						}
						jump();
					}			
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}			
				
			}
			@Override
		     public void onFailure(Throwable e, String response) 
			{
				e.printStackTrace();
				MyToast.alert("网络异常,登录失败!");
				Log.i("登录onFailure", response);
		     }
		    @Override
		    public void onFinish()
		    {
		    	progressDialog.dismiss();
		    }
			
		});
		
	}
	
	/**
	 * 解析JSON为User对象，保存至本地
	 * @param response 登录成功返回的JSON字符串
	 * @author Felix
	 */
	private void parseJsonToUser(String response)
	{
		User user =new User();
		try 
		{
			JSONObject jso =new JSONObject(response);
			user.setUserId(jso.getJSONObject("data").getLong("userId"));
			user.setUserEmail(jso.getJSONObject("data").getString("email"));
			user.setUserName(jso.getJSONObject("data").getString("displayName"));
			HomeApp.setLocalUser(user);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 判断跳转到哪个页面
	 * @author Felix
	 */
	private void jump()
	{
		switch(jumpFlag)
		{
		case Define.LOGIN_JUMP_PPT: {MyToast.alert("Goto PPT");}break;
		
		case Define.LOGIN_JUMP_ATTENDING:{MyToast.alert("Goto Attending");}break;
		
		case Define.LOGIN_JUMP_FOUNDING:{MyToast.alert("Goto Founding");}break;
		}		
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}
