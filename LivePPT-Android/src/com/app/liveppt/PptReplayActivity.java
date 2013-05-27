package com.app.liveppt;
import java.util.ArrayList;
import com.app.adapter.ViewPageAdapter;
import com.app.liveppt.R;
import com.app.utils.ImageCache;
import com.app.utils.PptBitmapUtils;
import com.app.utils.myApp;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PptReplayActivity extends Activity {
	
	private Long pptId;
	private boolean isUpdating;
	private int pages;
	private int pageCount;
	private myApp app;	
	private ArrayList<String> mViewList;	
	private TextView pagesText;
	private TextView proBarInfo;
	private ProgressBar proBar;
	private ViewPager mViewPager;
	private ViewPageAdapter mViewPageAdapter;
	private ImageCache mCache;
	private getPptTask getTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppt_relapy);
		Bundle bundle=getIntent().getExtras();
		       pptId=bundle.getLong("pptId");
		       pageCount=bundle.getInt("pageCount");		       
		       init();		       
		       getTask=new getPptTask();
		       getTask.execute();		
	}
	
	/**
	 * 控件、变量初始化
	 */
	public void init()
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		 app =(myApp)getApplication();
		 pages=1;
		 isUpdating=false;
		 mCache = new ImageCache(this);
		 mCache.clearDiskCache(pptId.toString());
		 mCache.init(this);
	     
	     mViewList =new ArrayList<String>();
	     mViewPageAdapter =new ViewPageAdapter(mViewList, mCache,getLayoutInflater());
	     mViewList.ensureCapacity(pageCount+1); 
	     
	     proBarInfo=(TextView)findViewById(R.id.proBar_info);
	     pagesText=(TextView)findViewById(R.id.pages_text);
	     proBar =(ProgressBar)findViewById(R.id.progressBar_viewpager);
	     proBar.setMax(pageCount);
	     mViewPager=(ViewPager)findViewById(R.id.myViewPager);		     
	     mViewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			
			@Override
			/**
			 * 当前页处于倒数第三页至最后一页之间同时PPT未加载完成且没有预加载线程正在处理时执行预加载
			 * 页面切换时更新当前页码
			 */
			public void onPageSelected(int position) {
				
				if(position>=mViewPager.getAdapter().getCount()-3&&mViewPager.getAdapter().getCount()<pageCount&&!isUpdating)
				{
					getTask=new getPptTask();
					getTask.execute();
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
	 * 加载PPT的异步线程
	 * @author Felix
	 *
	 */
	
	class getPptTask extends AsyncTask<Void, Integer,Void>
	{
		/**
		 * 标识加载执行中
		 */
		@Override
		protected void onPreExecute()
		{
			isUpdating=true;
		}
		
		/**
		 * 后台加载PPT线程
		 */
		@Override
		protected synchronized Void doInBackground(Void ...v) {			
		
			String key;
			Bitmap bmp;		
			
			for(int mark=pages;pages<=mark+10&&pages<=pageCount;pages++)
			{		
				if (isCancelled()) 
					return null;
				key=pptId+"-"+pages;
				bmp=mCache.getBitmap(key);
				while(bmp==null)
				{
					Log.i("getPptTask", "不存在");
					bmp=new PptBitmapUtils().downLoadBitmap(app.getHttpClient(), pptId,pages);
				}
				mCache.putBitmap(key, bmp);
				mViewList.add(key);							      
			    publishProgress(pages);					  
			}			
			return null;			
		}
		
		
		/**
		 * 更新加载进度
		 * 刷新UI
		 */
		@Override
		protected void onProgressUpdate(Integer...current)
		{
			mViewPageAdapter.setList(mViewList);
			if(mViewPager.getAdapter()==null)
			{
				mViewPager.setAdapter(mViewPageAdapter);
			}
			else
			{
				mViewPageAdapter.notifyDataSetChanged();			
			}
			
			
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
		 *取消加载执行中标识
		 */
		@Override
		protected void onPostExecute(Void v)
		{				
		   isUpdating=false;			
		}		
	}	
	
	
	
	
	/**退出处理
	 * 终结异步线程
	 * 清除缓存文件
	 * 
	 */
	@Override
	   protected void onDestroy() 
	  {
		  if (getTask != null && getTask.getStatus()!= AsyncTask.Status.FINISHED)
	            getTask.cancel(true);	       
		   mCache.clearMemCache();    	  		     
	       Log.i("PptReplayActivity", "onDestory");	       
	       super.onDestroy();
	   }

}
