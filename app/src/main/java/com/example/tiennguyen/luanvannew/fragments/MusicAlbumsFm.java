package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
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
        getAlbums.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements albums = data.select("div.zcontent div.fn-list div.album-item");
                for (Element albItem:albums){
                    Element aTag = albItem.select("a").first();
                    String img = aTag.select("img").attr("src");
                    Element aTagTitle = albItem.select("div.des .title-item a").first();
                    String albHref = aTagTitle.attr("href");
                    String title = aTagTitle.text();

                    ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    Elements singers = albItem.select(".singer-name");
                    for(Element singer:singers){
                        String singerHref = singer.select("a").attr("href");
                        String singerName = singer.select("a").text();
                        PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                        arrSingers.add(singerItem);
                    }

                    AlbumItem albumItem = new AlbumItem(title, albHref, img, 300, arrSingers);
                    arrAlbums.add(albumItem);
                }
                albumsAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getAlbums.execute(Constants.HOME_PAGE + categoryItem.getLink());
    }

}