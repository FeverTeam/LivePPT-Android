package net.cloudslides.app.activity;
import java.util.Locale;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.model.User;
import net.cloudslides.app.utils.HmacSha1Signature;
import net.cloudslides.app.utils.MyActivityManager;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MySharedPreferences;
import net.cloudslides.app.utils.MyToast;
import net.cloudslides.app.utils.MyVibrator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
	private TextView signUp;
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
		signUp = (TextView)findViewById(R.id.login_sign_up_text);
		
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
				if(!checkIfComplete())
				{
					return;
				}
				if(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_HAS_LOGINED_KEY, false).equals("true")
				   &&MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_AUTO_LOGIN, false).equals("true")
				   &&emailEdt.getText().toString().trim().equals(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_EMAIL_KEY, false))
				   &&passwordEdt.getText().toString().trim().equals(MySharedPreferences.getShared(Define.CONFINFO, Define.LOCAL_USER_INFO_PASSWORD_KEY, true)))
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
		signUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
				startActivity(intent);
				MyVibrator.doVibration(50);
				finish();
			}
		});
	}
	
	/**
	 * 检查登录信息是否完整
	 * @return 是否为空
	 * @author Felix
	 */
	private boolean checkIfComplete()
	{
		if(emailEdt.getText().toString().trim().equals("")||passwordEdt.toString().trim().equals(""))
		{
			MyToast.alert("请填写完整的登录信息");
			return false;
		}
		return true;
		
	}
	/**
	 * 登录操作
	 * @author Felix
	 */
	private void doLogin()
	{   
		 String seed = String.valueOf(System.currentTimeMillis() / 1000);
		String pswEnc= HmacSha1Signature.encryptHMAC_SHA(password,seed);
		RequestParams params = new RequestParams();
		params.put(Param.UEMAIL,email.toLowerCase(Locale.getDefault()).trim());
		params.put(Param.PASSWORD,pswEnc);
		params.put(Param.SEED,seed);
		
		String url = "/user/login";
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler(){
			@Override
			public void onStart()
			{
				progressDialog=ProgressDialog.show(LoginActivity.this,"","登录中...", true, false);
			}
			@Override
			public void onSuccess(String response)//请求成功
			{
				Log.i("登陆返回:",response+"");
				try
				{
					JSONObject jso=new JSONObject(response);
					if(jso.getInt("retcode")!=0)//登录失败
					{
						MyToast.alert(jso.getInt("retcode"));
						progressDialog.dismiss();
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
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
						    	progressDialog.dismiss();
								jump();								
							}
						}, 1000);
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
		    	progressDialog.dismiss();
				e.printStackTrace();
				MyToast.alert("网络异常,登录失败!");
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
			user.setUserEmail(email.toLowerCase(Locale.getDefault()).trim());
			user.setUserName(jso.getJSONObject("data").getString(Param.DISPLAY_NAME_KEY));
			user.setToken(jso.getJSONObject("data").getString(Param.TOKEN));
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
		Intent intent = new Intent(this, MainActivity.class);
		switch(jumpFlag)
		{
		case Define.LOGIN_JUMP_PPT: {intent.putExtra("content",Define.LOGIN_JUMP_PPT);}break;
		
		case Define.LOGIN_JUMP_ATTENDING:{intent.putExtra("content",Define.LOGIN_JUMP_ATTENDING);}break;
		
		case Define.LOGIN_JUMP_FOUNDING:{intent.putExtra("content",Define.LOGIN_JUMP_FOUNDING);}break;
		}		
		startActivity(intent);
		finish();
	}
}
