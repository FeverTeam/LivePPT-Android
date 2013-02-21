package fever.liveppt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PPT_upload extends Activity implements OnClickListener{

	private Button upload_butn,concel_butn;
	public PPT_upload() {
		// TODO Auto-generated constructor stub
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pptupload);
		upload_butn=(Button)this.findViewById(R.id.upload_butn);
		concel_butn=(Button)this.findViewById(R.id.concel_butn);
		upload_butn.setOnClickListener(this);
		concel_butn.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upload_butn:// 点击Gallery按钮
			Intent intent1=new Intent(this,PPT_Grid.class);//新意图
			startActivity(intent1);
			break;
		case R.id.concel_butn:// 点击ImageSwitcher按钮
			Intent intent2=new Intent(this,PPT_Grid.class);//新意图
			startActivity(intent2);
		    break;
		default:
			break;
		}
	}
}
