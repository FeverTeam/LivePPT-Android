package com.app.fragment;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.adapter.PptAdapter;
import com.app.base.PptFile;
import com.app.httputils.HttpRequest;
import com.app.httputils.myApp;
import com.app.login.R;

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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 显示PPT列表信息
 * @author Felix
 *
 */
public class MyPptListFrag extends Fragment {
	
	PptAdapter pptad;
	ListView lv;
	ProgressBar proBar;	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{ 		
		
        View pptListView= inflater.inflate(R.layout.my_ppt_frag, container, false);        
        lv=(ListView) pptListView.findViewById(R.id.pptListView);
        proBar=(ProgressBar)pptListView.findViewById(R.id.pptList_progressBar);
        new GetPptListTask().execute();  
        
    this.registerForContextMenu(lv);
        return pptListView;
    } 			
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View arg1,ContextMenuInfo arg2)
	{
		
		menu.setHeaderTitle("");
		menu.add(0, 1, 1, "打开PPT");		
		menu.add(0, 2, 2, "删除");
		menu.add(0, 3, 3,"PPT详情");
		
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
       myApp app=(myApp)getActivity().getApplication();
		
		  switch (item.getItemId()) {
		  case 1:
		  Toast.makeText(getActivity(), app.localUser.getPpts().get(info.position).getPptId()+"", Toast.LENGTH_SHORT).show();
		    return true;
		  case 2:	
			  Toast.makeText(getActivity(), "Coming soon...", Toast.LENGTH_SHORT).show();
			  return true;
		  case 3:
			  Toast.makeText(getActivity(), "Coming soon...", Toast.LENGTH_SHORT).show();
		    return true;
		    
		    default:
		    	return super.onContextItemSelected(item);
		  }
	
	}
	
	
	
	/**
	 * 刷新PPT列表
	 * @author Felix
	 * 
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
				myApp app=(myApp)getActivity().getApplication();
				String getListUrl="http://live-ppt.com/app/getPptList?userId="+app.localUser.getUserId();
				
				HttpRequest httpRequest =new HttpRequest();
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
					
						//ppt.setPptStatus()
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
			  Toast.makeText(getActivity().getApplicationContext(), message[0], Toast.LENGTH_LONG).show();
			  
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
}
