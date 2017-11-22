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
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    private String songsLink;

    public static ArtistSongsFm newInstance(int page, String songsLink) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString("songsLink", songsLink);
        ArtistSongsFm fragment = new ArtistSongsFm();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        songsLink = getArguments().getString("songsLink");

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
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES, rcSongs);
        rcSongs.setAdapter(songsAdapter);
        prepareSongs();

        return view;
    }

    private void prepareSongs() {
        GetPage getSongs = new GetPage(getContext());
        getSongs.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements songs;

                Boolean isSearch;
                if(data.select("ul.list_item_music li").size()!= 0){
                    songs = data.select("ul.list_item_music li");
                    isSearch = false;
                }else {
                    songs = data.select("ul.search_returns_list li");
                    isSearch = true;
                }
                for(Element song:songs){
                    String title = song.select("div.item_content h3 a").text();
                    String href = song.select("div.item_content h3 a").attr("href");
                    Elements singers;
                    if(isSearch){
                        singers = song.select("div.item_content a");
                    }else {
                        singers = song.select("div.item_content span a");
                    }
                    ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    for (Element singer:singers){
                        String singerHref = singer.attr("href");
                        String singerName = singer.text();
                        PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                        arrSingers.add(singerItem);
                    }
                    ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
                    PersonItem composer = new PersonItem("NHAC SĨ", "", 200);
                    arrComposers.add(composer);
                    SongItem songItem = new SongItem(title, 200, href, arrSingers, arrComposers, "", "");
                    arrSongs.add(songItem);
                }
                songsAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getSongs.execute(songsLink);
    }
}
