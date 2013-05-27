package com.app.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.adapter.PptAdapter;
import com.app.liveppt.PptReplayActivity;
import com.app.liveppt.R;
import com.app.model.PptFile;
import com.app.utils.HttpRequest;
import com.app.utils.MyToast;
import com.app.utils.myApp;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 显示PPT列表信息
 * @author Felix
 *
 */
public class MyPptListFrag extends Fragment {
	
	private PptAdapter pptad;
	private ListView lv;
	private ProgressBar proBar;	
	private Long pptId;
	private String topic;
	private myApp app;
	private HttpRequest  httpRequest;
	private MyMeetingFrag meetingfrag;
	private View pptListView;
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{ 		
		
        pptListView= inflater.inflate(R.layout.my_ppt_frag, container, false);      
        init();        
        new GetPptListTask().execute();        
        this.registerForContextMenu(lv);
        return pptListView;
    } 	
	
	/**
	 * 初始化控件
	 * @author Felix
	 */
	private void init()
	{
		lv=(ListView) pptListView.findViewById(R.id.pptListView);
        proBar=(ProgressBar)pptListView.findViewById(R.id.pptList_progressBar);
        app=(myApp)getActivity().getApplication();
        httpRequest =new HttpRequest();
        
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
			{
				PptFile ppt =new PptFile(); 
				ppt=app.localUser.getPpts().get(position);
				showPptInfoDialog(ppt);
				
			}
		});
        
        
	}
	
	/**
	 * 显示ppt信息的dialog
	 * @author Felix
	 */
	
	private void showPptInfoDialog(PptFile ppt)
	{    
	    View view=LayoutInflater.from(getActivity()).inflate(R.layout.ppt_info_dialog, null);
	    TextView pptId_text=(TextView)view.findViewById(R.id.ppt_id_ppt_info_dialog);
	    TextView pptTime_text=(TextView)view.findViewById(R.id.ppt_time_ppt_info_dialog);
	    TextView pptTitle_text=(TextView)view.findViewById(R.id.ppt_title_ppt_info_dialog);
	    TextView pptPage_text=(TextView)view.findViewById(R.id.ppt_page_ppt_info_dialog);
	    TextView pptStatus_text=(TextView)view.findViewById(R.id.ppt_status_ppt_info_dialog);
	    TextView pptOwner_text=(TextView)view.findViewById(R.id.ppt_owner_ppt_info_dialog);
	    
	    pptId_text.setText(ppt.getPptId().toString());
	    pptTime_text.setText(ppt.getPptTime());
	    pptTitle_text.setText(ppt.getPptTitle());
	    pptPage_text.setText(ppt.getPptPageCount()+"");
	    pptOwner_text.setText(app.localUser.getUserName());
	    if(ppt.getPptStatus())
	    {
	    	pptStatus_text.setText("已转换");
	    }
	    else
	    {
	    	pptStatus_text.setText("未转换");
	    }
	    
	    AlertDialog dialog=new AlertDialog.Builder(getActivity())
	    .setTitle("PPT详情")
	    .setView(view)
	    .create();
	    dialog.show();	   
	}
	
	
	
	/**
	 * PPT列表上下文菜单
	 */
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View arg1,ContextMenuInfo arg2)
	{		
		menu.setHeaderTitle("");
		menu.add(0, 1, 1, "浏览PPT");		
		menu.add(0, 2, 2, "放入主持会议列表");		
	}
	
	
	/**
	 * 上下文菜单选项监听
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		  switch (item.getItemId()) {
		  case 1:
		  {			  
			 ConnectivityManager connectivityManager=(ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			 NetworkInfo net=connectivityManager.getActiveNetworkInfo();
			 Log.i("连接方式:", net.getTypeName());			 
			// if(net.getTypeName().equals("WIFI"))
			// {			  
			  Intent intent =new Intent(getActivity(), PptReplayActivity.class);
			  Bundle bundle =new Bundle();
			         bundle.putLong("pptId",app.localUser.getPpts().get(info.position).getPptId());
			         bundle.putInt("pageCount", app.localUser.getPpts().get(info.position).getPptPageCount());
			         intent.putExtras(bundle);			         
			  startActivity(intent);	
			// }
			// else
			// {
			//	 new MyToast().alert(getActivity().getApplicationContext(),"打开PPT将消耗较多流量，请连接WIFI");				
			// }	
			 return true;
		  }		  
		    
		  case 2:
		  {			  
			  AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			  final View dialogView=LayoutInflater.from(getActivity()).inflate(R.layout.found_new_meeting_dialog, null);
			  final EditText topic_text=(EditText)dialogView.findViewById(R.id.meeting_topic_foundmeeting);
			  builder.setView(dialogView);			
			  builder.setPositiveButton("设置", new OnClickListener() 
			  {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					pptId=app.localUser.getPpts().get(info.position).getPptId();
					topic=topic_text.getText().toString().trim();
					new foundNewMeetingTask().execute();					
				}
			});
			  builder.setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						
						
					}
				});
			  
			    AlertDialog mDialog =builder.create();	
			    
			    Window window = mDialog.getWindow();    
			    
			    WindowManager.LayoutParams lp = window.getAttributes();       
		        lp.alpha = 0.8f; //透明度		               
			    window.setAttributes(lp);				    
			    mDialog.show();				    
			  return true;	 
		  }
		    
		    default:
		    	return super.onContextItemSelected(item);
		  }
	
	}
	
	
	
	/**
	 * 刷新PPT列表
	 * @author Felix
	 * 
	 */
	
	public void refresh()
	{		
	  new GetPptListTask().execute();  
	}	
		
	
	
	/**
	 * 获取PPT列表线程
	 * 获取用户的PPT列表，并把列表存入本地用户
	 * @author Felix
	 */
	class GetPptListTask extends AsyncTask<Void, String, List<PptFile>>
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
		 * 执行GET请求获取PPT列表
		 */
		@Override
		protected List<PptFile> doInBackground(Void... params)
		{		
				List<PptFile> pptList=new ArrayList<PptFile>();				
				String getListUrl=HttpRequest.httpProtocol+HttpRequest.hostName+"/app/getPptList?userId="+app.localUser.getUserId();
				
				
				String strResult;
				JSONObject resInfo;
				
				strResult=httpRequest.HttpGetRequest(app.getHttpClient(), getListUrl);	
				Log.i("获取列表返回:", strResult);
				try
				{
					resInfo=new JSONObject(strResult);
				
				if(!resInfo.getBoolean("isSuccess"))
				{
					publishProgress("你的列表为空!");
				}
				else
				{			
					JSONArray data;
		            data = new JSONArray(resInfo.getString("data"));
		            PptFile ppt;
					for(int i=0;i<data.length();i++)
					{
						JSONObject temp;
						temp=(JSONObject) data.get(i);
					
						ppt=new PptFile();
						ppt.setPptId(temp.getLong("pptId"));
						ppt.setPptTitle(temp.getString("title"));
						ppt.setPptTime(temp.getString("time"));
						ppt.setPptSize(temp.getLong("size"));
						ppt.setPptPageCount(temp.getInt("pageCount"));					
						ppt.setPptStatus(temp.getBoolean("isConverted"));
						pptList.add(ppt);
					}
					app.localUser.setPpts(pptList);
				}
				} catch (JSONException e) 
				{
					Log.i("出错:", e.getMessage());
					e.printStackTrace();
				}
				return pptList;				
		}
		
		
		/**
		 * 处理出错信息
		 */
		 @Override
		  protected void onProgressUpdate(String ...message)
		  {
			 new MyToast().alert(getActivity().getApplicationContext(),message[0]);			  
		  }
		 
		 
		 /**
		  * 根据执行结果更新列表
		  * 取消忙状态
		  */
		protected void onPostExecute(List<PptFile> list)
		{			
			pptad=new PptAdapter(getActivity().getApplicationContext(), list);
			proBar.setVisibility(View.INVISIBLE);
			lv.setAdapter(pptad);
		}
	}
	
	
	
	
	/**
	 * 添加新主持会议线程
	 * @author Felix 
	 */
	
	class foundNewMeetingTask extends AsyncTask<Void, Void,Boolean>
	{

		@Override
		protected Boolean doInBackground(Void... params) 
		{
			String Url =HttpRequest.httpProtocol+HttpRequest.hostName+"/app/foundNewMeeting";
			JSONObject jso;
			String strResult;
			ArrayList<NameValuePair> paramList =new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("pptId",pptId+""));
			paramList.add(new BasicNameValuePair("userId", app.localUser.getUserId()+""));
			paramList.add(new BasicNameValuePair("topic",topic));
			strResult=httpRequest.HttpPostRequest(app.getHttpClient(), Url, paramList);
			Log.i("发起会议", strResult);
			
			try
			{
				jso=new JSONObject(strResult);
				if(jso.getBoolean("isSuccess"))
				{
					return true;
				}
				else
				{
					return false;
				}
			} catch (JSONException e) 
			{				
				e.printStackTrace();
			}	
			return false;
			
		}
		
		@Override
		protected void onPostExecute(Boolean flag)
		{
			if(flag)
			{
				meetingfrag=(MyMeetingFrag)getActivity().getSupportFragmentManager().findFragmentById(R.id.meetingFrag);
				meetingfrag.refresh();
				new MyToast().alert(getActivity(), "成功添加会议");
			}
			else
			{
				new MyToast().alert(getActivity(), "添加失败，请重试");
			}
		}
		
	}
}
