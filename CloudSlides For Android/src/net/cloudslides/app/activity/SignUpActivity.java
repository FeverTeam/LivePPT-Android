package net.cloudslides.app.activity;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cloudslides.app.Define;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.utils.AESEnc;
import net.cloudslides.app.utils.MyActivityManager;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 注册页面
 * @author Felix
 *
 */
public class SignUpActivity extends Activity {
	private LinearLayout paperLayout;
	private EditText email;
	private EditText password;
	private EditText confirmPassword;
	private EditText displayName;
	private Button   signUpBtn;
	private ProgressBar proBar;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		MyActivityManager.getInstance().add(this);
		setUpView();
		init();
	}

	private void setUpView()
	{
		RelativeLayout.LayoutParams lp =new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		int marginH =(int)(Define.WIDTH_PX*0.02);
		int marginV =(int)(Define.HEIGHT_PX*0.02);
		lp.setMargins(marginH, marginV, marginH, marginV);
		paperLayout =(LinearLayout)findViewById(R.id.sign_up_paper_layout);		
		paperLayout.setLayoutParams(lp);
		
		email=(EditText)findViewById(R.id.sign_up_email_edit_text);
		password=(EditText)findViewById(R.id.sign_up_password_edit_text);
		confirmPassword=(EditText)findViewById(R.id.sign_up_confirm_password_edit_text);
		displayName=(EditText)findViewById(R.id.sign_up_display_name_edit_text);
		signUpBtn=(Button)findViewById(R.id.sign_up_button);
		proBar=(ProgressBar)findViewById(R.id.sign_up_pro_bar);
	}
	
	private void init()
	{
		signUpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isComplete())
				{
					MyToast.alert("请输入完整的注册信息");
				}
				else
					if(!isSame())
					{
						MyToast.alert("密码不一致，重新输入!");
					}else
						if(!isEmail())
						{
							MyToast.alert("Email地址无效!");
						}
						else
						{
							String seed = String.valueOf(System.currentTimeMillis() / 1000)+"000000";	
							String pswEnc= AESEnc.encryptAES(password.getText().toString().trim(),seed);
							proBar.setVisibility(View.VISIBLE);
							RequestParams params =new RequestParams();
							params.put(Param.UEMAIL, email.getText().toString().toLowerCase(Locale.getDefault()).trim());
							params.put(Param.SIGN_UP_PASSWORD,pswEnc);
							params.put(Param.SIGN_UP_DISPALY_NAME, displayName.getText().toString().trim());
							params.put(Param.SEED, seed);
							MyHttpClient.post("/user/register", params, new AsyncHttpResponseHandler(){

								@Override
								public void onSuccess(String response) {
									Log.i("注册返回:",response);
									try 
									{
										JSONObject jso =new JSONObject(response);
										MyToast.alert(jso.getString("message"));
										if(jso.getInt("retcode")==0)
										{										
											finish();
										}
										
									} catch (JSONException e) 
									{
										e.printStackTrace();
									}				                			              
					            }
								
								@Override
								public void onFailure(Throwable e) {
									e.printStackTrace();
									MyToast.alert("网络异常");								
								}
								
								@Override
								public void onFinish() {
									proBar.setVisibility(View.GONE);
								}

							});
						}													
			}
		});
	}	
	
	/**
	 * 判断两次密码输入是否相等
	 * @return T/F
	 * @author Felix
	 */
	private boolean isSame()
	{
		return password.getText().toString().trim()
				.equals(confirmPassword.getText().toString().trim());
	}
	
	
	
	/**
	 * 判断是否信息完整、非空
	 * @return T/F
	 * @author Felix
	 */	
	private boolean isComplete()
	{
		if(email.getText().toString().trim().equals(""))
		{
			return false;
		}
		if(password.getText().toString().trim().equals(""))
		{
			return false;
		}
		if(confirmPassword.getText().toString().trim().equals(""))
		{
			return false;
		}
		if(displayName.getText().toString().trim().equals(""))
		{
			return false;
		}
		return true;
	}
	/**
	 * 检查是否为邮箱
	 * @param email
	 * @return True/False
	 * @author Felix
	 */
	public boolean isEmail() 
	{
		String e =email.getText().toString().trim();
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(e);
		return m.matches();
	}
}
