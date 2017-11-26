package com.example.tiennguyen.luanvannew.fragments;

import android.content.Context;
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
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.GetDataCodeFromZing;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SliderItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TIENNGUYEN on 11/12/2017.
 */

public class MusicHotFm extends Fragment implements View.OnClickListener {

    final Context myApp = getContext();
    // view pager
    private ArrayList<SliderItem> arrSlider = new ArrayList<>();
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

    //Loading
    private LinearLayout llMusicHot;
    private RelativeLayout rlMusicHotLoading;

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

        //Loading
        llMusicHot = (LinearLayout) view.findViewById(R.id.llMusicHot);
        rlMusicHotLoading = (RelativeLayout) view.findViewById(R.id.rlMusicHotLoading);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.ll_slider_dots);
        createArraySlider();

        //rcalbum
        rcAlbums = (RecyclerView) view.findViewById(R.id.rc_albums);
        rcAlbums.setHasFixedSize(true);
        albumsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcAlbums.setLayoutManager(albumsLayoutManager);
        albumsAdapter = new AlbumsAdapter(getContext(), arrAlbums, Constants.HORIZONTAL_ALBUMS_LIST);
        rcAlbums.setAdapter(albumsAdapter);


        //rcyclesongs

        rcSongs = (RecyclerView) view.findViewById(R.id.rc_songs);
        rcSongs.setHasFixedSize(true);
        rcSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES, rcSongs);
        rcSongs.setAdapter(songsAdapter);

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
//        arrSlider.add("http://thefashionshows.com/file/2618/600x338/16:9/windows-8-piano-1920x1080_1419478427.jpg");
//        arrSlider.add("http://www.abc.net.au/radionational/image/7982806-3x2-700x467.jpg");
//        arrSlider.add("http://www.real.com/resources/wp-content/uploads/2013/08/youtube-music-playlist-762x294-1431640159.jpg");

        setLoadingVisible(true);
        GetPage getHomePage = new GetPage(getContext());
        getHomePage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                setLoadingVisible(false);

                //create slider
                viewSliderList(data);

                //prepare albums
                viewAlbumList(data);

                //prepare Songs
                viewSongList(data);
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getHomePage.execute(Constants.HOME_PAGE);


    }

    private void viewAlbumList(Document data) {
        Elements albElements = data.select("div.list_album").first().select("ul li");
        for (Element albItem:albElements){
            String img = albItem.select(".avatar img").attr("src");
            Element info = albItem.select("div.info_album").first();
            String albHref = info.select("h3 a").attr("href");
            String title = info.select("h3 a").text();
            ArrayList<PersonItem> arrSingers = new ArrayList<>();
            Elements singers = info.select("h4 a");
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

    private void viewSongList(Document data) {
        Elements songs = data.select("div.list_chart_music ul li");
        for (Element song:songs){
            Element info = song.select("div.info_data").first();
            final String songHref = info.select("h3 a").attr("href");
            final String title = info.select("h3 a").text();
            final ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
            Elements singers = info.select("h4 a");
            for(Element singer:singers){
                String singerHref = singer.attr("href");
                String singerName = singer.text();
                PersonItem singerItem = new PersonItem(singerName, singerHref, 192);
                arrSingers.add(singerItem);
            }
            final ArrayList<PersonItem> arrComposers = new ArrayList<>();
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
    }

    private void viewSliderList(Document data) {
        Elements aElements = data.select("#marquee_imgid_musicHubMarquee li");
        for (int i = 0; i < aElements.size(); i++) {
            Element element = aElements.get(i).select("div a").first();
            String href = element.attr("href");
            String title = element.attr("title");
            String img = element.select("img").attr("src");
            SliderItem item = new SliderItem(img, title, href);
            arrSlider.add(item);
        }
        createSlider();
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
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_left, R.anim.slide_in_left)
                .replace(idLayout, fragment)
                .commit();
    }

    public void setLoadingVisible(boolean b) {
        if (b) {
            rlMusicHotLoading.setVisibility(View.VISIBLE);
            llMusicHot.setVisibility(View.GONE);
        } else {
            rlMusicHotLoading.setVisibility(View.GONE);
            llMusicHot.setVisibility(View.VISIBLE);
        }
    }
}
