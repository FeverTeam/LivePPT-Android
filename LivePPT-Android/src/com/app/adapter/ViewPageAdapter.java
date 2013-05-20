package com.app.adapter;

import java.util.ArrayList;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class ViewPageAdapter extends PagerAdapter {

	private ArrayList<View> viewList=null;
	public ViewPageAdapter(ArrayList<View> list)
	{
        this.viewList = list;        
      
     }  
	public ViewPageAdapter()
	{
		
	}
	public void setList(ArrayList<View> newList)
	{
		this.viewList=newList;
	}
	@Override
	public int getCount()
	{	
		
		return viewList.size();
	}
	 
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) 	{
		
		return arg0==arg1;
	}
	 @Override  
     public void destroyItem(ViewGroup container, int position, Object object) {
		 container.removeView(viewList.get(position));
          
     }  
	@Override  
    public int getItemPosition(Object object)
	{  
        
        return POSITION_NONE;  
    } 
	
	@Override
	 public Object instantiateItem(ViewGroup container, int position)
	{
		container.addView(viewList.get(position));
		return viewList.get(position);		 
	 }

}
