package fever.liveppt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MoreActivity extends Activity {

	private ListView lvMore;
	private static final String[] Info=new String[]{
		"账号管理","关于","反馈"
	};//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//锁定竖屏
		setContentView(R.layout.activity_more);
		lvMore=(ListView)this.findViewById(R.id.lv_more);
		lvMore.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Info));
		lvMore.setOnItemClickListener(new OnItemClickListener(){ 
			  
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					 if(Info[arg2].equals("账号管理")){
						 Intent intent=new Intent(MoreActivity.this,AccountActivity.class);
						 startActivity(intent);
					 }
					 else{
						 Intent intent=new Intent(MoreActivity.this,AbourtActivity.class);
						 startActivity(intent);
					 }
				} 
	              
	        }); 
	    } 


	
}