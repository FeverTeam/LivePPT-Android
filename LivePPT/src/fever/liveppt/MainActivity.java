package fever.liveppt;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class MainActivity extends TabActivity {


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		setContentView(R.layout.tabs);
		final TabHost tabhost = getTabHost();
		//tabhost.addView(getTabWidget());
		final ViewGroup tabWidget = tabhost.getTabWidget();
		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("")//,getResources().getDrawable(R.drawable.img10))// ��Meeting_List.class
				.setContent(new Intent(MainActivity.this, PPT_Grid.class)));
		
		tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("")//,getResources().getDrawable(R.drawable.img8))// ��Meeting_List.class
				.setContent(new Intent(MainActivity.this, Meeting_List.class)));

		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("")//,getResources().getDrawable(R.drawable.img9))
				.setContent(R.id.tab_Live));
		tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator("")//,getResources().getDrawable(R.drawable.img10))
				.setContent(new Intent(MainActivity.this, MoreActivity.class)));
		
		tabhost.setPadding(0,0,0,0);
		tabhost.setCurrentTab(0);//Tab
		for(int i=0;i<tabWidget.getChildCount();i++){
	       
	        View v=tabWidget.getChildAt(i);      
	        //初始化
	        switch(i){
	        case 0:
	           //第一选项卡是亮的
	            v.setBackgroundResource(R.drawable.ppt_white);
	            break;
	        case 1:
	          //剩下的是暗的
	            v.setBackgroundResource(R.drawable.meeting_black);
	            break;
	        case 2:
	           v.setBackgroundResource(R.drawable.live_black);
	            break;
	        case 3:
	        	v.setBackgroundResource(R.drawable.more_black);
	        	
	        }
	    }


		
		/**
		 *点击tab更换图片
		 */
		tabhost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
			     for(int i = 0; i < tabWidget.getChildCount(); i++)
		         {
		            
		         if (tabhost.getCurrentTab() == i) {
		             View v=tabWidget.getChildAt(i); 
		             
		            switch (i){
		            case 0:
			               
			               v.setBackgroundResource(R.drawable.ppt_white);    
			               break;
			            
		           case 1:
		               //如果是第一个选项卡就第一个选项卡亮
		                v.setBackgroundResource(R.drawable.meeting_white);    
		               break;
		           case 2:
		               
		                v.setBackgroundResource(R.drawable.live_white);    
		                break;
		           case 3:
		               
		               v.setBackgroundResource(R.drawable.more_white);    
		               break;
                   
		            }}else{
		                 View v=tabWidget.getChildAt(i);
		                switch (i){
		                case 0:
		                    //不是这个选项卡，这个选项卡的颜色就是暗的
		                    v.setBackgroundResource(R.drawable.ppt_black);    
		                   break;
		                case 1:
		                 
		                    v.setBackgroundResource(R.drawable.meeting_black);    
		                   break;
		              case 2:
		                  
		                 v.setBackgroundResource(R.drawable.live_black);    
		                  break;
		             case 3:
		                 
		                  v.setBackgroundResource(R.drawable.more_black);  
		                  
		                    break;
		                }
		            }
		         }
			    
				
							}
		});

		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
