package com.example.tiennguyen.luanvannew.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.services.CheckInternet;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class MusicAlbumsFm extends Fragment {
    //title name
    private TextView tvTitleName;

    //recycler albums
    private RecyclerView rcAlbums;
    private AlbumsAdapter albumsAdapter;
    private ArrayList<AlbumItem> arrAlbums = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;

    private RelativeLayout rlMusicAlbumLoading;

    private String res = "";
    private CategoryItem categoryItem;

    public static MusicAlbumsFm newInstance(CategoryItem categoryItem) {
        MusicAlbumsFm contentFragment = new MusicAlbumsFm();
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
        View view = inflater.inflate(R.layout.fm_music_albums, viewGroup, false);

        //title name
        tvTitleName = (TextView) view.findViewById(R.id.tv_title_name);
        tvTitleName.setText(categoryItem.getName());

        rlMusicAlbumLoading = (RelativeLayout) view.findViewById(R.id.rlMusicAlbumLoading);
        //recyclerview albums
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
        setLoadingVisible(true);
        getAlbums.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                setLoadingVisible(false);
                viewAlbumList(data);
        }

            @Override
            public void dataDownloadFailed() {
                CheckInternet.goNoInternet(getContext(), R.id.music_content);
            }
        });
        getAlbums.execute(categoryItem.getLink());
    }

    private void viewAlbumList(Document data) {
        Elements albums = data.select("div.list_album ul li");
        for (Element albItem:albums){
            String img = albItem.select("div.box-left-album .avatar img").attr("data-src");
            Element info = albItem.select("div.info_album").first();
            String albHref = info.select("h2 a").attr("href");
            String title = info.select("h2 a").text();

            ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
            Elements singers = info.select("p a");
            for(Element singer:singers){
                String singerHref = singer.attr("href");
                String singerName = singer.text();
                PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                arrSingers.add(singerItem);
            }

            AlbumItem albumItem = new AlbumItem(title, albHref, img, 300, arrSingers);
            arrAlbums.add(albumItem);
        }
        albumsAdapter.notifyDataSetChanged();
    }

    private void setLoadingVisible(boolean b) {
        if (b) {
            rlMusicAlbumLoading.setVisibility(View.VISIBLE);
        } else {
            rlMusicAlbumLoading.setVisibility(View.GONE);
        }
    }
}
