package net.cloudslides.app.adapter;

import java.util.ArrayList;

import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.model.ChatInfo;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommunicationBoxAdapter extends BaseAdapter {
	
	Context context;
	
	ArrayList<String> chatInfos;
	
	LayoutInflater inflate;
	
	public CommunicationBoxAdapter(Context c,ArrayList<String> infos)
	{
		this.context=c;
		this.chatInfos=infos;
		inflate = LayoutInflater.from(this.context);		
	}

	@Override
	public int getCount() {
		return chatInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder =null;
				if(convertView==null)
				{
					holder=new ViewHolder();
					convertView = inflate.inflate(R.layout.live_meeting_question_item, null);
					holder.name =(TextView)convertView.findViewById(R.id.question_item_user_name);
					holder.content=(TextView)convertView.findViewById(R.id.question_content);
					holder.time = (TextView)convertView.findViewById(R.id.question_item_time);
					convertView.setTag(holder);					
				}
				else
				{
					holder = (ViewHolder)convertView.getTag();
				}
				ChatInfo info = new ChatInfo(chatInfos.get(position));
				holder.name.setText(info.publisherDisplayName);
				if(info.publisherDisplayName.equals(HomeApp.getLocalUser().getUserName()))
				{
					holder.name.setTextColor(Color.RED);
				}
				else
				{
					holder.name.setTextColor(Color.BLACK);
				}
				holder.time.setText(info.getFormatTime());
				holder.content.setText(info.chatText);
				
		return convertView;
	}
	
	static class ViewHolder
	{
		TextView name;
		TextView time;
		TextView content;
	}

}
