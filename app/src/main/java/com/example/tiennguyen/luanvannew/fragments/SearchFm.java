package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class SearchFm extends Fragment {

    private String res = "";

    public static SearchFm newInstance(String name) {
        SearchFm contentFragment = new SearchFm();
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
        View view = inflater.inflate(R.layout.fm_search, viewGroup, false);
        TextView output= (TextView)view.findViewById(R.id.msg);
        output.setText("Fragment Search");
        return view;
    }

}