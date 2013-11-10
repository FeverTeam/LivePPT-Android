package net.cloudslides.app.thirdlibs.widget.photoview;

import java.util.ArrayList;

import net.cloudslides.app.utils.MyPathUtils;

import android.content.Context;
import android.graphics.Path;
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

	private boolean canScroll=true;

	public ZoomAbleViewPager(Context context) {
		super(context);
	}

	public ZoomAbleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try 
		{
			if(this.canScroll)
			{
				return super.onInterceptTouchEvent(ev);
			}else
			{
				return false;
			}
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
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (this.canScroll) 
	    {
	        return super.onTouchEvent(event);
	    }

	    return false;
	}

	/**
	 * 是否可以滑动
	 * @param enabled
	 * @author Felix
	 */
	public void setScrollEnabled(boolean enabled) {
	    this.canScroll = enabled;
	} 
	
	/**
	 * 设置是否可以画笔迹
	 * @param isCanDraw
	 */
	public void setCanDraw(boolean isCanDraw)
	{
		PhotoView v =((PhotoView) this.findViewWithTag(this.getCurrentItem()));
		if(null!=v)
	    {
			v.setCanDraw(isCanDraw);
	    }
	}
	
	/**
	 * 清除笔迹
	 * @param position
	 */
	public void cleanPath()
	{
		PhotoView v =((PhotoView) this.findViewWithTag(this.getCurrentItem()));
		if(null!=v)
	    {
			v.cleanPath();
	    }
	}
	/**
	 * 返回是否处于放大状态
	 * @return
	 */
	public boolean currentViewIsZoomIn()
	{
		PhotoView v =((PhotoView) this.findViewWithTag(this.getCurrentItem()));
		if(null!=v)
	    {
			return v.getZoomState();
	    }
		return false;
	}
	/**
	 * 绘制笔迹
	 * @param points 坐标比例数组
	 * @author Felix
	 */
	public void drawPathOnCurrentView(ArrayList<Integer> points)
	{
		PhotoView v =((PhotoView) this.findViewWithTag(this.getCurrentItem()));
		if(null!=v)
	    {
			v.drawPath(MyPathUtils.createPath(points, this.findViewWithTag(this.getCurrentItem())));
	    }
	}
}
