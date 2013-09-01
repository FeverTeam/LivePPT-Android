package net.cloudslides.app.activity;

import java.util.ArrayList;

import net.cloudslides.app.R;
import net.cloudslides.app.adapter.IntroPageAdapter;
import net.cloudslides.app.utils.MyActivityManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class IntroActivity extends Activity {

	private ViewPager viewPager;
	private ArrayList<View> viewList;
	private IntroPageAdapter viewAdapter;
	private Button startBtn;
	private LinearLayout indicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intropage);
		MyActivityManager.getInstance().add(this);
		viewPager=(ViewPager)findViewById(R.id.intro_viewpager);
		indicator=(LinearLayout)findViewById(R.id.intro_indicator);
		viewList=new ArrayList<View>();
		viewList=initViewList();
		viewAdapter=new IntroPageAdapter(viewList);
		viewPager.setAdapter(viewAdapter);	
		viewPager.setOnPageChangeListener(new myPageChangeListener());
		
		
		
	}
	private ArrayList<View> initViewList()
	{
		LayoutInflater inflater =getLayoutInflater();
		viewList.add(inflater.inflate(R.layout.intro_item1, null));
		viewList.add(inflater.inflate(R.layout.intro_item2, null));
		View v=inflater.inflate(R.layout.intro_item3, null);
		startBtn=(Button)v.findViewById(R.id.start_btn);
		startBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/**
				 * 测试版关闭标注				 
				 */
				//HomeApp.getMyApplication().markLaunched();
				Intent intent =new Intent(IntroActivity.this,HomeActivity.class);				
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			}
		});
		viewList.add(v);
		return viewList;
	}
	public class myPageChangeListener implements ViewPager.OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int arg0) {
			switch(arg0)
			{
			case 0:indicator.setBackgroundResource(R.drawable.intro_indicator_1);break;
			case 1:indicator.setBackgroundResource(R.drawable.intro_indicator_2);break;
			case 2:indicator.setBackgroundResource(R.drawable.intro_indicator_3);break;
			default:break;			
			}
			
		}
		
	}

}
