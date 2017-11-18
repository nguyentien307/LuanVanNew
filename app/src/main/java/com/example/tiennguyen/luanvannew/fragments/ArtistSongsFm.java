package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class ArtistSongsFm extends Fragment{
    //Recyclcer
    private RecyclerView rcSongs;
    private RecyclerView.LayoutManager songsLayoutManager;
    private SongsAdapter songsAdapter;
    private ArrayList<SongItem> arrSongs = new ArrayList<>();

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static ArtistSongsFm newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ArtistSongsFm fragment = new ArtistSongsFm();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_songs, container, false);


        //recycler song
        rcSongs = (RecyclerView) view.findViewById(R.id.rc_songs);
        rcSongs.setHasFixedSize(true);
        rcSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES);
        rcSongs.setAdapter(songsAdapter);
        prepareSongs();

        return view;
    }

    private void prepareSongs() {
        JSONObject data = null;
        try {
            data = new JSONObject(Constants.SONG_DATA);

            JSONArray songList = data.getJSONArray("list");
            for(int songIndex = 0; songIndex < songList.length() ; songIndex++){
                JSONObject song = songList.getJSONObject(songIndex);
                String title = song.getString("title");
                String img = song.getString("img");
                String href = song.getString("href");
                JSONArray singersJSON = song.getJSONArray("singers");
                ArrayList<PersonItem> arrSinger = new ArrayList<PersonItem>();
                ArrayList<PersonItem> arrComposer = new ArrayList<PersonItem>();
                for (int singerIndex = 0; singerIndex < singersJSON.length(); singerIndex++ ){
                    JSONObject singer = singersJSON.getJSONObject(singerIndex);
                    String singerName = singer.getString("singerName");
                    String singerHref = singer.getString("singerHref");
                    PersonItem singerItem = new PersonItem(singerName, singerHref, 200);
                    arrSinger.add(singerItem);
                }
                PersonItem composer = new PersonItem("NHAC SĨ", "", 200);
                arrComposer.add(composer);
                SongItem songItem = new SongItem(title,200, href, arrSinger, arrComposer, "", img);
                arrSongs.add(songItem);

            }

            songsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
