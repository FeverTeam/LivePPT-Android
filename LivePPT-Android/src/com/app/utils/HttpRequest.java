package com.app.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpRequest {
	
	
	public static String httpProtocol="http://";
	public static String hostName ="live-ppt.com";
	public static String ws_port ="9000";
	public static String ws_Protocol="ws://";
	/**
	 * 客户端POST请求
	 * @param httpClient
	 * @param url
	 * @param params
	 * @return JSON字符串或出错信息
	 * @author M.
	 */
	
	
	public String HttpPostRequest(HttpClient httpClient,String url ,ArrayList<NameValuePair> params)
	{		
		String strResult="";
		HttpPost httpPost=new HttpPost(url);
		HttpResponse  response;
		try 
		{
			httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			response =httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==200)
			{
				strResult=EntityUtils.toString(response.getEntity());				
			}
			else
			{
				strResult=response.getStatusLine().toString();				
			}			
		} 
		catch (UnsupportedEncodingException e) 
		{
			if(e.getMessage()!=null)
			Log.i("UnsupportedEncodingException",e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) 
		{
			if(e.getMessage()!=null)
			Log.i("ClientProtocolException", e.getMessage());			
			e.printStackTrace();
		} catch (IOException e) 
		{	
			if(e.getMessage()!=null)
			Log.i("IOException",e.getMessage());
			e.printStackTrace();
		}		
		return strResult;
	}
	
	/**
	 * 
	 * 客户端GET请求
	 * @param httpClient
	 * @param uri 
	 * @return JSON字符串或出错信息
	 * @author Felix
	 */
	public String HttpGetRequest(HttpClient httpClient,String uri)
	{		
		String strResult="";
		HttpGet httpGet=new HttpGet(uri);
		HttpResponse response;
		
		try
		{
			response=httpClient.execute(httpGet);
			
			if(response.getStatusLine().getStatusCode()==200)
			{
				strResult=EntityUtils.toString(response.getEntity());
			}
			else
			{
				strResult=response.getStatusLine().toString();				
			}
			
		} catch (ClientProtocolException e) 
		{
			if(e.getMessage()!=null)
			Log.i("ClientProtocolException", e.getMessage());			
			e.printStackTrace();
		} catch (IOException e)
		{
			if(e.getMessage()!=null)
			Log.i("IOException",e.getMessage());
			e.printStackTrace();
		}		
         
		return strResult;
	}
}
