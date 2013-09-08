package net.cloudslides.app.custom.widget;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
@SuppressWarnings("deprecation")
public class CoverFlow extends Gallery {
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
		 return false;
	}

	private Camera mCamera = new Camera();
    private int mMaxRotationAngle =60;
    private int mMaxZoom =-60;  
    private int mCoveflowCenter; 
    
    public CoverFlow(Context context) {
            super(context);
           this.setStaticTransformationsEnabled(true);
    }
    public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
           this.setStaticTransformationsEnabled(true);
    }        
    
    public CoverFlow(Context context, AttributeSet attrs) {
		 super(context, attrs);
		 this.setStaticTransformationsEnabled(true);	       
    }
	
    
    /**
     * Get the Centre of the Coverflow
     * 
     * @return The centre of this Coverflow.
     */
    private int getCenterOfCoverflow() {
            return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /**
     * Get the Centre of the View
     * 
     * @return The centre of the given view.
     */
    private static int getCenterOfView(View view) {
            return view.getLeft() + view.getWidth() / 2;
    }

    /**
     * {@inheritDoc}
     * 
     * @see #setStaticTransformationsEnabled(boolean)
     */
   
    protected boolean getChildStaticTransformation(View child, Transformation t) {
    	        	
            final int childCenter = getCenterOfView(child);
            final int childWidth = child.getWidth()+20;
            int rotationAngle = 0;
            t.clear();                
            /*
           if(child.isSelected())
           {
            ViewHelper.setAlpha(child, 1.0f);
           }
           else
           {
        	  ViewHelper.setAlpha(child, 0.5f);
        	}*/
            t.setTransformationType(Transformation.TYPE_MATRIX);
            if (childCenter == mCoveflowCenter) {
               transformImageBitmap((View) child, t, 0);   
              
            } else { 
                    rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
                    if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                            rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
                                            : mMaxRotationAngle;
                    }
                    transformImageBitmap((View) child, t, rotationAngle);
            }
            child.invalidate();//jelly bean or higher
            return true;
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     * 
     * @param w
     *            Current width of this view.
     * @param h
     *            Current height of this view.
     * @param oldw
     *            Old width of this view.
     * @param oldh
     *            Old height of this view.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCoveflowCenter = getCenterOfCoverflow();
            super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Transform the Image Bitmap by the Angle passed
     * 
     * @param imageView
     *            ImageView the ImageView whose bitmap we want to rotate
     * @param t
     *            transformation
     * @param rotationAngle
     *            the Angle by which to rotate the Bitmap
     */
    
    
    private void transformImageBitmap(View child, Transformation t,int rotationAngle) {
            mCamera.save();
            final Matrix imageMatrix = t.getMatrix();
            final int imageHeight = child.getLayoutParams().height;
            final int imageWidth = child.getLayoutParams().width;
            final int rotation = Math.abs(rotationAngle);

            // 在Z轴上正向移动camera的视角，实际效果为放大图片。
            // 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
            mCamera.translate(0.0f, 0.0f, 100.0f);

            // As the angle of the view gets less, zoom in
            if (rotation < mMaxRotationAngle) {
                    float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
                    mCamera.translate(0.0f, 0.0f, zoomAmount);
            }

            // 在Y轴上旋转，对应图片竖向向里翻转。
            // 如果在X轴上旋转，则对应图片横向向里翻转。

           mCamera.rotateY(rotationAngle); //旋转 
            mCamera.getMatrix(imageMatrix);
            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
            mCamera.restore();
    }
}
