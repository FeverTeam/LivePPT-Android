package fever.liveppt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	String status=null,access_token=null;
	
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
			if (RegisterActivity.this.regUsername.getText().toString().equals("")
					&& RegisterActivity.this.regPassword.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"username and password can't be empty!",
						Toast.LENGTH_SHORT).show();
			} else if (RegisterActivity.this.regUsername.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"username can't be empty!", Toast.LENGTH_SHORT).show();
			} else if (RegisterActivity.this.regPassword.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"password can't be empty!", Toast.LENGTH_SHORT).show();
			} else if (!CheckUsername(RegisterActivity.this.regUsername)) {
				Toast.makeText(getApplicationContext(), "Email Address Error!",
						Toast.LENGTH_SHORT).show();
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
				 * 网络请求
				 */
				if ((httpResponse.getStatusLine().getStatusCode() ==HttpStatus.SC_OK)||
						(httpResponse.getStatusLine().getStatusCode() ==400)) {
					HttpEntity httpEntity = httpResponse.getEntity();
					String strResult = EntityUtils.toString(httpEntity);
					String info = checkInfo(Json(strResult));
					Toast.makeText(getApplicationContext(), info,
							Toast.LENGTH_LONG).show();
					if(status.equals("0k"))
						JumpToUpload();
				}else{
					Toast.makeText(getApplicationContext(), "连接失败!", Toast.LENGTH_LONG).show();
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
	
	//对返回的JSON串的strResult对象进行解析
		public String Json(String res){
			try{
				JSONTokener jsonParser = new JSONTokener(res);
				JSONObject js = (JSONObject)jsonParser.nextValue();
				status = js.getString("status");
				if (status.equals("ok")){
					access_token = js.getString("access_token");
				}
				return status;
			}catch(JSONException e){
				Toast.makeText(getApplicationContext(),"解析出错！",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
				return null;
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
		Intent intent2 = new Intent(RegisterActivity.this,PPT_upload.class);
		startActivity(intent2);
	}
	
	//对返回信息进行判断
			public String checkInfo(String info){
					String tip = null;
					if(info.startsWith("101"))
						tip="密码错误";
					else if(info.startsWith("102"))
						tip="用户不存在";
					else if(info.startsWith("103"))
						tip="用户名已存在";
					else if(info.startsWith("104"))
						tip="输入为空";
					else if(info.startsWith("ok"))
						tip="注册成功";
					return tip;
				}
}
