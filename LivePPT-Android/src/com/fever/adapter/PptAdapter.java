package com.fever.adapter;

import java.util.List;

import com.fever.liveppt.R;
import com.fever.model.PptFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * ppt列表子项适配器
 * @author Felix
 *
 */
public class PptAdapter extends BaseAdapter {

	private List<PptFile> pptList;
    private Context context;
    public PptAdapter(Context context,List<PptFile> pptList)
    {
    	this.context=context;
    	this.pptList=pptList;
    }
	public void setListItem(List<PptFile> pptList)
	{
		this.pptList=pptList;
	}
	@Override
	public int getCount()
	{
		
		return pptList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
	
		return pptList.get(arg0);
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
			convertView=LayoutInflater.from(context).inflate(R.layout.ppt_item, null);
		}
		PptFile ppt=pptList.get(position);
		
		TextView title=(TextView)convertView.findViewById(R.id.pptTitle_Text);
		title.setText(ppt.getPptTitle());
		
		TextView status=(TextView)convertView.findViewById(R.id.ppt_status);
		if(ppt.getPptStatus())
		{
			status.setText("已转换");
			status.setTextColor(context.getResources().getColor(R.color.blue));
		}
		   
		else
		{
			status.setText("未转换");
			status.setTextColor(context.getResources().getColor(R.color.darkred));
		}
		
		TextView pages=(TextView)convertView.findViewById(R.id.pptPages);
		pages.setText(ppt.getPptPageCount()+"页");			
		return convertView;
	}
}
