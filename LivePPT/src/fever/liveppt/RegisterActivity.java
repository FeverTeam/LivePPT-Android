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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_ui);
		btnRegister = (Button) this.findViewById(R.id.btn_register);
		registerBack=(Button)this.findViewById(R.id.btn_register_back);
		btnRegister.setOnClickListener(new registerListener());
		registerBack.setOnClickListener(this);
		regUsername=(EditText)this.findViewById(R.id.register_username);
		regPassword=(EditText)this.findViewById(R.id.register_password);
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
			//param.add(new BasicNameValuePair("indate","7"));
			try {
				httpResquest.setEntity(new UrlEncodedFormEntity(param,
						HTTP.UTF_8));
				HttpResponse httpResponse = httpclient.execute(httpResquest);
				Toast.makeText(getApplicationContext(), "linked...",
						Toast.LENGTH_SHORT).show();
				/**
				 * 网络请求
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
					String info = checkInfo(sb.toString());
					Toast.makeText(getApplicationContext(), sb.toString(),
							Toast.LENGTH_LONG).show();
					if(sb.toString().equals("注册成功！"))
						JumpToUpload();
				}else{
					Toast.makeText(getApplicationContext(), "连接失败!!", Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "连接失败!!", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "网络连接错误!!", Toast.LENGTH_LONG).show();

			}
		}
	}
	// 判断用户名（即邮箱地址）格式是否正确
	public boolean CheckUsername(EditText editName) {
		String name = editName.getText().toString();// 取得输入的内容
		if (name.matches("\\w+@\\w+\\.\\w+"))
			return true;
		else
			return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this,Login_UI.class);
		startActivity(intent);
	}
	
	//JumpToUpload
	public void JumpToUpload(){
		Intent intent2 = new Intent(this,Login_UI.class);
		startActivity(intent2);
	}
	
	//对返回信息进行判断
		public String checkInfo(String info){
			String tip = null;
			if(info.equals("101"))
				tip="用户名不存在";
			else if(info.equals("102"))
				tip="密码错误";
			else if(info.equals("103"))
				tip="用户名已存在";
			else if(info.equals("104"))
				tip="out of indate";
			else if(info.equals("105"))
				tip="in indate";
			else if(info.equals("106"))
				tip="Register successfully!";
			return tip;
		}
		

}
