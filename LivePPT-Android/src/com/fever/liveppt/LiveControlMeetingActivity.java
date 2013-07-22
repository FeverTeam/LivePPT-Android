package com.fever.liveppt;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.fever.adapter.ViewPageAdapter;
import com.fever.utils.HttpRequest;
import com.fever.utils.ImageCache;
import com.fever.utils.MyApp;
import com.fever.utils.PptBitmapUtils;

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

/**
 * 控制主持会议类
 * @author Felix
 */
public class LiveControlMeetingActivity extends Activity {
	private ViewPager mViewPager;
	private ViewPageAdapter mViewAdapter;
	private Long pptId;
	private int pageCount;
	private int pages;
	private Long meetingId;
	private ArrayList<String> mViewList;
	private MyApp app;
	private boolean isUpdating;
	private ProgressBar proBar;
	private TextView proBarInfo;
	private TextView pagesText;
	private HttpRequest httpRequest;
	private String Url;
	private String strResult;	
	private boolean isSuccess;
	private ImageCache mCache;
	private getPptTask getTask;
	private setMeetingPageTask setTask;


    /**
     * 初始化界面
     * @param savedInstanceState
     * last modified: Frank
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_control_meeting);
		
		Bundle bundle=getIntent().getExtras();
		pptId=bundle.getLong("pptId");
		meetingId=bundle.getLong("meetingId");
		pageCount=bundle.getInt("pageCount");
		init();
		new getPptTask().execute();
	}


    /**
     * 初始化相关参数
     */
    private void init(){
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	app=(MyApp)getApplication();
    	pages=1;    	
    	mCache = new ImageCache(this);
	    mCache.clearDiskCache(pptId.toString());
	    mCache.init(this);
    	isUpdating=false;
    	isSuccess=false;
    	httpRequest=new HttpRequest();
    	Url=HttpRequest.httpProtocol+HttpRequest.hostName+"/app/setMeetingPageIndex";
    	
    	mViewAdapter=new ViewPageAdapter(mViewList, mCache,getLayoutInflater());
    	mViewList=new ArrayList<String>();
    	mViewList.ensureCapacity(pageCount+1);
    	mViewPager=(ViewPager)findViewById(R.id.myViewPager_control_meeting);
    	
    	proBarInfo=(TextView)findViewById(R.id.proBar_info_control_meeting);
	    pagesText=(TextView)findViewById(R.id.pages_text_control_meeting);
	    proBar =(ProgressBar)findViewById(R.id.progressBar_control_meeting);
	    proBar.setMax(pageCount);  
	    
	    mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			/**
			 * 当前页处于倒数第三页至最后一页之间同时PPT未加载完成且没有预加载线程正在处理时执行预加载
			 * 页面切换时更新当前页码
             * last modified: Frank
			 */
			public void onPageSelected(int position) {
				if(position>=mViewPager.getAdapter().getCount()-3&&mViewPager.getAdapter().getCount()<pageCount&&!isUpdating){
					getTask=new getPptTask();
					getTask.execute();
				}		
				setTask=new setMeetingPageTask();
				setTask.execute(position+1);
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
	 */
	class getPptTask extends AsyncTask<Void, Integer,Void>{

        /**
		 * 标识加载执行中
         * last modified: Frank
		 */
		@Override
		protected void onPreExecute()
		{
			isUpdating=true;
		}
		
		/**
		 * 后台加载PPT线
         * last modified: Frank
		 */
		@Override
		protected synchronized Void doInBackground(Void ...v) {			
		
			String key;
			Bitmap bmp;		
			
			for(int mark=pages;pages<=mark+10&&pages<=pageCount;pages++){
				if (isCancelled()) 
					return null;
				key=pptId+"-"+pages;
				bmp=mCache.getBitmap(key);
				while(bmp==null){
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
         *last modified: Frank
		 */
		@Override
		protected void onProgressUpdate(Integer...current){
			mViewAdapter.setList(mViewList);
			if(mViewPager.getAdapter()==null){
				mViewPager.setAdapter(mViewAdapter);
			}
			else{
				mViewAdapter.notifyDataSetChanged();			
			}
			
			
			if(current[0]==pageCount){
				proBar.setVisibility(View.GONE);
				proBarInfo.setText("-本PPT已全部加载完毕-");
			}
			else{
				proBar.setProgress(current[0]);
				proBarInfo.setText("共"+pageCount+"页,已加载"+current[0]+"页");
			}			
		}
		
		/**
		 *取消加载执行中标识
         *last modified: Frank
		 */
		@Override
		protected void onPostExecute(Void v){
		   isUpdating=false;			
		}		
	}	


	
	/**
	 * 设置直播页面
	 * @author Felix
	 */
	class setMeetingPageTask extends AsyncTask<Integer,Void,Void>{

		@Override
		protected Void doInBackground(Integer... params) {			
			
			ArrayList<NameValuePair> paramList=new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("meetingId", meetingId+""));
			paramList.add(new BasicNameValuePair("pageIndex", params[0]+""));
			
			try {
				while(isSuccess!=true&&!isCancelled()){//失败则重试
					strResult=httpRequest.HttpPostRequest(app.getHttpClient(), Url, paramList);					
					isSuccess=new JSONObject(strResult).getBoolean("isSuccess");
				}
				isSuccess=false;
				
			} 
			catch (JSONException e) {
			   e.printStackTrace();
			}			
			return null;
		}
		
	}
	
	
	/**退出处理
	 * 终结异步线程
	 * 清除缓存文件
	 * last modified: Frank
	 */
	@Override
	   protected void onDestroy() {
		  if (getTask != null && getTask.getStatus()!= AsyncTask.Status.FINISHED)
	            getTask.cancel(true);	 
		  if (setTask != null && setTask.getStatus()!= AsyncTask.Status.FINISHED)
	            setTask.cancel(true);
		  mCache.clearMemCache();      	      
	       Log.i("LiveControl", "onDestroy");
	       super.onDestroy();
	   }
}
