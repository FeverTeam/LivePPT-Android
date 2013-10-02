package net.cloudslides.app.utils;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import android.widget.Toast;
/**
 * quick toast
 * @author Felix
 *
 */
public class MyToast {	
	
	/**
	 * 提示语
	 * @param message 内容
	 * @author Felix
	 */
	public static void alert(String message)
	{
		Toast toast = Toast.makeText(HomeApp.getMyApplication().getApplicationContext(), message, Toast.LENGTH_SHORT);	    
	    toast.show();
	 }
	/**
	 * 服务器返回码转提示语
	 * @param retcode 返回码
	 * @author Felix
	 */
	public static void alert(int retcode)
	{
		switch(retcode)
		{
		case 0:alert(HomeApp.getMyApplication().getString(R.string.code_0));break;
		case -101:alert(HomeApp.getMyApplication().getString(R.string.code_101));break;
		case -102:alert(HomeApp.getMyApplication().getString(R.string.code_102));break;
		case -103:alert(HomeApp.getMyApplication().getString(R.string.code_103));break;
		case -201:alert(HomeApp.getMyApplication().getString(R.string.code_201));break;
		case -202:alert(HomeApp.getMyApplication().getString(R.string.code_202));break;
		case -203:alert(HomeApp.getMyApplication().getString(R.string.code_203));break;
		case -301:alert(HomeApp.getMyApplication().getString(R.string.code_301));break;
		case -302:alert(HomeApp.getMyApplication().getString(R.string.code_302));break;
		case -303:alert(HomeApp.getMyApplication().getString(R.string.code_303));break;
		case -304:alert(HomeApp.getMyApplication().getString(R.string.code_304));break;
		case -305:alert(HomeApp.getMyApplication().getString(R.string.code_305));break;
		case -306:alert(HomeApp.getMyApplication().getString(R.string.code_306));break;
		case -401:alert(HomeApp.getMyApplication().getString(R.string.code_401));break;
		case -402:alert(HomeApp.getMyApplication().getString(R.string.code_402));break;
		default :alert(HomeApp.getMyApplication().getString(R.string.code_999));break;
		}
	}
}
