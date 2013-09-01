package net.cloudslides.app.widget.photoview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 本类和一般的ViewPager基本一致，创建此类的目的是由于引入PhotoView库，
 * 该库在ViewPager控件中作图片缩放时有时会出现越界等异常（ViewPager的bug），故在此捕获解决。
 * 
 * 
 * 备注：在极个别的暴力测试中仍然会有微小的概率出现越界异常，按照正常的用户操作习惯不会出现该问题。 
 * @author Chris Banes 
 * @last_modified 俊浩 
 */

public class ZoomAbleViewPager extends ViewPager {

	

	public ZoomAbleViewPager(Context context) {
		super(context);
	}

	public ZoomAbleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
}
