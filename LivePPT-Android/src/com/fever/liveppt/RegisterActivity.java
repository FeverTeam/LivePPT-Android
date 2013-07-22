package com.fever.liveppt;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.fever.utils.HttpRequest;
import com.fever.utils.MyToast;
import com.fever.utils.MyApp;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 注册账号页面类
 * @author Felix
 */
public class RegisterActivity extends Activity {
	private EditText email;
	private EditText passWord;
	private EditText userName;
	private Button   reg_btn;
	private MyApp app;
	private HttpRequest httpRequest;
	private String   Url;


    /**
     * 创建界面
     * @param savedInstanceState
     * last modified: Frank
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		init();
	}


	/**
	 * 初始化控件
     * last modified: Frank
	 */
	private void init(){
		app=(MyApp)getApplication();
		httpRequest=new HttpRequest();
		Url=HttpRequest.httpProtocol+HttpRequest.hostName+"/app/register";
		email=(EditText)findViewById(R.id.email_edit_register);
		passWord=(EditText)findViewById(R.id.password_edit_register);
		userName=(EditText)findViewById(R.id.name_edit_register);
		reg_btn=(Button)findViewById(R.id.register_button);
		reg_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(email.getText().toString().trim().isEmpty()||passWord.getText().toString().trim().isEmpty()||userName.getText().toString().trim().isEmpty()){
					new MyToast().alert(getBaseContext(), "--请填写完整的注册信息--");
				}
				else{
					new registerTask().execute();					
				}
			}
		});
	}
	
	/**
	 * 注册异步线程
	 * @author Felix
	 *
	 */
	class registerTask extends AsyncTask<Void,String,Void>{

        /**
         * 执行注册异步线程
         * @param params
         * @return
         * last modified: Frank
         */
		@Override
		protected Void doInBackground(Void... params){
			String strResult;
			JSONObject jso;
			ArrayList<NameValuePair> paramList=new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("email", email.getText().toString()));
			paramList.add(new BasicNameValuePair("password", passWord.getText().toString()));
			paramList.add(new BasicNameValuePair("displayName", userName.getText().toString()));
			
			strResult=httpRequest.HttpPostRequest(app.getHttpClient(), Url, paramList);
			try {
				jso=new JSONObject(strResult);
				publishProgress(jso.getString("message"));
				if(jso.getBoolean("isSuccess")){
					Intent intent =new Intent(RegisterActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
				
				
			}
            catch (JSONException e){
				e.printStackTrace();
			}
		
			return null;
		}


        /**
         * 执行注册线程后显示注册成功的信息
         * @param message
         */
		@Override
		protected void onProgressUpdate(String... message){
			new MyToast().alert(getApplication(), message[0]);
		}
	}

}
