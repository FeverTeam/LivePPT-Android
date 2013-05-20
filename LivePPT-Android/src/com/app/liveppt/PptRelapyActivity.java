package com.app.liveppt;

import java.util.ArrayList;

import com.app.adapter.ViewPageAdapter;
import com.app.httputils.PptDownLoad;
import com.app.httputils.myApp;
import com.app.login.R;

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

public class PptRelapyActivity extends Activity {
	int pageCount;
	Long pptId;
	boolean isUpdating=false;
	
	myApp app;	
	ArrayList<View> mViewList;
	Bitmap bmp;	
	TextView pagesText;
	TextView proBarInfo;
	ProgressBar proBar;
	ViewPager mViewPager;
	ViewPageAdapter mViewPageAdapter;
	
	int pages=1;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppt_relapy);
		Bundle bundle=getIntent().getExtras();
		       pptId=bundle.getLong("pptId");
		       pageCount=bundle.getInt("pageCount");		
		       init();
		       
		new task().execute(mViewList);		
		
	}
	public void init()
	{
		 app =(myApp)getApplication();
		 
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
			public void onPageSelected(int position) {
				if(position>=mViewPager.getAdapter().getCount()-2&&mViewPager.getAdapter().getCount()<pageCount&&!isUpdating)
				{
					new task().execute(mViewList);
				}
				pagesText.setText("第"+(position+1)+"页");
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		  
	}

	public void updateViewList(ArrayList<View> list,Bitmap bmp)
	{
		LayoutInflater inflater=getLayoutInflater(); 
		View view =inflater.inflate(R.layout.pptviewpage_item, null);
		ImageView iv=(ImageView)view.findViewById(R.id.image_ViewPage);
		iv.setImageBitmap(bmp);
		list.add(view);
		
	}	
	

	class task extends AsyncTask<ArrayList<View>, Integer, ArrayList<View>>
	{

		@Override
		protected void onPreExecute()
		{
			isUpdating=true;
		}
		
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
		}
		
	}	

}
