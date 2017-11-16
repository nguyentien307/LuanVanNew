package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class UserFm extends Fragment implements View.OnClickListener {

    ArrayList<PlaylistItem> arrPlaylists;

    private String res = "";

    private Boolean isLogin;

    public static UserFm newInstance(String name) {
        UserFm contentFragment = new UserFm();
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
        isLogin = ((MyApplication) getActivity().getApplication()).getLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_user, viewGroup, false);
        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                ((MyApplication) getActivity().getApplication()).setLogin(true);
                preparePlaylists();
                ((MyApplication) getActivity().getApplication()).setArrPlaylists(arrPlaylists);
                PlaylistFm frag = PlaylistFm.newInstance("new");
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
            }; break;
        }
    }

    private void preparePlaylists() {
        arrPlaylists = new ArrayList<>();
        PlaylistItem item0 = new PlaylistItem("Nhạc yêu thích", "", R.drawable.hot_slider1, 19);
        arrPlaylists.add(item0);
        PlaylistItem item1 = new PlaylistItem("Nhạc buồn", "", R.drawable.hot_slider2, 10);
        arrPlaylists.add(item1);
        PlaylistItem item2 = new PlaylistItem("Nhạc sôi động", "", R.drawable.hot_slider3, 15);
        arrPlaylists.add(item2);
    }
}