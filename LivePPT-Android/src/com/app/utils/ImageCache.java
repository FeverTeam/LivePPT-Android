package com.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache {
	private LruCache<String, Bitmap> mBmpCache;
	private LinkedHashMap<String, SoftReference<Bitmap>> mSoftBmpCache;
	private LruCache<String, Long> mFileCache;
	private File diskCacheDir;
	private int maxMemory;
	private int BMP_CACHE_SIZE;
	private int BMP_SOFT_CACHE_SIZE ;
	private int BMP_DISK_CACHE_SIZE ;
	/**
	 * 图片缓存处理类
	 * @param context
	 * @author Felix
	 */
	public ImageCache(Context context)
	{			
	   init(context);
	}
	
	
	
	@SuppressWarnings("serial")
	private void init(Context context)
    {
		/**
		 * 根据最大内存设置内存强引用缓存区的大小
		 * 设置软引用内存区大小
		 * 设置磁盘缓存大小
		 *   
		 */
		maxMemory=((int)Runtime.getRuntime().maxMemory()/1024);		
		BMP_CACHE_SIZE=maxMemory/4; //单位 KB
		
		BMP_SOFT_CACHE_SIZE = 40;
		
		BMP_DISK_CACHE_SIZE =4*1024;//单位 KB
		
		Log.i("maxMemory", maxMemory+"");
		
		
		/**
		 * 设置磁盘缓存位置
		 * 
		 */		
		
		diskCacheDir=getDiskCacheDir(context, "pptCache");
		diskCacheDir.mkdirs();
		
		
		/**
		 * 初始化内存强引用缓存		
		 */
		mBmpCache = new LruCache<String, Bitmap>(BMP_CACHE_SIZE) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				
				Log.i("图片大小:", value.getRowBytes()*value.getHeight()/1024+"KB");
				return value.getRowBytes()*value.getHeight()/1024;//单位 KB				
			}
			@Override
			protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
				
				putBitmapToDisk(key, oldValue);						
				Log.i("LruCacheRemoved","内存强引用缓存转磁盘缓存");
			}
		};
		
		
		
		/**
		 * 初始化内存软引用缓存
		 * 
		 */
		mSoftBmpCache = new LinkedHashMap<String, SoftReference<Bitmap>>(BMP_SOFT_CACHE_SIZE, 0.75f, true){
			@Override
			public SoftReference<Bitmap> put(String key,SoftReference<Bitmap> value){
				
				return super.put(key, value);
			}

			@Override
			protected boolean removeEldestEntry(LinkedHashMap.Entry<String, SoftReference<Bitmap>> eldest){
				if (size()>BMP_SOFT_CACHE_SIZE){
					
					return true;
				}
				return false;
			}
		};
		
		
		
		/**
		 * 初始化磁盘缓存
		 * 注意：
		 * 本缓存对象是String-Long键值对，String(key)对应文件唯一标识，Long(value)对应缓存文件大小
		 * mFileCache根据Long值由LRU规则判断哪个一个key对应的缓存文件需要被踢出磁盘缓存
		 */
		mFileCache = new LruCache<String, Long>(BMP_DISK_CACHE_SIZE){
			@Override
			protected int sizeOf(String key, Long value) {
				
				return value.intValue()/1024;				
			}

			@Override//磁盘缓存超出缓存上限则取对应的图片(LRU)存入软引用缓存
			protected void entryRemoved(boolean evicted, String key,Long oldValue, Long newValue) {
				
				 File file=getFile(key);
				 if(file!=null)
				 {
					 final SoftReference<Bitmap> bitmap =new SoftReference<Bitmap>(getBitmapFromDisk(key));
						if(bitmap!=null)
						{
							mSoftBmpCache.put(key, bitmap);
						} 
						file.delete();
				 }				 
			}			
		};
	}
	
	
	
	
	
	/**
	 * 获取磁盘缓存路径，优先使用外置缓存路径，没有则使用内置缓存路径
	 * @param context
	 * @param name
	 * @return 指定目录文件
	 * @author Android Official Reference
	 */
	
	private static File getDiskCacheDir(Context context, String name) 
	{	    
	    
		final String cachePath =Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED||!Environment.isExternalStorageRemovable() 
	                            ?context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
	                            Log.i("缓存路径:", cachePath + File.separator + name);
	                            new MyToast().alert(context, cachePath + File.separator + name);
	    return new File(cachePath + File.separator + name);
	}
	
	
	
	/**
	 * 取磁盘缓存指定名称的文件
	 * @param fileName
	 * @return file
	 */
	private File getFile(String fileName) 
	{  
		File file = new File(diskCacheDir, fileName); 
             
		if(!file.exists())
		 return null;
			
		 return file; 
        
    }  
	
	
	/**
	  * 获取指定文件路径的输出流
	  * @param key
	  * @return fos / null
	  * @author Felix
	  */
	 private FileOutputStream getOutputStream(String key) 
	 {  
	       if(diskCacheDir == null)  
	            return null;  
	        FileOutputStream fos;
			try
			{
				File file =new File(diskCacheDir.getAbsolutePath(), key);
				file.createNewFile();
				fos = new FileOutputStream(file);
				return fos;
				
			}
			catch (FileNotFoundException e) 
			{
				Log.i("文件读写出错:","无法写入");
			  e.printStackTrace();
			} catch (IOException e)
			{
				Log.i("文件创建出错", e.getMessage());
				e.printStackTrace();
			}
			return null;  	         
	  }  
	 
	
	/**
	 * 将图片存入磁盘缓存中
	 * @param key
	 * @param bitmap
	 * @return 是否成功存入
	 * @author Felix
	 */
	
	 private boolean putBitmapToDisk(String key, Bitmap bitmap)
	 {  
	        File file = getFile(key);
	        if(file!= null)//查看是否已存在 
	        {              
	           return true;  
	        }        
	        
	        FileOutputStream fos = getOutputStream(key);//创建输出流
	        boolean isSuccess = bitmap.compress(CompressFormat.JPEG, 100, fos);//转换图片至目标位置  
	        try 
	        {
				fos.flush();
				fos.close();
			} 
	        catch (IOException e)
	        {	
	        	Log.i("TAG",e.getMessage());
				e.printStackTrace();
			}  
	          
	        if(isSuccess)
	        {  
	            synchronized(mFileCache)
	            {  
	                mFileCache.put(key, getFile(key).length()); 
	            }  
	            return true;   
	        }  
	        return false;  
	    }  
	 
	 
	 
	 
	 /**
	  * 尝试从磁盘缓存中获取图片
	  * @param key
	  * @return bitmap / null
	  * @author Felix
	  */
	 
	 private Bitmap getBitmapFromDisk(String key)
	 { 
		/*图片可回收
		 BitmapFactory.Options opts;
		opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		*/
		
		File bitmapFile = getFile(key);
		if (bitmapFile!= null) 
		{
			try 
			{
				Bitmap bitmap;
				bitmap = BitmapFactory.decodeStream(new FileInputStream(bitmapFile), null,null);
				if (bitmap != null)
				{
					mBmpCache.put(key, bitmap);
					return bitmap;
				}
			} 
			catch (FileNotFoundException e) 
			{
				Log.i("TAG", e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	 }
	  
	 
	 
	 
	 /**
	  * <外部调用接口>
	  * 将图片保存至内存中 
	  * @param key
	  * @param bitmap
	  * @return 是否成功
	  * @author Felix
	  */	 
	public boolean putBitmap(String key, Bitmap bitmap)
	{  
        if(bitmap!= null)
        {  
            synchronized(mBmpCache)
            {  
            	mBmpCache.put(key, bitmap);  
            }  
            return true;  
        }         
        return false;  
    }  	
	
	
	
	
	/**
	 * <外部调用接口>
	 * 尝试从缓存中获取图片
	 * 查找缓存顺序:内存强引用缓存-内存软引用缓存-磁盘缓存
	 * @param key
	 * @return bitmap / null
	 */	
	
	public Bitmap getBitmap(String key)
	{  
        synchronized(mBmpCache)
        {  
            final Bitmap bitmap = mBmpCache.get(key);  
            if(bitmap != null)  
                return bitmap;  
        }  
        //内存强引用缓存区不存在，查找软引用缓存区
        synchronized(mSoftBmpCache)
        {  
            SoftReference<Bitmap> bmpReference = mSoftBmpCache.get(key);  
            if(bmpReference!= null)
            {  
                final Bitmap bitmap =bmpReference.get();  
                if(bitmap != null)
                {
                	Log.i("SoftReference", "从软引用缓存中找到图片");
                	mBmpCache.put(key, bitmap);
                	mSoftBmpCache.remove(key);                 	
                    return bitmap; 
                }                
            }  
        }
        //软引用缓存区不存在，查找磁盘缓存
        
			final Bitmap bitmap =getBitmapFromDisk(key);
			if(bitmap!=null)
			{
				Log.i("DiskCache", "从磁盘缓存找到图片");
				return bitmap;
				
			}
		
        //找不到，返回空
        return null; 
    } 
	
	
	
	/**
	 * <外部调用接口>
	 * 释放内存强引用缓存
	 * @author Felix
	 */
	public void clearCache()
	{
		mBmpCache.evictAll();
	}	
	
	
	public void setBmpCache(LruCache<String, Bitmap> mCache)
	{
	   this.mBmpCache=mCache;
	}	
	
	
	

}
