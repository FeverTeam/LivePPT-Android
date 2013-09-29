package net.cloudslides.app.fragment;

import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.activity.FoundMeetingActivity;
import net.cloudslides.app.activity.MainActivity;
import net.cloudslides.app.utils.MyActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuFragment extends Fragment {

	private ExpandableListView exp;
	private MyExpandableListAdapter adapter;
	private View layout;
	private Button share;
	private Button exit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		layout=inflater.inflate(R.layout.sliding_menu_frag, null);
		return layout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setUpView();
		initView();
		
	}
	
	
	private void setUpView()
	{
		exp=(ExpandableListView)layout.findViewById(R.id.slidig_menu_list);
		share=(Button)layout.findViewById(R.id.menu_share_btn);
		exit=(Button)layout.findViewById(R.id.menu_exit_btn);
	}
	
	private void initView()
	{
		adapter=new MyExpandableListAdapter(this,exp);
		exp.setAdapter(adapter);
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TO-DO
			}
		});
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Handler h = new Handler();
				h.postDelayed(new Runnable() 
				{
					public void run() 
					{
						MyActivityManager.getInstance().exit();
					}
				}, 200);				
			}
		});
	}
	/**
	 * 切换MainActivity的内容
	 * @param fragment
	 * @author Felix
	 */
	public void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity ma = (MainActivity) getActivity();
			ma.switchContent(fragment);
		}
	}
}

/**
 * 菜单适配器
 * @author Felix
 *
 */

class MyExpandableListAdapter extends BaseExpandableListAdapter{
	
	private String[]groupName={"会议相关","我的文稿","更多","关于"};
	private String[][]childName={{"发起会议","加入会议"},{},{},{}};
	
	private int[] groupIcon={R.drawable.menu_meeting_icon,
			                 R.drawable.menu_myppt_icon,
			                 R.drawable.menu_more_icon,
			                 R.drawable.menu_about_icon};
	private int[][]childIcon={{R.drawable.menu_founding_icon,R.drawable.menu_attending_icon},{},{},{}};

	private MenuFragment menu;
	private ExpandableListView exp;
	
	public MyExpandableListAdapter(MenuFragment m,ExpandableListView e)
	{
		this.menu=m;
		this.exp=e;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childName[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}	

	@Override
	public int getChildrenCount(int groupPosition) {
		return childName[groupPosition].length;		
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupName[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return groupName.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	@Override
	public View getChildView(int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null)
		{
			convertView =LayoutInflater.from(HomeApp.getMyApplication()).inflate(R.layout.sliding_menu_child_item, null);
			holder=new ViewHolder();
			holder.tv=(TextView)convertView.findViewById(R.id.menu_child_text);
			holder.icon=(ImageView)convertView.findViewById(R.id.menu_child_icon);
			holder.indicator=(ImageView)convertView.findViewById(R.id.menu_child_arrow);
			convertView.setTag(holder);
		}else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		
		holder.tv.setText(childName[groupPosition][childPosition]);
		holder.icon.setImageResource(childIcon[groupPosition][childPosition]);
		holder.indicator.setImageResource(R.drawable.down_indicator);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(childPosition==0)
				{
					Intent intent =new Intent(menu.getActivity(), FoundMeetingActivity.class);
					menu.getActivity().startActivity(intent);
				}
				else if(childPosition==1)
				{
					Fragment content =new AttendingMeetingFragment((MainActivity)menu.getActivity());
					if(content!=null)
					{
						menu.switchFragment(content);
					}
				}
			}
		});
		return convertView;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,	View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null)
		{
			convertView=LayoutInflater.from(HomeApp.getMyApplication()).inflate(R.layout.sliding_menu_group_item, null);
			holder =new ViewHolder();
			holder.tv=(TextView)convertView.findViewById(R.id.menu_group_text);
			holder.icon=(ImageView)convertView.findViewById(R.id.menu_group_icon);
			holder.indicator=(ImageView)convertView.findViewById(R.id.menu_group_arrow);
			convertView.setTag(holder);
		}else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		
		holder.tv.setText(groupName[groupPosition]);
		holder.tv.getPaint().setFakeBoldText(true);		
		holder.icon.setImageResource(groupIcon[groupPosition]);		
		if(groupPosition==0)
		{
			if(isExpanded)
			{
				holder.indicator.setImageResource(R.drawable.down_indicator);
			}
			else
			{
				holder.indicator.setImageResource(R.drawable.right);
			}				
		}
		else
		{
			holder.indicator.setImageResource(R.drawable.right);
		}		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Fragment content=getTargetFragment(groupPosition);
				if(content!=null)
				{
					menu.switchFragment(content);
				}
				else
				{
					if(isExpanded)
					{
						exp.collapseGroup(groupPosition);
					}
					else
					{
						exp.expandGroup(groupPosition);
					}					
				}
			}
		});
		return convertView;
	}	
	
	static class ViewHolder
	{
		TextView tv;
		ImageView icon;
		ImageView indicator;
	}	 
	
	private Fragment getTargetFragment(int pos)
	{
		switch(pos)
		{
		case 1:return new MyPptFragment((MainActivity)menu.getActivity());
		case 2:return new MoreFragment((MainActivity)menu.getActivity());
		case 3:return new AboutFragment((MainActivity)menu.getActivity());
		default :return null;
		}
	}
}
