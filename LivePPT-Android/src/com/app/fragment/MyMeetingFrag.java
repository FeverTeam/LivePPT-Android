package com.app.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.adapter.MeetingAdapter;
import com.app.base.Meeting;
import com.app.base.PptFile;
import com.app.base.User;
import com.app.httputils.HttpRequest;
import com.app.httputils.myApp;
import com.app.login.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MyMeetingFrag extends Fragment {
	
	private MeetingAdapter meetingAd;
	private ProgressBar proBar;
	private TextView numOfFounded;
	private TextView numOfParticipated;	
	private ListView foundedListView;
	private ListView participatedListView;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{  		
        View meetingListView= inflater.inflate(R.layout.my_meeting_frag, container, false);
        numOfFounded=(TextView)meetingListView.findViewById(R.id.num_of_founded_text); 
        numOfParticipated=(TextView)meetingListView.findViewById(R.id.num_of_participated_text);
        foundedListView=(ListView)meetingListView.findViewById(R.id.foundedListView); 
        participatedListView=(ListView)meetingListView.findViewById(R.id.participated_listView);
        proBar=(ProgressBar)meetingListView.findViewById(R.id.meetingProgressBar);
        
        new getFoundedMeetingTask().execute();
        new getParticipatedMeetingTask().execute();
        return meetingListView;
    } 
	
	
	
	/**
	 * 获取发起的会议列表线程
	 * @author Felix
	 *
	 */
	
	
	class getFoundedMeetingTask extends AsyncTask<Void, Integer, List<Meeting>>
	{

		/**
		 * 设置忙状态
		 */
		@Override
		protected void onPreExecute()
		{					
			proBar.setVisibility(View.VISIBLE);	
			
		}	
		/**
		 * 执行GET请求获取发起的会议列表
		 */
		@Override
		protected List<Meeting> doInBackground(Void... params) 
		{
			List<Meeting> meetingList=new ArrayList<Meeting>();
			myApp app=(myApp)getActivity().getApplicationContext();
			String Url ="http://live-ppt.com/app/getMyFoundedMeetings?userId="+app.getLocalUser().getUserId();
			String strResult="";
			JSONObject resInfo;
			JSONArray data;
			
			HttpRequest httpRequest =new HttpRequest();			  
			strResult=httpRequest.HttpGetRequest(app.getHttpClient(), Url);
			
			
			Log.i("获取主持的会议返回:", strResult);
			try 
			{
				resInfo=new JSONObject(strResult);
				   data=new JSONArray(resInfo.getString("data"));
				if(data.length()==0)
				{
					publishProgress(0);
				}
				else
				{					
					publishProgress(data.length());
					for(int i=0;i<data.length();i++)
					{
						JSONObject temp;
						temp=(JSONObject) data.get(i);
						
						Meeting meeting=new Meeting();
						meeting.setMeetingId(temp.getLong("meetingId"));
						meeting.setMeetingTopic(temp.getString("topic"));						
						meeting.setMeetingPpt(JSonParsePPT(temp.getJSONObject("ppt")));
						meeting.setMeetingFounder(JSonParseFounder(temp.getJSONObject("founder")));
						meetingList.add(meeting);
					}	
					app.localUser.setFoundedMeeting(meetingList);
				}
			} 
			catch (JSONException e) 
			{
				Log.i("出错", e.getMessage());
				e.printStackTrace();
			}			
			return meetingList;
		}
		
		/**
		 * 更新UI
		 */
		
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			numOfFounded.setText("主持了"+values[0].toString()+"个会议");
		}
		
		/**
		 * 根据返回结果更新会议列表
		 * 取消忙状态
		 */
		
		@Override
		protected void onPostExecute(List<Meeting> list)
		{
			proBar.setVisibility(View.INVISIBLE);
			meetingAd =new MeetingAdapter(getActivity().getApplicationContext(), list);
			foundedListView.setAdapter(meetingAd);	
			
			
		}		
	}
	
	
	
	/**
	 * 获取参与的会议列表线程
	 * @author Felix
	 *
	 */
	class getParticipatedMeetingTask extends AsyncTask<Void, Integer, List<Meeting>>
	{

		/**
		 * 设置忙状态
		 */
		@Override
		protected void onPreExecute()
		{					
			proBar.setVisibility(View.VISIBLE);	
			
		}	
		
		/**
		 * 执行GET请求，获取参与的会议列表
		 */
		@Override
		protected List<Meeting> doInBackground(Void... params) 
		{
			List<Meeting> meetingList=new ArrayList<Meeting>();
			myApp app=(myApp)getActivity().getApplicationContext();
			String Url ="http://live-ppt.com/app/getMyAttendingMeetings?userId="+app.getLocalUser().getUserId();
			String strResult="";
			JSONObject resInfo;
			JSONArray data;
			
			HttpRequest httpRequest =new HttpRequest();
			strResult=httpRequest.HttpGetRequest(app.getHttpClient(), Url);
			Log.i("获取参与的会议返回:", strResult);
			try 
			{
				resInfo=new JSONObject(strResult);
				   data=new JSONArray(resInfo.getString("data"));
				if(data.length()==0)
				{
					publishProgress(0);
				}
				else
				{					
					publishProgress(data.length());
					for(int i=0;i<data.length();i++)
					{
						JSONObject temp;
						temp=(JSONObject) data.get(i);
						
						Meeting meeting=new Meeting();
						meeting.setMeetingId(temp.getLong("meetingId"));
						meeting.setMeetingTopic(temp.getString("topic"));						
						meeting.setMeetingPpt(JSonParsePPT(temp.getJSONObject("ppt")));
						meeting.setMeetingFounder(JSonParseFounder(temp.getJSONObject("founder")));
						meetingList.add(meeting);
					}				
				}
			} 
			catch (JSONException e) 
			{
				Log.i("出错", e.getMessage());
				e.printStackTrace();
			}			
			return meetingList;
		}		
		
		
		/**
		 * 更新UI
		 */
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			numOfParticipated.setText("参与了"+values[0].toString()+"个会议");
		}
		
		
		/**
		 * 根据返回结果更新会议列表
		 * 取消忙状态
		 */
		@Override
		protected void onPostExecute(List<Meeting> list)
		{
			proBar.setVisibility(View.INVISIBLE);
			meetingAd =new MeetingAdapter(getActivity().getApplicationContext(), list);
			participatedListView.setAdapter(meetingAd);	
			
		}		
	}
	
	
	
	
	
	
	
	/**
	 * 解析JSON数组 data域 的PPT对象
	 * @param pptObj
	 * @return
	 */
	public PptFile JSonParsePPT(JSONObject pptObj){
		PptFile ppt =new PptFile();
		try 
		{
			ppt.setPptId(pptObj.getLong("pptId"));
			ppt.setPptTitle(pptObj.getString("title"));
			ppt.setPptTime(pptObj.getString("time"));
			ppt.setPptSize(pptObj.getLong("size"));
			ppt.setPptPageCount(pptObj.getInt("pageCount"));			
		} 
		catch (JSONException e) 
		{
			Log.i("JSON解析PPT出错:", e.getMessage().toString());
			e.printStackTrace();
		}		
		return ppt;		
	}
	
	
	
	
	/**
	 * 解析JSON数组 data域的user对象
	 * @param userObj
	 * @return
	 */
	public User JSonParseFounder(JSONObject userObj)
	{
		User user =new User();
		try 
		{
			user.setUserId(userObj.getLong("userId"));
			user.setUserName(userObj.getString("displayName"));
			user.setUserEmail(userObj.getString("email"));
		} 
		catch (JSONException e)
		{
			Log.i("JSON解析Founder出错:", e.getMessage().toString());
			e.printStackTrace();
		}
		
		return user;
	}

}
