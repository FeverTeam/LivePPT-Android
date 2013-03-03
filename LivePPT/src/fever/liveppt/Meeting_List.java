package fever.liveppt;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Meeting_List extends TabActivity implements OnClickListener {

	public Meeting_List() {
		// TODO Auto-generated constructor stub
	}

	private Button meeting_butn;
	public TextView myMeeting;
	private ListView lvLeader,lvJoin;
	private static final String[] Info=new String[]{
		"PPT1","PPT2","PPT3"
	};//
	//private Spinner spinnerLeader,spinnerJoin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//锁定竖屏
		setContentView(R.layout.meetinglist);
		meeting_butn = (Button) this.findViewById(R.id.meeting_butn);
		myMeeting=(TextView) this.findViewById(R.id.tv_mymeeting);
		meeting_butn.setOnClickListener(myShowAlertDialog);
		
		/**
		 * listview的显示
		 */
		lvLeader=(ListView)this.findViewById(R.id.lv_leader);
		lvLeader.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Info));
		lvLeader.setOnItemClickListener(new OnItemClickListener(){ 
			  
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					 if(Info[arg2].equals("PPT1")){
						 Intent intent=new Intent(Meeting_List.this,AccountActivity.class);
						 startActivity(intent);
					 }
					 else{
						 Intent intent=new Intent(Meeting_List.this,AbourtActivity.class);
						 startActivity(intent);
					 }
				} 
	              
	        }); 
		
    }
    
	
    Button.OnClickListener myShowAlertDialog = new Button.OnClickListener()
    {
      public void onClick(View arg0)
      {

        new AlertDialog.Builder(Meeting_List.this).setTitle("请选择")
        .setNegativeButton("加入会议", new DialogInterface.OnClickListener()
        { 
          public void onClick(DialogInterface d, int which)
          { 
        	  Intent intent1 = new Intent();
				intent1.setClass(Meeting_List.this,
						Login_UI.class);
				startActivity(intent1);   

          } 
        }).setPositiveButton("发起会议", new DialogInterface.OnClickListener()
        { 
            public void onClick(DialogInterface d, int which)
            { 
            	Intent intent1 = new Intent();
				intent1.setClass(Meeting_List.this,
						Login_UI.class);
				startActivity(intent1); 
            } 
          })
        .show();

      } 
    };
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}