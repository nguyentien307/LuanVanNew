package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.models.TabTilte;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class MusicFm extends Fragment implements View.OnClickListener {

    private LinearLayout tabSongs, tabHot, tabAlbums, categoriesButton, stylesButton;
    private TextView tvTabSongs, tvTabHot, tvTabAlbums, tvCategories, tvStyles;

    private ArrayList<TabTilte> arrTabTitle;
    private String res = "";
    private String tabName;

    public static MusicFm newInstance(String tabName) {
        MusicFm contentFragment = new MusicFm();
        Bundle bundle = new Bundle();
        bundle.putString("tabName", tabName);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            tabName = getArguments().getString("tabName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_music, viewGroup, false);

        categoriesButton = (LinearLayout) view.findViewById(R.id.ll_category_button);
        tvCategories = (TextView) view.findViewById(R.id.tvCategoriesButton);
        tvCategories.setOnClickListener(this);
        categoriesButton.setOnClickListener(this);
        stylesButton = (LinearLayout) view.findViewById(R.id.ll_styles_button);
        tvStyles = (TextView) view.findViewById(R.id.tvStylesButton);
        tvStyles.setOnClickListener(this);
        stylesButton.setOnClickListener(this);

        tabSongs = (LinearLayout) view.findViewById(R.id.tabSongs);
        tabHot = (LinearLayout) view.findViewById(R.id.tabHot);
        tabAlbums = (LinearLayout) view.findViewById(R.id.tabAlbums);
        tvTabSongs = (TextView) view.findViewById(R.id.tvTabSongs);
        tvTabHot = (TextView) view.findViewById(R.id.tvTabHot);
        tvTabAlbums = (TextView) view.findViewById(R.id.tvTabAlbums);

        tabSongs.setOnClickListener(this);
        tabHot.setOnClickListener(this);
        tabAlbums.setOnClickListener(this);
        createTabArray();
        return view;
    }

    public void createTabArray() {
        arrTabTitle = new ArrayList<>();
        if(tabName == Constants.TAB_HOT) {
            arrTabTitle.add(new TabTilte(tabSongs, tvTabSongs, false));
            arrTabTitle.add(new TabTilte(tabHot, tvTabHot, true));
            arrTabTitle.add(new TabTilte(tabAlbums, tvTabAlbums, false));
        }
        else if (tabName == Constants.TAB_SONGS ){
            arrTabTitle.add(new TabTilte(tabSongs, tvTabSongs, true));
            arrTabTitle.add(new TabTilte(tabHot, tvTabHot, false));
            arrTabTitle.add(new TabTilte(tabAlbums, tvTabAlbums, false));
        }
        else {
            arrTabTitle.add(new TabTilte(tabSongs, tvTabSongs, false));
            arrTabTitle.add(new TabTilte(tabHot, tvTabHot, false));
            arrTabTitle.add(new TabTilte(tabAlbums, tvTabAlbums, true));
        }
        setItemState();
    }

    public void checkedItem(int id){
        for( int i = 0; i < 3; i++){
            if(arrTabTitle.get(i).getView().getId() == id) {
                arrTabTitle.get(i).setChecked(true);
            }
            else arrTabTitle.get(i).setChecked(false);
        }
        setItemState();
    }

    public void setItemState(){
        Fragment fragment = new Fragment();
        if(arrTabTitle.get(0).isChecked()){
            tabSongs.setBackground(getResources().getDrawable(R.drawable.tab_title_left_pressed));
            tvTabSongs.setTextColor(getResources().getColor(R.color.black));
            categoriesButton.setVisibility(View.VISIBLE);
            stylesButton.setVisibility(View.VISIBLE);

            fragment = MusicSongsFm.newInstance(new CategoryItem("Hot Songs", "", R.drawable.style1));
        }
        else {
            tabSongs.setBackground(getResources().getDrawable(R.drawable.tab_title_left_normal));
            tvTabSongs.setTextColor(getResources().getColor(R.color.lightBlue));
        }

        if(arrTabTitle.get(1).isChecked()){
            tabHot.setBackground(getResources().getDrawable(R.drawable.tab_title_center_pressed));
            tvTabHot.setTextColor(getResources().getColor(R.color.black));
            categoriesButton.setVisibility(View.INVISIBLE);
            stylesButton.setVisibility(View.INVISIBLE);

            fragment = MusicHotFm.newInstance("new");

        }
        else {
            tabHot.setBackground(getResources().getDrawable(R.drawable.tab_title_center_normal));
            tvTabHot.setTextColor(getResources().getColor(R.color.lightBlue));
        }

        if(arrTabTitle.get(2).isChecked()){
            tabAlbums.setBackground(getResources().getDrawable(R.drawable.tab_title_right_pressed));
            tvTabAlbums.setTextColor(getResources().getColor(R.color.black));
            categoriesButton.setVisibility(View.VISIBLE);
            stylesButton.setVisibility(View.INVISIBLE);

            fragment = MusicAlbumsFm.newInstance(new CategoryItem("Hot Albums", "", R.drawable.style1));
        }
        else {
            tabAlbums.setBackground(getResources().getDrawable(R.drawable.tab_title_right_normal));
            tvTabAlbums.setTextColor(getResources().getColor(R.color.lightBlue));
        }

        transaction(R.id.music_content, fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tabSongs:{
                checkedItem(R.id.tabSongs);
            } break;

            case R.id.tabHot:{
                checkedItem(R.id.tabHot);
            } break;

            case R.id.tabAlbums:{
                checkedItem(R.id.tabAlbums);
            } break;
            case R.id.tvCategoriesButton:
            case R.id.ll_category_button:{
                LinearLayout fullScreen  = (LinearLayout) getActivity().findViewById(R.id.full_screen_content);
                fullScreen.setVisibility(View.VISIBLE);
                Fragment frag;
                if(arrTabTitle.get(0).isChecked()){
                    frag = CategoryFm.newInstance("songs");
                }
                else {
                    frag = CategoryFm.newInstance("albums");
                }
                transaction(R.id.full_screen_content, frag);
            } break;
            case R.id.tvStylesButton:
            case R.id.ll_styles_button:{
                LinearLayout fullScreen  = (LinearLayout) getActivity().findViewById(R.id.full_screen_content);
                fullScreen.setVisibility(View.VISIBLE);
                Fragment frag = CategoryFm.newInstance("styles");
                transaction(R.id.full_screen_content, frag);
            } break;
        }
    }

    public void transaction(int idLayout, Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(idLayout, fragment)
                .commit();
    }
}