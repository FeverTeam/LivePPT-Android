package com.app.liveppt;
import com.app.utils.ImageCache;
import com.app.utils.MyToast;
import com.app.utils.PptDownLoad;
import com.app.utils.myApp;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class LiveWatchingMeetingActivity extends Activity {
	myApp app;	
	WebSocketConnection mConnection;	
	ImageView pptImag;
	Long meetingId;
	Long pptId;	
	int  page;
	int  pageCount;
	int  preLoadPage;
	int reLoadPage;
	String wsLatestMsg;
	String wsuri;
	boolean isUpdating;
	boolean toPreLoad;
	boolean toReLoad;
	ImageCache mCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_watching_meeting);		    
		Bundle bundle=getIntent().getExtras();
	    meetingId=bundle.getLong("meetingId");
	        pptId=bundle.getLong("pptId");
	    pageCount=bundle.getInt("pageCount");
	    init();
		start();
		
	}
	
	public void init()
	{
		pptImag=(ImageView)findViewById(R.id.live_Watching_imageView);
	    app=(myApp)getApplicationContext();
	    mCache = new ImageCache(getApplicationContext());	    
	    isUpdating=false;
	    toPreLoad=false;
	    toReLoad=false;
	}
	
	public void start()
	{
		mConnection = new WebSocketConnection();
		wsuri="ws://live-ppt.com:9000/viewWebsocket";		
		try 
		{
			mConnection.connect(wsuri, new WebSocketHandler()
			{
				@Override
	            public void onOpen() 
				{
					mConnection.sendTextMessage(meetingId.toString());
					new MyToast().alert(getApplicationContext(), "正在进入会议..ID:"+meetingId);
	               
	            }

	            @Override
	            public void onTextMessage(String payload) 
	            {    	
	               
	               wsLatestMsg=payload;
	               if(!isUpdating)
	               {  
	            	   
	            	 String temp[]=new String[2];
	   				 temp=wsLatestMsg.split("-");		   
	   				 page=Integer.parseInt(temp[1]);
	            	 new LiveGetPptTask().execute(wsLatestMsg);
	   				 if(toPreLoad==false)
	   				 {
	   					 toPreLoad=true;
	   					 new preLoadTask().execute();
	   				 }
	   				 if(toReLoad==false)
	   				 {
	   					 toReLoad=true;
	   					 new reLoadTask().execute();
	   				 }
	            	 //new MyToast().alert(getApplicationContext(), "执行更新的PPT："+wsLatestMsg);	
	                 
	                                  
	               }
	               
	            }

	            @Override
	            public void onClose(int code, String reason) 
	            {	               
	               
	               new MyToast().alert(getApplicationContext(),"会议结束"+reason);
	               setTitle(reason);
	            }
				
			});
		} 
		catch (WebSocketException e)
		{
			
			e.printStackTrace();
			
		}		
	}
	
	
	class LiveGetPptTask extends AsyncTask<String, String, Bitmap>
	{

		
		@Override
		protected void onPreExecute()
		{
			isUpdating=true;
			wsLatestMsg=null;
		}
		
		
		@Override
		protected synchronized Bitmap doInBackground(String...params) 
		{	
		  String temp[]=new String[2];
				 temp=params[0].split("-");
				 int page_;
				 page_=Integer.parseInt(temp[1]);
			Bitmap bitmap;
			Log.i("live页码", page_+"");
			synchronized (mCache)
			{
				bitmap=mCache.getBitmap(params[0]);				
			}
			
			if(bitmap==null)
			{					   
				bitmap=null;
				while(bitmap==null)
				{
				  bitmap=new PptDownLoad().downLoadBitmap(app.getHttpClient(), pptId, page_);
				}				
		   
				synchronized (mCache)
				{
					mCache.putBitmap(params[0],bitmap);	
					Log.i("LiveGet插入:", pptId+"-"+page_+"##"+params[0]);
				}
				publishProgress("本地没有啊！！");
			}
			else
			{
				//publishProgress("本地存在副本:"+params[0]);
			}
		   return bitmap;
		}
		
		
	
		@Override
		protected void onProgressUpdate(String...params)
		{
			new MyToast().alert(getApplicationContext(),params[0]);
		}
		
		
		@Override
		protected void onPostExecute(Bitmap bmp)
		{
			isUpdating=false;
			pptImag.setImageBitmap(bmp);
			if(wsLatestMsg!=null)		
			{
			  new LiveGetPptTask().execute(wsLatestMsg);
			}
		}
		
	}
	
	
	
	
	class preLoadTask extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected void onPreExecute()
		{
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bmp;
			
			for(preLoadPage=page+1;preLoadPage<=page+5&&preLoadPage<=pageCount;preLoadPage++)
			{
				Log.i("pre页码", preLoadPage+"");
				String key=pptId+"-"+preLoadPage;
				synchronized(mCache) 
				{
				    bmp= mCache.getBitmap(key);
				}
				if(bmp==null)
				{
					while(bmp==null)
					{
						bmp=new PptDownLoad().downLoadBitmap(app.getHttpClient(), pptId, preLoadPage);
					}					
					synchronized(mCache) 
					{
					    mCache.putBitmap(key,bmp);
					}
				}			
			}			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void i)
		{			
			toPreLoad=false;
		}
		
	}
	
	class reLoadTask extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected void onPreExecute()
		{
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bmp;
			
			for(reLoadPage=page-1;reLoadPage>=1&&reLoadPage>=page-5;reLoadPage--)
			{
				Log.i("re页码", reLoadPage+"");
				String key=pptId+"-"+reLoadPage;
				synchronized(mCache) 
				{
				    bmp= mCache.getBitmap(key);
				}
				if(bmp==null)
				{
					while(bmp==null)
					{
						bmp=new PptDownLoad().downLoadBitmap(app.getHttpClient(), pptId, reLoadPage);
					}					
					synchronized(mCache) 
					{
					    mCache.putBitmap(key,bmp);
					}
				}			
			}			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void i)
		{			
			toReLoad=false;
		}
		
	}
	
	  @Override
	   protected void onDestroy() 
	  {
	       super.onDestroy();
	       mCache.clearCache();
	       Log.i("退出", "onDestory");
	       if (mConnection.isConnected()) 
	       {
	          mConnection.disconnect();
	       }
	   }

	

}
