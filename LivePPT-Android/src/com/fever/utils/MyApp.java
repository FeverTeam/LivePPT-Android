package com.fever.utils;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.fever.model.User;

import android.app.Application;

/**
 * 应用程序类
 * @author Felix
 */
public class MyApp extends Application {
	private HttpClient httpclient;
	public  User localUser;


	/**
     * 获取HttpClient
	 * @param
	 * @return 全局HttpClient
	 * last modified: Frank
	 */
	public  HttpClient getHttpClient(){
		 if (httpclient==null){
			 HttpParams params =new BasicHttpParams();
	         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	         HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	         HttpProtocolParams.setUseExpectContinue(params, true);	            

	         // 超时设置
	         /* 从连接池中取连接的超时时间 */
	         ConnManagerParams.setTimeout(params, 3000);

	         /* 连接超时 */
	         HttpConnectionParams.setConnectionTimeout(params, 4000);

	         /* 请求超时 */
	         HttpConnectionParams.setSoTimeout(params, 4000);
	            
	         //设置我们的HttpClient支持HTTP和HTTPS两种模式
	         SchemeRegistry schReg =new SchemeRegistry();
	         schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
             schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

	         //使用线程安全的连接管理来创建HttpClient
	         ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg);
             httpclient =new DefaultHttpClient(conMgr, params);	   
	                     
	    }		 
		 return httpclient;
	}


    /**
     *设置为本地用户
     * @param user
     * @return
     * last modified: Frank
     */
	public void setLocalUser(User user){
		this.localUser=user;
	}


    /**
     * 获取本地用户
     * @param
     * @return localUser
     * last modified: Frank
     */
	public User getLocalUser(){
		return localUser;
	}
	
}
