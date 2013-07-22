package com.app.liveppt;
import com.app.utils.HttpRequest;
import com.app.utils.ImageCache;
import com.app.utils.MyToast;
import com.app.utils.PptBitmapUtils;
import com.app.utils.MyApp;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class LiveWatchingMeetingActivity extends Activity {
	private MyApp app;	
	private WebSocketConnection mConnection;	
	private ImageView pptImag;
	private Long meetingId;
	private Long pptId;	
	private int  page;
	private int  pageCount;
	private int  preLoadPage;//当前正向预载的页码
	private int  reLoadPage;//当前逆向预载的页码
	private String wsLatestMsg;
	private String wsuri;
	private boolean isUpdating;
	private boolean toPreLoad;
	private boolean toReLoad;
	private ImageCache mCache;
	private LiveGetPptTask getTask;
	private preLoadTask preloadTask;
	private reLoadTask  reloadTask;
	

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
    /**
     * 初始化控件和标识状态
     * @author Felix
     */
	public void init()
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		pptImag=(ImageView)findViewById(R.id.live_Watching_imageView);
	    app=(MyApp)getApplicationContext();
	    mCache = new ImageCache(this);
	    mCache.clearDiskCache(pptId.toString());
	    mCache.init(this);
	    isUpdating=false;
	    toPreLoad=false;
	    toReLoad=false;
	}
	
	
	/**
	 * 启动webSocket
	 * @author Felix
	 */
	public void start()
	{
		mConnection = new WebSocketConnection();
		wsuri=HttpRequest.ws_Protocol+HttpRequest.hostName+":"+HttpRequest.ws_port+"/viewWebsocket";		
		try 
		{
			mConnection.connect(wsuri, new WebSocketHandler()
			{
				@Override
	            public void onOpen() 
				{
					mConnection.sendTextMessage(meetingId.toString());//发送会议请求
					new MyToast().alert(getApplicationContext(), "正在进入会议..ID:"+meetingId);	               
	            }

	            @Override
	            public void onTextMessage(String payload) //接收会议操控端反馈
	            {               
	               wsLatestMsg=payload;//全局参数，标记最新请求
	               page=getPage(wsLatestMsg);
	               if(!isUpdating)//若正在更新则拦截
	               {   				 
	            	 getTask =new LiveGetPptTask();
	            	 getTask.execute(wsLatestMsg);//更新最新PPT页面
	   				 if(toPreLoad==false)//上一次预加载结束则激活新一轮预加载
	   				 {
	   					 toPreLoad=true;
	   					 preloadTask=new preLoadTask();
	   					 preloadTask.execute();
	   				 }
	   				 if(toReLoad==false)//上一次预加载结束则激活新一轮预加载
	   				 {
	   					 toReLoad=true;
	   					 reloadTask= new reLoadTask();
	   					 reloadTask.execute();
	   				 }	                                  
	               }
	               
	            }

	            @Override
	            public void onClose(int code, String reason) 
	            {	               
	               
	               new MyToast().alert(getApplicationContext(),"会议结束");
	               Log.i("onClose", reason);
	               setTitle(reason);
	            }
				
			});
		} 
		catch (WebSocketException e)
		{
			
			e.printStackTrace();
			
		}		
	}
	
	
	
	
	
	
	/**
	 * 加载当前页面PPT线程
	 * @author Felix
	 *
	 */
	
	class LiveGetPptTask extends AsyncTask<String, String, Bitmap>
	{

		
		@Override
		protected void onPreExecute()
		{
			isUpdating=true;//忙状态
			wsLatestMsg=null;//清空最新反馈，线程结束时判断此全局参数是否依然为null即可知道是否有最新请求
		}
		
		
		@Override
		protected  Bitmap doInBackground(String...params) 
		{		
			
		    int currPage=getPage(params[0]);
		    
			Bitmap bitmap;				
			bitmap=mCache.getBitmap(params[0]);	//查看缓存中是否有对应图片			
			
			if(bitmap==null)//没有，通过网络请求下载
			{					   
				bitmap=null;
				while(bitmap==null)
				{
				  if (isCancelled()||wsLatestMsg!=null) 
					{
					  Log.i("GETPPT线程提前结束:","有新反馈信息或线程主动结束");
					  return null;//线程结束时跳出循环或者有新反馈信息时结束线程
					}
				  bitmap=new PptBitmapUtils().downLoadBitmap(app.getHttpClient(), pptId, currPage);
				  if(bitmap==null)
					  Log.i("getPptFail","网络下载失败，正在重试..");
				}		   
				
				mCache.putBitmap(params[0],bitmap);//缓存在本地					
				Log.i("LiveGet插入:", pptId+"-"+currPage+"##"+params[0]);
				
				publishProgress("==本地没有缓存目标图片==");
			}			
		   return bitmap;
		}
		
		
	
		@Override
		protected void onProgressUpdate(String...params)
		{
			Log.i("LiveGetPPT",params[0]);
			//new MyToast().alert(getApplicationContext(),params[0]);
		}
		
		/**
		 * 取消忙状态 
		 * 
		 */
		
		
		@Override
		protected void onPostExecute(Bitmap bmp)
		{
			isUpdating=false;
			if(bmp==null)
			{
				pptImag.setImageDrawable(getResources().getDrawable(R.drawable.img_not_found));
			}
			pptImag.setImageBitmap(bmp);
			if(wsLatestMsg!=null)//有新请求		
			{
			  new LiveGetPptTask().execute(wsLatestMsg);
			}
		}		
	}	
	
	
	
	
	
	/**
	 * 正向预加载线程
	 * @author Felix
	 *
	 */
	class preLoadTask extends AsyncTask<Void,Void,Void>
	{
		@Override
		protected void onPreExecute()
		{
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bmp;
			   int flag;//同步当前页码，若在预加载过程中匹配不一致则刷新当前页码并在新位置预加载
			
			for(preLoadPage=page+1,flag=page;preLoadPage<=page+15&&preLoadPage<=pageCount;preLoadPage++)
			{
				if (isCancelled()) 
					return null;
				if(flag!=page)
				{
					preLoadPage=page+1;
				    flag=page;
				}
				String key=pptId+"-"+preLoadPage;
				
			    bmp= mCache.getBitmap(key);				    
				
				if(bmp==null)
				{
					for(int t=1;t<=3;t++)//若下载失败则重新下载，重连三次失败则放弃
					{
						bmp=new PptBitmapUtils().downLoadBitmap(app.getHttpClient(), pptId, preLoadPage);
						if(bmp!=null)
						{							
						   mCache.putBitmap(key,bmp);								    
						   break;
							
						}	
						Log.i("peloadFail","网络下载失败，正在重试..");
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
	
	
	
	
	/**
	 * 反向预加载线程
	 * @author Felix
	 *
	 */
	class reLoadTask extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected void onPreExecute()
		{
			
		}
		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bmp;
			   int flag;//同步当前页码，若在预加载过程中匹配不一致则刷新当前页码并在新位置预加载
			
			for(reLoadPage=page-1,flag=page;reLoadPage>=1&&reLoadPage>=page-15;reLoadPage--)
			{
				if (isCancelled()) 
					return null;
				if(flag!=page)
				{
					flag=page;
					reLoadPage=page-1;
				}
				String key=pptId+"-"+reLoadPage;
				bmp= mCache.getBitmap(key);		    
				
				if(bmp==null)
				{
					for(int t=1;t<=3;t++)//若下载失败则重新下载，重连三次失败则放弃
					{
						bmp=new PptBitmapUtils().downLoadBitmap(app.getHttpClient(), pptId, reLoadPage);
						if(bmp!=null)
						{					
							mCache.putBitmap(key,bmp);							    
							break;
							
						}
						Log.i("reloadFail","网络下载失败，正在重试..");
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
	
	
	
	/**
	 * 根据返回信息提取页码
	 * @param message
	 * @return page
	 * @author Felix
	 */
	
	private int getPage(String message)
	{
		String temp[]=new String[2];
			   temp=message.split("-");		   
			   page=Integer.parseInt(temp[1]);
		return page;
	}
	
	
	/**
	 * 退出处理，清理缓存，解除websocket
	 * @author Felix
	 */
	
	  @Override
	   protected void onDestroy() 
	  {
		  if (getTask != null && getTask.getStatus() != AsyncTask.Status.FINISHED)
	            getTask.cancel(true);
		  if (reloadTask != null && reloadTask.getStatus() != AsyncTask.Status.FINISHED)
			  reloadTask.cancel(true);
		  if (preloadTask != null && preloadTask.getStatus() != AsyncTask.Status.FINISHED)
			  preloadTask.cancel(true);
	       
	       mCache.clearMemCache();      	      
	       Log.i("LiveWatch", "onDestory");
	       if (mConnection.isConnected()) 
	       {
	          mConnection.disconnect();
	       }
	       super.onDestroy();
	   }

	

}
