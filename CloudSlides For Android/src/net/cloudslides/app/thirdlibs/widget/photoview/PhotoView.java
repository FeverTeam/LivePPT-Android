/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.cloudslides.app.thirdlibs.widget.photoview;

import java.util.ArrayList;

import net.cloudslides.app.thirdlibs.widget.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoViewAttacher.OnPhotoTapListener;
import net.cloudslides.app.thirdlibs.widget.photoview.PhotoViewAttacher.OnViewTapListener;
import net.cloudslides.app.utils.MyPathUtils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class PhotoView extends ImageView implements IPhotoView {

	private final PhotoViewAttacher mAttacher;

	private ScaleType mPendingScaleType;

	public PhotoView(Context context) {
		this(context, null);
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(3);
        mGesturePaint.setColor(Color.RED);
	}

	public PhotoView(Context context, AttributeSet attr) {
		this(context, attr, 0);
		mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(3);
        mGesturePaint.setColor(Color.RED);
	}
	
	public PhotoView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		super.setScaleType(ScaleType.MATRIX);
		mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(3);
        mGesturePaint.setColor(Color.RED);
		mAttacher = new PhotoViewAttacher(this);

		if (null != mPendingScaleType) {
			setScaleType(mPendingScaleType);
			mPendingScaleType = null;
		}
	}

	@Override
	public boolean canZoom() {
		return mAttacher.canZoom();
	}

	@Override
	public RectF getDisplayRect() {
		return mAttacher.getDisplayRect();
	}

	@Override
	public float getMinScale() {
		return mAttacher.getMinScale();
	}

	@Override
	public float getMidScale() {
		return mAttacher.getMidScale();
	}

	@Override
	public float getMaxScale() {
		return mAttacher.getMaxScale();
	}

	@Override
	public float getScale() {
		return mAttacher.getScale();
	}

	@Override
	public ScaleType getScaleType() {
		return mAttacher.getScaleType();
	}

    @Override
    public void setAllowParentInterceptOnEdge(boolean allow) {
        mAttacher.setAllowParentInterceptOnEdge(allow);
    }

    @Override
	public void setMinScale(float minScale) {
		mAttacher.setMinScale(minScale);
	}

	@Override
	public void setMidScale(float midScale) {
		mAttacher.setMidScale(midScale);
	}

	@Override
	public void setMaxScale(float maxScale) {
		mAttacher.setMaxScale(maxScale);
	}

	@Override
	// setImageBitmap calls through to this method
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
		mAttacher.setOnMatrixChangeListener(listener);
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mAttacher.setOnLongClickListener(l);
	}

	@Override
	public void setOnPhotoTapListener(OnPhotoTapListener listener) {
		mAttacher.setOnPhotoTapListener(listener);
	}

	@Override
	public void setOnViewTapListener(OnViewTapListener listener) {
		mAttacher.setOnViewTapListener(listener);
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (null != mAttacher) {
			mAttacher.setScaleType(scaleType);
		} else {
			mPendingScaleType = scaleType;
		}
	}

	@Override
	public void setZoomable(boolean zoomable) {
		mAttacher.setZoomable(zoomable);
	}

	@Override
	public void zoomTo(float scale, float focalX, float focalY) {
		mAttacher.zoomTo(scale, focalX, focalY);
	}

	@Override
	protected void onDetachedFromWindow() {
		mAttacher.cleanup();
		super.onDetachedFromWindow();
	}
	
	private float mX;
    private float mY;
    
    private final Paint mGesturePaint = new Paint();
    
    private Path mPath = new Path();
    
    private Path tmpPath = new Path();
    
    private Path pathNotDraw = new Path();
    
    private boolean canDraw=false;
    
    private boolean isZoomIn=false;
    
    private Matrix mMatrix = new Matrix();
    
    private ArrayList<Integer> latestPath = new ArrayList<Integer>();
    
    public onDrawCompleteListener mDrawCompleteListener;
    
    public onZoomViewListener mZoomListener;
    
    public static interface onZoomViewListener{
    	/**
    	 * 图片放大触发
    	 * @param view 当前放大的视图
    	 * @author Felix
    	 */
    	public void onZoomIn(View view);
    	/**
    	 * 图片复原触发
    	 * @param view 当前复原视图
    	 * @author Felix
    	 */
    	public void onZoomReset(View view);
    }
    public static interface onDrawCompleteListener{
    	
    	/**
    	 * 一条完整的笔迹画完后触发
    	 * @param points 笔迹的坐标比例数组（x，y相间），坐标比例即坐标相对图像的十万分比位置
    	 * @author Felix
    	 */
    	public void onDrawComplete(ArrayList<Integer> points);
    	
    }
    public void setOnZoomViewListener(onZoomViewListener listener)
    {
    	this.mZoomListener=listener;
    }
    public void setOnDrawCompleteListener(onDrawCompleteListener listener)
    {
    	this.mDrawCompleteListener=listener;
    }
   /* private float mMatrixBoundsDetaX = 0.0f;
    
    private float mMatrixBoundsDetaY = 0.0f;*/

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
       if(canDraw&&!isZoomIn)
       {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:                
            	touchDown(event);
                 break;
            case MotionEvent.ACTION_MOVE:
            	touchMove(event);
            	break;
            case MotionEvent.ACTION_UP:
            	touchUp();
            	break;
        }

        //更新绘制
        invalidate();
       }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //通过画布绘制多点形成的图形 
        canvas.drawPath(mPath, mGesturePaint);
    }

    //手指点下屏幕时调用
    private void touchDown(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();
        latestPath.clear();
        latestPath.add(MyPathUtils.getXRatio(x,this));
        latestPath.add(MyPathUtils.getYRatio(y,this));
        mX = x;
        mY = y;
        //mPath绘制的绘制起点
        mPath.moveTo(x, y);
        Log.i("起点", "("+x+","+y+")");
    }
    
    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event)
    {
        final float x = event.getX();
        final float y = event.getY();
        latestPath.add(MyPathUtils.getXRatio(x,this));
        latestPath.add(MyPathUtils.getYRatio(y,this));
        Log.i("移动", "("+x+","+y+")");

        final float previousX = mX;
        final float previousY = mY;

        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        
        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3)
        {
            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;

            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);

            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }
    
    private void touchUp()
    {
    	if(null!=mDrawCompleteListener&&latestPath.size()>2)
    	{
    		mDrawCompleteListener.onDrawComplete(latestPath);
    	}
    }
    /**
     * 缩放图像
     * @param x 缩放焦点x坐标
     * @param y 缩放焦点y坐标
     * @author Felix
     */
    public void zoomInCallback(float scale,float x,float y)
    {
    	if(tmpPath.isEmpty())
    	{
    		tmpPath.addPath(mPath);
    	}    	
    	mMatrix.setScale(scale, scale, x, y);
    	mPath.transform(mMatrix);  
    	mPath.moveTo(x, y);
    	isZoomIn=true;    	
    	if(null!=mZoomListener)
    	{
    		mZoomListener.onZoomIn(this);
    	}
    }

    //复原图像大小
    /**
     * 复原图像大小时触发
     * @author Felix
     */
    public void zoomResetCallback()
    { 
    	mPath.rewind();
		mPath.addPath(tmpPath);
    	tmpPath.rewind();
    	isZoomIn=false;
    	drawReadyPath();
    	invalidate();
    	if(null!=mZoomListener)
    	{
    		mZoomListener.onZoomReset(this);
    	}
    }
    /**
     * 设置是否可以画笔迹
     * @author Felix
     * @param isCanDraw
     */
    public void setCanDraw(boolean isCanDraw)
    {
    	canDraw=isCanDraw;
    }
    /**
     * 返回当前状态是否可画
     * @return 是否可绘制笔迹
     * @author Felix
     */
    public boolean canDraw()
    {
    	return canDraw;
    }
    
    /**
     * 绘制笔迹
     * @param pathToDraw 要绘制的笔迹路径
     * @author Felix
     */
    public void drawPath(Path pathToDraw)
    {
    	if(isZoomIn)
    	{
    		pathNotDraw.addPath(pathToDraw);
    	}
    	else
    	{
    		mPath.addPath(pathToDraw);
    	}    	
    	invalidate();
    }
    /**
     * 绘制放大期间未绘制的笔迹
     * @author Felix
     */
    private void drawReadyPath()
    {
    	if(!pathNotDraw.isEmpty())
    	{
    		drawPath(pathNotDraw);
    		pathNotDraw.rewind();
    	}
    }
    
    /**
     * 清除笔迹
     * @author Felix
     */
    public void cleanPath()
    {
    	mPath.rewind();
    	tmpPath.rewind();
    	pathNotDraw.rewind();
    	invalidate();
    }
    /**
     * 返回缩放状态
     * @return isZoomIn
     */
    public boolean getZoomState()
    {
    	return isZoomIn;
    }
    
    

    
    
    
   /* public synchronized void setBoudsDeta(float x,float y)
    {
    	this.mMatrixBoundsDetaX=x;
    	this.mMatrixBoundsDetaY=y;
    }*/
    
    /*public void scaleCallBack(float scale,float x,float y)
    {
    	if(tmpPath.isEmpty())
    	{
    		tmpPath.addPath(mPath);
    	}
    	Matrix scaleMatrix = new Matrix();
    	scaleMatrix.postScale(scale, scale, x, y);
    	mPath.transform(scaleMatrix); 
    	mPath.moveTo(x, y);
    	isZoomIn=true;
    }*/
    /*public synchronized void dragCallBack(float x,float y)
    {
    	Log.i("delta", "x:"+mMatrixBoundsDetaX+"   y:"+mMatrixBoundsDetaY);
    	if(isZoomIn)
    	{    	
			Log.i("hit", "x=="+x+"   y=="+y);
    		if(mMatrixBoundsDetaX==0.0f&&mMatrixBoundsDetaY!=0.0f)
    		{
    			mMatrix.setTranslate(x/2, 0);
    		}
    		else
    			if(mMatrixBoundsDetaX!=0.0f&&mMatrixBoundsDetaY==0.0f)
    			{
    				mMatrix.setTranslate(0, y/2);
    			}else
    				if(mMatrixBoundsDetaX!=0.0f&&mMatrixBoundsDetaY!=0.0f)
    				{
    					mMatrix.setTranslate(0, 0);
    				}
    				else if(mMatrixBoundsDetaX==0.0f&&mMatrixBoundsDetaY==0.0f)
    				{   					
    		        	mMatrix.setTranslate(x/2, y/2);
    				}
        	mPath.transform(mMatrix);
        	mPath.moveTo(x, y);
    	}
    }*/
}