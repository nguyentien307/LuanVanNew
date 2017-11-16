package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.CategoriesAdapter;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.utils.Constants;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class CategoryFm extends Fragment implements View.OnClickListener {

    private TextView tvHeaderTitle;
    private LinearLayout llBackButton;
    private ImageView ivBackButton;

    //recycler view
    private RecyclerView rcCategories;
    private RecyclerView.LayoutManager categoriesLayoutManager;
    private CategoriesAdapter categoriesAdapter;
    private ArrayList<CategoryItem> arrCategories = new ArrayList<>();

    private String res = "";

    public static CategoryFm newInstance(String name) {
        CategoryFm contentFragment = new CategoryFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            res = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_category, viewGroup, false);
        tvHeaderTitle = (TextView) view.findViewById(R.id.tv_header_title);
        tvHeaderTitle.setText(R.string.category_header_title);
        llBackButton = (LinearLayout) view.findViewById(R.id.ll_btn_back);
        ivBackButton = (ImageView) view.findViewById(R.id.iv_btn_back);
        llBackButton.setOnClickListener(this);
        ivBackButton.setOnClickListener(this);

        //recycler view
        rcCategories = (RecyclerView) view.findViewById(R.id.rc_categories);
        rcCategories.setHasFixedSize(true);
        rcCategories.setNestedScrollingEnabled(false);
        categoriesLayoutManager = new LinearLayoutManager(getContext());
        rcCategories.setLayoutManager(categoriesLayoutManager);

        if(res == "albums") {
            categoriesAdapter = new CategoriesAdapter(getContext(), getActivity(), arrCategories, Constants.ALBUM_CATEGORIES);
            rcCategories.setAdapter(categoriesAdapter);
            prepareAlbumCategories();
        }
        if(res == "songs"){
            categoriesAdapter = new CategoriesAdapter(getContext(), getActivity(), arrCategories, Constants.SONG_CATEGORIES);
            rcCategories.setAdapter(categoriesAdapter);
            prepareSongCategories();
        }
        if(res == "styles"){
            categoriesAdapter = new CategoriesAdapter(getContext(), getActivity(), arrCategories, Constants.SONG_STYLES);
            rcCategories.setAdapter(categoriesAdapter);
            prepareSongStyles();
        }


        return view;
    }

    private void prepareSongStyles() {
        CategoryItem item0 = new CategoryItem("Pop", "", R.drawable.style1);
        arrCategories.add(item0);
        CategoryItem item5 = new CategoryItem("Rock & Roll", "", R.drawable.category1);
        arrCategories.add(item5);
        CategoryItem item1 = new CategoryItem("Jazz", "", R.drawable.style1);
        arrCategories.add(item1);
        CategoryItem item2 = new CategoryItem("Blue", "", R.drawable.category1);
        arrCategories.add(item2);
        CategoryItem item3 = new CategoryItem("Valse", "", R.drawable.category2);
        arrCategories.add(item3);
        categoriesAdapter.notifyDataSetChanged();
    }

    private void prepareSongCategories() {
        CategoryItem item0 = new CategoryItem("Hot Songs", "", R.drawable.style1);
        arrCategories.add(item0);
        CategoryItem item5 = new CategoryItem("New Songs", "",  R.drawable.category1);
        arrCategories.add(item5);
        CategoryItem item1 = new CategoryItem("Nhạc Trẻ", "", R.drawable.category2);
        arrCategories.add(item1);
        CategoryItem item2 = new CategoryItem("Nhạc Cách Mạng", "", R.drawable.style1);
        arrCategories.add(item2);
        CategoryItem item3 = new CategoryItem("Nhạc Trữ Tình", "",  R.drawable.category1);
        arrCategories.add(item3);
        CategoryItem item4 = new CategoryItem("Nhạc Dân Ca", "", R.drawable.category2);
        arrCategories.add(item4);
        categoriesAdapter.notifyDataSetChanged();
    }

    private void prepareAlbumCategories() {
        CategoryItem item0 = new CategoryItem("Hot Albums", "", R.drawable.category2);
        arrCategories.add(item0);
        CategoryItem item5 = new CategoryItem("New Albums", "",  R.drawable.category1);
        arrCategories.add(item5);
        CategoryItem item1 = new CategoryItem("Albums Nhạc Trẻ", "",  R.drawable.style1);
        arrCategories.add(item1);
        CategoryItem item2 = new CategoryItem("Albums Nhạc Cách Mạng", "", R.drawable.category2);
        arrCategories.add(item2);
        CategoryItem item3 = new CategoryItem("Albums Nhạc Trữ Tình", "",  R.drawable.style1);
        arrCategories.add(item3);
        CategoryItem item4 = new CategoryItem("Albums Nhạc Dân Ca", "",  R.drawable.category1);
        arrCategories.add(item4);
        categoriesAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
            case R.id.iv_btn_back:{
                LinearLayout fullScreen  = (LinearLayout) getActivity().findViewById(R.id.full_screen_content);
                fullScreen.setVisibility(View.GONE);
            }

        }
    }
}