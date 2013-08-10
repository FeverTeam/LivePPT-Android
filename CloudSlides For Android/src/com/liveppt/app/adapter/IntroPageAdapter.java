package com.liveppt.app.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class IntroPageAdapter extends PagerAdapter {

    ArrayList<View> viewList ;
	
	public IntroPageAdapter(ArrayList<View> list)
	{
		viewList=list;
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		 ((ViewPager)container).removeView((View)object);
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	

	@Override
	public Object instantiateItem(ViewGroup container, int position) {	
		 ((ViewPager) container).addView(viewList.get(position)); 
			return viewList.get(position);		
		
	}

}
