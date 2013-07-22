package com.fever.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 本类封装了Toast消息，调用Toast信息来显示相关提示信息
 * @author Felix
 */
public class MyToast {


    /**
     *在屏幕中显示Toast消息
     * @param context
     * @param message 要显示的提示文字内容
     * @return
     * last modified: Frank
     */
	public void alert(Context context,String message){
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
	    toast.show();
	 }

}
