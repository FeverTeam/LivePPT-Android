package net.cloudslides.app.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoView;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoView.onDrawCompleteListener;
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
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;

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
		adapter.setOnDrawCompleteListener(new onDrawCompleteListener() {
			/**
	    	 * 一条完整的笔迹画完后触发
	    	 * @param points 笔迹的坐标比例数组（x，y相间），坐标比例即坐标相对图像的十万分比位置
	    	 * @author Felix
	    	 */
			@Override
			public void onDrawComplete(ArrayList<Integer> points) {
				try 
				{
					
					JSONArray js = new JSONArray("[31125,86875,30256,85909,29633,84497,30058,82557,31306,78667,31994,75544,34237,69306,37249,64006,40302,59690,43271,56186,46173,53467,48432,52272,50631,51932,52562,52343,53856,53609,54712,55754,54773,57432,54541,59274,53638,61199,52282,62565,50754,63469,49568,63737,48414,63146,47507,61678,47500,59325,49475,54764,52802,50605,57765,46098,63740,42076,70465,38970,76283,36962,81745,35563,86054,35328]");
					ArrayList<Integer> l = new ArrayList<Integer>();
					for(int i =0;i<js.length();i++)
					{
						l.add(js.getInt(i));
					}			
					zoomPager.drawPathOnCurrentView(l);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				JSONArray jsa = new JSONArray(points);
				Log.i("路径复原坐标:", jsa.toString());
				Log.i("路径复原大小:",jsa.toString().getBytes().length+"Bytes");
				
				
			}
		});
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
			  currentIndexTopicUri = jso.getString("topicUri");					      
					      
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
	 * 根据返回信息提取页码
	 * @param message
	 * @return page
	 * @author Felix
	 */
	
	/*private int getPage(String message)
	{
		String temp[]=new String[2];
			   temp=message.split("-");		   
	    return Integer.parseInt(temp[1]);	
	}*/
}
