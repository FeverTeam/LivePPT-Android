package com.fever.fragment;

import com.fever.liveppt.LoginActivity;
import com.fever.liveppt.R;
import com.fever.utils.MySharedPreferences;
import com.fever.utils.MyToast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 帐号页面Fragment类
 * @author Felix
 */
public class MyAccountFrag extends Fragment {
	LinearLayout logout;
	LinearLayout exit;
	LinearLayout setPassWord;


    /**
     *创建账号界面
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     * last modified: Frank
     */
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.my_account_frag, container, false);
        logout=(LinearLayout)view.findViewById(R.id.logout);
        exit=(LinearLayout)view.findViewById(R.id.exit_application);
        setPassWord=(LinearLayout)view.findViewById(R.id.setting_password);

        init();
        return view;
    }


    /**
     * 初始化界面，并为相关布局注册监听器
     * last modified: Frank
     */
	public void init(){
		//注销
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MySharedPreferences.SaveShared(getActivity(), "loginInfo", "email", "", false);
				MySharedPreferences.SaveShared(getActivity(), "loginInfo", "password", "", false);
				Intent intent=new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		//退出
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		
		//修改密码
		setPassWord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new MyToast().alert(getActivity(), "--Coming Soon--");
			}
		});
		
	}
}
