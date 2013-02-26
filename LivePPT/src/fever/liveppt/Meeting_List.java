package fever.liveppt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Meeting_List extends Activity implements OnClickListener {

	public Meeting_List() {
		// TODO Auto-generated constructor stub
	}

	private Button meeting_butn;
	private Builder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.meetinglist);
		meeting_butn = (Button) this.findViewById(R.id.meeting_butn);
		meeting_butn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.tip).setTitle("提示")
						.setMessage("您还没有登录，请登录再进行其他操作！");
				builder.setPositiveButton("登录",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								Intent intent1 = new Intent();
								intent1.setClass(Meeting_List.this,
										Login_UI.class);
								startActivity(intent1);

							}

						});

				builder.setNegativeButton("注册",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent2 = new Intent();
								intent2.setClass(Meeting_List.this,
										RegisterActivity.class);
								startActivity(intent2);
							}
						});
				builder.create().getWindow().setGravity(Gravity.BOTTOM);
				builder.show();
			}
		});

		builder = new AlertDialog.Builder(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}