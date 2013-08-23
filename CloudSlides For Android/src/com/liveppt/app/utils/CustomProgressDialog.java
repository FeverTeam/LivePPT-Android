package com.liveppt.app.utils;

import com.liveppt.app.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * 自定义ProgressDialog
 * @author Felix
 *
 */

public class CustomProgressDialog extends Dialog {
	private static CustomProgressDialog customProgressDialog = null;
	private static TextView tvMsg;
	
	public CustomProgressDialog(Context context){
		super(context);
		
	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static CustomProgressDialog createDialog(Context context,String message,boolean cancelable){
		
		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.custom_progressdialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCancelable(cancelable);
		tvMsg=(TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
        tvMsg.setText(message);		
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 标题
     * @param strTitle
     * @return
     *
     */
  /*  public CustomProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }*/
    
    /**
     * 
     * [Summary]
     *       setMessage 提示内容
     * @param strMessage
     * @return
     *
     */
    public CustomProgressDialog setMessage(String strMessage){
    		tvMsg.setText(strMessage);    	
    	return customProgressDialog;
    }
    
    /**
     * 设置文字大小
     * 
     */
    
    public void setMessageTextSize(float size)
    {
    	tvMsg.setTextSize(size);
    }
    
    /**
     * 设置文字颜色
     * 
     * 
     */
    public void setMessageTextColor(int color)
    {
    	tvMsg.setTextColor(color);
    }
    
    
    
    
}
