package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class StyleFm extends Fragment implements View.OnClickListener {

    private TextView tvHeaderTitle;
    private LinearLayout llBackButton;
    private ImageView ivBackButton;
    private String res = "";

    public static StyleFm newInstance(String name) {
        StyleFm contentFragment = new StyleFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            res = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_style, viewGroup, false);
        tvHeaderTitle = (TextView) view.findViewById(R.id.tv_header_title);
        tvHeaderTitle.setText(R.string.style_header_title);
        llBackButton = (LinearLayout) view.findViewById(R.id.ll_btn_back);
        ivBackButton = (ImageView) view.findViewById(R.id.iv_btn_back);
        llBackButton.setOnClickListener(this);
        ivBackButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
            case R.id.iv_btn_back:{
                LinearLayout fullScreen  = (LinearLayout) getActivity().findViewById(R.id.full_screen_content);
                fullScreen.setVisibility(View.GONE);
            }

        }
    }
}