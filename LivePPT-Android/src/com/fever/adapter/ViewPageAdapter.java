package com.fever.adapter;

import java.util.ArrayList;
import com.fever.liveppt.R;
import com.fever.utils.ImageCache;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPageAdapter extends PagerAdapter {

	/**
	 * ViewPager适配器
	 * @author Felix
	 */
	private ArrayList<String> viewList=null;
	private ImageCache mCache;
	private LayoutInflater inflater; 	
	private Bitmap bitmap;	
	public ViewPageAdapter(ArrayList<String> list,ImageCache mCache,LayoutInflater inflater)
	{
		this.inflater=inflater;
        this.viewList = list;        
        this.mCache =mCache;       
      
     }

    public void setList(ArrayList<String> newList)
	{
		this.viewList=newList;
	}
	@Override
	public int getCount()
	{	
		
		return viewList.size();
	}
	@Override
	public void setPrimaryItem(View container, int position, Object object)
	{
		
	}
	 
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) 	{
		
		return arg0==arg1;
	}
	 @Override  
     public void destroyItem(ViewGroup container, int position, Object object) {
		 
		 container.removeView((View) object);
          
     }  
	@Override  
    public int getItemPosition(Object object)
	{          
        return POSITION_NONE;  
    } 
	
	@Override
	 public Object instantiateItem(ViewGroup container, int position)
	{
		
		View view =inflater.inflate(R.layout.pptviewpage_item, null);
	  ImageView iv=(ImageView)view.findViewById(R.id.image_ViewPage);
		
		bitmap=mCache.getBitmap(viewList.get(position));
		if(bitmap!=null)
		{
			iv.setImageBitmap(bitmap);			
		}
		else
		{			
			iv.setImageResource(R.drawable.img_not_found);			
		}
		((ViewPager)container).addView(view);
		
		return view;		
	 }	
	
}
