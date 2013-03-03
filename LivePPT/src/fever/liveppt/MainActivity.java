package fever.liveppt;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends TabActivity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.tabs);
		this.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ppt);
		
		TabHost tabhost = getTabHost();
		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("PPT")// 跳到Meeting_List.class
				.setContent(new Intent(MainActivity.this, PPT_Grid.class)));

		tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("Meeting")// 跳到Meeting_List.class
				.setContent(new Intent(MainActivity.this, Meeting_List.class)));

		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("Live")
				.setContent(R.id.tab_Live));
		tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator("more")
				.setContent(R.id.tab_more));
		
		TabWidget tabWidget =this.getTabWidget();
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
            TextView tv=(TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setGravity(BIND_AUTO_CREATE);
            tv.setPadding(10, 10,10, 10);
            tv.setTextSize(16);//设置字体的大小；
            tv.setTextColor(Color.WHITE);//设置字体的颜色；
                //获取tabs图片；
            ImageView iv=(ImageView)tabWidget.getChildAt(i).findViewById(android.R.id.icon);
        }

	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, PPT_Grid.class);// 新意图
		startActivity(intent);
	}
}
