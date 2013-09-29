package net.cloudslides.app.fragment;

import net.cloudslides.app.R;
import net.cloudslides.app.activity.MainActivity;
import net.cloudslides.app.utils.MyToast;
import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AboutFragment extends Fragment {

	private View layout;
	private MainActivity activity;
	private Button menu;
	private TextView email;
	public  AboutFragment(MainActivity a)
	{
		this.activity=a;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout =inflater.inflate(R.layout.about_frag, null);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		setupView();
		initView();
	
	}
	
	private void setupView()
	{
		menu = (Button)layout.findViewById(R.id.about_top_bar_btn);
	   email = (TextView)layout.findViewById(R.id.about_email_text);
	}
	
	private void initView()
	{
		menu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				activity.toggleMenu();
			}
		});
		email.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				try 
				{
					email.onTouchEvent(event);
				} 
				catch (ActivityNotFoundException e) 
				{
					MyToast.alert("在您的设备上没有找到发送邮件的应用");
				}
				catch (Exception e) 
				{
					MyToast.alert("未知错误");
				}
				
				return true;
			}
		});
	}
	
}