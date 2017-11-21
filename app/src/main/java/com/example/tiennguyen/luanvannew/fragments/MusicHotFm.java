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
//        arrSlider.add("http://thefashionshows.com/file/2618/600x338/16:9/windows-8-piano-1920x1080_1419478427.jpg");
//        arrSlider.add("http://www.abc.net.au/radionational/image/7982806-3x2-700x467.jpg");
//        arrSlider.add("http://www.real.com/resources/wp-content/uploads/2013/08/youtube-music-playlist-762x294-1431640159.jpg");

        GetPage getHomePage = new GetPage(getContext());
        getHomePage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {

                //create slider
                Elements aElements = data.select("div.slide-scroll a");
                for (int i = 0; i < aElements.size(); i++) {
                    Element element = aElements.get(i);
                    String href = element.attr("href");
                    String title = element.attr("title");
                    String img = "";
                    if(i==0){
                        img = element.select("img").attr("src");
                    }
                    else {
                        img = element.select("img").attr("data-lazy");
                    }
                    SliderItem item = new SliderItem(img, title, href);
                    arrSlider.add(item);
                }
                createSlider();


                //prepare albums
                Element albDiv = data.select("div.mt20-").get(1);
                Elements albElements = albDiv.select("div.fn-list div.album-item");
                for (Element albItem:albElements){
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
        getHomePage.execute(Constants.HOME_PAGE);


    }

    private void prepareSongs() {
        GetPage getChartSongsPage = new GetPage(getContext());
        getChartSongsPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements songElements = data.select("div.table-body ul li");
                for(int i = 0; i < 20; i++){
                    Element songElement = songElements.get(i);
                    String data_code = songElement.attr("data-code");
                    Element e_item = songElement.select("div.e-item").first();
                    String img = e_item.select("a").select("img").attr("src");

                    Element aTagTitle = e_item.select(".title-item a").first();
                    String title = aTagTitle.text();

                    ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    Elements singers = e_item.select(".title-sd-item");
                    for(Element singer:singers){

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
            public void dataDownloadFailed() {

            }
        });
        getChartSongsPage.execute(Constants.SONG_CHART_PAGE);
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
}
