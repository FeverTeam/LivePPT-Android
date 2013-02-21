package fever.liveppt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PPT_Grid extends Activity implements OnClickListener{

	public PPT_Grid() {
		// TODO Auto-generated constructor stub
	}
		private Button ppt_butn;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.pptgridshow);
			ppt_butn=(Button)this.findViewById(R.id.ppt_butn);
			ppt_butn.setOnClickListener(this);
			
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			Intent intent=new Intent(this,PPT_upload.class);//–¬“‚Õº
			startActivity(intent);
		}
}
