package fever.liveppt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		//String FILENAME = "UserInfo";
		SharedPreferences load = getSharedPreferences("UserInfo",Activity.MODE_PRIVATE);
		String username = load.getString("username", "none");
		String access_token = load.getString("access_token", "none");
		String expires_in = load.getString("expires_in", "none");
		Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
		if (username.equals("none")||checkState(username,access_token,expires_in)==false){
			Intent intent1 = new Intent (this,Login_UI.class); //跳转至登录界面
			startActivity(intent1);
		}else if(checkState(username,access_token,expires_in)==true){
			Intent intent2 = new Intent(this, PPT_upload.class);//跳转至上传页面
			startActivity(intent2);
		}
	}
	
	/*对用户在线状态进行判断*/
	public boolean checkState(String username,String access,String expires_in) {
		HttpPost httpResquest = new HttpPost("http://liveppt.net/login");
		HttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("username",username));
		param.add(new BasicNameValuePair("access_token",access));
		try {
			httpResquest.setEntity(new UrlEncodedFormEntity(param,
					HTTP.UTF_8));
			HttpResponse httpResponse = httpclient.execute(httpResquest);
			/*网络请求*/
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				String strResult = EntityUtils.toString(httpEntity);
				Toast.makeText(getApplicationContext(),strResult,
						Toast.LENGTH_LONG).show();
				return true;
			} else {
				Toast.makeText(getApplicationContext(), "网络连接失败!",
						Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(), "网络连接失败!!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}	
	
}
