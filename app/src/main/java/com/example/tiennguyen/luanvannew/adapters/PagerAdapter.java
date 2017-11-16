package com.example.tiennguyen.luanvannew.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tiennguyen.luanvannew.fragments.ArtistAlbumsFm;
import com.example.tiennguyen.luanvannew.fragments.ArtistSongsFm;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Songs", "Albums" };

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new Fragment();
        switch (position) {
            case 0: fragment = ArtistSongsFm.newInstance(1); break;
            case 1: fragment = ArtistAlbumsFm.newInstance(1); break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
