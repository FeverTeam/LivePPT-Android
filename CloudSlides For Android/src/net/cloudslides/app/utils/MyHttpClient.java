package net.cloudslides.app.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MyHttpClient {

	  private static final String BASE_URL = "http://live-ppt.com";

	  private static AsyncHttpClient client = new AsyncHttpClient();

	  /**
	   * GET Request
	   * @param url 相对地址[“/”开头]
	   * @param params 参数（空则null）
	   * @param responseHandler 返回参数的handler
	   * @author Felix 
	   */
	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
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
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }
}
