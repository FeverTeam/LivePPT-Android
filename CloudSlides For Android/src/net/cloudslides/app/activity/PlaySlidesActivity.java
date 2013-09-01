package net.cloudslides.app.activity;

import java.util.ArrayList;
import com.nostra13.universalimageloader.core.ImageLoader;
import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.widget.photoview.ZoomAbleViewPager;
import net.cloudslides.app.widget.wheel.ArrayWheelAdapter;
import net.cloudslides.app.widget.wheel.WheelView;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

public class PlaySlidesActivity extends Activity {
	private ZoomAbleViewPager zoomPager;
	private ArrayList<String> urls;
	private PlaySlidesPagerAdapter adapter;
	private FrameLayout covert;
	private int pptPos;
	private long pptId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_slides);
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
	
	private void setupView()
	{
		zoomPager=(ZoomAbleViewPager)findViewById(R.id.play_slides_flipview);
		   covert=(FrameLayout)findViewById(R.id.play_slides_covert_frame);		
	}
	
	
	private void initView()
	{
		adapter=new PlaySlidesPagerAdapter(urls,this);
		//flipView=new FlipViewController(this, FlipViewController.HORIZONTAL);
		zoomPager.setAdapter(adapter);
	}
	
	
	private void initPptUrls()
	{
		pptPos=getIntent().getIntExtra(Define.Intent_KEY_PPT_POSITION, 0);
		pptId=HomeApp.getLocalUser().getPpts().get(pptPos).getPptId();
		urls=new ArrayList<String>();
		for(int i=1;i<=HomeApp.getLocalUser().getPpts().get(pptPos).getPptPageCount();i++)
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
			dialogPopWindow.showAtLocation(findViewById(R.id.paly_slides_main_layout), Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);				
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


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			showPickerDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	

}
