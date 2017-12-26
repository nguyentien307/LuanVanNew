package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.commons.ZingMP3LinkTemplate;
import com.example.tiennguyen.luanvannew.fragments.PlayerCollapseFm;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SliderItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/12/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Activity activity;
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<SliderItem> arrSlider;

    public ViewPagerAdapter(Activity activity, Context context, ArrayList<SliderItem> arrSlider) {
        this.activity = activity;
        this.context = context;
        this.arrSlider = arrSlider;
    }

    @Override
    public int getCount() {
        return arrSlider.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_pager, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_slider);

        Glide.with(context).load(arrSlider.get(position).getImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.hot_slider1)
                .error(R.drawable.hot_slider1)
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final String songName = arrSlider.get(position).getTitle().split("-")[0];
                StringUtils convertedToUnsigned = new StringUtils();
                String name = convertedToUnsigned.convertedToUnsigned(songName);
                GetPage getPage = new GetPage(context);
                getPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
                    @Override
                    public void dataDownloadedSuccessfully(Document data) {
                        ArrayList<SongItem> arrList = new ArrayList<>();
                        Elements item = data.select("div.item-song");
                        String key = "ZGcGykHNhSmpAAZtHTDnZmTkWLFFdEhGz";
                        String img = "https://zmp3-photo.zadn.vn/thumb/94_94/covers/6/e/6e7b90d96728c9ce1b4c2a104d622784_1507799020.jpg";
                        if (item.size() > 0) {
                            key = item.get(0).attr("data-code");
                            img = item.get(0).select("img").attr("src");
                            String titleAndArtists = item.get(0).select("h3").select("a").attr("title");
                            String[] arrTitleArtists = titleAndArtists.split(" - ", 2);
                            String[] arrArtists = arrTitleArtists[1].split(", ");
                            ArrayList<PersonItem> arrArtist = new ArrayList<>();
                            for (int j = 0; j < arrArtists.length; j++) {
                                arrArtist.add(new PersonItem(arrArtists[j], "", 157523));
                            }
                            arrList.add(new SongItem(songName, 1437659, key, arrArtist, null, "", img));
                            playSong(arrList);
                        }
                    }

                    @Override
                    public void dataDownloadFailed() {

                    }
                });
                getPage.execute(ZingMP3LinkTemplate.SEARCH_URL + name);
            }
        });


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    private void playSong(ArrayList<SongItem> arrList) {
        LinearLayout llPlayerCol = (LinearLayout) activity.findViewById(R.id.llPlayerCollapse);
        llPlayerCol.setVisibility(View.VISIBLE);
        PlayerCollapseFm playerCollapseFm = PlayerCollapseFm.newInstance(arrList.get(0));
        FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        ft.add(R.id.llPlayerCollapse, playerCollapseFm).commit();

        Bundle bundle = new Bundle();
        bundle.putSerializable("songItem", arrList.get(0));
        bundle.putParcelableArrayList("arrArtist", arrList.get(0).getArtist());
        bundle.putParcelableArrayList("arrComposer", arrList.get(0).getComposer());
        bundle.putString("type", Constants.SONG_CATEGORIES);
        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.putExtra("data", bundle);
        activity.startActivityForResult(intent, com.example.tiennguyen.luanvannew.commons.Constants.REQUEST_CODE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
