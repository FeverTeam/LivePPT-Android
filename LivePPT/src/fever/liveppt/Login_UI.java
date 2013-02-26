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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login_UI extends Activity implements OnClickListener {

	private Button login, register, butLoginBack;
	private EditText Username = null, Password = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_ui);
		login = (Button) this.findViewById(R.id.login);
		register = (Button) this.findViewById(R.id.register);
		butLoginBack = (Button) this.findViewById(R.id.btn_login_back);
		Username = (EditText) this.findViewById(R.id.et_username);
		Password = (EditText) this.findViewById(R.id.et_password);
		login.setOnClickListener(new loginListener());
		butLoginBack.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	/**
	 * 登录按钮监听
	 * 
	 * @author Administrator
	 * 
	 */
	public class loginListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (Login_UI.this.Username.getText().toString().equals("")
					&& Login_UI.this.Password.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"username and password can't be empty!",
						Toast.LENGTH_SHORT).show();
			} else if (Login_UI.this.Username.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"username can't be empty!", Toast.LENGTH_SHORT).show();
			} else if (Login_UI.this.Password.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"password can't be empty!", Toast.LENGTH_SHORT).show();
			} else if (!CheckUsername(Login_UI.this.Username)) {
				Toast.makeText(getApplicationContext(), "Email Address Error!",
						Toast.LENGTH_SHORT).show();
			} else {
				String httpUrl = "http://liveppt.net/login";
				String Username = Login_UI.this.Username.getText().toString();
				String Password = Login_UI.this.Password.getText().toString();
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
			param.add(new BasicNameValuePair("indate","7"));
			try {
				httpResquest.setEntity(new UrlEncodedFormEntity(param,
						HTTP.UTF_8));
				HttpResponse httpResponse = httpclient.execute(httpResquest);
				/*网络请求*/
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
					//将返回的token存储到SharedPreferences中
					String info=checkInfo(result);
					Toast.makeText(getApplicationContext(),info,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "连接失败!",
							Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "连接失败!!",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "网络连接错误!!",
						Toast.LENGTH_LONG).show();
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
	
	/**
	 * 将服务器端返回的记录信息存储到SharedPreferences中
	 * @param token
	 */
	public void saveToken(String username,String password,String token) {
		final String FILENAME="Token";
		SharedPreferences share = this.getSharedPreferences(FILENAME,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = share.edit();
		edit.putString("Username", username);
		edit.putString("Password", password);
		edit.commit();//提交更新
	}
	
	public String loadToken(String Path){
		String info;
		SharedPreferences share = this.getSharedPreferences(Path, Activity.MODE_PRIVATE);
		info = share.getString("token","No record");
		return info;
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
		return tip;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login_back:// 点击按钮
			Intent intent1 = new Intent(this, PPT_upload.class);// 新意图
			startActivity(intent1);
			break;
		case R.id.register:// 点击ImageSwitcher按钮
			Intent intent2;
			intent2 = new Intent();
			intent2.setClass(this, RegisterActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

}
