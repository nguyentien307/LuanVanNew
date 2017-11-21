package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.interfaces.OnLoadMoreListener;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    //continue load
    private Boolean isRemain = true;
    private int index = 0;
    private int end = 20;

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
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES, rcSongs);
        rcSongs.setAdapter(songsAdapter);
        prepareSongs();

        songsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isRemain) {
                    arrSongs.add(null);
                    songsAdapter.notifyItemInserted(arrSongs.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            arrSongs.remove(arrSongs.size() - 1);
                            songsAdapter.notifyItemRemoved(arrSongs.size());

                            //Generating more data
                            index = arrSongs.size();
                            end = index + 20;
                            //Toast.makeText(getActivity(), "index" + index, Toast.LENGTH_SHORT).show();
                            //prepareSongs();
                            songsAdapter.notifyDataSetChanged();
                            songsAdapter.setLoaded();
                        }
                    }, 5000);
                } else {
                    Toast.makeText(getActivity(), "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void prepareSongs() {
        GetPage getSongs = new GetPage(getContext());
        getSongs.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements songElements = data.select("div.table-body ul li");
                if(end>songElements.size()) {
                    isRemain = false;
                    end = songElements.size();
                }
                    for (int i = index; i < end; i++) {
                        Element songElement = songElements.get(i);
                        String data_code = songElement.attr("data-code");
                        Element e_item = songElement.select("div.e-item").first();
                        String img = e_item.select("a").select("img").attr("src");

                        Element aTagTitle = e_item.select(".title-item a").first();
                        String title = aTagTitle.text();

                        ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                        Elements singers = e_item.select(".title-sd-item");
                        for (Element singer : singers) {

                            String singerHref = singer.select("a").attr("href");
                            String singerName = singer.select("a").text();
                            PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                            arrSingers.add(singerItem);
                        }
                        ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
                        PersonItem composer = new PersonItem("NHAC SĨ", "", 200);
                        arrComposers.add(composer);
                        SongItem songItem = new SongItem(title, 200, data_code, arrSingers, arrComposers, "", img);
                        arrSongs.add(songItem);
                    }
                    songsAdapter.notifyDataSetChanged();


            }

            @Override
            public void dataDownloadFailed () {

            }
        });
        getSongs.execute(Constants.HOME_PAGE + categoryItem.getLink());
    }

}
