package fever.liveppt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class PPT_Grid extends Activity implements OnItemSelectedListener,OnClickListener,OnItemClickListener {

	public PPT_Grid() {
		// TODO Auto-generated constructor stub
	}

	private Button ppt_butn;

	private ImageView imvGrid;
	private int[] resIds=new int[]{
			R.drawable.dog,R.drawable.rabbit
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//锁定竖屏
		setContentView(R.layout.pptgridshow);
		ppt_butn = (Button) this.findViewById(R.id.ppt_butn);
		ppt_butn.setOnClickListener(this);
		GridView grv=(GridView)this.findViewById(R.id.ppt_gridview);
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		
		for(int i=0;i<resIds.length;i++){
			Map<String,Object> img=new HashMap<String,Object>();
			img.put("imageview", resIds[i]);
			list.add(img);
		}
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, 
				list, R.layout.ppt_gridimg,
				new String[]{"imageview"}, new int[]{R.id.imv });
		
		//ListAdapter simpleAdapter = null;
		grv.setAdapter(simpleAdapter);
		imvGrid=(ImageView)findViewById(R.id.imv);
		grv.setOnItemClickListener(this);
		grv.setOnItemSelectedListener(this);
		imvGrid.setImageResource(resIds[0]);

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
			Intent intent1 = new Intent (this,Login_UI.class); //
			startActivity(intent1);
		}else if(checkState(httpUrl,username,password)==true){
			Intent intent2 = new Intent(this, PPT_upload.class);//
			startActivity(intent2);
			
		}
	}

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
			/* */
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
				Toast.makeText(getApplicationContext(), "连接错误！",
						Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (ClientProtocolException e) {
			Toast.makeText(getApplicationContext(), "协议出错!!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//
	public String checkInfo(String info){
		String tip = null;
		if(info.equals("101"))
			tip="error:101";
		else if(info.equals("102"))
			tip="error:102";
		else if(info.equals("103"))
			tip="error:103";
		else if(info.equals("104"))
			tip="out of indate";
		else if(info.equals("105"))
			tip="in indate";
		return tip;
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		imvGrid.setImageResource(resIds[position]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {
		// TODO Auto-generated method stub
		imvGrid.setImageResource(resIds[position]);
	}
	
	
}
