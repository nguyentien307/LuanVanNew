package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.adapters.ViewPagerAdapter;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TIENNGUYEN on 11/12/2017.
 */

public class MusicHotFm extends Fragment implements View.OnClickListener {

    // view pager
    private ArrayList<String> arrSlider = new ArrayList<>();
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    //dots in viewpager
    private LinearLayout sliderDotspanel;
    private int dotsCount;
    private ImageView[] dots;
    //auto vieew pager
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 5000; // time in milliseconds between successive task executions.
    //RecyclerView album
    private RecyclerView rcAlbums;
    private RecyclerView.LayoutManager albumsLayoutManager;
    private AlbumsAdapter albumsAdapter;
    private ArrayList<AlbumItem> arrAlbums = new ArrayList<>();
    //Recyclerview Songs
    private RecyclerView rcSongs;
    private RecyclerView.LayoutManager songsLayoutManager;
    private SongsAdapter songsAdapter;
    private ArrayList<SongItem> arrSongs = new ArrayList<>();

    //Titlte bar
    private TextView tvAlbumsTitle, tvSongsTitle, tvAlbumsViewAll, tvSongsViewAll;
    private RelativeLayout llAlbumsViewAll, llSongsViewAll;
    private ImageView ivAlbumsViewAll, ivSongsViewAll;
    //fragment
    private String res = "";

    public static MusicHotFm newInstance(String name) {
        MusicHotFm contentFragment = new MusicHotFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            res = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_music_hot, viewGroup, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.ll_slider_dots);
        createArraySlider();
        createSlider();

        //rcalbum
        rcAlbums = (RecyclerView) view.findViewById(R.id.rc_albums);
        rcAlbums.setHasFixedSize(true);
        albumsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcAlbums.setLayoutManager(albumsLayoutManager);
        albumsAdapter = new AlbumsAdapter(getContext(), arrAlbums, Constants.HORIZONTAL_ALBUMS_LIST);
        rcAlbums.setAdapter(albumsAdapter);
        prepareAlbums();



        //rcyclesongs

        rcSongs = (RecyclerView) view.findViewById(R.id.rc_songs);
        rcSongs.setHasFixedSize(true);
        rcSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONGS_LIST_WITH_IMAGE);
        rcSongs.setAdapter(songsAdapter);
        prepareSongs();

        // title bar
        tvAlbumsTitle = (TextView) view.findViewById(R.id.tv_albums_title_name);
        tvAlbumsTitle.setText(R.string.hot_albums_title_bar);
        llAlbumsViewAll = (RelativeLayout) view.findViewById(R.id.ll_albums_view_all);
        tvAlbumsViewAll = (TextView) view.findViewById(R.id.tv_albums_view_all);
        ivAlbumsViewAll = (ImageView) view.findViewById(R.id.iv_albums_view_all);
        llAlbumsViewAll.setOnClickListener(this);
        tvAlbumsViewAll.setOnClickListener(this);
        ivAlbumsViewAll.setOnClickListener(this);

        tvSongsTitle = (TextView) view.findViewById(R.id.tv_songs_title_name);
        tvSongsTitle.setText(R.string.hot_songs_title_bar);
        llSongsViewAll = (RelativeLayout) view.findViewById(R.id.ll_songs_view_all);
        tvSongsViewAll = (TextView) view.findViewById(R.id.tv_songs_view_all);
        ivSongsViewAll = (ImageView) view.findViewById(R.id.iv_songs_view_all);
        llSongsViewAll.setOnClickListener(this);
        tvSongsViewAll.setOnClickListener(this);
        ivSongsViewAll.setOnClickListener(this);



        return view;
    }



    private void createSlider(){
        viewPagerAdapter = new ViewPagerAdapter(getContext(), arrSlider);
        viewPager.setAdapter(viewPagerAdapter);

        // Auto slider
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == (viewPagerAdapter.getCount())) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        createSliderDots();

    }

    private void createSliderDots(){
        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++){
            dots[i] = new ImageView(getContext());
            //dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
            dots[i].setImageResource(R.drawable.nonactive_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);
        }

        //dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
        dots[0].setImageResource(R.drawable.active_dot);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < dotsCount; i++){
                    //dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
                    dots[i].setImageResource(R.drawable.nonactive_dot);
                }
                //dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
                dots[position].setImageResource(R.drawable.active_dot);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void createArraySlider() {
        arrSlider.clear();
        arrSlider.add("http://thefashionshows.com/file/2618/600x338/16:9/windows-8-piano-1920x1080_1419478427.jpg");
        arrSlider.add("http://www.abc.net.au/radionational/image/7982806-3x2-700x467.jpg");
        arrSlider.add("http://www.real.com/resources/wp-content/uploads/2013/08/youtube-music-playlist-762x294-1431640159.jpg");

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
                PersonItem composer = new PersonItem("Trịnh Công Sơn", "", 200);
                PersonItem composer1 = new PersonItem("Vũ Cát Tường", "", 200);
                arrComposer.add(composer);
                arrComposer.add(composer1);
                SongItem songItem = new SongItem(title,200, href, arrSinger, arrComposer, "", img);
                arrSongs.add(songItem);

            }

            songsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_albums_view_all:
            case R.id.tv_albums_view_all:
            case R.id.iv_albums_view_all:
            {
                MusicFm musicFm = MusicFm.newInstance(Constants.TAB_ALBUMS);
                transaction(R.id.fragment_container, musicFm);

            }
            break;

            case R.id.ll_songs_view_all:
            case R.id.tv_songs_view_all:
            case R.id.iv_songs_view_all:
            {
                MusicFm musicFm = MusicFm.newInstance(Constants.TAB_SONGS);

                transaction(R.id.fragment_container, musicFm);
            }
            break;
        }
    }

    public void transaction(int idLayout, Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(idLayout, fragment)
                .commit();
    }
}