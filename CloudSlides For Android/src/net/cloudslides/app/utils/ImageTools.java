package net.cloudslides.app.utils;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
/**
 * Tools for handler picture
 * 
 * @author Ryan.Tang
 * 
 */
public final class ImageTools {
	
	/**
	 * 返回图片字节
	 * @param data
	 * @return 字节数
	 */
	
	 @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	    public static int getBitmapSizeInByte(Bitmap data) {
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
	            return data.getRowBytes() * data.getHeight();
	        } else {
	            return data.getByteCount();
	        }
	    }
}
