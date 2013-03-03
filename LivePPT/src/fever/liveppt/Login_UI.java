package fever.liveppt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login_UI extends Activity implements OnClickListener {

	private Button login, register, butLoginBack;
	private EditText Username = null, Password = null;
	String status = null;
	private CheckBox savePass = null;
	private SharedPreferences share=null;

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
		savePass = (CheckBox) this.findViewById(R.id.savePass);
		
		login.setOnClickListener(new loginListener());
		butLoginBack.setOnClickListener(this);
		register.setOnClickListener(this);
	}

	//��¼��ť����
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
				LoginTask login = new LoginTask(Username,Password);
				login.execute(httpUrl);
				Toast.makeText(getApplicationContext(), httpUrl,
						Toast.LENGTH_LONG).show();
			}
		}
		/*
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
				
				if ((httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)||
						httpResponse.getStatusLine().getStatusCode()==400) {
					HttpEntity httpEntity = httpResponse.getEntity();
					String str = EntityUtils.toString(httpEntity);
					String strResult = new String(str.getBytes(),"UTF-8");
					//�������ص�JSON����,��¼�ɹ���������û���Ϣ
					String info = checkInfo(Json(strResult));
					Toast.makeText(getApplicationContext(),info,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "����ʧ��!",
							Toast.LENGTH_LONG).show();
				}
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "����ʧ��!!",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "IO����!!"+e.toString(),
						Toast.LENGTH_LONG).show();
			} catch (Exception e){
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Other����!!"+e.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
		*/
	}

	private class LoginTask extends AsyncTask<String,Void,String>{
		
		String username, password,status=null;
		public LoginTask(String regUsername, String regPassword) {
			username = regUsername;
			password = regPassword;
		}
		@Override
		protected String doInBackground(String... url) {
			// TODO Auto-generated method stub
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpRequest = new HttpPost(url[0]);
			Toast.makeText(getApplicationContext(), url[0],
					Toast.LENGTH_LONG).show();
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("username",username));
			param.add(new BasicNameValuePair("password",password));
			
			try{
				httpRequest.setEntity(new UrlEncodedFormEntity(param,HTTP.UTF_8));
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				if((httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)||
						httpResponse.getStatusLine().getStatusCode()==400){
					HttpEntity httpEntity = httpResponse.getEntity();
					String str = EntityUtils.toString(httpEntity);
					String strResult = new String(str.getBytes(),"UTF-8");
					status = Json(strResult);
				}
			}catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "�������!!",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "IO����!!"+e.toString(),
						Toast.LENGTH_LONG).show();
			} catch (Exception e){
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Other����!!"+e.toString(),
						Toast.LENGTH_LONG).show();
			}
			return status;
		}
		@Override
		 protected void onPostExecute(String result){
				String info = checkInfo(result);
				Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
//				if(result.startsWith("ok")){
//					JumpToUpload();
		 }
	}
	
	//�Է��ص�JSON����strResult������н���,����¼�ɹ���洢�����Ϣ
	public String Json(String res){
		try{
			JSONTokener jsonParser = new JSONTokener(res);
			JSONObject js = (JSONObject)jsonParser.nextValue();
			status = js.getString("status");
			//��¼�ɹ���洢�����Ϣ
			if (status.equals("ok")){
				String access_token = js.getString("access_token");
				String expires_in = js.getString("expires_in");
				share = getSharedPreferences("UserInfo",Activity.MODE_PRIVATE);
				SharedPreferences.Editor edit = share.edit();
				edit.putBoolean("IsSave", false);
				edit.putString("username",Username.getText().toString());
				edit.putString("access_token", access_token);
				edit.putString("expire_in", expires_in);
				if(savePass.isChecked()){
					edit.putBoolean("IsSave", true);
					edit.putString("password", Password.getText().toString());
					savePass.setChecked(true);
				}
				edit.commit();
			}
			return status;
		}catch(JSONException e){
			Toast.makeText(getApplicationContext(),"��������"+e.toString(),
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return null;
		}
	}
	
	// �ж��û������������ַ����ʽ�Ƿ���ȷ
	public boolean CheckUsername(EditText editName) {
		String name = editName.getText().toString();// ȡ�����������
		if (name.matches("\\w+@\\w+\\.\\w+"))
			return true;
		else
			return false;
	}
	
	//��ת���ϴ�PPTҳ��
	public void JumpToUpload(){
		Intent intent = new Intent(Login_UI.this,PPT_upload.class);
		startActivity(intent);
	}
	
	//�Է�����Ϣ�����ж�
	public String checkInfo(String info){
				String tip = null;
				if(info.startsWith("101"))
					tip="�������";
				else if(info.startsWith("102"))
					tip="�û�������";
				else if(info.startsWith("103"))
					tip="�û����Ѵ���";
				else if(info.startsWith("104"))
					tip="����Ϊ��";
				else if(info.startsWith("ok"))
					tip="��¼�ɹ�";
				return tip;
			}
	
	//ҳ�水ť����
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login_back:// �����ť
			Intent intent1 = new Intent(this, PPT_upload.class);// ����ͼ
			startActivity(intent1);
			break;
		case R.id.register:// ���ImageSwitcher��ť
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
