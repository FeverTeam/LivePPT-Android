package net.cloudslides.app.activity;

import java.util.ArrayList;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.PlaySlidesPagerAdapter;
import net.cloudslides.app.custom.widget.MultiDirectionSlidingDrawer;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import net.cloudslides.app.thirdlibs.widget.photoview.ZoomAbleViewPager;
import net.cloudslides.app.thirdlibs.widget.wheel.ArrayWheelAdapter;
import net.cloudslides.app.thirdlibs.widget.wheel.WheelView;
import net.cloudslides.app.utils.MyHttpClient;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.nostra13.universalimageloader.core.ImageLoader;

public class PlaySlidesActivity extends Activity {
	private ZoomAbleViewPager zoomPager;
	
	private ArrayList<String> urls;
	
	private PlaySlidesPagerAdapter adapter;
	
	private FrameLayout covert;
	
	private Button pageBtn;
	
	private int pptPos;
	
	private long pptId;
	
	private LinearLayout drawerLayout;
	
	private Button backBtn;
	
    private MultiDirectionSlidingDrawer slidingDrawer;    

	private PopupWindow dialogPopWindow;	 
	
	private WheelView wheel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags
		(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_play_slides);
		setupView();
		initPptUrls();
		initView();
	}
	private void setupView()
	{
		zoomPager = (ZoomAbleViewPager)findViewById(R.id.play_slides_flipview);
		   covert = (FrameLayout)findViewById(R.id.play_slides_covert_frame);	
		  pageBtn = (Button)findViewById(R.id.play_slides_page_picker_btn);
	 drawerLayout = (LinearLayout)findViewById(R.id.play_slides_drawer_main_layout);
		  backBtn = (Button)findViewById(R.id.play_slides_drawer_back_btn);
    slidingDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.play_slides_drawer);
	}
	
	
	private void initView()
	{
		adapter=new PlaySlidesPagerAdapter(urls,this);
		adapter.setOnItemClickListener(new OnViewTapListener() {
			
			@Override
			public void onViewTap(View view, float x, float y) {
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
		});
		zoomPager.setAdapter(adapter);
		zoomPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
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
				showPickerDialog();
				if(slidingDrawer.isOpened())
				{
					slidingDrawer.close();
				}
			}
		});
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
		
	}
	
	/**
	 * 初始化ppt图片地址
	 * @author Felix
	 */
	private void initPptUrls()
	{
		pptPos=getIntent().getIntExtra(Define.Intent_KEY_PPT_POSITION, 0);
		pptId=HomeApp.getLocalUser().getPpts().get(pptPos).getPptId();
		urls=new ArrayList<String>();
		for(int i=1;i<=HomeApp.getLocalUser().getPpts().get(pptPos).getPptPageCount();i++)
		{
			String url =MyHttpClient.BASE_URL+"/ppt/pageImage?pptId="+pptId+"&page="+i
					+"&token="+HomeApp.getLocalUser().getToken()
					+"&uemail="+HomeApp.getLocalUser().getUserEmail();
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
			slidingDrawer.toggle();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
    @Override
    protected void onDestroy() {
    super.onDestroy();
    Log.i("onDestroy","stopImageLoader");
    ImageLoader.getInstance().stop();
    if(null!=dialogPopWindow&&dialogPopWindow.isShowing())
	{
		dialogPopWindow.dismiss();
	}
    }
}
