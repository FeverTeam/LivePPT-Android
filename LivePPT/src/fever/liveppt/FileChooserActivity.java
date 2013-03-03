package fever.liveppt;

import java.io.File;
import java.util.ArrayList;
import fever.liveppt.FileChooserAdapter.FileInfo;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.widget.TextView;

public class FileChooserActivity extends Activity {
	
	private GridView mGridView;
	private View mBackView;
	private View mBtExit;
	private TextView mTvPath ;
	
	private String mSdcardRootPath ;  //sdcard 鏍硅矾寰�	
	private String mLastFilePath ;    //褰撳墠鏄剧ず鐨勮矾寰�	
	private ArrayList<FileInfo> mFileLists  ;
	private FileChooserAdapter mAdatper ;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filechooser_show);

		mSdcardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();// 锟矫碉拷sdcard目录

		mBackView = findViewById(R.id.imgBackFolder);
		mBackView.setOnClickListener(mClickListener);
		mBtExit = findViewById(R.id.btExit);
		mBtExit.setOnClickListener(mClickListener);
		
		mTvPath = (TextView)findViewById(R.id.tvPath);
		
		mGridView = (GridView)findViewById(R.id.gvFileChooser);
		mGridView.setEmptyView(findViewById(R.id.tvEmptyHint));
		mGridView.setOnItemClickListener(mItemClickListener);
		setGridViewAdapter(mSdcardRootPath);
	}
	private void setGridViewAdapter(String filePath) {
		updateFileItems(filePath);
		mAdatper = new FileChooserAdapter(this , mFileLists);
		mGridView.setAdapter(mAdatper);
	}
	//鏍规嵁璺緞鏇存柊鏁版嵁锛屽苟涓旈�鐭datper鏁版嵁鏀瑰彉
	private void updateFileItems(String filePath) {
		mLastFilePath = filePath ;
		mTvPath.setText(mLastFilePath);
		
		if(mFileLists == null)
			mFileLists = new ArrayList<FileInfo>() ;
		if(!mFileLists.isEmpty())
			mFileLists.clear() ;
		
		File[] files = folderScan(filePath);
		if(files == null) 
			return ;
		
		for (int i = 0; i < files.length; i++) {
			if(files[i].isHidden())  // 涓嶆樉绀洪殣钘忔枃浠�				
				continue ;
			String fileAbsolutePath = files[i].getAbsolutePath() ;
			String fileName = files[i].getName();
		    boolean isDirectory = false ;
			if (files[i].isDirectory()){
				isDirectory = true ;
			}
		    FileInfo fileInfo = new FileInfo(fileAbsolutePath , fileName , isDirectory) ;
			mFileLists.add(fileInfo);
		}
		//When first enter , the object of mAdatper don't initialized
		if(mAdatper != null)
		    mAdatper.notifyDataSetChanged();  //閲嶆柊鍒锋柊
	}
	//鑾峰緱褰撳墠璺緞鐨勬墍鏈夋枃浠�	
		private File[] folderScan(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		return files;
	}
	
	private View.OnClickListener mClickListener = new  OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgBackFolder:
				backProcess();
				break;
			case R.id.btExit :
				setResult(RESULT_CANCELED);
				finish();
			    break ;
			default :
			    	break ;
			}
		}
	};
	
	private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long id) {
			FileInfo fileInfo = (FileInfo)(((FileChooserAdapter)adapterView.getAdapter()).getItem(position));
			if(fileInfo.isDirectory())   			
				updateFileItems(fileInfo.getFilePath()) ;
			else if(fileInfo.isPPTFile()){  		    
				Intent intent = new Intent();
			    intent.putExtra(PPT_upload.EXTRA_FILE_CHOOSER, fileInfo.getFilePath());
			    setResult(RESULT_OK , intent);
			    finish();
			}
			else {   
				toast("The format of file is error");
			}
		}
	};
    
	public boolean onKeyDown(int keyCode , KeyEvent event){
		if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode()
			== KeyEvent.KEYCODE_BACK){
			backProcess();   
			return true ;
		}
		return super.onKeyDown(keyCode, event);
	}
	//杩斿洖涓婁竴灞傜洰褰曠殑鎿嶄綔
	public void backProcess(){
		//鍒ゆ柇褰撳墠璺緞鏄笉鏄痵dcard璺緞 锛�濡傛灉涓嶆槸锛屽垯杩斿洖鍒颁笂涓�眰銆�		
		if (!mLastFilePath.equals(mSdcardRootPath)) {  
			File thisFile = new File(mLastFilePath);
			String parentFilePath = thisFile.getParent();
			updateFileItems(parentFilePath);
		} 
		else {   //鏄痵dcard璺緞 锛岀洿鎺ョ粨鏉�			
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	private void toast(CharSequence hint){
	    Toast.makeText(this, hint , Toast.LENGTH_SHORT).show();
	}
}