package fever.liveppt;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class Login_UI extends Activity{

	private Button login,register;
	private EditText username,password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_ui);
		
		login=(Button)this.findViewById(R.id.login);
		register=(Button)this.findViewById(R.id.register);
		username=(EditText)this.findViewById(R.id.et_username);
		password=(EditText)this.findViewById(R.id.et_password);
	}

	
}
