package fever.liveppt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AccountActivity extends Activity implements OnClickListener{
	private Button btnAccountHeader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//锁定竖屏
		setContentView(R.layout.activity_account);
		btnAccountHeader=(Button)this.findViewById(R.id.btn_account_header);
		btnAccountHeader.setOnClickListener(this);
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}
}
