package net.cloudslides.app.activity;

import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.utils.AESEnc;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.HmacSha1Signature;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends Activity {

	private Button back;
	private Button commit;
	private EditText originPSW;
	private EditText newPSW;
	private EditText confirmPSW;
	private String oldPassword;
	private String newPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		setupView();
		initView();
	}
	
	private void setupView()
	{
			back = (Button)findViewById(R.id.change_psw_top_bar_btn);
	   originPSW = (EditText)findViewById(R.id.change_psw_origin_psw_edittext);
		  newPSW = (EditText)findViewById(R.id.change_psw_new_psw_edittext);
	  confirmPSW = (EditText)findViewById(R.id.change_psw_confirm_new_psw_edittext);
	      commit = (Button)findViewById(R.id.change_psw_commit_btn);
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
				if(checkIsValid()&&checkIsSame())
				{
					changePassword();
				}				
			}
		});
	}
	
	/**
	 * 检查密码是否一致
	 * @author Felix
	 * @return 是否一致
	 */
	private boolean checkIsSame()
	{
		if(newPSW.getText().toString().trim().equals(confirmPSW.getText().toString().trim()))
		{
			return true;
		}
		else
		{
			MyToast.alert("密码不一致，请重新输入");
			return false;
		}
	}
	
	/**
	 * 检查信息是否完整
	 * @return 是否完整
	 * @author Felix
	 */
	private boolean checkIsValid()
	{
		if(originPSW.getText().toString().trim().equals("")||newPSW.getText().toString().trim().equals("")||confirmPSW.getText().toString().trim().equals(""))
		{
			MyToast.alert("请填写完整信息");
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * 修改密码请求
	 * @author Felix
	 */
	private void changePassword()
	{
		String url = "/user/update_password";
		String seed = String.valueOf(System.currentTimeMillis() / 1000)+"000000";
		
		oldPassword = originPSW.getText().toString().trim();
		newPassword = newPSW.getText().toString().trim();
		newPassword = AESEnc.encryptAES(newPassword,seed);
		oldPassword = HmacSha1Signature.encryptHMAC_SHA(oldPassword, seed);
		
		RequestParams params = new RequestParams();
		params.put(Param.SEED,seed);
		params.put(Param.OLDPASSWORD,oldPassword);
		params.put(Param.NEWPASSWORD,newPassword);
		
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			boolean isSuccess=false;
			CustomProgressDialog loadingDialog;	
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(ChangePasswordActivity.this, "正在提交请求...", false);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
				loadingDialog.show();
			}
			@Override
			public void onSuccess(String response) 
			{
				Log.i("修改密码返回:",response);				
				try 
				{
					JSONObject jso =new JSONObject(response);
					if(jso.getInt("retcode")==0)
					{
						MyToast.alert("修改成功");
						isSuccess=true;
					}
					else
					{
						MyToast.alert(jso.getInt("retcode"));
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}				
			}
			@Override
			public void onFailure(Throwable e) 
			{
				e.printStackTrace();
				MyToast.alert("您的网络开小差,请稍后重试");
			}

			@Override
			public void onFinish() 
			{
				loadingDialog.dismiss();
				if(isSuccess)
				{
					onBackPressed();
				}
			}
			
		});		
	}
}
