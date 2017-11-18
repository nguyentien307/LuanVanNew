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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject data = null;
        try {
            data = new JSONObject(Constants.DATA);

            JSONArray albumListJSON = data.getJSONArray("list");
            for (int albIndex = 0; albIndex < albumListJSON.length(); albIndex++) {
                JSONObject album = albumListJSON.getJSONObject(albIndex);
                String title = album.getString("title");
                String img = album.getString("img");
                String href = album.getString("href");
                JSONArray singersJSON = album.getJSONArray("singers");
                ArrayList<PersonItem> arrSinger = new ArrayList<PersonItem>();
                for (int singerIndex = 0; singerIndex < singersJSON.length(); singerIndex++) {
                    JSONObject singer = singersJSON.getJSONObject(singerIndex);
                    String singerName = singer.getString("singerName");
                    String singerHref = singer.getString("singerHref");
                    PersonItem singerItem = new PersonItem(singerName, singerHref, 100);
                    arrSinger.add(singerItem);
                }

                AlbumItem albumItem = new AlbumItem(title, href, img, 200, arrSinger);

                arrAlbums.add(albumItem);

            }

            albumsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}