package com.app.liveppt;

import com.app.login.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import com.app.fragment.MyPptListFrag;



/* test */
public class HomeActivity extends FragmentActivity
{
	
	private TabHost mTabHost;
	private Button  refresh;
	private MyPptListFrag pptfrag ;
	
	private static String TAB1 ="PPT";
	private static String TAB2 ="会议";
	private static String TAB3 ="帐号";
	private static String TAB4 ="更多";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initTabHost();	
		
	}
	
	/**
	 * 初始化TabHost
	 * @author Felix
	 */
	private void initTabHost()
	{
		pptfrag=(MyPptListFrag) getSupportFragmentManager().findFragmentById(R.id.pptListFrag);
		refresh=(Button)findViewById(R.id.refreshBt);
        refresh.setOnClickListener(new OnClickListener() 
        {			
			@Override
			public void onClick(View v) 
			{				
				pptfrag.refresh();		
			}
		});
		mTabHost=(TabHost)findViewById(android.R.id.tabhost);		
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec(TAB1).setIndicator("",getResources().getDrawable(R.drawable.ppt_icon_selector)).setContent(R.id.tab1));
		mTabHost.addTab(mTabHost.newTabSpec(TAB2).setIndicator("",getResources().getDrawable(R.drawable.meeting_icon_selector)).setContent(R.id.tab2));
		mTabHost.addTab(mTabHost.newTabSpec(TAB3).setIndicator("",getResources().getDrawable(R.drawable.account_icon_selector)).setContent(R.id.tab3));
		mTabHost.addTab(mTabHost.newTabSpec(TAB4).setIndicator("",getResources().getDrawable(R.drawable.more_icon_selector)).setContent(R.id.tab4));
	
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) 
		{
			mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
		}
		    mTabHost.setCurrentTab(0);
	}

}
