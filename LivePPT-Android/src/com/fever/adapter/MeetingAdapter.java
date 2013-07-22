package com.fever.adapter;

import java.util.List;

import com.fever.liveppt.R;
import com.fever.model.Meeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 会议列表子项适配器
 * @author Felix
 */
public class MeetingAdapter extends BaseAdapter{
	private List<Meeting> meetingList;
    private Context context;


    /**
     * 适配器构造器
     * @param context
     * @param meetingList
     * last modified: Frank
     */
    public MeetingAdapter(Context context,List<Meeting> meetingList){
    	this.context=context;
    	this.meetingList=meetingList;
    }


    /**
     * 设置适配器引用的列表
     * @param meetingList
     * last modified: Felix
     */
	public void setListItem(List<Meeting> meetingList){
		this.meetingList=meetingList;
	}

	@Override
	public int getCount()
	{
		
		return meetingList.size();
	}

	@Override
	public Object getItem(int arg0){
		return meetingList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


    /**
     * 实例化会议列表子项
     * @param position 会议在会议列表中的位置
     * @param convertView
     * @param parent
     * @return 会议列表子项实例
     * last modified: Felix
     */
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if(convertView==null){
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
