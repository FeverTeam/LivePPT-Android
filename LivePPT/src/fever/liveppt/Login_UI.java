package fever.liveppt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login_UI extends Activity implements OnClickListener {

	private Button login, register,butLoginBack;
	private EditText Username=null,Password=null;
	private Builder builder;
	private TextView tvRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//锁定竖屏
		/**
		 * 国际化语言
		 */
		Resources resources = getResources();//
		Configuration config = resources.getConfiguration();//
		DisplayMetrics dm =resources.getDisplayMetrics();//
		config.locale = Locale.SIMPLIFIED_CHINESE; //中文作本地语言
		resources.updateConfiguration(config, dm);



		setContentView(R.layout.login_ui);
		RelativeLayout layout=(RelativeLayout)findViewById(R.id.progress_layout);
		layout.setVisibility(View.GONE);
		login = (Button) this.findViewById(R.id.login);
		butLoginBack=(Button )this.findViewById(R.id.btn_login_back);
		Username = (EditText) this.findViewById(R.id.et_username);
		Password = (EditText) this.findViewById(R.id.et_password);
		tvRegister=(TextView)this.findViewById(R.id.tv_register2);
		String registerText="立即注册";
		SpannableString spannableString=new SpannableString(registerText);
		spannableString.setSpan(new ClickableSpan() {
			
			@Override
			public void onClick(View widget) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Login_UI.this,RegisterActivity.class);
				startActivity(intent);
			}
		}, 0, registerText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvRegister.setText(spannableString);
		tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
		login.setOnClickListener(new loginListener());
		butLoginBack.setOnClickListener(this);
		
	}

	/**
	 * loginListener类
	 * @author Administrator
	 *
	 */
	public class loginListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(Login_UI.this.Username.getText().toString()
					.equals("")&&Login_UI.this.Password.getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "username and password can't be empty!", Toast.LENGTH_SHORT).show();
			}
			else if(Login_UI.this.Username.getText().toString()
					.equals("")) {
				Toast.makeText(getApplicationContext(),
						"username can't be empty!", Toast.LENGTH_SHORT).show();
			} else if(Login_UI.this.Password.getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "password can't be empty!", Toast.LENGTH_SHORT).show();
			}else if (!CheckUsername(Login_UI.this.Username)) {
				Toast.makeText(getApplicationContext(), "Email Address Error!", Toast.LENGTH_SHORT)
						.show();
			} else {				
			
				findViewById(R.id.progress_layout).setVisibility(View.VISIBLE);
				String httpUrl = "http://liveppt.net/login";
				String Username = Login_UI.this.Username.getText()
						.toString();
				String Password = Login_UI.this.Password.getText()
						.toString();
				Login(httpUrl, Username, Password);
			}
		}
		
		public void Login(String HttpUrl, String regUsername,
				String regPassword) {
			HttpPost httpResquest = new HttpPost(HttpUrl);
			HttpClient httpclient = new DefaultHttpClient();
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("username", regUsername));
			param.add(new BasicNameValuePair("password", regPassword));
			try {
				httpResquest.setEntity(new UrlEncodedFormEntity(param,
						HTTP.UTF_8));
				
				HttpResponse httpResponse = httpclient.execute(httpResquest);
				//HttpParams httpParameters = null;
				//
				// HttpConnectionParams.setConnectionTimeout(httpParameters, 3000); 

				/**
				 * 
				 */
				if (httpResponse.getStatusLine().getStatusCode() != 404) {
					HttpEntity httpEntity = httpResponse.getEntity();
					InputStream input = httpEntity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input, "utf-8"));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					input.close();
					String result = sb.toString();
					Toast.makeText(getApplicationContext(), result,
							Toast.LENGTH_LONG).show();	
							
					Intent intent2=new Intent(Login_UI.this,PPT_upload.class);
					startActivity(intent2);
					Login_UI.this.finish();   

					
				}else{
					//System.out.println("��¼ʧ�ܣ�");
					Toast.makeText(getApplicationContext(), "登录失败!!", Toast.LENGTH_LONG).show();
					findViewById(R.id.progress_layout).setVisibility(View.GONE);
				}
			
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "连接失败!!", Toast.LENGTH_LONG).show();
				e.printStackTrace();
				findViewById(R.id.progress_layout).setVisibility(View.GONE);
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "网络错误!!", Toast.LENGTH_LONG).show();
				findViewById(R.id.progress_layout).setVisibility(View.GONE);
				

			}
		}
	}
	// 
	public boolean CheckUsername(EditText editName) {
		String name = editName.getText().toString();// 
		if (name.matches("\\w+@\\w+\\.\\w+"))
			return true;
		else
			return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login_back://
		
			this.finish();
			break;
		default:
			break;
		}
	}
		
}
