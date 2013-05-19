package com.app.adapter;

import java.util.List;

import com.app.base.Meeting;
import com.app.login.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * 会议列表子项适配器
 * @author Felix
 *
 */

public class MeetingAdapter extends BaseAdapter{

	private List<Meeting> meetingList;
    private Context context;
    public MeetingAdapter(Context context,List<Meeting> meetingList)
    {
    	this.context=context;
    	this.meetingList=meetingList;
    }
	public void setListItem(List<Meeting> meetingList)
	{
		this.meetingList=meetingList;
	}
	@Override
	public int getCount()
	{
		
		return meetingList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
	
		return meetingList.get(arg0);
	}

	@Override
	public long getItemId(int position) 
	{
	
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		if(convertView==null)
		{
			convertView=LayoutInflater.from(context).inflate(R.layout.meeting_item, null);
		}
		Meeting meeting=meetingList.get(position);
		
		TextView topic=(TextView)convertView.findViewById(R.id.meeting_topic);
		topic.setText("主题:"+meeting.getMeetingTopic());
		
		TextView meetingId=(TextView)convertView.findViewById(R.id.meeting_id);
		meetingId.setText("会议ID:"+meeting.getMeetingId());
		TextView founder=(TextView)convertView.findViewById(R.id.meeting_founder);
		founder.setText("主持:"+meeting.getMeetingFounder().getUserName());			
		return convertView;
	}

}
