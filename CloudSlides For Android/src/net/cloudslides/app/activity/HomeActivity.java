package net.cloudslides.app.activity;


import net.cloudslides.app.Define;
import net.cloudslides.app.R;
import net.cloudslides.app.custom.widget.SpotlightView;
import net.cloudslides.app.custom.widget.SpotlightView.AnimationSetupCallback;
import net.cloudslides.app.utils.MyActivityManager;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class HomeActivity extends Activity {
	private Button signUpBtn;
	private Button attendingBtn;
	private Button foundingBtn;
	private Button pptBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		MyActivityManager.getInstance().add(this);		
		showSpotLight();
		setupView();
		initView();
        
	}
	
	private void setupView()
	{
		attendingBtn =(Button)findViewById(R.id.splash_attending_button);
		signUpBtn =(Button)findViewById(R.id.splash_join_button);
		foundingBtn =(Button)findViewById(R.id.splash_foudning_button);
		pptBtn  =(Button)findViewById(R.id.splash_ppt_button);  		
	}
 
	private void initView()
	{
		//-------------加入会议---------------------------------
		attendingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
				intent.putExtra("Goto", Define.LOGIN_JUMP_ATTENDING);
				startActivity(intent);
			}
		});         
		
		//-------------发起会议---------------------------------
		foundingBtn.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
 				intent.putExtra("Goto", Define.LOGIN_JUMP_FOUNDING);
 				startActivity(intent);
 			}
 		});	
		
		//-------------我的文稿--------------------------------- 
		pptBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(HomeActivity.this, LoginActivity.class);
				intent.putExtra("Goto", Define.LOGIN_JUMP_PPT);
				startActivity(intent);
			}
		});
		
		//-------------注册---------------------------------
       signUpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(HomeActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});		
       attendingBtn.getPaint().setFakeBoldText(true);
       	foundingBtn.getPaint().setFakeBoldText(true);
       		 pptBtn.getPaint().setFakeBoldText(true);
       	  signUpBtn.getPaint().setFakeBoldText(true);
	}	
	
	/**
	 * 展示spotlight
	 * 低于4.1不支持
	 */
	private void showSpotLight()
	{
		Log.i("sdk",Build.VERSION.SDK_INT+"");
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
		{
			SpotlightView spotlight = (SpotlightView) findViewById(R.id.spotlight);
			
			spotlight.setAnimationSetupCallback(new AnimationSetupCallback() {
				@Override
				public void onSetupAnimation(SpotlightView spotlight) {
					createAnimation(spotlight);
				}
			});		
		}
		else
		{
			findViewById(R.id.content).setVisibility(View.VISIBLE);
			findViewById(R.id.spotlight).setVisibility(View.GONE);
		}
	}
	/**
	 * 创建spotlight轨迹动画
	 * @param spotlight
	 */
	private void createAnimation(final SpotlightView spotlight) {
		View top = findViewById(R.id.home_title);//spot开始点
		View bottom = findViewById(R.id.splash_join_button);//结束点

		final float textHeight = bottom.getBottom() - top.getTop();
		final float startX = top.getLeft();
		final float startY = top.getTop() + textHeight / 2.0f;
		final float endX = Math.max(top.getRight(), bottom.getRight());
		
		spotlight.setMaskX(endX);
		spotlight.setMaskY(startY);

		spotlight.animate().alpha(1.0f).withLayer().withEndAction(new Runnable() {
			@Override
			public void run() {
				ObjectAnimator moveLeft = ObjectAnimator.ofFloat(spotlight, "maskX", endX, startX);
				moveLeft.setDuration(2000);//左移动

				float startScale = spotlight.computeMaskScale(textHeight);
				ObjectAnimator scaleUp = ObjectAnimator.ofFloat(spotlight, "maskScale", startScale, startScale * 3.0f);
				scaleUp.setDuration(2000);//mask放大

				ObjectAnimator moveCenter = ObjectAnimator.ofFloat(spotlight, "maskX", spotlight.getWidth() / 2.0f);
				moveCenter.setDuration(1000);//移到中间

				ObjectAnimator moveUp = ObjectAnimator.ofFloat(spotlight, "maskY", spotlight.getHeight() / 2.0f);
				moveUp.setDuration(1000);//上移动

				ObjectAnimator superScale = ObjectAnimator.ofFloat(spotlight, "maskScale",
						spotlight.computeMaskScale(Math.max(spotlight.getHeight(), spotlight.getWidth()) * 1.7f));
				superScale.setDuration(2000);//放大

				//设置轨迹
				AnimatorSet set = new AnimatorSet();
				set.play(moveLeft).with(scaleUp);
				set.play(moveCenter).after(scaleUp);
				set.play(moveUp).after(scaleUp);
				set.play(superScale).after(scaleUp);
				set.start();

				set.addListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}
					
					@Override
					public void onAnimationRepeat(Animator animation) {
					}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						findViewById(R.id.content).setVisibility(View.VISIBLE);
						findViewById(R.id.spotlight).setVisibility(View.GONE);
						getWindow().setBackgroundDrawable(null);
					}
					
					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
			}
		});
	}	
}
