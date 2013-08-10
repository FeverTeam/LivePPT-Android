package com.liveppt.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.liveppt.app.R;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Button btn =(Button)findViewById(R.id.splash_attending_button);
		
		btn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction())
				{
				       
				  case MotionEvent.ACTION_DOWN: Toast.makeText(HomeActivity.this, "Get Your Finger Off!", Toast.LENGTH_SHORT).show();
				       break;			       
				 
				  
				  case MotionEvent.ACTION_UP :  Toast.makeText(HomeActivity.this, "Good!", Toast.LENGTH_SHORT).show();
					
				  
				}
				

				return false;
			}
		});
	}

}
