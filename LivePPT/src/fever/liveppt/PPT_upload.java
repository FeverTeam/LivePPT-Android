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
import android.widget.TextView;

public class PPT_upload extends Activity implements OnClickListener {

	private Button upload_butn, cancel_butn;
	private Builder builder;

	public PPT_upload() {
		// TODO Auto-generated constructor stub
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pptupload);
		upload_butn = (Button) this.findViewById(R.id.upload_butn);
		cancel_butn = (Button) this.findViewById(R.id.cancel_butn);
		cancel_butn.setOnClickListener(this);
		// upload_butn.setOnClickListener(this);
		upload_butn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				builder.setIcon(R.drawable.tip).setTitle("��ʾ")
						.setMessage("����û�е�¼�����¼�ٽ�������������");
				builder.setPositiveButton("��¼",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								Intent intent1 = new Intent();
								intent1.setClass(PPT_upload.this,
										Login_UI.class);
								startActivity(intent1);

							}

						});

				builder.setNegativeButton("ע��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent2 = new Intent();
								intent2.setClass(PPT_upload.this,
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
		switch (v.getId()) {
		case R.id.upload_butn:// �����ť
			Intent intent1 = new Intent(this, Login_UI.class);// ����ͼ
			startActivity(intent1);
			break;
		case R.id.cancel_butn:// �����ť
			Intent intent2 = new Intent(this, PPT_Grid.class);// ����ͼ
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
}
