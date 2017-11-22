package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class ArtistAlbumsFm extends Fragment{
    //Recyclcer
    private RecyclerView rcAlbums;
    private AlbumsAdapter albumsAdapter;
    private ArrayList<AlbumItem> arrAlbums = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private String albumsLink;

    public static ArtistAlbumsFm newInstance(int page, String albumsLink) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString("albumsLink", albumsLink);
        ArtistAlbumsFm fragment = new ArtistAlbumsFm();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        albumsLink = getArguments().getString("albumsLink");
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_albums, container, false);
        //recycler song
        rcAlbums = (RecyclerView) view.findViewById(R.id.rc_albums);
        albumsAdapter = new AlbumsAdapter(getContext(), arrAlbums, Constants.VERTICAL_ALBUMS_LIST);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rcAlbums.setLayoutManager(layoutManager);
        rcAlbums.addItemDecoration(new AlbumsAdapter.GridSpacingItemDecoration(2, albumsAdapter.dpToPx(10), true));
        rcAlbums.setItemAnimator(new DefaultItemAnimator());
        rcAlbums.setNestedScrollingEnabled(false);
        rcAlbums.setAdapter(albumsAdapter);

        prepareAlbums();

        return view;
    }

    private void prepareAlbums() {
        GetPage getAlbums = new GetPage(getContext());
        getAlbums.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements albums;
                if (data.select("div.fram_select ul li").size() != 0){
                    albums = data.select("div.fram_select ul li");
                }
                else {
                    albums = data.select("ul.search_returns_list li");
                }
                for (Element album : albums) {
                    String img = album.select("div.box-left-album a span.avatar img").attr("data-src");
                    Element info = album.select("div.info_album").first();
                    String href = info.select("h3 a").attr("href");
                    String title = info.select("h3 a").text();
                    Elements singers = info.select("p a");
                    ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    for (Element singer : singers) {
                        String singerHref = singer.attr("href");
                        String singerName = singer.text();
                        PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                        arrSingers.add(singerItem);
                    }
                    AlbumItem albumItem = new AlbumItem(title, href, img, 300, arrSingers);
                    arrAlbums.add(albumItem);
                }
                albumsAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getAlbums.execute(albumsLink);
    }
}
