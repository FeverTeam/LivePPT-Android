package net.cloudslides.app.activity;

import java.util.ArrayList;
import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyToast;
import net.cloudslides.app.widget.photoview.ZoomAbleViewPager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attending_meeting);
		setupView();
		initPptUrls();
		start();
		loadingDialog=CustomProgressDialog.createDialog(this, "正在连接服务器...", false);
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
		if(mConnection.isConnected())
		{
			mConnection.disconnect();
		}
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
			String url ="http://live-ppt.com/getpptpage?pptid="+pptId+"&pageid="+i;
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
		wsuri="ws://live-ppt.com:9000/viewWebsocket";		
		try 
		{
			mConnection.connect(wsuri, new WebSocketHandler()
			{
				@Override
	            public void onOpen() 
				{				
					loadingDialog.dismiss();
					mConnection.sendTextMessage(meetingId+"");//发送会议请求
					MyToast.alert("正在进入会议...");	               
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
	               MyToast.alert("会议结束");
	               Log.i("onClose", reason+"");
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
