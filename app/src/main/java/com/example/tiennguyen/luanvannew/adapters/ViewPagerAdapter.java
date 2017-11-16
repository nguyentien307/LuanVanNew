package com.example.tiennguyen.luanvannew.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/12/2017.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> arrSlider;

    public ViewPagerAdapter(Context context, ArrayList<String> arrSlider) {
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

        Glide.with(context).load(arrSlider.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.hot_slider1)
                .error(R.drawable.hot_slider1)
                .into(imageView);

        view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //this will log the page number that was click
                //Log.i("TAG", "This page was clicked: " + position);
                Toast.makeText(context, "click "+position, Toast.LENGTH_SHORT).show();
            }
        });


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
