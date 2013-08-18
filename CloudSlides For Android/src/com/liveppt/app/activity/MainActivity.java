package com.liveppt.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.liveppt.app.R;
import com.liveppt.app.fragment.MenuFragment;
import com.liveppt.app.fragment.MyPptFragment;
import com.liveppt.app.utils.MyActivityManager;

public class MainActivity extends SlidingFragmentActivity {
	Fragment mContent;
	SlidingMenu sm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyActivityManager.getInstance().add(this);
		if (savedInstanceState != null)
		{
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		}
		if (mContent == null)
		{
			mContent = new MyPptFragment();
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
	 * 根据返回键切换slidingmenu
	 * @author felix
	 */
	@Override
	public void onBackPressed() {
		if(sm!=null)
		sm.toggle();
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
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}	

	

}
