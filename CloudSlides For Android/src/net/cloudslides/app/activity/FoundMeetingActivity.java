package net.cloudslides.app.activity;

import java.util.ArrayList;
import java.util.List;
import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.adapter.CoverFlowAdapter;
import net.cloudslides.app.custom.widget.CoverFlow;
import net.cloudslides.app.model.Meeting;
import net.cloudslides.app.model.PptFile;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FoundMeetingActivity extends Activity {
	private CoverFlow cf;
	private CoverFlowAdapter adapter;
	private List<Meeting> meetings;
	private PopupWindow dialogPopWindow;
    private View dialogLayout;
    private Button add;
    private Button start;
    private Button delete;
    private ArrayList<PptFile> pptList;
    private GridView grid;
    private RelativeLayout bar;
    private TextView topicInfo;
    private TextView idInfo;
    private TextView pageInfo;
    private TextView fileInfo;
    private int currPos;    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_found_meeting);
		setupView();
		initView();
		getFoundedMeeting(false);
		
	}

	/**
	 * 绑定视图
	 * @author Felix
	 */
	private void setupView()
	{
		cf = (CoverFlow)findViewById(R.id.found_meeting_coverFlow);
		   add = (Button)findViewById(R.id.found_meeting_add_btn);
		 start = (Button)findViewById(R.id.found_meeting_start_btn);
	    delete = (Button)findViewById(R.id.found_meeting_delete_btn);     
	       bar = (RelativeLayout)findViewById(R.id.found_meeting_navigationbar);
	 topicInfo = (TextView)findViewById(R.id.found_meeting_info_topic);
	    idInfo = (TextView)findViewById(R.id.found_meeting_info_id);
	  pageInfo = (TextView)findViewById(R.id.found_meeting_info_page);
	  fileInfo = (TextView)findViewById(R.id.found_meeting_info_file);
 	}
	/**
	 * 初始化视图逻辑
	 * @author Felix 
	 */
	private void initView()
	{
		meetings=new ArrayList<Meeting>();
		/**
		 * 由于gallery本身没有类似scrollstop的监听,而在scrolling时更新UI会出现闪跳现象(即使采用异步),
		 * 所以只能在Scroll Stop后再更新UI信息(比如此处刷新会议信息),而这里采用touch监听中的Action_Up事件监听scroll stop
		 * 再以300毫秒的延迟执行UI更新
		 * @author Felix
		 */
		cf.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					if(HomeApp.getLocalUser().getFoundedMeeting()!=null&&HomeApp.getLocalUser().getFoundedMeeting().size()!=0)
					{
						new Handler().postDelayed(new Runnable() {					
							@Override
							public void run() {
								topicInfo.setText("会议主题:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingTopic());
								   idInfo.setText("会议编号:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingId());
								 fileInfo.setText("会议文稿:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingPpt().getPptTitle());
							     pageInfo.setText("文稿页数:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingPpt().getPptPageCount()+"页");				   
							
								}
						}, 300);
					}
					
				}
				return false;
			}
		});
		cf.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,final int position, long id) {
				currPos=position;//标记最新选中位置，作为scroll stop后的更新依据				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0){}
		});
		
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getPptList();
			}
		});
	
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if(HomeApp.getLocalUser().getFoundedMeeting()!=null&&HomeApp.getLocalUser().getFoundedMeeting().size()>0)
				{
					showConfirmDeleteMeetingDialog(HomeApp.getLocalUser().getFoundedMeeting().get(cf.getSelectedItemPosition()).getMeetingTopic(),
				
					HomeApp.getLocalUser().getFoundedMeeting().get(cf.getSelectedItemPosition()).getMeetingId());
				}
			}
		});
		
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(HomeApp.getLocalUser().getFoundedMeeting()!=null&&HomeApp.getLocalUser().getFoundedMeeting().size()!=0)
				{
					Intent intent =new Intent(FoundMeetingActivity.this,LiveMeetingActivity.class);
					intent.putExtra(Define.Intent_KEY_MEETING_POSITION,cf.getSelectedItemPosition());
					startActivity(intent);
				}
				else
				{
					return;
				}
				
			}
		});
	}
	
	
	
	/**
	 * 获取发起会议的列表
	 * @param isSelectTheNewMeeting 是否默认选中最后一项
	 * @author Felix
	 */
	private void getFoundedMeeting(final boolean isSelectTheNewMeeting)
	{
		meetings=new ArrayList<Meeting>();
		String url="/meeting/myFoundedMeetings";
		MyHttpClient.get(url, null, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;	
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(FoundMeetingActivity.this, "正在加载会议信息...", false);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
				loadingDialog.show();
			}
			@Override
			public void onSuccess(String responce)
			{
				Log.i("获取发起的会议列表",responce);
				try 
				{
					JSONObject jso =new JSONObject(responce);
					if(jso.getInt("retcode")==0)
					{
						Log.i("获取发起的会议列表","成功");
						String coverUrl;
						JSONArray jsa =jso.getJSONArray("data");
						JSONObject obj;
						JSONObject objj;
						for(int i =0;i<jsa.length();i++)
						{
							obj=jsa.getJSONObject(i);
							Meeting meet =new Meeting();
							meet.setMeetingId(obj.getLong(Param.MEETING_ID_KEY));
							meet.setMeetingTopic(obj.getString(Param.TOPIC_KEY));							
							PptFile ppt=new PptFile();
							objj=obj.getJSONObject(Param.PPT_KEY);
							ppt.setPptId(objj.getLong(Param.PPT_ID_KEY));
							ppt.setPptPageCount(objj.getInt(Param.PAGE_COUNT_KEY));
							ppt.setPptSize(objj.getLong(Param.SIZE_KEY));
							ppt.setPptStatus(objj.getBoolean(Param.ISCONVERTED_KEY));
							ppt.setPptTime(objj.getString(Param.TIME_KEY));
							ppt.setPptTitle(objj.getString(Param.TITLE_KEY));
							coverUrl=MyHttpClient.BASE_URL+"/ppt/pageImage?pptId="+objj.getLong(Param.PPT_ID_KEY)+"&page=1"
							+"&token="+HomeApp.getLocalUser().getToken()
							+"&uemail="+HomeApp.getLocalUser().getUserEmail();
							ppt.setCoverUrl(coverUrl);
							meet.setMeetingPpt(ppt);
							
							meetings.add(meet);							
						}
						HomeApp.getLocalUser().setFoundedMeeting(meetings);

						adapter =new CoverFlowAdapter(FoundMeetingActivity.this);
						cf.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						if(isSelectTheNewMeeting)
						{
							cf.setSelection(meetings.size());
						}
						else
						{
							cf.setSelection(meetings.size()/2);
						}
					}
					else
					{
						Log.i("获取发起的会议列表","失败");
					}
					
				} catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			@Override
		     public void onFailure(Throwable e, String response) 
			{
				e.printStackTrace();
				loadingDialog.dismiss();
				MyToast.alert("网络不给力!");
		     }
		    @Override
		    public void onFinish()
		    {    	
				loadingDialog.dismiss();
				if(HomeApp.getLocalUser().getFoundedMeeting()!=null&&HomeApp.getLocalUser().getFoundedMeeting().size()==0)
				{
					MyToast.alert("你没有发起任何会议");
					topicInfo.setText("会议主题:");
					   idInfo.setText("会议编号:");
					 fileInfo.setText("会议文稿:");
				     pageInfo.setText("文稿页数:");				   
				}
				else if(HomeApp.getLocalUser().getFoundedMeeting()!=null&&HomeApp.getLocalUser().getFoundedMeeting().size()!=0)
				{
					new Handler().postDelayed(new Runnable() {					
						@Override
						public void run() {
							topicInfo.setText("会议主题:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingTopic());
							   idInfo.setText("会议编号:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingId());
							 fileInfo.setText("会议文稿:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingPpt().getPptTitle());
						     pageInfo.setText("文稿页数:"+HomeApp.getLocalUser().getFoundedMeeting().get(currPos).getMeetingPpt().getPptPageCount()+"页");				   
						
							}
					}, 300);
				}
		    }
			
		});
	}		
	
	/**
	 * 弹出用户的PPt列表提供发起会议选择
	 * @author Felix
	 */
	private void showPptGrid()
	{
		dialogLayout = (View)LayoutInflater.from(this).inflate(R.layout.found_meeting_add_meeting_grid_layout, null);	
	            grid = (GridView)dialogLayout.findViewById(R.id.found_meeting_add_meeting_gridview);
	            grid.setAdapter(new GridAdapter(HomeApp.getLocalUser().getPpts()));
        dialogPopWindow=new PopupWindow(dialogLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);		
        dialogPopWindow.setFocusable(true);
        dialogPopWindow.setAnimationStyle(R.style.PopupAnimationFromLeftToRight);
        dialogPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialogPopWindow.showAtLocation(bar, Gravity.CENTER_VERTICAL,0,0);
        dialogLayout.setOnTouchListener(new OnTouchListener() {
		//点击隐藏				
		public boolean onTouch(View v, MotionEvent event) 
		{
			dialogPopWindow.dismiss();																	
			return true;
		}
	});
	}
	
	
	
	/**
	 * 获取ppt列表
	 * @author Felix
	 */
	private void getPptList()
	{
		String url ="/ppt/info_all";
		if(HomeApp.getLocalUser().getPpts()==null)//若从未获取过PPT列表则先向服务器拿
		{
			MyHttpClient.get(url, null, new AsyncHttpResponseHandler()
			{
				CustomProgressDialog loadingDialog;		
				
				@Override
				public void onStart()
				{
					loadingDialog=CustomProgressDialog.createDialog(FoundMeetingActivity.this, "正在加载文稿信息...", true);
					loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
					loadingDialog.show();
				}
				
				@Override
				public void onSuccess(String response)
				{
					Log.i("获取ppt列表返回:",response+"");
					try 
					{
						JSONObject jso =new JSONObject(response);

						if(jso.getInt("retcode")!=0)
						{
							MyToast.alert(jso.getString("message"));
						}
						else
						{
							JSONArray jsa= jso.getJSONArray("data");
							String coverUrl ="";
							pptList=new ArrayList<PptFile>();
							for(int i =0;i<jsa.length();i++)
							{
								jso=jsa.getJSONObject(i);
								PptFile ppt =new PptFile();							
								ppt.setPptId(jso.getLong("pptId"));
								ppt.setPptPageCount(jso.getInt("pageCount"));
								ppt.setPptSize(jso.getLong("size"));
								ppt.setPptStatus(jso.getBoolean("isConverted"));
								ppt.setPptTime(jso.getString("time"));
								ppt.setPptTitle(jso.getString("title"));
								coverUrl=MyHttpClient.BASE_URL+"/ppt/pageImage?pptId="+jso.getLong("pptId")+"&page=1"
								+"&token="+HomeApp.getLocalUser().getToken()
								+"&uemail="+HomeApp.getLocalUser().getUserEmail();
								ppt.setCoverUrl(coverUrl);
								pptList.add(ppt);
							}							
							HomeApp.getLocalUser().setPpts(pptList);
						}
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();
					}
				}
				
				@Override
			     public void onFailure(Throwable e, String response) 
				{
					e.printStackTrace();
					loadingDialog.dismiss();
					MyToast.alert("网络异常!");
			     }
			    @Override
			    public void onFinish()
			    {    	
					loadingDialog.dismiss();
					showPptGrid();
			    }
			});
		}
		else
		{
			showPptGrid();
		}
	}
	
	
	
	/**
	 * 添加新的主持会议
	 * @param topic
	 * @param pptId
	 * @author Felix
	 */
	private void foundNewMeeting(String topic,long pptId)
	{
		String url ="/meeting/create";
		RequestParams params =new RequestParams();
		params.put(Param.TOPIC_KEY,topic);
		params.put(Param.PPT_ID_KEY,pptId+"");
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			@Override
			public void onStart()
			{				
				loadingDialog=CustomProgressDialog.createDialog(FoundMeetingActivity.this, "正在添加新会议...", true);
				loadingDialog.setOnCancelListener(new OnCancelListener() {					
					@Override
					public void onCancel(DialogInterface dialog) {
						MyHttpClient.getClientInstance().cancelRequests(FoundMeetingActivity.this,true);
						loadingDialog.dismiss();
						MyToast.alert("用户主动取消");
					}
				});
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
				loadingDialog.show();
			}
			
			@Override
			public void onSuccess(String response)
			{
				Log.i("添加新会议返回:",response+"");
				try 
				{
					JSONObject jso =new JSONObject(response);
					
					if(jso.getInt("retcode")!=0)
					{
						MyToast.alert("添加失败，请重试");
					}
					else
					{
						MyToast.alert("成功添加会议");
						getFoundedMeeting(true);
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			
			@Override
		     public void onFailure(Throwable e, String response) 
			{
				e.printStackTrace();
				loadingDialog.dismiss();
				MyToast.alert("您的网络开小差,请稍后重试!");
		     }
		    @Override
		    public void onFinish()
		    {    	
				loadingDialog.dismiss();
		    }
		});
	}
	

	/**
	 * 弹出添加新会议的对话框
	 * @param position
	 * @author Felix
	 */
	private void showFoundMeetingDialog(final int position)
	{
		final Dialog dialog = new Dialog(this, R.style.mDialog);
		        View layout = LayoutInflater.from(this).inflate(R.layout.found_meeting_dialog,null);
		     Button cancel  = (Button)layout.findViewById(R.id.found_meeting_dialog_cancel_btn);
		     Button confirm = (Button)layout.findViewById(R.id.found_meeting_dialog_confirm_btn);
		       TextView msg = (TextView)layout.findViewById(R.id.found_meeting_dialog_message);
	   final EditText topic = (EditText)layout.findViewById(R.id.found_meeting_dialog_topic);
		
		msg.setText(HomeApp.getLocalUser().getPpts().get(position).getPptTitle());
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				foundNewMeeting(topic.getText().toString().trim(), HomeApp.getLocalUser().getPpts().get(position).getPptId());
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	
	
	/**
	 * 删除会议
	 * @param topic 会议主题
	 * @param meetingId
	 * @author Felix
	 */
	private void deleteMeeting(final String topic,long meetingId)
	{
		Log.i("meetingId", meetingId+"");
		String url ="/meeting/delete";
		RequestParams params =new RequestParams();
		params.put(Param.MEETING_ID_KEY,meetingId+"");
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			@Override
			public void onStart()
			{				
				loadingDialog=CustomProgressDialog.createDialog(FoundMeetingActivity.this, "正在删除会议:"+topic+"...", true);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
				loadingDialog.show();
			}
			
			@Override
			public void onSuccess(String response)
			{
				Log.i("删除会议返回:",response+"");
				try 
				{
					JSONObject jso =new JSONObject(response);
					
					if(jso.getInt("retcode")!=0)
					{
						MyToast.alert("会议删除失败，请重试");
					}
					else
					{
						MyToast.alert("会议删除成功");
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			
			@Override
		     public void onFailure(Throwable e, String response) 
			{
				e.printStackTrace();
				loadingDialog.dismiss();
				MyToast.alert("您的网络开小差，请稍后重试!");
		     }
		    @Override
		    public void onFinish()
		    {    	
				loadingDialog.dismiss();
				getFoundedMeeting(false);
		    }
		});
	}
	
	/**
	 * 弹出删除会议确认对话框
	 * @param topic 会议主题
	 * @author Felix
	 */
	private void showConfirmDeleteMeetingDialog(final String topic,final long meetingId)
	{
		final Dialog dialog =new Dialog(this, R.style.mDialog);
		View layout =LayoutInflater.from(this).inflate(R.layout.normal_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.normal_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.normal_dialog_confirm_btn);
		TextView title = (TextView)layout.findViewById(R.id.normal_dialog_title);
		TextView   msg = (TextView)layout.findViewById(R.id.normal_dialog_message);
		msg.setText("即将删除会议: "+topic+" 是否确认?");
		title.setText("删除会议");
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				deleteMeeting(topic,meetingId);
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	
	

	/**
	 * ppt列表GridView适配器
	 * @author Felix
	 *
	 */
	class GridAdapter extends BaseAdapter{
		
		private List<PptFile> ppts;		
		public GridAdapter(List<PptFile> list)
		{
			this.ppts=list;
		}
		@Override
		public int getCount() {
			return ppts.size();
		}

		@Override
		public Object getItem(int position) {
			return ppts.get(position);			
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder;
			if(convertView==null)
			{
				holder=new ViewHolder();
			    convertView = LayoutInflater.from(FoundMeetingActivity.this).inflate(R.layout.found_meeting_gridview_item,null);
				 holder.img = (ImageView)convertView.findViewById(R.id.grid_view_item_img);
			   holder.title = (TextView)convertView.findViewById(R.id.grid_view_item_name);
			   convertView.setTag(holder);
			}
			else
			{
				holder=(ViewHolder) convertView.getTag();
			}
			ImageLoader.getInstance().displayImage(ppts.get(position).getCoverUrl(),holder.img);
			holder.title.setText(ppts.get(position).getPptTitle());
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) 
				{
					if(!HomeApp.getLocalUser().getPpts().get(position).getPptStatus())
					{
						MyToast.alert("该文稿还未完成转换,请稍后重试...");
					}
					else
					{
						showFoundMeetingDialog(position);
					}
					dialogPopWindow.dismiss();
				}
			});
		    return convertView;
		}	
		
	}
	static class ViewHolder
	{
		ImageView img;
		TextView title;
	}
}
