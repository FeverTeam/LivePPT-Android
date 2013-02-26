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
import android.widget.Toast;

public class PPT_Grid extends Activity implements OnClickListener {


	private Button ppt_butn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pptgridshow);
		ppt_butn = (Button) this.findViewById(R.id.ppt_butn);
		ppt_butn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String FILENAME = "Token";
		String httpUrl = "http://liveppt.net/login";
		SharedPreferences load = this.getSharedPreferences(FILENAME,Activity.MODE_PRIVATE);
		String username = load.getString("Username", "none");
		String password = load.getString("Password", "none");
		if (username.equals("none")||checkState(httpUrl,username,password)==false){
			Intent intent1 = new Intent (this,Login_UI.class); //��ת����¼����
			startActivity(intent1);
		}else if(checkState(httpUrl,username,password)==true){
			Intent intent2 = new Intent(this, PPT_upload.class);//��ת���ϴ�ҳ��
			startActivity(intent2);
			
		}
	}
	
	/*���û�����״̬�����ж�*/
	public boolean checkState(String HttpUrl, String regUsername,
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
			/*��������*/
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
				String info=checkInfo(result);
				if(info.equals("in indate"))
					return true;
				else 
					return false;
//				Toast.makeText(getApplicationContext(),info,
//						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "��������ʧ��!",
						Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(), "��������ʧ��!!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//�Է�����Ϣ�����ж�
	public String checkInfo(String info){
		String tip = null;
		if(info.equals("101"))
			tip="�û���������";
		else if(info.equals("102"))
			tip="�������";
		else if(info.equals("103"))
			tip="�û����Ѵ���";
		else if(info.equals("104"))
			tip="out of indate";
		else if(info.equals("105"))
			tip="in indate";
		return tip;
	}
	
	
}
