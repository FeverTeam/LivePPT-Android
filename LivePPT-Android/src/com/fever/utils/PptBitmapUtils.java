package com.fever.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * PPT图片处理类
 * @author Felix
 */
public class PptBitmapUtils {
	private Bitmap bitmap;
	private HttpGet httpGet;
	private String uri;
	private HttpResponse response;	
	private HttpEntity entity;


    /**
     * 从无武器端下载图片
     * @param client
     * @param pptId
     * @param pageId
     * last modified: Frank
     */
	public  Bitmap downLoadBitmap(HttpClient client,Long pptId,int pageId){
		uri=HttpRequest.httpProtocol+HttpRequest.hostName+"/app/getPptPage?";
		httpGet=new HttpGet(uri+"pptId="+pptId+"&"+"pageIndex="+pageId);
		try {
			response=client.execute(httpGet);
			entity=response.getEntity();
			if(response.getStatusLine().getStatusCode()!=200){
				Log.i("图片下载出错", response.getStatusLine().toString());
				return null;
			}
			else
			if(entity!=null){
				Log.i(pptId+"-"+pageId+"图片大小",entity.getContentLength()+"");
				InputStream is =null;
				is=entity.getContent();				
				bitmap=decodeBitapFromStream(is,1);					
				is.close();		
				entity.consumeContent();				
				return bitmap;
			}
		}
        catch (ClientProtocolException e) {
			e.printStackTrace();
		}
        catch (IOException e) {
			e.printStackTrace();
		}
       return null;
	}


	/**
	 * 根据要求尺寸计算图片的缩放比例
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return inSamplesize
	 */
	/*
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int height = options.outHeight;
	    final int  width = options.outWidth;
	          int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) 
	    {	        
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

    return inSampleSize;
     }
	*/

	
	/** 
	 * 解码输入流生成指定尺寸的图片
	 * @param inputStream
	 * @param reqWidth
	 * @param reqHeight
	 * @return bitmap
	 */
	/*
	public static Bitmap decodeBitapFromStream(InputStream inputStream,int reqWidth, int reqHeight) 
	{
		//获取原始图片尺寸参数，但不生成实体图片
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    InputStream is =new BufferedInputStream(inputStream);
	    try 
	    {
	    	is.mark(is.available());			
		    BitmapFactory.decodeStream(is, null, options);
		    //根据目标尺寸求缩放比例		    
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);	    
		    options.inJustDecodeBounds = false;
		    options.inPreferredConfig =Config.RGB_565;		    
			is.reset();			
	    } 
	    catch (IOException e) 
	    {
	    	Log.i("TAG",e.getMessage());
			e.printStackTrace();
		}
	    //生成最终图片
	    return BitmapFactory.decodeStream(is, null, options);
	}
	*/


    /**
     * 从输入流解压图片
     * @param inputStream
     * @param inSampleSize
     * @return 图像bitmap
     * last modified: Frank
     */
	public static Bitmap decodeBitapFromStream(InputStream inputStream,int inSampleSize){
		
		 final BitmapFactory.Options options = new BitmapFactory.Options();
		 options.inSampleSize=inSampleSize;	
		 options.inPurgeable =true;
		 options.inPreferQualityOverSpeed=true;
		 options.inPreferredConfig=Config.RGB_565;
		 /*try {

				BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options,true);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			*/
		        	 
		 return BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options);	
		 
	}	
}



/**
 * Android对于InputStream流有个小bug在慢速网络的情况下可能产生中断，
 * 重写FilterInputStream处理skip方法来解决这个bug。  
 * @author Felix
 */
class FlushedInputStream extends FilterInputStream {

    /**
     * FlushedInputStream构造器
     * @param inputStream
     */
    public FlushedInputStream(InputStream inputStream) {
		super(inputStream);
	}


    /**
     * 重写skip方法
     * @param n
     * @return
     * @throws IOException
     */
	@Override
	public long skip(long n) throws IOException {
		long totalBytesSkipped = 0L;
		while (totalBytesSkipped < n) {
			long bytesSkipped = in.skip(n - totalBytesSkipped);
			if (bytesSkipped == 0L) {
				int by_te = read();
				if (by_te < 0) {
					break; 
				}
                else {
					bytesSkipped = 1; 
				}
			}
			totalBytesSkipped += bytesSkipped;
		}
		return totalBytesSkipped;
	}
}

