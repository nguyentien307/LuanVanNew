package com.example.tiennguyen.luanvannew.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.PlaylistsNameAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class MyAlertDialogFragment extends DialogFragment {

    public static final String TITLE = "dataKey";
    ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();
    SessionManagement session;
    PlaylistsNameAdapter playlistsAdapter;
    SongItem songItem;
    public static MyAlertDialogFragment newInstance(ArrayList<PlaylistItem> arrPlaylists, SongItem songItem) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TITLE, arrPlaylists);
        args.putSerializable("songItem", songItem);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //arrPlaylists = getArguments().getParcelableArrayList(TITLE);
        songItem = (SongItem) getArguments().getSerializable("songItem");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_playlist_box, null);
        LinearLayout btnCancel = (LinearLayout) view.findViewById(R.id.btn_cancel);


        RecyclerView rcPlaylists = (RecyclerView) view.findViewById(R.id.rc_playlists);
        //recycler
        RecyclerView.LayoutManager playlistsLayoutManager;

        session = new SessionManagement(getContext());

        //ArrayList<PlaylistItem> arrPlaylists = ((MyApplication) getActivity().getApplication()).getArrPlaylists();
        //recycler view
        rcPlaylists.setHasFixedSize(true);
        rcPlaylists.setNestedScrollingEnabled(false);
        playlistsLayoutManager = new LinearLayoutManager(getContext());
        rcPlaylists.setLayoutManager(playlistsLayoutManager);
        preparePlaylists();
        playlistsAdapter = new PlaylistsNameAdapter(getContext(), getActivity(), arrPlaylists, MyAlertDialogFragment.this, songItem);
        rcPlaylists.setAdapter(playlistsAdapter);
        setCancelable(true);
        builder.setView(view);
        builder.setTitle(Constants.ADD_PLAYLIST_TITLE);
        final Dialog dialog = builder.create();

//        dialog.getWindow().setBackgroundDrawable(
//                new ColorDrawable(Color.rgb(60, 50, 50)));
//        dialog.getWindow().setTitleColor(Color.rgb(255, 255, 255));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return dialog;

    }

    private void preparePlaylists() {
        if(session.getPlaylist() != "") {
            String jsonPlaylists = session.getPlaylist();
            //Toast.makeText(getContext(), jsonPlaylists, Toast.LENGTH_SHORT).show();
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}