package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class MusicSongsFm extends Fragment {

    //title
    private TextView tvTitleName;

    //recycler song
    private RecyclerView rcSongs;
    private RecyclerView.LayoutManager songsLayoutManager;
    private SongsAdapter songsAdapter;
    private ArrayList<SongItem> arrSongs = new ArrayList<>();

    //instance category
    private CategoryItem categoryItem;

    private String res = "";

    public static MusicSongsFm newInstance(CategoryItem categoryItem) {
        MusicSongsFm contentFragment = new MusicSongsFm();
        Bundle bundle = new Bundle();
        bundle.putSerializable("categoryItem", categoryItem);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
        categoryItem = (CategoryItem) getArguments().getSerializable("categoryItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_music_songs, viewGroup, false);
        //title name
        tvTitleName = (TextView) view.findViewById(R.id.tv_title_name);
        tvTitleName.setText(categoryItem.getName());



        //recycle songs
        rcSongs = (RecyclerView) view.findViewById(R.id.rc_songs);
        rcSongs.setHasFixedSize(true);
        rcSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES );
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
