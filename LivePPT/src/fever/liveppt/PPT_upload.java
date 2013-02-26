package fever.liveppt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PPT_upload extends Activity implements OnClickListener {

	private Button upload_butn, cancel_butn;
	private static String TAG = "PPT_upload";
	private Intent fileChooserIntent;
	private static int REQUEST_CODE = 1;
	public static final String EXTRA_FILE_CHOOSER = "file_chooser";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pptupload);
		upload_butn = (Button) this.findViewById(R.id.upload_butn);
		cancel_butn = (Button) this.findViewById(R.id.cancel_butn);
		cancel_butn.setOnClickListener(this);
		upload_butn.setOnClickListener(this);
		fileChooserIntent = new Intent (this,FileChooserActivity.class);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upload_butn:// 点击按钮
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				startActivityForResult(fileChooserIntent,REQUEST_CODE);
			else
				Toast.makeText(getApplicationContext(), "Please mount the sdcard at first", Toast.LENGTH_SHORT).show();
			break;
		case R.id.cancel_butn:// 点击按钮
			Intent intent2 = new Intent(this, PPT_Grid.class);// 新意图
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
	
	public void onActivityResult(int requestCode , int resultCode , Intent data){
		Log.v(TAG, "onActivityResult#requestCode:"+ requestCode  + "#resultCode:" +resultCode);
		if(resultCode == RESULT_CANCELED){
			Toast.makeText(getApplicationContext(), "You don't choose any file", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
			//获取路径名
			String pptPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
			Log.v(TAG, "onActivityResult # pptPath : "+ pptPath );
			if(pptPath != null){
				Toast.makeText(getApplicationContext(), "Choose File : " + pptPath, Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(getApplicationContext(), "Open file failed!", Toast.LENGTH_SHORT).show();
		}
	}

}
