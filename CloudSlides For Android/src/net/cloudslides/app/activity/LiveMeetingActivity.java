package net.cloudslides.app.activity;

import java.util.ArrayList;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.custom.widget.MultiDirectionSlidingDrawer;
import net.cloudslides.app.custom.widget.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoView.onDrawCompleteListener;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoView.onZoomViewListener;
import net.cloudslides.app.thirdlibs.widget.photoview.ZoomAbleViewPager;
import net.cloudslides.app.thirdlibs.widget.wheel.ArrayWheelAdapter;
import net.cloudslides.app.thirdlibs.widget.wheel.WheelView;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyPathUtils;
import net.cloudslides.app.utils.MyToast;
import net.cloudslides.app.utils.MyVibrator;

import org.json.JSONArray;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.Wamp.CallHandler;
import de.tavendo.autobahn.WampConnection;

public class LiveMeetingActivity extends Activity {

	private ZoomAbleViewPager zoomPager;
	
	private ArrayList<String> urls;
	
	private PlaySlidesPagerAdapter adapter;
	
	private FrameLayout covert;
	
	private Button pageBtn;
	
	private Button cleanBtn;
	
	private CheckBox drawBtn;
	
	private int meetingPos;
	
	private long pptId;
	
	private long meetingId;
	
	private int currPageIndex =1;
	
	private WampConnection mConnection;
	
	private boolean isConnected=false;
	
	private boolean isZoomIn=false;
	
	private String  wsUri;
	
	private boolean close=false;
	
	private JSONArray jsaData;
	
	private CustomProgressDialog loadingDialog;
	
	private MultiDirectionSlidingDrawer slidingDrawer;
	
	private LinearLayout drawLayout;
	
	private RelativeLayout slidingDrawerMainLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags
		(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setFlags
		(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_live_meeting);
		setupView();
		initPptUrls();
		initView();
		openWamp();
		loadingDialog=CustomProgressDialog.createDialog(this, "正在连接服务器...", true);
		loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_light_green));
		loadingDialog.show();
	}	

	@Override
	public void onResume()
	{
		super.onResume();
		drawBtn.setChecked(false);
	}
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
	    close=true;//人为断开
		if(mConnection.isConnected())
		{
			mConnection.disconnect();
		}
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			slidingDrawer.toggle();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void setupView()
	{
		zoomPager = (ZoomAbleViewPager)findViewById(R.id.live_meeting_viewpager);
		   covert = (FrameLayout)findViewById(R.id.live_meeting_covert_frame);
		  pageBtn = (Button)findViewById(R.id.live_meeting_drawer_page_picker_btn);
		  drawBtn = (CheckBox)findViewById(R.id.live_meeting_drawer_draw_checkbox);
		 cleanBtn = (Button)findViewById(R.id.live_meeting_drawer_clean_btn);
	slidingDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.live_meeting_drawer);
	   drawLayout = (LinearLayout)findViewById(R.id.drawer_draw_layout);
	   slidingDrawerMainLayout = (RelativeLayout)findViewById(R.id.live_meeting_drawer_main_layout);
	
	}
	
	
	private void initView()
	{
		drawLayout.setVisibility(View.INVISIBLE);//建立连接前先隐藏笔迹按钮
		
		adapter=new PlaySlidesPagerAdapter(urls,this);
		adapter.setOnDrawCompleteListener(new onDrawCompleteListener() {
			/**
	    	 * 一条完整的笔迹画完后触发
	    	 * @param points 笔迹的坐标比例数组（x，y相间），坐标比例即坐标相对图像的十万分比位置
	    	 * @author Felix
	    	 */
			@Override
			public void onDrawComplete(ArrayList<Integer> points) {
				if(isConnected)
				{
					addPath(meetingId, currPageIndex, points);
				}
			}
		});
		adapter.setOnZoomViewListener(new onZoomViewListener() {
			
			@Override
			public void onZoomReset(View view) {
				if(isConnected)
				{
					drawLayout.setVisibility(View.VISIBLE);
				}
				isZoomIn=false;
			}
			
			@Override
			public void onZoomIn(View view) {
				drawLayout.setVisibility(View.INVISIBLE);
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.animateClose();
				}
				isZoomIn=true;
			}
		});
		zoomPager.setAdapter(adapter);
		zoomPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) 
			{				
				Log.i("currPos", position+"");
				currPageIndex=position+1;
				resetPath(meetingId, currPageIndex,true);
				drawBtn.setChecked(false);
				if(!zoomPager.currentViewIsZoomIn()&&isConnected)
				{
					drawLayout.setVisibility(View.VISIBLE);
				}
				else
				{
					drawLayout.setVisibility(View.INVISIBLE);
				}
				setMeetingPage();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		pageBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.animateClose();
				}
				showPickerDialog();
			}
		});
		drawBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.animateClose();
				}
				zoomPager.setCanDraw(isChecked);
			    zoomPager.setScrollEnabled(!isChecked);			
			    
			}
		});
		cleanBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.animateClose();
				}
				showConfirmCleanPathDialog();
			}
		});
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				if(!mConnection.isConnected())
				{
					MyToast.alert("尚未建立连接或已断开");
					slidingDrawer.close();
				}				
			}
		});
		slidingDrawerMainLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
		meetingId=HomeApp.getLocalUser().getFoundedMeeting().get(meetingPos).getMeetingId();
		pptId=HomeApp.getLocalUser().getFoundedMeeting().get(meetingPos).getMeetingPpt().getPptId();
		urls=new ArrayList<String>();
		for(int i=1;i<=HomeApp.getLocalUser().getFoundedMeeting().get(meetingPos).getMeetingPpt().getPptPageCount();i++)
		{
			String url = MyHttpClient.BASE_URL+"/ppt/pageImage?pptId="+pptId+"&page="+i
					+"&token="+HomeApp.getLocalUser().getToken()
					+"&uemail="+HomeApp.getLocalUser().getUserEmail();
			urls.add(url);			
		}
	}
	
	/**
	 * 弹出清除笔迹确认对话框
	 * @author Felix
	 */
	private void showConfirmCleanPathDialog()
	{
		final Dialog dialog =new Dialog(this, R.style.mDialog);
		View layout =LayoutInflater.from(this).inflate(R.layout.normal_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.normal_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.normal_dialog_confirm_btn);
		TextView title = (TextView)layout.findViewById(R.id.normal_dialog_title);
		TextView   msg = (TextView)layout.findViewById(R.id.normal_dialog_message);
		msg.setText("确认清除当前页面所有笔迹?");
		title.setText("清除笔迹");
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				resetPath(meetingId, currPageIndex, false);
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	
	
	/**
	 * 页码选择框
	 * @author Felix
	 */
	  private void showPickerDialog()
	  {
		  covert.setVisibility(View.VISIBLE);
		  final PopupWindow dialogPopWindow;	 	  
		  final WheelView wheel;
		  final String[] items= new String[urls.size()];
		  for(int i =0 ;i<urls.size();i++)
		  {
			  items[i]="第"+(i+1)+"页";
		  }
		  View dialogLayout;
		  Button cancel;
		  Button select;
		 
		  ArrayWheelAdapter<String> adapter =new ArrayWheelAdapter<String>(this, items);		  
		  
			dialogLayout=(View)LayoutInflater.from(this).inflate(R.layout.play_slides_page_picker, null);							  
		    cancel=(Button)dialogLayout.findViewById(R.id.picker_cancel_btn);
		    select=(Button)dialogLayout.findViewById(R.id.picker_select_btn);
			wheel=(WheelView)dialogLayout.findViewById(R.id.page_picker_wheel);
		    wheel.setViewAdapter(adapter);
		    wheel.setVisibleItems(4);
		    wheel.setCurrentItem(zoomPager.getCurrentItem());
		    adapter.setTextSize(20);
		    dialogPopWindow=new PopupWindow(dialogLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);		
			dialogPopWindow.setFocusable(true);
			dialogPopWindow.setAnimationStyle(R.style.PopupAnimation);
			dialogPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
			dialogPopWindow.showAtLocation(findViewById(R.id.live_meeting_main_layout), Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);				
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialogPopWindow.dismiss();
				}
			});			
			
			select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					drawBtn.setChecked(false);
					zoomPager.setCurrentItem(wheel.getCurrentItem());
					dialogPopWindow.dismiss();
				}
			});
			dialogPopWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					covert.setVisibility(View.GONE);
				}
			});
	  }
	  
	  /**
	   * 设置会议直播页码
	   * @author Felix
	   */
	  
	  private void setMeetingPage()
	  {
		  if(mConnection.isConnected())
		  {
			  String userEmail = HomeApp.getLocalUser().getUserEmail();
			  String token     = HomeApp.getLocalUser().getToken();
			  
			  mConnection.call("page#set", String.class, new CallHandler() {
				
				@Override
				public void onResult(Object result) {
					Log.i("setMeetingPageOnResult:",result+"");
				}
				
				@Override
				public void onError(String errId, String errInfo) {
					setMeetingPage();
				}
			},userEmail,token,meetingId,currPageIndex);
		  }
		  else
		  {
			  MyToast.alert("尚未建立连接或已断开");
		  }		  
	  }
	  
	  
	  /**
	   * 建立wamp(Websocket Application Message Protocol)连接
	   * @author Felix
	   */
	  private void openWamp()
	  {
		  wsUri=MyHttpClient.WS_URL+"/wamp";
		  mConnection = new WampConnection();
		  mConnection.connect(wsUri, new Wamp.ConnectionHandler() {
			
			@Override
			public void onOpen() {
				MyVibrator.doVibration(500);
			    loadingDialog.dismiss();
				MyToast.alert("连接成功");
				isConnected=true;
				setMeetingPage();
				if(!isZoomIn)
				{
					drawLayout.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onClose(int code, String reason) {
				Log.i("WAMPonClose", reason+"");
				if(!close)
            	{
            		if(isConnected)
            		{
            			Toast.makeText(LiveMeetingActivity.this,"网络中断,正在努力重新建立连接...",Toast.LENGTH_LONG).show();
            			isConnected=false;
            			drawLayout.setVisibility(View.INVISIBLE);
            			drawBtn.setChecked(false);
            			if(slidingDrawer.isOpened())
        				{
        					slidingDrawer.animateClose();
        				}
            		}
            		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
						
						@Override
						public void run() {
							openWamp();							
						}
					}, 1000);
            	}
			}
		});
	  }
	  
	  /**
	   * 通知服务器添加笔迹
	   * @param meetingId 会议id
	   * @param pageIndex 页码
	   * @param list 笔迹坐标比例数组
	   * @author Felix
	   */
	  public void addPath(long meetingId,int pageIndex,ArrayList<Integer> list)
	  {
		  if(mConnection.isConnected())
		  {
			  jsaData = new JSONArray(MyPathUtils.getIncrementalList(list));
			  Log.i("addPath:", jsaData.toString()+"");
			  mConnection.call("path#add", String.class, new CallHandler() {
				
				@Override
				public void onResult(Object result) {
					Log.i("#addPath-onResult", (String)result+"");//返回笔迹序号
				}
				
				@Override
				public void onError(String errorId, String errorInfo) {
					MyToast.alert(errorInfo+"");
				}
				
			}, meetingId,pageIndex,jsaData.toString());		  
		  }
		  else
		  {
			  MyToast.alert("尚未建立连接或已断开");
		  }
	  }
	  
	  /**
	   * 清除指定页面笔迹
	   * @param meetingId
	   * @param pageIndex
	   * @param preClean
	   * @author Felix
	   */
	  public void resetPath(long meetingId,final int pageIndex,final boolean preClean)
	  {
		  if(mConnection.isConnected())
		  {
			  if(preClean)
			  {
				  zoomPager.cleanPath();
			  }
			  else
			  {
				  MyToast.alert("正在清除笔迹...");
			  }
			  mConnection.call("path#reset", String.class, new CallHandler() {
				
				@Override
				public void onResult(Object result) {
					String str =(String)result;
					Log.i("resetPathOnResult", result+"");
					if(str.equals("ok")&&pageIndex==currPageIndex&&!preClean)
					{					
						zoomPager.cleanPath();
					}
				}
				
				@Override
				public void onError(String errId, String errInfo) {
					MyToast.alert(errInfo+"");
				}
			}, meetingId,pageIndex);
		  }
		  else
		  {
			  MyToast.alert("尚未建立连接或已断开");
		  }		  
	  }
}
