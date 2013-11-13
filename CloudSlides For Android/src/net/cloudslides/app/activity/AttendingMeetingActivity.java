package net.cloudslides.app.activity;

import java.util.ArrayList;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.adapter.CommunicationBoxAdapter;
import net.cloudslides.app.custom.widget.MultiDirectionSlidingDrawer;
import net.cloudslides.app.model.ChatData;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoView.onZoomViewListener;
import net.cloudslides.app.thirdlibs.widget.photoview.ZoomAbleViewPager;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyPathUtils;
import net.cloudslides.app.utils.MyToast;
import net.cloudslides.app.utils.MyVibrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;
import de.tavendo.autobahn.Wamp.CallHandler;
import de.tavendo.autobahn.Wamp.EventHandler;

public class AttendingMeetingActivity extends Activity {

	private ZoomAbleViewPager zoomPager;
	
	private ArrayList<String> urls;
	
	private PlaySlidesPagerAdapter adapter;
	
	private String wsuri;
	
	private String currentIndexTopicUri;	
	
	private String pathTopicUri;
	
	private WampConnection mConnection;	
	
	private int meetingPos;
	
	private long pptId;
	
	private long meetingId;
	
	private int currPageIndex;
	
	private CustomProgressDialog loadingDialog;
	
	private boolean close=false;
	
	private boolean isSuccess=false;//是否连接成功
	
	private LinearLayout drawerLayout;
	
	private Button backBtn;
	
	private Button cBoxBtn;
	
	private MultiDirectionSlidingDrawer slidingDrawer;
	
	private ListView communicationBoxLv;
	
	private View communicationBoxLayout;

	private PopupWindow communicationBoxWindow;
	
	private ArrayList<String> communicationInfos = new ArrayList<String>();
	
	private CommunicationBoxAdapter cBoxAdapter;
	
	private TextView communicationBoxTitle;
	
	private TextView communicationBoxState;
	
	private Button cBoxBackBtn;
	
	private Button cBoxSayBtn; 
	
	private View saySomethingLayout;

	private PopupWindow saySomethingWindow;
	
	private EditText saySomethingContent;
	
	private Button saySomethingSendBtn;
	
	private Button saySomethingNotSendBtn;
	
	private String chatTopicUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags
		(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setFlags
		(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_attending_meeting);
		setupView();
		initPptUrls();
		openWamp();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			slidingDrawer.toggle();
		}
		return super.onKeyDown(keyCode, event);
	}
	 @Override
	public void onConfigurationChanged(Configuration config)
	{
		 setContentView(R.layout.activity_attending_meeting);
	}
	private void setupView()
	{
		   zoomPager = (ZoomAbleViewPager) findViewById(R.id.attending_meeting_viewpager);
		drawerLayout = (LinearLayout) findViewById(R.id.attend_meeting_drawer_main_layout);
		   	 backBtn = (Button) findViewById(R.id.attend_meeting_drawer_back_btn);
		   	 cBoxBtn = (Button) findViewById(R.id.attend_meeting_drawer_communication_box_btn);
       slidingDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.attend_meeting_drawer);
       communicationBoxState = (TextView) findViewById(R.id.attend_meeting_drawer_communication_box_state);
	}

	private void initView()
	{
		adapter=new PlaySlidesPagerAdapter(urls,this);
		zoomPager.setAdapter(adapter);	
		zoomPager.setScrollEnabled(false);
		adapter.setOnItemClickListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
				return false;
			}
		});
		adapter.setOnZoomViewListener(new onZoomViewListener() {
			
			@Override
			public void onZoomReset(View view) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
			
			@Override
			public void onZoomIn(View view) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
		});		
		adapter.notifyDataSetChanged();	
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		drawerLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
		});
		cBoxBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showCommunicationBoxWindow();
				communicationBoxState.setText("会议交流");
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
		});
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
	public void openWamp()
	{
		mConnection = new WampConnection();
		wsuri=MyHttpClient.WS_URL+"/wamp";
		
			mConnection.connect(wsuri, new Wamp.ConnectionHandler() {
				
				@Override
				public void onOpen() {
					MyVibrator.doVibration(500);
				    loadingDialog.dismiss();
				    MyToast.alert("连接成功");
					getCurrentPageRPC();
					getPathRPC(meetingId);
					getALLChat();
				}
				
				@Override
				public void onClose(int code, String reason) {
					Log.i("WAMPonClose", reason+"");
					if(close)
	            	{
	            		MyToast.alert("结束观看会议");
	            	}
	            	else
	            	{
	            		if(isSuccess)
	            		{
	            			Toast.makeText(AttendingMeetingActivity.this,"网络中断,正在努力重新建立连接...",Toast.LENGTH_LONG).show();
	            			isSuccess=false;
	            		}
	            		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
							
							@Override
							public void run() {
								openWamp();//网络因素导致socket断开则每1秒检查一次后重连								
							}
						}, 1000);
	            	}	             
				}
			});
	}
	/**
	 * 远程调用获取当前会议对应的页码编号
	 * @author Felix
	 */
	private void getCurrentPageRPC()
	{
		mConnection.call("page#currentIndex", String.class, new Wamp.CallHandler() {
			
			@Override
			public void onResult(Object result) 
			{
				String str = (String)result;
				try 
				{
					JSONObject jso = new JSONObject(str);
					 currPageIndex = jso.getInt("pageIndex");
			  currentIndexTopicUri = jso.getString("pageTopicUri");					      
					      
			       Log.i("getCurrentPageRPC返回:", str+"");	
					      
					isSuccess=true;
					if(adapter==null)
	            	{
	            		initView();
	            	}
	            	zoomPager.setCurrentItem(currPageIndex-1);	            	
	            	getAllPathRPC(meetingId, currPageIndex);
					mConnection.subscribe(currentIndexTopicUri, Integer.class, new Wamp.EventHandler() 
					{
						@Override
						public void onEvent(String topicUri, Object event) 
						{
							boolean pageNotChange = ((Integer)event).equals(currPageIndex)?true:false;
							currPageIndex = (Integer)event;
							zoomPager.setCurrentItem(currPageIndex-1);	
							zoomPager.cleanPath();
							if(pageNotChange)
							{
								getAllPathRPC(meetingId, currPageIndex);
								Log.i("page not change", "page not change");
							}
						}								
					});
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(String errId, String errInfo) {
				Log.i("getCurrentPageRPC-Error:", errInfo+"");
				MyToast.alert("网络异常");
			}
		}, meetingId);
	}
	
	/**
	 * 订阅笔迹同步服务
	 * @param meetingId 会议id
	 * @author Felix
	 */
	private void getPathRPC(final long meetingId)
	{
		mConnection.call("path#getTopic", String.class, new Wamp.CallHandler() {
			
			@Override
			public void onResult(Object result) {
				String str = (String)result;
				Log.i("getPathTopicOnResult:", str+"");
				try 
				{
					JSONObject jso = new JSONObject(str);
					pathTopicUri = jso.getString("topicUri");
					mConnection.subscribe(pathTopicUri, pathResult.class, new Wamp.EventHandler() {
						
						@Override
						public void onEvent(String topicUri, Object event) {
							pathResult result =(pathResult)event;		
							if(result!=null)
							{
								if(result.type.equals("resetPath")&&result.pageIndex.equals(currPageIndex))
								{
									zoomPager.cleanPath();
									getAllPathRPC(meetingId, currPageIndex);//防止网络延迟导致滞后clean带来的问题
								}
								else if(result.type.equals("newPath")&&result.pageIndex.equals(currPageIndex))
								{
									try 
									{
										JSONArray jsa = new JSONArray(result.data);
										ArrayList<Integer> points = MyPathUtils.resetIncrementalList
												(MyPathUtils.parseJSONArrayToList(jsa));
										zoomPager.drawPathOnCurrentView(points);										
										
									} catch (JSONException e) {
										e.printStackTrace();
									}									
								}
							}
							Log.i("pathResult", result.toString());
							
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(String errId, String errInfo) {
				Log.i("getPathTopicRPC-Error:", errInfo+"");
				MyToast.alert("网络异常");
			}
		}, meetingId);
	}
	
	/**
	 * 获取当前页所有笔迹
	 * @param meetingId 会议id
	 * @param pageIndex 页码
	 * @author Felix
	 */
	private void getAllPathRPC(long meetingId,int pageIndex)
	{
		mConnection.call("path#queryAll", String.class, new Wamp.CallHandler() {
			
			@Override
			public void onResult(Object result) {
				String str = (String)result;
				Log.i("getAllPathRPCOnResult:", str+"");
				
				try 
				{
					JSONArray jsa = new JSONArray(str);
					ArrayList<Integer> points = new ArrayList<Integer>();
					for(int i = 0;i<jsa.length();i++)
					{
						points = MyPathUtils.resetIncrementalList(MyPathUtils.parseJSONArrayToList(jsa.getJSONArray(i)));
						if(points.size()>2)
						{
							zoomPager.drawPathOnCurrentView(points);
						}						
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(String errId, String errInfo) {
				Log.i("getAllPathRPC-Error:", errInfo+"");
				MyToast.alert("网络异常");
			}
		}, meetingId,pageIndex);
	}
	
	private static class pathResult
	{
		public String type;//笔记更新类型 newPath resetPath
		public Integer pageIndex;
		public String data;
		@Override
		public String toString() {
			return "{type: " + type +
	                ", pageIndex: " + pageIndex +
	                ", data: " + data + "}";
		}
	}

	
	  /**
	   * 获取所有交流对话以及订阅交流对话
	   * @author Felix
	   */
	  private void getALLChat()
	  {
		  if(mConnection.isConnected())
		  {
			  mConnection.call("chat#queryAll", String.class, new CallHandler() {
				
				@Override
				public void onResult(Object result) {
					Log.i("queryAllChatOnResult", result+"");
				   String str =(String)result;
					   try 
					   {						   
						JSONObject jso = new JSONObject(str);
						chatTopicUri = jso.getString("chatTopicUri");
						JSONArray jsa = jso.getJSONArray("chats");
						communicationInfos.clear();
						for(int i = 0 ;i<jsa.length();i++)
						{
							communicationInfos.add(jsa.getString(i));
						}
						mConnection.subscribe(chatTopicUri, ChatData.class, new EventHandler() {
							
							@Override
							public void onEvent(String arg0, Object event) {	
							Log.i("getChatOnEvent:", event+"");
							ChatData cd = (ChatData)event;
							if(cd.type.equals("newChat"))
							{
								communicationInfos.add(cd.data);								
								if(communicationBoxWindow.isShowing())
								{
									communicationBoxTitle.setText("会议交流("+communicationInfos.size()+"条)");	
									cBoxAdapter.notifyDataSetChanged();
								}
								else
								{
									MyToast.alert("有新的发言");
									communicationBoxState.setText("会议交流(新)");
								}								
							}
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onError(String errId, String errInfo) {
					MyToast.alert("getAllChatOnError:"+errInfo+"");
				}
			}, meetingId);
		  }
	  }
	  
	  /**
	   * 发言
	   * @param content 内容
	   * @author Felix
	   */
	  private void saySomething(String content)
	  {
		  if(mConnection.isConnected())
		  {
			  String userEmail = HomeApp.getLocalUser().getUserEmail();
			  String token = HomeApp.getLocalUser().getToken(); 
			  long time = System.currentTimeMillis(); 
			  mConnection.call("chat#say", String.class, new CallHandler() {
				
				@Override
				public void onResult(Object result) {
					String state = (String)result;
					Log.i("saySomethingOnResult:", state+"");
					if(state.equals("ok"))
					{
						MyToast.alert("发送成功");
						saySomethingWindow.dismiss();
						communicationBoxLv.setSelection(cBoxAdapter.getCount()-1);
					}
					else
					{
						MyToast.alert("发送失败");
					}					
				}
				
				@Override
				public void onError(String errId, String errInfo) {
					MyToast.alert("chat#SayOnError:"+errInfo);
				}
			}, userEmail,token,meetingId,content,time);
		  }
			  
	  }
	  /**
	   * 弹出会议交流窗口
	   * @author Felix
	   */
	  private void showCommunicationBoxWindow()
	  {
			communicationBoxLayout = (View)LayoutInflater.from(this).inflate(R.layout.communication_box_layout, null);	
			communicationBoxLv = (ListView)communicationBoxLayout.findViewById(R.id.communication_box_listview);
			communicationBoxTitle = (TextView)communicationBoxLayout.findViewById(R.id.communication_box_title);
			          cBoxBackBtn = (Button)communicationBoxLayout.findViewById(R.id.communication_box_back_btn);
			          cBoxSayBtn = (Button)communicationBoxLayout.findViewById(R.id.communication_box_say_btn);
			cBoxAdapter = new CommunicationBoxAdapter(this, communicationInfos);
			communicationBoxLv.setAdapter(cBoxAdapter);
			communicationBoxWindow=new PopupWindow(communicationBoxLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);		
			communicationBoxWindow.setFocusable(true);
			communicationBoxWindow.setAnimationStyle(R.style.PopupAnimationFromRight);
			communicationBoxWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
			communicationBoxWindow.showAtLocation(findViewById(R.id.attend_meeting_main_layout), Gravity.CENTER_VERTICAL,0,0);
			communicationBoxTitle.setText("会议交流("+communicationInfos.size()+"条)");
			if(cBoxAdapter.getCount()!=0)//scroll to the last item
			{
				communicationBoxLv.setSelection(cBoxAdapter.getCount()-1);
			}
			cBoxBackBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					communicationBoxWindow.dismiss();					
				}
			});
			cBoxSayBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showSaySomethingWindow();
				}
			});
		}
		
		/**
		 * 弹出提问窗口
		 * @author Felix
		 */
		private void showSaySomethingWindow()
		{
			saySomethingLayout = (View)LayoutInflater.from(this).inflate(R.layout.saysomething_layout, null);	
			saySomethingSendBtn = (Button)saySomethingLayout.findViewById(R.id.say_something_send_btn);
			saySomethingNotSendBtn = (Button)saySomethingLayout.findViewById(R.id.say_something_back_btn);
			saySomethingContent = (EditText)saySomethingLayout.findViewById(R.id.say_something_edit_text);
			saySomethingSendBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(saySomethingContent.getText().toString().trim().equals(""))
					{
						MyToast.alert("无语了么?");
						return;
					}
					if(mConnection.isConnected())
					{
						saySomething(saySomethingContent.getText().toString().trim());
						MyToast.alert("正在发送...");
					}
					else
					{
						MyToast.alert("未连接服务器,请检查您的网络");
					}
				}
			});
			saySomethingNotSendBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					saySomethingWindow.dismiss();
				}
			});
			saySomethingWindow=new PopupWindow(saySomethingLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);		
			saySomethingWindow.setFocusable(true);
			saySomethingWindow.setAnimationStyle(R.style.PopupAnimationFromTop);
			saySomethingWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
			saySomethingWindow.showAtLocation(findViewById(R.id.attend_meeting_main_layout), Gravity.CENTER_VERTICAL,0,0);
			
		}
}
