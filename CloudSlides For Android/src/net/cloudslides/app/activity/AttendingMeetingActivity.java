package net.cloudslides.app.activity;

import java.util.ArrayList;
import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.thirdlibs.widget.photoview.ZoomAbleViewPager;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import net.cloudslides.app.utils.MyVibrator;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class AttendingMeetingActivity extends Activity {

	private ZoomAbleViewPager zoomPager;
	private ArrayList<String> urls;
	private PlaySlidesPagerAdapter adapter;
	private String wsuri;
	private WebSocketConnection mConnection;	
	private int meetingPos;
	private long pptId;
	private long meetingId;
	private int currPageIndex;
	private CustomProgressDialog loadingDialog;
	private boolean close=false;
	private boolean isSuccess=false;//是否连接成功
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attending_meeting);
		setupView();
		initPptUrls();
		start();
		loadingDialog=CustomProgressDialog.createDialog(this, "正在连接服务器...", true);
		loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_light_green));
		loadingDialog.show();
	}
	/**
	 * 退出前停止图片加载的所有任务
	 * @author Felix
	 */
	@Override
	public void onPause() 
	{
		super.onPause();
		Log.i("onPause","stopImageLoader");
		ImageLoader.getInstance().stop();
	}

	@Override
    protected void onDestroy() 
	{
		super.onDestroy();
	    close=true;//人为断开socket
		if(mConnection.isConnected())
		{
			mConnection.disconnect();
		}
	    System.gc();
    }
	 @Override
	public void onConfigurationChanged(Configuration config)
	{
		 setContentView(R.layout.activity_attending_meeting);
	}
	private void setupView()
	{
		zoomPager = (ZoomAbleViewPager) findViewById(R.id.attending_meeting_viewpager);
	}

	private void initView()
	{
		adapter=new PlaySlidesPagerAdapter(urls,this);
		zoomPager.setAdapter(adapter);	
		zoomPager.setScrollEnabled(false);
		adapter.notifyDataSetChanged();	
	}
	/**
	 * 初始化PPT页面地址
	 * @author Felix
	 */
	private void initPptUrls()
	{
		meetingPos=getIntent().getIntExtra(Define.Intent_KEY_MEETING_POSITION, 0);
		meetingId=HomeApp.getLocalUser().getParticipatedMeeting().get(meetingPos).getMeetingId();
		pptId=HomeApp.getLocalUser().getParticipatedMeeting().get(meetingPos).getMeetingPpt().getPptId();
		urls=new ArrayList<String>();
		for(int i=1;i<=HomeApp.getLocalUser().getParticipatedMeeting().get(meetingPos).getMeetingPpt().getPptPageCount();i++)
		{
			String url =MyHttpClient.BASE_URL+"/ppt/pageImage?pptId="+pptId+"&page="+i
					+"&token="+HomeApp.getLocalUser().getToken()
					+"&uemail="+HomeApp.getLocalUser().getUserEmail();
			urls.add(url);			
		}
	}
	
	/**
	 * 启动webSocket
	 * @author Felix
	 */
	public void start()
	{
		mConnection = new WebSocketConnection();
		wsuri=MyHttpClient.WS_URL+"/viewWebsocket";
		try 
		{
			mConnection.connect(wsuri, new WebSocketHandler()
			{
				@Override
	            public void onOpen() 
				{			
					MyVibrator.doVibration(500);
					loadingDialog.dismiss();
					mConnection.sendTextMessage(meetingId+"");//发送会议请求
					MyToast.alert("连接成功");	 
					isSuccess=true;
	            }
				
	            @Override
	            public void onTextMessage(String payload) //接收会议操控端反馈
	            { 
	            	Log.i("socket返回",payload+"");
	            	if(adapter==null)
	            	{
	            		initView();
	            	}
	            	currPageIndex = getPage(payload);
	            	zoomPager.setCurrentItem(currPageIndex-1);
	            }

	            @Override
	            public void onClose(int code, String reason) 
	            {           
	            	if(close)
	            	{
	            		MyToast.alert("结束观看会议");
	 	               Log.i("onClose", reason+"");
	            	}
	            	else
	            	{
	            		if(isSuccess)
	            		{
	            			Toast.makeText(AttendingMeetingActivity.this,"网络中断,正在努力重新建立连接...",Toast.LENGTH_LONG).show();
	            			isSuccess=false;
	            		}
	            		new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								start();//网络因素导致socket断开则每1秒检查一次后重连
							}
						}, 1000);	            		
	            	}	               
	            }				
			});
		} 
		catch (WebSocketException e)
		{		
			e.printStackTrace();			
		}		
	}
	
	/**
	 * 根据返回信息提取页码
	 * @param message
	 * @return page
	 * @author Felix
	 */
	
	private int getPage(String message)
	{
		String temp[]=new String[2];
			   temp=message.split("-");		   
	    return Integer.parseInt(temp[1]);	
	}
}
