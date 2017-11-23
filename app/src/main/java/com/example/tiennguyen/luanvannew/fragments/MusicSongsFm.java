package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.GetDataCodeFromZing;
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

    private ArrayList<String> arrPages = new ArrayList<>();
    private Boolean isLoadPages = true;
    private  int index = 0;

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
        prepareSongs(categoryItem.getLink());

        songsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isRemain) {
                    arrSongs.add(null);
                    songsAdapter.notifyItemInserted(arrSongs.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            arrSongs.remove(arrSongs.size() - 1);
//                            songsAdapter.notifyItemRemoved(arrSongs.size());

                            //Generating more data
                            prepareSongs(arrPages.get(index));
                            index = index + 1;
                            if (index == arrPages.size()) isRemain = false;

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


    private void prepareSongs(String href) {
        GetPage getSongs = new GetPage(getContext());
        getSongs.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements songElements = data.select("ul.list_item_music li");
                for (int i = 0; i < songElements.size(); i++) {
                    Element songElement;
                    songElement = songElements.get(i).select("div.item_content h2").first();
                    if(songElement == null){
                        songElement = songElements.get(i).select("div.item_content h3").first();
                    }
                    Elements info = songElement.select("a");
                    final String title = info.get(0).text();
                    String href = info.get(0).attr("href");
                    final ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    for (int index = 1 ; index < info.size(); index++){
                        Element aTag = info.get(index);
                        String singerHref = aTag.attr("href");
                        String singerName = aTag.text();
                        PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                        arrSingers.add(singerItem);
                    }
                    final ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
                    PersonItem composer = new PersonItem("NHAC SĨ", "", 200);
                    arrComposers.add(composer);
                    GetDataCodeFromZing getDataCodeFromZing = new GetDataCodeFromZing(new GetDataCodeFromZing.KeyCodeFromZing() {
                        @Override
                        public void keyCodeFromZing(String key, String imgLink) {
                            SongItem item = new SongItem(title, 200, key, arrSingers, arrComposers, "", imgLink);
                            arrSongs.add(item);
                            songsAdapter.notifyDataSetChanged();
                        }
                    });
                    getDataCodeFromZing.getKeyFromZing(getContext(), title);
                }

                if(isLoadPages){
                    Elements pages = data.select("div.box_pageview a");
                    for(int i = 1; i < pages.size() -1 ; i++){
                        String href = pages.get(i).attr("href");
                        arrPages.add(href);
                    }
                }
            }

            @Override
            public void dataDownloadFailed () {

            }
        });
        getSongs.execute(href);
    }

}
