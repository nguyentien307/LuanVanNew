package com.example.tiennguyen.luanvannew.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.adapters.PlaylistsAdapter;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.services.CheckInternet;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private LinearLayout llAddPlaylist;

    private String res = "";
    private Boolean isLogin;
    SessionManagement session;

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

        isLogin = checkLogin();
    }

    private Boolean checkLogin() {
        SessionManagement session = new SessionManagement(getContext(), new SessionManagement.HaveNotLoggedIn() {
            @Override
            public void haveNotLoggedIn() {

            }
        });
        return session.isLoggedIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_playlist, viewGroup, false);;
        session = new SessionManagement(getContext());
        if (CheckInternet.isConnected(getContext())) {
            if (isLogin) {
                view = inflater.inflate(R.layout.fm_playlist_login, viewGroup, false);
                ivAdd = (ImageView) view.findViewById(R.id.iv_add);
                llAddPlaylist = (LinearLayout) view.findViewById(R.id.llAddPlaylist);
//            ivAdd.setOnClickListener(this);
                llAddPlaylist.setOnClickListener(this);

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

            } else {
                view = inflater.inflate(R.layout.fm_playlist, viewGroup, false);
                Button btnLogin = (Button) view.findViewById(R.id.btn_login);
                btnLogin.setOnClickListener(this);
            }
        } else {
            CheckInternet.goNoInternet(getContext(), R.id.rlPlaylistContent);
        }
        return view;
    }

    private void preparePlaylists() {
        if(session.getPlaylist() != "") {
            String jsonPlaylists = session.getPlaylist();
            Toast.makeText(getContext(), jsonPlaylists, Toast.LENGTH_SHORT).show();
            try {
                JSONArray arr = new JSONArray(jsonPlaylists);
                for(int i = 0 ; i < arr.length(); i++){
                    JSONObject jsonItem = arr.getJSONObject(i);
//                    //JSONArray songs = jsonItem.getJSONArray("arrSongs");
//                    JSONArray songs = new JSONArray(jsonItem.getString("arrSongs"));
//                    ArrayList<SongItem> arrSongs = new ArrayList<>();
//                    for(int j = 0; j < songs.length(); j ++){
//                        JSONObject song = songs.getJSONObject(j);
//                        String title = song.getString("title");
//                        int views = song.getInt("views");
//                        String link = song.getString("link");
//                        String linkLyric = song.getString("linkLyric");
//                        String linkImg = song.getString("linkImg");
//                        JSONArray singers = song.getJSONArray("artist");
//                        JSONArray composers = song.getJSONArray("composer");
//                        final ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
//                        final ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
//                        for (int index = 0 ; index < singers.length(); index++){
//                            JSONObject singer = singers.getJSONObject(index);
//                            String singerHref = singer.getString("link");
//                            String singerName = singer.getString("name");
//                            int view = singer.getInt("views");
//                            PersonItem singerItem = new PersonItem(singerName, singerHref, view);
//                            arrSingers.add(singerItem);
//                        }
//                        for (int index = 0 ; index < composers.length(); index++){
//                            JSONObject composer = composers.getJSONObject(index);
//                            String composerHref = composer.getString("link");
//                            String composerName = composer.getString("name");
//                            int view = composer.getInt("views");
//                            PersonItem composerItem = new PersonItem(composerName, composerHref, view);
//                            arrComposers.add(composerItem);
//                        }
//                        SongItem item = new SongItem(title, views, link, arrSingers, arrComposers, linkLyric, linkImg );
//                        arrSongs.add(item);
//                    }
                    PlaylistItem item = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                    arrPlaylists.add(item);
                }
                playlistsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login: {
                //((MyApplication) getActivity().getApplication()).setLogin(true);

                LoginFm frag = LoginFm.newInstance("Playlist");
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
            }; break;

            case R.id.llAddPlaylist: {
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

                        ArrayList<String> arrName = new ArrayList<String>();
                        Boolean isValid = true;

                        if(session.getPlaylist() != "") {
                            String jsonPlaylists = session.getPlaylist();
                            try {
                                JSONArray arr = new JSONArray(jsonPlaylists);
                                for(int i = 0 ; i < arr.length(); i++){
                                    JSONObject jsonItem = arr.getJSONObject(i);
                                    String title = jsonItem.getString("name");
                                    arrName.add(title);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(arrName.size()!=0){
                            for(int i = 0; i < arrName.size(); i++){
                                if(name.contentEquals(arrName.get(i))) {
                                    isValid = false;
                                    break;
                                }
                            }
                        }

                        if(!isValid){
                            Toast.makeText(getContext(), name + " already defined", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            PlaylistItem item = new PlaylistItem(name,"", R.drawable.hot_slider1, 0, "");
                            arrPlaylists.add(item);
                            Gson gson = new Gson();
                            String jsonPlaylist = gson.toJson(arrPlaylists);
                            session.setPlaylist(jsonPlaylist);
                            PlaylistFm fragment = PlaylistFm.newInstance("new");
                            ((AppCompatActivity) getContext()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.fragment_container, fragment)
                                    .commit();
                        }
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        }
    }

}
