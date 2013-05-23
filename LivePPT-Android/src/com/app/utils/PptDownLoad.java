package com.app.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class PptDownLoad {
	private Bitmap bitmap;
	private HttpGet httpGet;
	private String uri;
	private HttpResponse response;	
	private HttpEntity entity;
	
	
	public  Bitmap downLoadBitmap(HttpClient client,Long pptId,int pageId)
	{
		
		uri="http://live-ppt.com/app/getPptPage/";
		httpGet=new HttpGet(uri+pptId+"/"+pageId);
		try 
		{
			response=client.execute(httpGet);
			entity=response.getEntity();
			if(response.getStatusLine().getStatusCode()!=200)
			{			
				Log.i("图片下载出错", response.getStatusLine().toString());
				return null;
			}
			else
			if(entity!=null)
			{
				InputStream is =null;
				is=entity.getContent();	
				
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565; 
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				opt.inSampleSize=2;
				opt.inJustDecodeBounds=false;
				long freeStart = Runtime.getRuntime().freeMemory();
				bitmap=BitmapFactory.decodeStream(is,null,opt);
				long freeEnd = Runtime.getRuntime().freeMemory();
				Log.i("图片内存消耗测试：","freeStart:"+freeStart+"\n freeEnd:"+freeEnd+"\n 相差："+(freeStart-freeEnd));
				is.close();		
				entity.consumeContent();
				
				return bitmap;
			}
		} catch (ClientProtocolException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
       return null;
	}

}
