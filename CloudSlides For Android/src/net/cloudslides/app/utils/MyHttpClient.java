package net.cloudslides.app.utils;

import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyHttpClient {

	  //private static final String BASE_URL = "http://live-ppt.com";

//	  public static final String BASE_URL = "http://192.168.103.1:9000";
//	  public static final String WS_URL ="ws://192.168.103.1:9000";
	public static final String BASE_URL = "http://cloudslides.net:9000";
	  public static final String WS_URL ="ws://cloudslides.net:9000";
	  private static AsyncHttpClient client = new AsyncHttpClient();
	  

	  /**
	   * GET Request
	   * @param url 相对地址[“/”开头]
	   * @param params 参数（空则null）
	   * @param responseHandler 返回参数的handler
	   * @author Felix 
	   */
	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
	      if(HomeApp.getLocalUser()!=null&&HomeApp.getLocalUser().getToken()!=null&&HomeApp.getLocalUser().getUserEmail()!=null)
	      {
	    	  client.addHeader(Param.UEMAIL,HomeApp.getLocalUser().getUserEmail());
	    	  client.addHeader(Param.TOKEN,HomeApp.getLocalUser().getToken());
	    	  Log.i("token", HomeApp.getLocalUser().getToken());
	    	  Log.i("uemail",HomeApp.getLocalUser().getUserEmail());
	      }
		  client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  /**
	   * POST Request
	   * @param url 相对地址[“/”开头]
	   * @param params 参数
	   * @param responseHandler 返回参数的Handler
	   * @author Felix
	   */
	  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		  if(url.equals("/ppt/upload"))
		  {
			  client.setTimeout(60000);
		  }
		  else
		  {
			  client.setTimeout(10000);
		  }
		  if(HomeApp.getLocalUser()!=null&&HomeApp.getLocalUser().getToken()!=null&&HomeApp.getLocalUser().getUserEmail()!=null)
	      {
	    	  client.addHeader(Param.UEMAIL,HomeApp.getLocalUser().getUserEmail());
	    	  client.addHeader(Param.TOKEN,HomeApp.getLocalUser().getToken());
	    	  Log.i("token", HomeApp.getLocalUser().getToken());
	    	  Log.i("uemail",HomeApp.getLocalUser().getUserEmail());
	      }
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }
	  public static AsyncHttpClient getClientInstance()
	  {
		  return client;
	  }
}
