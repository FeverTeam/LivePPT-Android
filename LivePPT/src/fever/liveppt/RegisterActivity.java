package fever.liveppt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener{
	/**
	 * 构造函数
	 */
	public RegisterActivity() {
		// TODO Auto-generated constructor stub
	}

	private Button btnRegister,registerBack;
	private EditText regUsername = null;
	private EditText regPassword = null;
	private static String Username;
	private static String Password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//
		setContentView(R.layout.register_ui);
		btnRegister = (Button) this.findViewById(R.id.btn_register);
		registerBack=(Button)this.findViewById(R.id.btn_register_back);
		btnRegister.setOnClickListener(new registerListener());
		registerBack.setOnClickListener(this);
		regUsername=(EditText)this.findViewById(R.id.register_username);
		regPassword=(EditText)this.findViewById(R.id.register_password);
		Username = this.regUsername.getText().toString();
		Password = this.regPassword.getText().toString();

	}

	public class registerListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (RegisterActivity.this.regUsername.getText().toString()
					.equals("")) {
				Toast.makeText(getApplicationContext(),
						"password can't be empty!", Toast.LENGTH_SHORT).show();
			} else if (!CheckUsername(RegisterActivity.this.regUsername)) {
				Toast.makeText(getApplicationContext(), "Email Address Error!", Toast.LENGTH_SHORT)
						.show();
			} else {
				String httpUrl = "http://liveppt.net/register";
				String Username = RegisterActivity.this.regUsername.getText()
						.toString();
				String Password = RegisterActivity.this.regPassword.getText()
						.toString();
				Register(httpUrl, Username, Password);
			}
		}
		
		public void Register(String HttpUrl, String regUsername,
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
				Toast.makeText(getApplicationContext(), "linked...",
						Toast.LENGTH_SHORT).show();
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
					
					Intent intent4=new Intent(RegisterActivity.this,PPT_upload.class);
					startActivity(intent4);
				}else{
					Toast.makeText(getApplicationContext(), "注册失败!!", Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "连接失败!!", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "网络错误!!", Toast.LENGTH_LONG).show();

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
		this.finish();
	}

}
