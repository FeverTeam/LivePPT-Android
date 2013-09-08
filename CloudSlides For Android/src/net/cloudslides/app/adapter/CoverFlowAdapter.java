package net.cloudslides.app.adapter;

import java.util.ArrayList;
import java.util.List;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.custom.widget.CoverFlow;
import net.cloudslides.app.model.Meeting;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

public class CoverFlowAdapter extends BaseAdapter {
	private List<Meeting> foundedMeetigs;
	private Context context;
	private ImageView i;
	public CoverFlowAdapter(Context c)
	{
		this.context=c;
		this.foundedMeetigs=new ArrayList<Meeting>();
		this.foundedMeetigs=HomeApp.getLocalUser().getFoundedMeeting();
	}

	@Override
	public int getCount() {
		
		return foundedMeetigs.size();
	}

	@Override
	public Object getItem(int position) {
		
		return foundedMeetigs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		i=new ImageView(context);
		String url = foundedMeetigs.get(position).getMeetingPpt().getCoverUrl();
		Log.i("url", url);
		i.setScaleType(ImageView.ScaleType.FIT_XY);
		//ImageLoader.getInstance().displayImage(url, i);
		getImage(i, url);
	    i.setLayoutParams(new CoverFlow.LayoutParams(Define.WIDTH_PX*420/640,Define.HEIGHT_PX*7/10));
		return i;
	}
	/**
	 * 获取图片
	 * @param iv
	 * @param url
	 */
	private void getImage(ImageView iv , String url)
	{
		DisplayImageOptions options = new DisplayImageOptions.Builder()        
        .cacheInMemory(true) 
        .cacheOnDisc(true)        
        .showImageOnFail(R.drawable.ic_error)
        .showStubImage(R.drawable.empty_picture)
        .showImageForEmptyUri(R.drawable.ic_error)
        .displayer(new BitmapDisplayer() {
			
			@Override
			public Bitmap display(Bitmap bitmap, ImageView imageView,LoadedFrom loadedFrom) 
			{
				imageView.setImageBitmap(getReflection(bitmap));				
				return bitmap;
			}
		})
        .build();		
		ImageLoader.getInstance().displayImage(url, iv, options);
	}
	
	
	public float getScale(boolean focused, int offset) 
	{
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}
	/**
	 * 生成带镜面倒影的bitmap
	 * @param originBitmap
	 * @return bitmapWithReflection
	 * @author Godknows
	 */
	  
	public Bitmap getReflection(Bitmap image)
	{
		final int reflectionGap = 4;
	    Bitmap originalImage = image ;    
	    int width = originalImage.getWidth();
	    int height = originalImage.getHeight();
	    
	    Matrix matrix = new Matrix();
	    matrix.preScale(1, -1);
	    Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);
	    Bitmap bitmapWithReflection = Bitmap.createBitmap(width,(height + height/2), Config.ARGB_8888);
	    
	    Canvas canvas = new Canvas(bitmapWithReflection);
	    canvas.drawBitmap(originalImage, 0, 0, null);
	    
	    Paint deafaultPaint = new Paint();
	    canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
	    canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);
	    
	    Paint paint = new Paint();
	    LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
	    bitmapWithReflection.getHeight() + reflectionGap, 0x80ffffff, 0x00ffffff,TileMode.CLAMP);
	    paint.setShader(shader);
	    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	    canvas.drawRect(0, height, width,bitmapWithReflection.getHeight() + reflectionGap, paint);
	    
	    return bitmapWithReflection;
	       
	 }


}
