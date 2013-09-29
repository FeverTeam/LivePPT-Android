package net.cloudslides.app.activity;

import net.cloudslides.app.Define;
import net.cloudslides.app.R;
import net.cloudslides.app.fragment.AttendingMeetingFragment;
import net.cloudslides.app.fragment.MenuFragment;
import net.cloudslides.app.fragment.MyPptFragment;
import net.cloudslides.app.utils.MyActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class MainActivity extends SlidingFragmentActivity {
	private Fragment mContent;
	public SlidingMenu sm;
	private int contentId;  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyActivityManager.getInstance().add(this);
		contentId=getIntent().getIntExtra("content",0);
		if (savedInstanceState != null)
		{
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		}
		if (mContent == null)
		{
			switch(contentId)
			{
			case Define.LOGIN_JUMP_PPT:mContent = new MyPptFragment(this);break;
			case Define.LOGIN_JUMP_ATTENDING:mContent = new AttendingMeetingFragment(this);break;
			case Define.LOGIN_JUMP_FOUNDING:
			{
				mContent = new MyPptFragment(this);
				Intent intent = new Intent(this,FoundMeetingActivity.class);
				startActivity(intent);
			}
			}
		}
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.main_content_frame, mContent)
		.commit();
		initSlidingMenu();		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	
	
	/**
	 * 侧拉菜单自动切换状态
	 * @author Felix
	 */
	public void toggleMenu()
	{
		if(sm!=null)
		sm.toggle();
	}
	
	/**
	 * 根据返回键切换slidingmenu
	 * @author Felix
	 */
	@Override
	public void onBackPressed() {
		toggleMenu();
		return;
	}
	
	
	/**
	 * 初始化侧拉菜单
	 * @author Felix
	 */
	private void initSlidingMenu()
	{
		setBehindContentView(R.layout.sliding_menu);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new MenuFragment())
		.commit();
		sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.sliding_menu_shadow);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setBehindScrollScale(0.35f);
		sm.setFadeDegree(0.5f);
	}
	
	
	/**
	 * 切换页面内容
	 * @param fragment 要切换的页面fragment
	 * @author Felix
	 */
	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.main_content_frame, fragment)
		.commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() 
		{
			public void run() 
			{
				getSlidingMenu().showContent();
			}
		}, 50);
	}
}
