package com.liveppt.app.fragment;

import com.liveppt.app.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyPptFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.my_ppt_frag, null);
	}

}
