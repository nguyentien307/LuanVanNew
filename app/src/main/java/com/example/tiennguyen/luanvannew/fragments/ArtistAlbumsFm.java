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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static ArtistAlbumsFm newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ArtistAlbumsFm fragment = new ArtistAlbumsFm();
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
