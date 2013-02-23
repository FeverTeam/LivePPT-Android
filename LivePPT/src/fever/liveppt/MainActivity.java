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

public class MainActivity extends TabActivity implements OnClickListener {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tabs);
		TabHost tabhost = getTabHost();
		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("PPT")// ����Meeting_List.class
				.setContent(new Intent(MainActivity.this, PPT_Grid.class)));

		tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("Meeting")// ����Meeting_List.class
				.setContent(new Intent(MainActivity.this, Meeting_List.class)));

		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("Live")
				.setContent(R.id.tab_Live));
		tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator("more")
				.setContent(R.id.tab_more));

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
		Intent intent = new Intent(this, PPT_Grid.class);// ����ͼ
		startActivity(intent);
	}
}
