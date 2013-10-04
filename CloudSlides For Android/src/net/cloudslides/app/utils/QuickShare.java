package net.cloudslides.app.utils;

import java.io.File;
import java.io.FileOutputStream;

import net.cloudslides.app.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/** 
 * 针对项目要求，根据shareSDK的接口封装的分享功能类
 * 
 * @使用 创建实例对象并传入需要显示的参数，调用share()即可
 * 
 * @注意 调用share()之前必须先初始化shareSDK，建议在onCreate()中初始化，即ShareSDK.initSDK(Context)
 *       并在调用结束后停止，建议在onDestroy()中停止，即ShareSDK.stopSDK(Context);
 * @author Felix
 *
 */
public class QuickShare {
	
	private String link;//图片Wap页链接地址，
	                    //在微博中作为文本尾部加入的链接，
	                    //在微信的两个平台中作为跳转链接即setUrl的参数，
	                    //在QQ空间中作为标题跳转地址
	
	private String imagePath;//图片本地路径
	private String text;//文本
	private String title;//标题
	private OnekeyShare oks;
	private Context context;
	private boolean canEdit = true;
	private int icon = R.drawable.ic_launcher;
	
	private String sizeName ="CloudSlides云幻灯";
	private String sizeUrl  ="http://cloudslides.net/appDownload";
	private String platForm =null;
	public static String DEFAULT_IMG_PATH;
	public static String DEFAULT_IMG_FILE_NAME = "/Pic4Share.jpg";
	
	/** 
	 * @param context
	 * @param title 标题
	 * @param text 标题/文本
	 * @param link 链接地址  【link在微博中作为文本尾部加入的链接，在微信的两个平台中作为跳转链接即setUrl的参数，注意补全http://】
	 * @author 俊浩
	 */	
	public QuickShare(Context context ,String title,String text,String link)
	{
		this.title=title;
		this.context=context;
		this.text=text;		
		this.imagePath=initImagePath();
		this.link=link;	
	}
	
	/**
	 * 调用分享页面，即弹出分享平台选择对话框
	 * @author Felix
	 */
	public void share()
	{
		oks = new OnekeyShare();
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				if(link!=null&&(platform.getName().equals("SinaWeibo")||platform.getName().equals("TencentWeibo")))
    				paramsToShare.text+=" "+link;				
			}
		});
		oks.setNotification(icon, "正在分享...");
		oks.setSilent(!canEdit);
		oks.setImagePath(imagePath);
		oks.setText(text);
		oks.setTitle(title);
		
		oks.setUrl(link);//微信两个平台用		
		oks.setTitleUrl(link);//QQ空间专属参数，标题链接地址
		oks.setSite(sizeName);//QQ空间专属参数，来自xxx的“xxx”
		oks.setSiteUrl(sizeUrl);//QQ空间专属参数，“来自xxx”的点击跳转地址
		
		//oks.setAppName(appName);//QQ分享的专属参数
		if(platForm!=null)
		{
			oks.setPlatform(platForm);
		}

		oks.show(context);
	}
	
	
	/**
	 * 设置链接地址，这个地址不会直接显示在编辑文本中,在分享内容的尾部显示
	 * 
	 * @param link
	 * @author Felix
	 */
	
	public void setLink(String link) {
		this.link = link;
	}
	
	
	
	
	/**
	 * 设置图片的本地存放路径
	 * @param imagePath
	 * @author Felix
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}	
	
	
	
	/**
	 * 预设文本内容（标题）
	 * @param text
	 * @author Felix
	 */	
	public void setText(String text) {
		this.text = text;
	}
	
	
	
	/**
	 * 设置是否可以编辑分享的文本，true：可编辑分享的文本，false：直接分享默认内容
	 * 默认为true
	 * @param canEdit
	 * @author Felix
	 */
	public void setCanEdit(boolean canEdit) {
		this.canEdit=canEdit;
	}
		
	
	
	
	/**
	 * 设置分享时状态栏显示的图标 
	 * @param icon 
	 * @author Felix
	 * 
	 */
	public void setIcon(int resId) {
		this.icon = resId;
	}
	
	/**
	 * 设置QQ空间的来源网站名称
	 * 即"来自xxx"中的xxx
	 * @param sizeName
	 * @author Felix
	 */
	public  void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}


	/**
	 * 设置QQ空间的来源网站地址
	 * 即点击"来自xxx"后的跳转地址
	 * @param sizeUrl
	 * @author Felix
	 */
	public  void setSizeUrl(String sizeUrl) {
		this.sizeUrl = sizeUrl;
	}

	/**
	 * 设置指定的分享平台
	 * 指定如下字符串：TencentWeibo SinaWeibo QZone Wechat WechatMoments QQ
	 * @param platform
	 * @author Felix
	 */
	public void setPlatform(String platform) {
	   this.platForm=platform;
	}

	private String initImagePath() {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					&& Environment.getExternalStorageDirectory().exists()) {
				DEFAULT_IMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + DEFAULT_IMG_FILE_NAME;
			}
			else {
				DEFAULT_IMG_PATH = context.getFilesDir().getAbsolutePath() + DEFAULT_IMG_FILE_NAME;
			}
			File file = new File(DEFAULT_IMG_PATH);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic4share);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			DEFAULT_IMG_PATH = null;
		}
		return DEFAULT_IMG_PATH;
	}

}
