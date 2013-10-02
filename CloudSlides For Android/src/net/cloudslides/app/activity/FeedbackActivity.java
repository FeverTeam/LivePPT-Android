package net.cloudslides.app.activity;

import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.utils.MyActivityManager;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FeedbackActivity extends Activity {
	
	private Button commit;
	
	private EditText content;
	
	private Button back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		MyActivityManager.getInstance().add(this);
		setupView();
		initView();
	}
	
	private void setupView()
	{
		 commit = (Button)findViewById(R.id.feed_back_commit_btn);
		content = (EditText)findViewById(R.id.feed_back_edit_text);
		   back = (Button)findViewById(R.id.feed_back_top_bar_btn);
	}
	
	private void initView()
	{
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(checkContent())
				{
					feedBack(content.getText().toString().trim());
				}
			}
		});
	}
	/**
	 * 检查内容是否为空
	 * @return 是否为空内容
	 * @author Felix
	 */
	private boolean checkContent()
	{
		if(content.getText().toString().trim().equals(""))
		{
			MyToast.alert("请输入您的反馈意见");
			return false;
		}
		else
		{
			return true;
		}
	}
	/**
	 * 反馈
	 * @param txt 反馈内容
	 * @author Felix
	 */
	private void feedBack(String txt)
	{
		PackageManager pm =getPackageManager();
		PackageInfo info;
		try 
		{
			info = pm.getPackageInfo(this.getPackageName(),0);
			txt="[android-phone] versionCode: "+info.versionCode+"\n"+txt;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		String url ="/addition/feedback";
		RequestParams params = new RequestParams();
		params.put(Param.TEXT,txt);
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			@Override
			public void onStart() 
			{
				onBackPressed();
			}

			@Override
			public void onSuccess(String response) 
			{
				Log.i("feedback返回",response+"");
				MyToast.alert("感谢您的宝贵意见");				
			}
			@Override
			public void onFailure(Throwable e) 
			{
				MyToast.alert("网络不给力，提交失败");
			}			
		});
	}
}
