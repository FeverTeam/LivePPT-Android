package net.cloudslides.app.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import net.cloudslides.app.Define;
import net.cloudslides.app.HomeApp;
import net.cloudslides.app.Param;
import net.cloudslides.app.R;
import net.cloudslides.app.activity.MainActivity;
import net.cloudslides.app.activity.PlaySlidesActivity;
import net.cloudslides.app.model.PptFile;
import net.cloudslides.app.thirdlibs.filechooser.FileChooserActivity;
import net.cloudslides.app.thirdlibs.filechooser.FileUtils;
import net.cloudslides.app.utils.CustomProgressDialog;
import net.cloudslides.app.utils.MyFileUtils;
import net.cloudslides.app.utils.MyHttpClient;
import net.cloudslides.app.utils.MyToast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyPptFragment extends Fragment {

	//PPT和PPTX文件的ContentType
    public static final String PPT_CONTENTTYPE = "application/vnd.ms-powerpoint";
    
    public static final String PPTX_CONTENTTYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
	
    private Button menuBtn;
	
    private Button uploadBtn;
	
    private View layout;
	
    private ArrayList<PptFile> pptList;
	
    private ItemAdapter adapter;
	
    private PullToRefreshListView xListview;
	
    private static final int  REQUEST_CODE=0x1234;
	
    public MyPptFragment()//必要
    {
    	
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
		xListview = (PullToRefreshListView)layout.findViewById(R.id.myppt_listview);
		  menuBtn = (Button)layout.findViewById(R.id.myppt_top_bar_btn);
		uploadBtn = (Button)layout.findViewById(R.id.myppt_upload_btn);
	}
	
	private void initView()
	{
		menuBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MainActivity)getActivity()).toggleMenu();
			}
		});
		
		xListview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getPptList();				
			}
		});
		
		
		uploadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(getActivity(),FileChooserActivity.class);
				try 
				{
					MyToast.alert("请选择要上传的文稿(PPT/PPTX)");
					startActivityForResult(intent, REQUEST_CODE);
					
				} catch (ActivityNotFoundException e) 
				{
					e.printStackTrace();
				}			
			}
		});
	}	
	/**
	 * 选中上传文件后返回
	 * @author Felix
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch (requestCode) {
		case REQUEST_CODE:	
			if (resultCode == Activity.RESULT_OK) {		
				if (data != null) {
					final Uri uri = data.getData();
					try 
					{
						final File file = FileUtils.getFile(uri);						
						Log.i("选中文件:", file.getAbsolutePath()+"");
						Log.i("文件大小:",file.length()/1024/1024+"MB");
						if(MyFileUtils.getFileExtenSion(file).equals("ppt")||MyFileUtils.getFileExtenSion(file).equals("pptx"))
						{
							if(file.length()/1024/1024>10)
							{
								MyToast.alert("文件不得大于10MB，请重新选择");
							}
							else
							{
								upLoadFile(file);							
							}
						}
						else
						{
							MyToast.alert("文件损坏或不是标准的PPT/PPTX文件,请重新选择");
						}
							
					} 
					catch (Exception e) 
					{
						Log.e("FileSelectorActivity", "File select error", e);
					}
				}
			} 
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);		
	}
	
	/**
	 * 上传文稿
	 * @param file
	 * @author Felix
	 */
	private void upLoadFile(File file)
	{
		String url ="/ppt/upload";
		String contentType="";
		if(MyFileUtils.getFileExtenSion(file).equals("ppt"))
		{
			contentType=PPT_CONTENTTYPE;
		}
		else if (MyFileUtils.getFileExtenSion(file).equals("pptx"))
		{
			contentType=PPTX_CONTENTTYPE;
		}
	
		
		RequestParams params = new RequestParams();
		try 
		{
			params.put(Param.PPTFILE,new FileInputStream(file),file.getName(),contentType);
			
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(getActivity(), "正在上传文稿...", false);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_light_green));
				loadingDialog.show();
			}

			@Override
			public void onSuccess(String response) 
			{
				Log.i("上传文稿返回:",response+"");
				try 
				{
					JSONObject jso =new JSONObject(response);
					
					if(jso.getInt("retcode")!=0)
					{
						MyToast.alert(jso.getInt("retcode"));
					}
					else
					{
						Toast.makeText(getActivity(), "上传成功,文稿正在转换中，请稍后刷新.", Toast.LENGTH_LONG).show();
						getPptList();						
					}
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				
			}
			@Override
			public void onFailure(Throwable e) 
			{
				e.printStackTrace();
				MyToast.alert("您的网络开小差,请稍后重试.");
			}

			@Override
			public void onFinish() 
			{
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() 
					{
						loadingDialog.dismiss();					
					}
				}, 700);
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
				Log.i("获取ppt列表返回:",response+"");
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
		    	if(xListview.isRefreshing())
		    	{
		    		xListview.onRefreshComplete();
		    	}
		    	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() 
					{
						loadingDialog.dismiss();					
					}
				}, 700);		    	
		    }
		});
	}
	
	/**
	 * 删除文稿
	 * @author Felix
	 */
	private void delePpt(final int position)
	{
		String url ="/ppt/delete";
		RequestParams params = new RequestParams();
		params.put(Param.PPT_ID_KEY, HomeApp.getLocalUser().getPpts().get(position).getPptId()+"");
		MyHttpClient.post(url, params, new AsyncHttpResponseHandler()
		{
			CustomProgressDialog loadingDialog;		
			
			@Override
			public void onStart()
			{
				loadingDialog=CustomProgressDialog.createDialog(getActivity(), "正在删除文稿...", false);
				loadingDialog.setMessageTextColor(getResources().getColor(R.color.theme_bright_red));
				loadingDialog.show();
			}
			
			@Override
			public void onSuccess(String response)
			{
				Log.i("删除PPT返回:",response+"");
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
		    	if(xListview.isRefreshing())
		    	{
		    		xListview.onRefreshComplete();
		    	}
		    	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
					loadingDialog.dismiss();
					getPptList();//刷新列表
					}
				}, 500);		    	
		    }
		});		
	}
	/**
	 * 弹出删除文稿确认对话框
	 * @param position
	 * @author Felix
	 */
	private void showConfirmDeletePptDialog(String name,final int position)
	{
		final Dialog dialog =new Dialog(getActivity(), R.style.mDialog);
		View layout =LayoutInflater.from(getActivity()).inflate(R.layout.normal_dialog,null);
		Button cancel  = (Button)layout.findViewById(R.id.normal_dialog_cancel_btn);
		Button confirm = (Button)layout.findViewById(R.id.normal_dialog_confirm_btn);
		TextView title = (TextView)layout.findViewById(R.id.normal_dialog_title);
		TextView   msg = (TextView)layout.findViewById(R.id.normal_dialog_message);
		msg.setText("即将删除文稿: "+name+" 是否确认?");
		title.setText("删除文稿");
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
				delePpt(position);
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();		
	}
	/**
	 * ppt列表适配器
	 * @author Felix
	 *
	 */
	class ItemAdapter extends BaseAdapter {
		

		private final List<PptFile> deleteableItems;
		public ItemAdapter() 
		{
			deleteableItems = new ArrayList<PptFile>();	
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

		public void delete(int position) 
		{
			deleteableItems.add(HomeApp.getLocalUser().getPpts().get(position));
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(HomeApp.getMyApplication()).inflate(R.layout.myppt_list_item, parent, false);
				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.my_ppt_item_title);
				holder.pptId = (TextView) convertView.findViewById(R.id.my_ppt_item_ppt_id);
				holder.status= (TextView) convertView.findViewById(R.id.my_ppt_item_status);
				holder.pages = (TextView) convertView.findViewById(R.id.my_ppt_item_total_pages);				
				holder.cover = (ImageView) convertView.findViewById(R.id.my_ppt_item_pic);
				holder.play  = (Button) convertView.findViewById(R.id.my_ppt_item_play);
				holder.delete= (Button) convertView.findViewById(R.id.my_ppt_delete_btn);
				convertView.setTag(holder);
			} 
			else 
			{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.getPaint().setFakeBoldText(true);
			holder.title.setText(HomeApp.getLocalUser().getPpts().get(position).getPptTitle()); 
			holder.pptId.setText("编号:"+HomeApp.getLocalUser().getPpts().get(position).getPptId()+""); 
			holder.status.setText("状态:"+(HomeApp.getLocalUser().getPpts().get(position).getPptStatus()?"已转换":"未转换"));			
			holder.pages.setText("总页数:"+HomeApp.getLocalUser().getPpts().get(position).getPptPageCount()+""); 
			if(HomeApp.getLocalUser().getPpts().get(position).getPptStatus())
			{
				ImageLoader.getInstance().displayImage(HomeApp.getLocalUser().getPpts().get(position).getCoverUrl(), holder.cover);
			}
			else
			{
				holder.cover.setImageResource(R.drawable.ic_coverting);
			}			
			holder.play.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!HomeApp.getLocalUser().getPpts().get(position).getPptStatus())
					{
						MyToast.alert("该文稿还未完成转换,请稍后重试...");
					}
					else
					{
						Intent intent =new Intent(getActivity(), PlaySlidesActivity.class);
						intent.putExtra(Define.Intent_KEY_PPT_POSITION, position);
						startActivity(intent);
					
					}
				}
			});	
			
			holder.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showConfirmDeletePptDialog(HomeApp.getLocalUser().getPpts().get(position).getPptTitle(), position);
				}
			});
			
			checkIfItemHasBeenMarkedAsDeleted(convertView, HomeApp.getLocalUser().getPpts().get(position));
			return convertView;
		}
		
		
		//----------------删除动画------------------------------------------------
		
		private void checkIfItemHasBeenMarkedAsDeleted(View view, PptFile item) {
			for (PptFile deletable : deleteableItems) {
				deleteItemIfMarkedAsDeletable(view, item, deletable);
			}
		}

		private void deleteItemIfMarkedAsDeletable(View view, PptFile item, PptFile deletable) {
			if(itemIsDeletable(item, deletable)){
				Animation anim = AnimationUtils.loadAnimation(getActivity(),R.anim.scale_out);
				anim.setDuration(500);
				deleteOnAnimationComplete(anim, item);
				animate(view, anim);
			}
		}

		private boolean itemIsDeletable(PptFile item, PptFile deletable) {
			return item.getPptId() == deletable.getPptId();
		}

		private void deleteOnAnimationComplete(Animation fadeout, final PptFile item) {
			fadeout.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) { }
				@Override
				public void onAnimationRepeat(Animation animation) { }

				@Override
				public void onAnimationEnd(Animation animation) {
					HomeApp.getLocalUser().getPpts().remove(item);
					deleteableItems.remove(item);
					notifyDataSetChanged();
				}
			});
		}

		private void animate(View view, Animation animation) {
			view.startAnimation(animation);
		}
	}	
	static class ViewHolder {
		public TextView title;
		public TextView user;
		public TextView pptId;
		public TextView status;
		public TextView pages;					
		public ImageView cover;
		public Button delete;
		public Button play;
	}
}
