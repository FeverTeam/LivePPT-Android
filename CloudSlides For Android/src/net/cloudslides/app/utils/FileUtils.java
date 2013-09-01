package net.cloudslides.app.utils;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtils {

	
	/**
	 * 递归删除文件或文件夹
	 * @param file
	 * @author Felix
	 * 
	 */	
  public static boolean DeleteFile(File file){
        if(file.isFile()){
            
            return file.delete();
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                
                return file.delete();
            }
            for(File f : childFile){
                DeleteFile(f);
            }
            return file.delete();
        }
        return false;
    }

	/** 
     * 获取文件夹大小 
     * @param file File实例 
     * @return long 单位为M 
     */  
    public static long getFolderSize(File file){  
        long size = 0;  
        File[] fileList = file.listFiles();  
        for (int i = 0; i < fileList.length; i++)  
        {  
            if (fileList[i].isDirectory())  
            {  
                size = size + getFolderSize(fileList[i]);  
            } else  
            {  
                size = size + fileList[i].length();  
            }  
        }  
        return size;  
    }  
	
	/**
	 * 返回指定文件路径的文件大小
	 * @return 文件的大小，带单位(MB、KB等)
	 */
	public static String fileLength(String filePath) {
        return fileLength(new File(filePath).length());
	}
	
	/**
	 * 格式化文件大小
	 * @return 文件的大小，带单位(MB、KB等)
	 */
	public static String fileLength(long length) {
	    String lenStr = null;
	    DecimalFormat formater = new DecimalFormat("#0.##");
	    if (length < 1024) {
	        lenStr = formater.format(length) + " Byte";
	    }
	    else if (length < 1024 * 1024) {
	        lenStr = formater.format(length / 1024.0f) + " KB";
	    }
	    else if (length < 1024 * 1024 * 1024) {
	        lenStr = formater.format(length / (1024 * 1024)) + " MB";
	    }
	    else {
	        lenStr = formater.format(length / (1024 * 1024 * 1024)) + " GB";
	    }
	    return lenStr;
	}
}
