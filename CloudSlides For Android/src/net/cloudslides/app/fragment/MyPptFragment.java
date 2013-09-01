package net.cloudslides.app.fragment;

import java.util.ArrayList;

import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.R;
import net.cloudslides.app.activity.MainActivity;
import net.cloudslides.app.activity.PlaySlidesActivity;
import net.cloudslides.app.model.PptFile;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyPptFragment extends Fragment {
	

	private Button menuBtn;
	private View layout;
	private MainActivity mActivity;
	private ArrayList<PptFile> pptList;
	private ItemAdapter adapter;
	private PullToRefreshListView xListview;
	
	public MyPptFragment(MainActivity activity)
	{		
		this.mActivity=activity;		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout =inflater.inflate(R.layout.my_ppt_frag, null);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupView();
		initView();		
		getPptList();
		
	}
	@Override
	public void onPause() 
	{
		super.onPause();
		Log.i("onPause","stopImageLoader");
		ImageLoader.getInstance().stop();
	}
	private void setupView()
	{
		adapter =new ItemAdapter();
		xListview=(PullToRefreshListView)layout.findViewById(R.id.myppt_listview);
		menuBtn=(Button)layout.findViewById(R.id.myppt_top_bar_btn);
	}
	
	private void initView()
	{
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.toggleMenu();
			}
		});
		
		xListview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getPptList();				
			}
		});
		xListview.setRefreshing();
	}
	
	
	/**
	 * 获取ppt列表
	 * @author Felix
	 */
	private void getPptList()
	{
		String url ="/app/getPptList?userId="+HomeApp.getLocalUser().getUserId();
		MyHttpClient.get(url, null, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(mActivity, "正在加载列表信息...", false);
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
					
					if(!jso.getBoolean("isSuccess"))
					{
						MyToast.alert("获取列表信息失败");
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
							coverUrl="http://live-ppt.com/getpptpage?pptid="+jso.getLong("pptId")+"&pageid=1";
							ppt.setCoverUrl(coverUrl);
							pptList.add(ppt);
						}							
						HomeApp.getLocalUser().setPpts(pptList);
						xListview.setAdapter(adapter);						
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
		    	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
					loadingDialog.dismiss();
					if(xListview.isRefreshing())
			    	{
			    		xListview.onRefreshComplete();
			    	}
					}
				}, 1000);		    	
		    }
		});
	}
	
	
	
	/**
	 * ppt列表适配器
	 * @author Felix
	 *
	 */
	class ItemAdapter extends BaseAdapter {
		
		private class ViewHolder {
			public TextView title;
			public TextView user;
			public TextView pptId;
			public TextView status;
			public TextView pages;					
			public ImageView cover;
			public Button play;
		}

		@Override
		public int getCount() {
			return HomeApp.getLocalUser().getPpts().size();
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(HomeApp.getMyApplication()).inflate(R.layout.myppt_list_item, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.my_ppt_item_title);
				holder.user  = (TextView) convertView.findViewById(R.id.my_ppt_item_belong);
				holder.pptId = (TextView) convertView.findViewById(R.id.my_ppt_item_ppt_id);
				holder.status= (TextView) convertView.findViewById(R.id.my_ppt_item_status);
				holder.pages = (TextView) convertView.findViewById(R.id.my_ppt_item_total_pages);				
				holder.cover = (ImageView) convertView.findViewById(R.id.my_ppt_item_pic);
				holder.play  = (Button) convertView.findViewById(R.id.my_ppt_item_play);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(HomeApp.getLocalUser().getPpts().get(position).getPptTitle()); 
			holder.user.setText("归属:"+HomeApp.getLocalUser().getUserName());   
			holder.pptId.setText("编号:"+HomeApp.getLocalUser().getPpts().get(position).getPptId()+""); 
			holder.status.setText("状态:"+(HomeApp.getLocalUser().getPpts().get(position).getPptStatus()?"已转换":"未转换"));			
			holder.pages.setText("总页数:"+HomeApp.getLocalUser().getPpts().get(position).getPptPageCount()+""); 
			if(HomeApp.getLocalUser().getPpts().get(position).getPptStatus())
			{
				ImageLoader.getInstance().displayImage(HomeApp.getLocalUser().getPpts().get(position).getCoverUrl(), holder.cover);
			}
			else
			{
				holder.cover.setImageResource(R.drawable.ic_error);
			}
			
			holder.play.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent =new Intent(mActivity, PlaySlidesActivity.class);
					intent.putExtra(Define.Intent_KEY_PPT_POSITION, position);
					startActivity(intent);
				}
			});			
			return convertView;
		}
	}
}
