package net.cloudslides.app.adapter;

import java.util.ArrayList;
import net.cloudslides.app.widget.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PlaySlidesPagerAdapter extends PagerAdapter {
	
	private ArrayList<String> urls;
	private Context context;
	private int i;
	private int j;
	
	public PlaySlidesPagerAdapter(ArrayList<String> urls,Context c)
	{
		this.urls=urls;
		this.context=c;
	}

	@Override
	public int getCount() {
		return urls.size();
	}	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0.equals(arg1);
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
	public Object instantiateItem(ViewGroup container, final int position) {
		PhotoView iv=new PhotoView(context);
		iv.setScaleType(ScaleType.FIT_XY);		
		ImageLoader.getInstance().displayImage(urls.get(position), iv,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				Log.i("ImageLoader", "<<<<<开始读取第"+position+"页<<<<<<");
				
			}			
			@Override
			public void onLoadingFailed(String imageUri, View view,	FailReason failReason) {}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				Log.i("ImageLoader", ">>>>>读取第"+position+"页完毕>>>>>>");
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {}
		});	
		
		
		//-----------------------向后预读取------------------------------
		
		for(i =position+1;i<urls.size()&&i<=position+3;i++)
		{
			ImageLoader.getInstance().loadImage(urls.get(i), new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				Log.i("ImageLoader", "-------------开始预读取-------------");
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,	FailReason failReason) {}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				Log.i("ImageLoader", "++++++++++++++预读取完毕++++++++++++++");				
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {}
		 });
		}
		

		//------------------------向前预读取-----------------------------
		
		for(j =position-1;j>=0&&j>=position-3;j--)
		{
			ImageLoader.getInstance().loadImage(urls.get(j), new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					Log.i("ImageLoader", "-------------开始预读取-------------");
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,	FailReason failReason) {
					
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					Log.i("ImageLoader", "++++++++++++++预读取完毕++++++++++++++");				
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					
				}
			});
		}
		 ((ViewPager) container).addView(iv);
		 iv.invalidate();
		 return iv;	
	}	
}
