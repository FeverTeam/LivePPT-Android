package com.app.liveppt;

import java.util.ArrayList;

import com.app.adapter.ViewPageAdapter;
import com.app.liveppt.R;
import com.app.utils.PptDownLoad;
import com.app.utils.myApp;

import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PptReplayActivity extends Activity {
	
	Long pptId;
	boolean isUpdating;
	int pages;
	int pageCount;
	Long start;
	Long finish;
	
	myApp app;	
	ArrayList<View> mViewList;
	Bitmap bmp;	
	TextView pagesText;
	TextView proBarInfo;
	ProgressBar proBar;
	ViewPager mViewPager;
	ViewPageAdapter mViewPageAdapter;
	

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppt_relapy);
		Bundle bundle=getIntent().getExtras();
		       pptId=bundle.getLong("pptId");
		       pageCount=bundle.getInt("pageCount");		
		       init();		       
		new getPptTask().execute(mViewList);		
	}
	
	/**
	 * 控件、变量初始化
	 */
	public void init()
	{
		 app =(myApp)getApplication();
		 pages=1;
		 isUpdating=false;
	     mViewPageAdapter =new ViewPageAdapter();
	     mViewList =new ArrayList<View>();
	     mViewList.ensureCapacity(pageCount+1);
	     
	     
	     proBarInfo=(TextView)findViewById(R.id.proBar_info);
	     pagesText=(TextView)findViewById(R.id.pages_text);
	     proBar =(ProgressBar)findViewById(R.id.progressBar_viewpager);
	     proBar.setMax(pageCount);
	     mViewPager=(ViewPager)findViewById(R.id.myViewPager);		     
	     mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			/**
			 * 当前页处于倒数第二页或最后一页同时PPT未加载完成且没有预加载线程正在处理时执行预加载
			 * 页面切换时更新当前页码
			 */
			public void onPageSelected(int position) {
				if(position>=mViewPager.getAdapter().getCount()-2&&mViewPager.getAdapter().getCount()<pageCount&&!isUpdating)
				{
					new getPptTask().execute(mViewList);
				}
				pagesText.setText("第"+(position+1)+"页");
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		  
	}

	
	
	
	/**
	 * 传入图片，更新ViewPager的适配器的View动态数组
	 * @param list
	 * @param bmp
	 */
	public void updateViewList(ArrayList<View> list,Bitmap bmp)
	{
		LayoutInflater inflater=getLayoutInflater(); 
		View view =inflater.inflate(R.layout.pptviewpage_item, null);
		ImageView iv=(ImageView)view.findViewById(R.id.image_ViewPage);
		iv.setImageBitmap(bmp);
		list.add(view);
		
	}	
	

	/**
	 * 加载PPT的异步线程
	 * @author Felix
	 *
	 */
	class getPptTask extends AsyncTask<ArrayList<View>, Integer, ArrayList<View>>
	{

		/**
		 * 标识加载执行中
		 */
		@Override
		protected void onPreExecute()
		{
			isUpdating=true;
			start=TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
		}
		
		/**
		 * 后台加载PPT线程
		 */
		@Override
		protected synchronized ArrayList<View> doInBackground(ArrayList<View>...list) {	
			
			Bitmap bmp;			
			for(int mark=pages;pages<=mark+2&&pages<=pageCount;pages++)
			{				
				bmp=new PptDownLoad().downLoadBitmap(app.getHttpClient(), pptId, pages);
				if(bmp==null)
				  {
					pages--;
				  }
				  else	
				  {
					  
					  Log.i("更新页码:", ""+pages);
				      updateViewList(list[0], bmp);				      
				      publishProgress(pages);
				  }		  
			}
			
			return mViewList;
			
		}
		
		
		/**
		 * 更新加载进度
		 */
		@Override
		protected void onProgressUpdate(Integer...current)
		{
			
			if(current[0]==pageCount)
			{
				proBar.setVisibility(View.GONE);
				proBarInfo.setText("-本PPT已全部加载完毕-");
			}
			else
			{
				proBar.setProgress(current[0]);
				proBarInfo.setText("共"+pageCount+"页,已加载"+current[0]+"页");
			}
			
		}
		
		/**
		 * 刷新ViewPager,取消加载执行中标识
		 */
		@Override
		protected void onPostExecute(ArrayList<View> newList)
		{
		
			mViewPageAdapter.setList(newList);
			if(mViewPager.getAdapter()==null)
			{
				mViewPager.setAdapter(mViewPageAdapter);
			}
			else
			{
				mViewPageAdapter.notifyDataSetChanged();			
			}	
			isUpdating=false;
			finish=TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes();
			Log.i("流量消耗：",((finish-start)/1024)+"KB");
		}
		
	}	

}
