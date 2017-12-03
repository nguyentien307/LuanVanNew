package com.example.tiennguyen.luanvannew.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/14/2017.
 */

public class PlaylistSongsFm extends Fragment implements View.OnClickListener {
    //heder title
    private TextView tvHeaderTitle;
    //button back
    private LinearLayout llBackButton;
    private ImageView ivBackButton;
    //title
    private ImageView ivBgAvatar, ivDelete, ivPlay;
    private TextView tvPlaylistName, tvPlaylistNumber;
    // title singer va title composer
   // private TextView tvTitleName;
    //Recycle view
    private RecyclerView rcSongs;
    private RecyclerView.LayoutManager songsLayoutManager;
    private SongsAdapter songsAdapter;
    private ArrayList<SongItem> arrSongs = new ArrayList<>();
    //InformationsAdapter singersAdapter, composersAdapter;

    private PlaylistItem playlistItem ;
    private int position;
    private SessionManagement session;

    public static PlaylistSongsFm newInstance(PlaylistItem playlistItem) {
        PlaylistSongsFm contentFragment = new PlaylistSongsFm();
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlistItem", playlistItem);
        bundle.putInt("position", -1);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    public static PlaylistSongsFm newInstance(PlaylistItem playlistItem, int position) {
        PlaylistSongsFm contentFragment = new PlaylistSongsFm();
        Bundle bundle = new Bundle();
        bundle.putSerializable("playlistItem", playlistItem);
        bundle.putInt("position", position);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            playlistItem = (PlaylistItem) getArguments().getSerializable("playlistItem");
            position = getArguments().getInt("position");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_playlist_songs, viewGroup, false);
        tvHeaderTitle = (TextView) view.findViewById(R.id.tv_header_title);
        tvHeaderTitle.setText(playlistItem.getName());
        llBackButton = (LinearLayout) view.findViewById(R.id.ll_btn_back);
        ivBackButton = (ImageView) view.findViewById(R.id.iv_btn_back);
        llBackButton.setOnClickListener(this);
        ivBackButton.setOnClickListener(this);

        ivBgAvatar = (ImageView) view.findViewById(R.id.iv_bg_avatar);
        Glide.with(getContext()).load(playlistItem.getImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(ivBgAvatar);
        ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
        ivPlay = (ImageView) view.findViewById(R.id.iv_play);

        ivDelete.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        tvPlaylistName = (TextView) view.findViewById(R.id.tv_playlist_name);
        tvPlaylistName.setText(playlistItem.getName());
        tvPlaylistNumber = (TextView) view.findViewById(R.id.tv_playlist_number);
        tvPlaylistNumber.setText(playlistItem.getNumber() + " song(s)");



        //recycler view
        rcSongs = (RecyclerView) view.findViewById(R.id.rc_songs);
        rcSongs.setHasFixedSize(true);
        rcSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONGS_LIST_IN_PLAYLIST, rcSongs, position);
        rcSongs.setAdapter(songsAdapter);
        prepareSongs();

        return view;
    }

    private void prepareSongs() {
        session = new SessionManagement(getContext());
        //ArrayList<PlaylistItem> newPlaylists = new ArrayList<>();

        PlaylistItem playlistItem;

        String jsonPlaylists = session.getPlaylist();
        //Toast.makeText(context, jsonPlaylists, Toast.LENGTH_LONG).show();
        try {
            JSONArray arr = new JSONArray(jsonPlaylists);
            JSONObject jsonItem = arr.getJSONObject(position);
            playlistItem = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
            //JSONObject item = new JSONObject(newPlaylists.get(position).getArrSongs());
            String a = playlistItem.getArrSongs();
            if (!a.equals("")) {
                JSONArray songs = new JSONArray(a);
                for (int j = 0; j < songs.length(); j++) {
                    JSONObject song = songs.getJSONObject(j);
                    String title = song.getString("title");
                    int views = song.getInt("views");
                    String link = song.getString("link");
                    String linkLyric = song.getString("linkLyric");
                    String linkImg = song.getString("linkImg");
                    JSONArray singers = song.getJSONArray("artist");
                    JSONArray composers = song.getJSONArray("composer");
                    final ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    final ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
                    for (int index = 0; index < singers.length(); index++) {
                        JSONObject singer = singers.getJSONObject(index);
                        String singerHref = singer.getString("link");
                        String singerName = singer.getString("name");
                        int view = singer.getInt("views");
                        PersonItem singerItem = new PersonItem(singerName, singerHref, view);
                        arrSingers.add(singerItem);
                    }
                    for (int index = 0; index < composers.length(); index++) {
                        JSONObject composer = composers.getJSONObject(index);
                        String composerHref = composer.getString("link");
                        String composerName = composer.getString("name");
                        int view = composer.getInt("views");
                        PersonItem composerItem = new PersonItem(composerName, composerHref, view);
                        arrComposers.add(composerItem);
                    }
                    SongItem item = new SongItem(title, views, link, arrSingers, arrComposers, linkLyric, linkImg);
                    arrSongs.add(item);
                }
                songsAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
            case R.id.iv_btn_back:{
                Fragment fragment = PlaylistFm.newInstance("new");
                ((AppCompatActivity) getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            };break;

            case R.id.iv_delete:{
                AlertDialog diaBox = AskOption();
                diaBox.show();
            };break;
            case R.id.iv_play:{
                playSong();
            }; break;

        }
    }

    private void playSong() {
        LinearLayout llPlayerCol = (LinearLayout) getActivity().findViewById(R.id.llPlayerCollapse);
        llPlayerCol.setVisibility(View.VISIBLE);
        PlayerCollapseFm playerCollapseFm = PlayerCollapseFm.newInstance(arrSongs.get(0));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.llPlayerCollapse, playerCollapseFm).commit();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("arrSong", arrSongs);
        for (int i = 0; i < arrSongs.size(); i++) {
            bundle.putParcelableArrayList("arrArtist" + i, arrSongs.get(i).getArtist());
            bundle.putParcelableArrayList("arrComposer" + i, arrSongs.get(i).getComposer());
        }
        bundle.putInt("index", 0);
        bundle.putString("type", Constants.ALBUM_CATEGORIES);
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, com.example.tiennguyen.luanvannew.commons.Constants.REQUEST_CODE);
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle(R.string.action_delete_all)
                .setMessage(R.string.delete_playlist_message)
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton(getResources().getString(R.string.action_delete), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        session = new SessionManagement(getContext());
                        //ArrayList<PlaylistItem> newPlaylists = new ArrayList<>();
                        ArrayList<SongItem> arrSong = new ArrayList<SongItem>();
                        ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();

                        PlaylistItem playlistItem;

                        String jsonPlaylists = session.getPlaylist();
                        //Toast.makeText(context, jsonPlaylists, Toast.LENGTH_LONG).show();
                        try {
                            JSONArray arr = new JSONArray(jsonPlaylists);
                            for(int i = 0 ; i < arr.length(); i++) {
                                JSONObject jsonItem = arr.getJSONObject(i);
                                PlaylistItem item = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                                arrPlaylists.add(item);
                            }
                            JSONObject jsonItem = arr.getJSONObject(position);
                            playlistItem = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                            playlistItem.setArrSongs("");
                            playlistItem.setNumber(0);
                            Gson gson = new Gson();
                            arrPlaylists.set(position, playlistItem);
                            String jsonPlaylist = gson.toJson(arrPlaylists);
                            session.setPlaylist(jsonPlaylist);

                            Fragment fragment = PlaylistSongsFm.newInstance(playlistItem, position);
                            ((AppCompatActivity) getContext()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
