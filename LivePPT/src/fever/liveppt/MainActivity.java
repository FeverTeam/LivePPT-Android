package fever.liveppt;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.tabs); 
		TabHost tabhost = getTabHost();
		tabhost.addTab(tabhost.newTabSpec("tab1").setIndicator("PPT")
				.setContent(R.id.tab_PPT));
		tabhost.addTab(tabhost.newTabSpec("tab2").setIndicator("Meeting")
				.setContent(R.id.tab_Meeting));
		tabhost.addTab(tabhost.newTabSpec("tab3").setIndicator("Live")
				.setContent(R.id.tab_Live));
		tabhost.addTab(tabhost.newTabSpec("tab4").setIndicator("more")
				.setContent(R.id.tab_more));
	}

}
