package net.cloudslides.app.fragment;

import java.util.ArrayList;
import java.util.List;
import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.activity.AttendingMeetingActivity;
import net.cloudslides.app.activity.MainActivity;
import net.cloudslides.app.custom.widget.MenuItem;
import net.cloudslides.app.custom.widget.PopupMenu;
import net.cloudslides.app.custom.widget.PopupMenu.OnItemSelectedListener;
import net.cloudslides.app.model.Meeting;
import net.cloudslides.app.model.PptFile;
import net.cloudslides.app.model.User;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AttendingMeetingFragment extends Fragment {
	
	private Button menuBtn;
	
	private View layout;
	
	private PullToRefreshListView list;
	
	private List<Meeting> meetings ;
	
	private AttendingMeetingAdapter adapter;
	
	private Button join;
	
	public AttendingMeetingFragment()
	{
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout =inflater.inflate(R.layout.attend_meeting_frag, null);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		setUpView();
		initView();
		getAttendDingList();
	}
	
	private void setUpView()
	{
		list = (PullToRefreshListView)layout.findViewById(R.id.attend_meeting_list);
	 menuBtn = (Button)layout.findViewById(R.id.attend_meeting_top_bar_btn);
	 	join = (Button)layout.findViewById(R.id.attending_meeting_join_btn);
	}
	
	private void initView()
	{		
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).toggleMenu();
			}
		});
		join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showJoinMeetingDialog();
			}
		});
		list.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getAttendDingList();
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, final int position,long id) 
			{
				PopupMenu menu = new PopupMenu(getActivity());
		        menu.setHeaderTitle(meetings.get(position-1).getMeetingTopic());
		        menu.add(0, R.string.attending_meeting)
		            .setIcon(getActivity().getResources().getDrawable(R.drawable.menu_item_attending_icon));
		        menu.add(1, R.string.quit_meeting)
		        	.setIcon(getResources().getDrawable(R.drawable.menu_item_quit_icon));
		        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
					
					@Override
					public void onItemSelected(MenuItem item) 
					{
						switch(item.getItemId())
						{
						case 0:attendMeeting(position-1);break;
						case 1:showConfirmQuitMeetingDialog(meetings.get(position-1).getMeetingTopic(), position-1);break;		
						}
					}
				});
		        menu.show(view);
			}
		});
		list.setRefreshing();
	}
	
	private void attendMeeting(int pos)
	{
		Intent intent =new Intent(getActivity(),AttendingMeetingActivity.class);
		intent.putExtra(Define.Intent_KEY_MEETING_POSITION,pos);
		startActivity(intent);
		
	}
	
	/**
	 * 退出会议
	 * @author Felix
	 */
	private void quitMeeting(final int position)
	{
		String url ="/meeting/quit";
		RequestParams params = new RequestParams();
		params.put(Param.MEETING_ID_KEY, HomeApp.getLocalUser().getParticipatedMeeting().get(position).getMeetingId()+"");
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(getActivity(), "正在退出会议...", false);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_bright_red));
				loadingDialog.show();
			}
			
			@Override
			public void onSuccess(String response)
			{
				Log.i("退出会议返回:",response+"");
				try 
				{
					JSONObject jso =new JSONObject(response);
					
					if(jso.getInt("retcode")!=0)
					{
						MyToast.alert(jso.getInt("retcode"));
					}
					else
					{
						adapter.delete(position);//动画效果退出会议
						
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
				MyToast.alert("网络异常!");
		     }
		    @Override
		    public void onFinish()
		    {
		    	if(list.isRefreshing())
		    	{
		    		list.onRefreshComplete();
		    	}
		    	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
					loadingDialog.dismiss();
					getAttendDingList();//刷新列表
					}
				}, 500);		    	
		    }
		});		
	}
	
	/**
	 * 获取参与会议的列表
	 * @author Felix
	 */
	private void getAttendDingList()
	{
		String url ="/meeting/myAttendingMeetings";
		MyHttpClient.get(url, null, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(getActivity(), "正在加载列表信息...", false);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
				loadingDialog.show();
			}
			
			@Override
			public void onSuccess(String response)
			{
				Log.i("获取参与会议列表返回:",response+"");
				try 
				{
					JSONObject jso =new JSONObject(response);
					
					if(jso.getInt("retcode")!=0)
					{
						MyToast.alert(jso.getInt("retcode"));
					}
					else
					{
						JSONArray jsa= jso.getJSONArray("data");
						meetings=new ArrayList<Meeting>();
						for(int i =0;i<jsa.length();i++)
						{
							jso=jsa.getJSONObject(i);
							Meeting meeting = new Meeting();
							meeting.setMeetingId(jso.getLong("meetingId"));
							meeting.setMeetingTopic(jso.getString("topic"));								
							meeting.setMeetingPpt(JSonParsePPT(jso.getJSONObject("ppt")));
							meeting.setMeetingFounder(JSonParseFounder(jso.getJSONObject("founder")));
							meetings.add(meeting);
						}							
						HomeApp.getLocalUser().setParticipatedMeeting(meetings);	
						adapter =new AttendingMeetingAdapter(getActivity());
						list.setAdapter(adapter);						
						adapter.notifyDataSetChanged();					
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
				MyToast.alert("网络异常!");
		     }
		    @Override
		    public void onFinish()
		    {
		    	if(list.isRefreshing())
		    	{
		    		list.onRefreshComplete();
		    	}
		    	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
					loadingDialog.dismiss();
					}
				}, 700);		    	
		    }
		});
	}	
	
	/**
	 * 解析JSON数组 data域 的PPT对象
	 * @param pptObj
	 * @return PptFile
	 * @author Felix
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
	 * @return  User
	 * @author Felix
	 */
	public User JSonParseFounder(JSONObject userObj)
	{
		User user =new User();
		try 
		{
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
	
	/**
	 * 加入会议
	 * @author Felix
	 */
	private void joinMeeting(String meetingId)
	{
		String url = "/meeting/join";
		RequestParams params = new RequestParams();
		params.put(Param.MEETING_ID_KEY, meetingId);
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(getActivity(), "正在添加会议...", true);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_blue));
				loadingDialog.show();
			}
			
			@Override
			public void onSuccess(String response)
			{
				Log.i("加入新会议返回:",response+"");
				try 
				{
					JSONObject jso =new JSONObject(response);
					
					if(jso.getInt("retcode")!=0)
					{
						MyToast.alert(jso.getInt("retcode"));
					}
					else
					{
						MyToast.alert("成功添加新会议.");
						getAttendDingList();
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
				MyToast.alert("网络异常!");
		     }
		    @Override
		    public void onFinish()
		    {
		    	if(list.isRefreshing())
		    	{
		    		list.onRefreshComplete();
		    	}
		    	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
					loadingDialog.dismiss();
					}
				}, 700);		    	
		    }
		});
	}
	
	/**
	 * 弹出加入新会议的对话框
	 * @param position
	 * @author Felix
	 */
	private void showJoinMeetingDialog()
	{
		final Dialog dialog =new Dialog(getActivity(), R.style.mDialog);
		View layout =LayoutInflater.from(getActivity()).inflate(R.layout.attending_meeting_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.attend_meeting_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.attend_meeting_dialog_confirm_btn);
		final EditText  id = (EditText)layout.findViewById(R.id.attend_meeting_dialog_meeting_id);
		id.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(id.getText().toString().trim().equals(""))
				{
					MyToast.alert("会议编号不能为空");
				}
				else
				{
					joinMeeting(id.getText().toString().trim());
					dialog.dismiss();
				}
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	
	/**
	 * 弹出退出会议确认对话框
	 * @param topic
	 * @param meetingId
	 * @author Felix
	 */
	private void showConfirmQuitMeetingDialog(String topic,final int position)
	{
		final Dialog dialog =new Dialog(getActivity(), R.style.mDialog);
		View layout =LayoutInflater.from(getActivity()).inflate(R.layout.normal_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.normal_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.normal_dialog_confirm_btn);
		TextView title = (TextView)layout.findViewById(R.id.normal_dialog_title);
		TextView   msg = (TextView)layout.findViewById(R.id.normal_dialog_message);
		msg.setText("即将退出会议: "+topic+" 是否确认?");
		title.setText("退出会议");
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
				quitMeeting(position);
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	
	/**
	 * 会议列表适配器,以及退出会议动画效果
	 * @author Felix
	 *
	 */
	class AttendingMeetingAdapter extends BaseAdapter
	{
		private Context context;
		private List<Meeting> attendings;
		private final List<Meeting> deleteableItems;
		public AttendingMeetingAdapter(Context c)
		{

			deleteableItems = new ArrayList<Meeting>();
			this.context=c;
			this.attendings=HomeApp.getLocalUser().getParticipatedMeeting();
		}
		

		@Override
		public int getCount() {
			return attendings.size();
		}

		@Override
		public Object getItem(int position) {
			return attendings.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		public void delete(int position) 
		{
			deleteableItems.add(attendings.get(position));
			notifyDataSetChanged();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView==null)
			{
				holder =new ViewHolder();
				convertView=LayoutInflater.from(context).inflate(R.layout.attending_meeting_list_item, null);
				holder.topic = (TextView)convertView.findViewById(R.id.attending_meeting_item_topic);
				 holder.name = (TextView)convertView.findViewById(R.id.attending_meeting_founder_name);
		    holder.meetingId = (TextView)convertView.findViewById(R.id.attending_meeting_id);
				 convertView.setTag(holder);				
			}
			else
			{
				holder=(ViewHolder)convertView.getTag();
			}
			holder.topic.setText(attendings.get(position).getMeetingTopic());
			holder.topic.getPaint().setFakeBoldText(true);
			holder.name.setText(attendings.get(position).getMeetingFounder().getUserName());
			holder.meetingId.setText("# "+attendings.get(position).getMeetingId());
			
			checkIfItemHasBeenMarkedAsDeleted(convertView, attendings.get(position));
			return convertView;
		}
		
		private void checkIfItemHasBeenMarkedAsDeleted(View view, Meeting item) {
			for (Meeting deletable : deleteableItems) {
				deleteItemIfMarkedAsDeletable(view, item, deletable);
			}
		}

		private void deleteItemIfMarkedAsDeletable(View view, Meeting item, Meeting deletable) {
			if(itemIsDeletable(item, deletable)){
				Animation anim = AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_out_right);
				anim.setDuration(500);
				deleteOnAnimationComplete(anim, item);
				animate(view, anim);
			}
		}

		private boolean itemIsDeletable(Meeting item, Meeting deletable) {
			return item.getMeetingId() == deletable.getMeetingId();
		}

		private void deleteOnAnimationComplete(Animation fadeout, final Meeting item) {
			fadeout.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) { }
				@Override
				public void onAnimationRepeat(Animation animation) { }

				@Override
				public void onAnimationEnd(Animation animation) {
					attendings.remove(item);
					deleteableItems.remove(item);
					notifyDataSetChanged();
				}
			});
		}

		private void animate(View view, Animation animation) {
			view.startAnimation(animation);
		}
				
	}
	static class  ViewHolder
	{
		TextView topic;
		TextView name;
		TextView meetingId;
	}
	
}


