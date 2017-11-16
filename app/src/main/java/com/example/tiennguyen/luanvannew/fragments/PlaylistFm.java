package com.example.tiennguyen.luanvannew.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.adapters.PlaylistsAdapter;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class PlaylistFm extends Fragment implements View.OnClickListener {
    private ImageView ivAdd;
    //recycler playlist
    private RecyclerView rcPlaylists;
    private PlaylistsAdapter playlistsAdapter;
    private ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;

    private String res = "";
    private Boolean isLogin;

    public static PlaylistFm newInstance(String name) {
        PlaylistFm contentFragment = new PlaylistFm();
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
        View view;
        if(isLogin){
            view = inflater.inflate(R.layout.fm_playlist_login, viewGroup, false);
            ivAdd = (ImageView) view.findViewById(R.id.iv_add);
            ivAdd.setOnClickListener(this);

            //recycler
            rcPlaylists = (RecyclerView) view.findViewById(R.id.rc_playlists);
            playlistsAdapter = new PlaylistsAdapter(getContext(), arrPlaylists);
            layoutManager = new GridLayoutManager(getContext(), 2);
            rcPlaylists.setLayoutManager(layoutManager);
            rcPlaylists.addItemDecoration(new AlbumsAdapter.GridSpacingItemDecoration(2, playlistsAdapter.dpToPx(10), true));
            rcPlaylists.setItemAnimator(new DefaultItemAnimator());
            rcPlaylists.setNestedScrollingEnabled(false);
            rcPlaylists.setAdapter(playlistsAdapter);

            preparePlaylists();

        }else {
            view = inflater.inflate(R.layout.fm_playlist, viewGroup, false);
            Button btnLogin = (Button) view.findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(this);
        }
        return view;
    }

    private void preparePlaylists() {
        arrPlaylists.clear();
        PlaylistItem item0 = new PlaylistItem("Nhạc yêu thích", "", R.drawable.hot_slider1, 19);
        arrPlaylists.add(item0);
        PlaylistItem item1 = new PlaylistItem("Nhạc buồn", "", R.drawable.hot_slider2, 10);
        arrPlaylists.add(item1);
        PlaylistItem item2 = new PlaylistItem("Nhạc sôi động", "", R.drawable.hot_slider3, 15);
        arrPlaylists.add(item2);
        playlistsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                //((MyApplication) getActivity().getApplication()).setLogin(true);

                UserFm frag = UserFm.newInstance("new");
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
            }; break;

            case R.id.iv_add: {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.create_playlist_box, null);
                final EditText etName = (EditText) alertLayout.findViewById(R.id.et_name);


                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("New Playlist");
                // this is set the view from XML inside AlertDialog
                alert.setView(alertLayout);
                // disallow cancel of AlertDialog on click of back button and outside touch
                alert.setCancelable(false);
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etName.getText().toString();
                        Toast.makeText(getContext(), "name: " + name, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        }
    }

}