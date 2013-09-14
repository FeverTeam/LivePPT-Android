package net.cloudslides.app.activity;

import java.util.ArrayList;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.widget.photoview.ZoomAbleViewPager;
import net.cloudslides.app.widget.wheel.ArrayWheelAdapter;
import net.cloudslides.app.widget.wheel.WheelView;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LiveMeetingActivity extends Activity {
	private ZoomAbleViewPager zoomPager;
	private ArrayList<String> urls;
	private PlaySlidesPagerAdapter adapter;
	private FrameLayout covert;
	private int meetingPos;
	private long pptId;
	private long meetingId;
	private int currPageIndex;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_meeting);
		setupView();
		initPptUrls();
		initView();
	}	

	@Override
	public void onPause() 
	{
		super.onPause();
		Log.i("onPause","stopImageLoader");
		ImageLoader.getInstance().stop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			showPickerDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void setupView()
	{
		zoomPager=(ZoomAbleViewPager)findViewById(R.id.live_meeting_viewpager);
		   covert=(FrameLayout)findViewById(R.id.live_meeting_covert_frame);		
	}
	
	
	private void initView()
	{
		adapter=new PlaySlidesPagerAdapter(urls,this);
		zoomPager.setAdapter(adapter);
		zoomPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) 
			{
				currPageIndex=position+1;
				setMeetingPage();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
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
			String url ="http://live-ppt.com/getpptpage?pptid="+pptId+"&pageid="+i;
			urls.add(url);			
		}
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
		  String url ="/app/setMeetingPageIndex";
		  
		  RequestParams params = new RequestParams();
		  params.put(Param.MEETING_ID_KEY, meetingId+"");
		  params.put(Param.PAGE_INDEX, currPageIndex+"");
		  MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		  {
			  @Override
				public void onSuccess(String response)
				{
				  Log.i("设置会议直播页返回:",response);	
				}
			  @Override
			     public void onFailure(Throwable e, String response) 
				{
				  e.printStackTrace();
				  Log.i("设置会议直播页onFailure返回:",response+"");
				  setMeetingPage();
			    }
		  });
		  
	  }

}
