package com.app.liveppt;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.liveppt.R;
import com.app.utils.HttpRequest;
import com.app.utils.MyToast;
import com.app.utils.MyApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;

import com.app.fragment.MyMeetingFrag;
import com.app.fragment.MyPptListFrag;


/* test */
public class HomeActivity extends FragmentActivity
{
	
	private TabHost mTabHost;
	private Button  refresh;
	private Button  join;
	private MyPptListFrag pptfrag ;
	private String meetingId;
	private MyApp  app;
	private HttpRequest httpRequest;
	private MyMeetingFrag meetingfrag;
	
	private static String TAB1 ="PPT";
	private static String TAB2 ="会议";
	private static String TAB3 ="帐号";
	private static String TAB4 ="更多";	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initTabHost();	
	}
	
	/**
	 * 初始化TabHost
	 * @author Felix
	 */
	private void initTabHost()
	{
		httpRequest =new HttpRequest();
		app =(MyApp)getApplication();
		pptfrag=(MyPptListFrag) getSupportFragmentManager().findFragmentById(R.id.pptListFrag);
		refresh=(Button)findViewById(R.id.refreshBt);
        refresh.setOnClickListener(new OnClickListener() 
        {			
        	/**
        	 * 刷新所有列表（包括会议列表和PPT列表）
        	 */
			@Override
			public void onClick(View v) 
			{				
				pptfrag.refresh();
				meetingfrag=(MyMeetingFrag)(HomeActivity.this).getSupportFragmentManager().findFragmentById(R.id.meetingFrag);
				meetingfrag.refresh();
				
			}
		});   
        
        
        join=(Button)findViewById(R.id.join_button);
        join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final View dialogView=LayoutInflater.from(HomeActivity.this).inflate(R.layout.join_meeting_dialog, null);
				final EditText meetingId_text=(EditText)dialogView.findViewById(R.id.meeting_ID_joinmeeting);
				AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
				.setTitle("加入会议")
				.setView(dialogView)
				.setPositiveButton("加入",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						meetingId=meetingId_text.getText().toString().trim();
						new joinNewMeeting().execute();
					}
				})
				.create();
				dialog.show();	
			}
		});
        
        
        
        
        
		mTabHost=(TabHost)findViewById(android.R.id.tabhost);		
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec(TAB1).setIndicator("",getResources().getDrawable(R.drawable.ppt_icon_selector)).setContent(R.id.tab1));
		mTabHost.addTab(mTabHost.newTabSpec(TAB2).setIndicator("",getResources().getDrawable(R.drawable.meeting_icon_selector)).setContent(R.id.tab2));
		mTabHost.addTab(mTabHost.newTabSpec(TAB3).setIndicator("",getResources().getDrawable(R.drawable.account_icon_selector)).setContent(R.id.tab3));
		mTabHost.addTab(mTabHost.newTabSpec(TAB4).setIndicator("",getResources().getDrawable(R.drawable.more_icon_selector)).setContent(R.id.tab4));
	
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) 
		{
			mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
		}
		    mTabHost.setCurrentTab(0);
	}	
	
	
	
	/**
	 * 加入会议线程
	 * @author Felix 
	 */
	
	class joinNewMeeting extends AsyncTask<Void, Void,Boolean>
	{

		@Override
		protected Boolean doInBackground(Void... params) 
		{
			String Url =HttpRequest.httpProtocol+HttpRequest.hostName+"/app/joinMeeting";
			JSONObject jso;
			String strResult;
			ArrayList<NameValuePair> paramList =new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("meetingId",meetingId));	
			paramList.add(new BasicNameValuePair("userId", app.getLocalUser().getUserId().toString()));
			strResult=httpRequest.HttpPostRequest(app.getHttpClient(), Url, paramList);
			Log.i("加入会议", strResult);
			
			try
			{
				jso=new JSONObject(strResult);
				if(jso.getBoolean("isSuccess"))
				{
					return true;
				}
				else
				{
					return false;
				}
			} catch (JSONException e) 
			{				
				e.printStackTrace();
			}	
			return false;
			
		}
		
		@Override
		protected void onPostExecute(Boolean flag)
		{
			if(flag)
			{
				meetingfrag=(MyMeetingFrag)(HomeActivity.this).getSupportFragmentManager().findFragmentById(R.id.meetingFrag);
				meetingfrag.refresh();
				new MyToast().alert(HomeActivity.this, "成功加入会议");
			}
			else
			{
				new MyToast().alert(HomeActivity.this, "加入失败，请重试");
			}
		}
		
	}
	
}
