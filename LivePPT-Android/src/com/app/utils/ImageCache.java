package com.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache {
	private LruCache<String, Bitmap> mBmpCache;

	private LruCache<String, Long> mFileCache;
	private File diskCacheDir;
	private int maxMemory;
	private int BMP_CACHE_SIZE;
	private int BMP_DISK_CACHE_SIZE;	
	private final int MAX_DISK_CACHE_SIZE=100*1024;//单位 KB
	
	/**
	 * 图片缓存处理类
	 * @param context
	 * @author Felix
	 */
	public ImageCache(Context context)
	{			
	   init(context);
	}	
	

	public void init(Context context)
    {		
		/**
		 * 设置磁盘缓存位置
		 * 
		 */		
		
		diskCacheDir=getDiskCacheDir(context, "pptCache");
		diskCacheDir.mkdirs();
		
		
		/**
		 * 根据最大内存设置内存强引用缓存区的大小		
		 * 根据存储环境可用空间设置合适的磁盘缓存大小
		 *   
		 */
		maxMemory=((int)Runtime.getRuntime().maxMemory()/1024);		
		BMP_CACHE_SIZE=maxMemory/4; //单位 KB	
		
		BMP_DISK_CACHE_SIZE =getCacheSize(diskCacheDir.getAbsolutePath());//单位 KB		
		Log.i("BMP_CASH_SIZE", BMP_CACHE_SIZE+"");
		Log.i("BMP_DISK_CASH_SIZE", BMP_DISK_CACHE_SIZE+"");
		
		
		/**
		 * 初始化内存强引用缓存		
		 */
		mBmpCache = new LruCache<String, Bitmap>(BMP_CACHE_SIZE) {
			@Override
			protected int sizeOf(String key, Bitmap value) {				
			
				return value.getRowBytes()*value.getHeight()/1024;//单位 KB				
			}
			@Override
			protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
				
				putBitmapToDisk(key, oldValue);						
				Log.i("LruCacheRemoved","内存强引用缓存转磁盘缓存");
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
				
				return value.intValue()/1024;//单位 KB				
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,Long oldValue, Long newValue) {
				
				 File file=getFile(key);
				 if(file!=null)
				 {
					file.delete();
				 }				 
			}			
		};
	}
	
	
	
	
	
	/**
	 * 获取磁盘缓存路径，优先使用SD卡缓存路径
	 * @param context
	 * @param name
	 * @return 指定目录文件
	 * @author Felix
	 */
	
	private static File getDiskCacheDir(Context context, String name) 
	{		
		final String cachePath =Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
	                            ?context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();	                            
	                            
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
				Log.i("文件读写出错:",e.getMessage());
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
	        if(file!=null)//查看是否已存在 
	        {  
	        	Log.i("putBitmapToDisk","已存在");
	           return true;  
	        }        
	        
	        FileOutputStream fos = getOutputStream(key);//创建输出流
	        boolean isSuccess = bitmap.compress(CompressFormat.PNG, 100, fos);//转换图片至目标位置  
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
	           mFileCache.put(key, getFile(key).length()); 	             
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
		File bitmapFile = getFile(key);
		if (bitmapFile!= null) 
		{
			try 
			{								
				Bitmap bitmap = PptBitmapUtils.decodeBitapFromStream(new FileInputStream(bitmapFile),1);
				if (bitmap != null)
				{
				   //mBmpCache.put(key, bitmap);	
				   return bitmap;
				}
			} 
			catch (FileNotFoundException e) 
			{
				Log.i("FileNotFoundException", e.getMessage());
				e.printStackTrace();
			}
			catch (OutOfMemoryError e) 
			{
				 Log.e("OutOfMemoryError", e.getMessage());
				 e.printStackTrace();
				 return null;
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
         	mBmpCache.put(key, bitmap);              
            return true;  
        }         
        return false;  
    }  	
	
	
	
	
	/**
	 * <外部调用接口>
	 * 尝试从缓存中获取图片
	 * 查找缓存顺序:内存强引用缓存-磁盘缓存
	 * @param key
	 * @return bitmap / null
	 */	
	
	public Bitmap getBitmap(String key)
	{  
        
            final Bitmap bitmap = mBmpCache.get(key);  
            
            if(bitmap != null)  
                return bitmap;
            
       
			final Bitmap bmp =getBitmapFromDisk(key);
			if(bmp!=null)
			{
				Log.i("DiskCache", "从磁盘缓存找到图片");
				return bmp;				
			}		
			
        //找不到，返回空
        return null; 
    } 
	
	
	
	/**
	 * <外部调用接口>
	 * 清除磁盘所有文件缓存
	 * @author Felix
	 */
	public void clearAllDiskCache()
	{				
		if(deleteDir(diskCacheDir));
	  Log.i("clearDiskCache", "success");
		
	}	
	
	/**
	 * <外部调用接口>
	 * 清除磁盘非指定pptId的文件缓存
	 * @author Felix
	 */
	public void clearDiskCache(String pptId)
	{				
		if(deleteDir(diskCacheDir,pptId));
	  Log.i("clearDiskCache", "success");
		
	}	
	
	
	
	/**
	 * 释放内存缓存至磁盘缓存中
	 * @author Felix
	 */
	
	public void clearMemCache()
	{
		mBmpCache.evictAll();
		Log.i("clearMemCache", "Done");
	}	
	
	/**
	 * 清除目录下所有文件;
	 * @param dir
	 * @return T/F
	 * @author Felix
	 */
	
	public boolean deleteDir(File dir) 
	{
		if (dir.isDirectory()) 
		{
			String[] child = dir.list();			
			for (int i = 0; i < child.length; i++) 
			{
				File tmp =new File(dir, child[i]);
				if (tmp.exists()) 
				{
					tmp.delete();
				}
			}
			if(dir.list().length==0)
				return true;
			else
				return false;
		}	
		else
			return false;
		
	}
	
	
	/**
	 * 清除目录下所有非指定pptId的文件;
	 * @param dir
	 * @return T/F
	 * @author Felix
	 */
	
	public boolean deleteDir(File dir,String pptId) 
	{
		if (dir.isDirectory()) 
		{
			String[] child = dir.list();			
			for (int i = 0; i < child.length; i++) 
			{
				File tmp =new File(dir, child[i]);
				if (tmp.exists()&&!tmp.getName().substring(0, tmp.getName().indexOf("-")).equals(pptId)) 
				{
					Log.i("文件pptId",tmp.getName().substring(0, tmp.getName().indexOf("-")));
					Log.i("删除", tmp.getName());
					tmp.delete();
				}
			}
			if(dir.list().length==0)
				return true;
			else
				return false;
		}	
		else
			return false;
		
	}
	
	
	/**
	 * 获取空余存储空间
	 * @param path
	 * @return 未用空间大小(KB)
	 * @author Felix
	 */
	
	public long getFreeMemory(String path)
	{
		StatFs stat =new StatFs(path);
		long blockSize =stat.getBlockSize();		
		long availableBlocks=stat.getAvailableBlocks();		
		return ((blockSize*availableBlocks)/1024);//单位 KB
		
	}
	
	/**
	 * 设置合适的磁盘缓存大小
	 * @param path
	 * @return 合适的缓存大小(KB)
	 * @author Felix
	 * 
	 */
	
	public int getCacheSize(String path)
	{
		long free=getFreeMemory(path);
		if(free<MAX_DISK_CACHE_SIZE)
		{
			return (int) (free*0.8);
		}
		else
		{
			return MAX_DISK_CACHE_SIZE;
		}	
			
	}
	
	public void setBmpCache(LruCache<String, Bitmap> mCache)
	{
	   this.mBmpCache=mCache;
	}		

}
