package fever.liveppt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Meeting_List extends Activity implements OnClickListener{

	public Meeting_List() {
		// TODO Auto-generated constructor stub
	}

	private Button meeting_butn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meetinglist);
		meeting_butn=(Button)this.findViewById(R.id.meeting_butn);
		meeting_butn.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent1=new Intent(this,PPT_upload.class);//–¬“‚Õº
		startActivity(intent1);
	}
}
