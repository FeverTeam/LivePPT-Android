package fever.liveppt;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;

public class MainActivity extends TabActivity implements OnClickListener{

	/** Called when the activity is first created. */
	//private Intent intent;
	//private Button ppt_butn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs); 
		       
		//ppt_butn=(Button)this.findViewById(R.id.tab_PPT);
		
		TabHost tabhost = getTabHost();
	

		/*tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("PPT")// 跳到PPT_Grid.class
				.setContent(R.id.tab_PPT));
		*/
		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("PPT")// 跳到Meeting_List.class
				.setContent(new Intent(MainActivity.this, PPT_Grid.class)));

		tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("Meeting")// 跳到Meeting_List.class
				.setContent(new Intent(MainActivity.this, Meeting_List.class)));

		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("Live")
				.setContent(R.id.tab_Live));
		tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator("more")
				.setContent(R.id.tab_more));
		//ppt_butn.setOnClickListener(this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this,PPT_Grid.class);//新意图
		startActivity(intent);
	}
}
