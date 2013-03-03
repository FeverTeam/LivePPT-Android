package fever.liveppt;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

public class AbourtActivity extends Activity {

	private TextView tvAbourt;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 锁定竖屏
		setContentView(R.layout.activity_main);
		tvAbourt = (TextView) this.findViewById(R.id.tv_abourt);
		tvAbourt.setText(Html
				.fromHtml("<font color=red>欢迎使用LivePPT~</font>我们是Fever团队<font color=blue>我们是博文、子靖、伟杰、方菲、序填、兴济...</font>"));
		
	}

	
}