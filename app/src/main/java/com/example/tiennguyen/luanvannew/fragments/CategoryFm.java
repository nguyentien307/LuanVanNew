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
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.services.CheckInternet;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        if (res == "albums") {
            categoriesAdapter = new CategoriesAdapter(getContext(), getActivity(), arrCategories, Constants.ALBUM_CATEGORIES);
            rcCategories.setAdapter(categoriesAdapter);
            prepareAlbumCategories();
        }
        if (res == "songs") {
            categoriesAdapter = new CategoriesAdapter(getContext(), getActivity(), arrCategories, Constants.SONG_CATEGORIES);
            rcCategories.setAdapter(categoriesAdapter);
            prepareSongCategories();
        }
        if (res == "styles") {
            categoriesAdapter = new CategoriesAdapter(getContext(), getActivity(), arrCategories, Constants.SONG_STYLES);
            rcCategories.setAdapter(categoriesAdapter);
            prepareSongStyles();
        }

        return view;
    }

    private void prepareSongStyles() {

        GetPage getSongPage = new GetPage(getContext());
        getSongPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements categories = data.select("ul.detail_menu_browsing_dashboard li");
                for(int i = 15; i < 22; i++){
                    Element category = categories.get(i);
                    String title = category.select("a").text();
                    String href = category.select("a").attr("href");
                    CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                    arrCategories.add(item);

                }

                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {
                CheckInternet.goNoInternet(getContext(), R.id.rlCategoryContent);
            }
        });
        getSongPage.execute(Constants.SONG_PAGE);
    }

    private void prepareSongCategories() {
        GetPage getSongPage = new GetPage(getContext());
        getSongPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements categories = data.select("ul.detail_menu_browsing_dashboard li");
                for(int i = 0; i < 11; i++){
                    Element category = categories.get(i);
                    if(i == 0){
                        String href = category.select("h3 a").attr("href");
                        String title = category.select("h3 a").text();
                        CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                        arrCategories.add(item);
                    }else if(i > 2 && i < 10) {
                        String title = category.select("a").text();
                        String href = category.select("a").attr("href");
                        CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                        arrCategories.add(item);
                    }
                    else {
                        Elements expandCate = category.select("ul li");
                        for(Element cate:expandCate){
                            String title = cate.select("a").text();
                            String href = cate.select("a").attr("href");
                            CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                            arrCategories.add(item);
                        }
                    }
                }

                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {
                CheckInternet.goNoInternet(getContext(), R.id.rlCategoryContent);
            }
        });
        getSongPage.execute(Constants.SONG_PAGE);
    }

    private void prepareAlbumCategories() {
        GetPage getSongPage = new GetPage(getContext());
        getSongPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements categories = data.select("ul.detail_menu_browsing_dashboard li");
                for(int i = 0; i < 11; i++){
                    Element category = categories.get(i);
                    if(i == 0){
                        String href = category.select("h3 a").attr("href");
                        String title = category.select("h3 a").text();
                        CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                        arrCategories.add(item);
                    }else if(i > 2 && i < 10) {
                        String title = category.select("a").text();
                        String href = category.select("a").attr("href");
                        CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                        arrCategories.add(item);
                    }
                    else {
                        Elements expandCate = category.select("ul li");
                        for(Element cate:expandCate){
                            String title = cate.select("a").text();
                            String href = cate.select("a").attr("href");
                            CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                            arrCategories.add(item);
                        }
                    }
                }

                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {
                CheckInternet.goNoInternet(getContext(), R.id.rlCategoryContent);
            }
        });
        getSongPage.execute(Constants.ALBUM_PAGE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
            case R.id.iv_btn_back:{
                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout fullScreen  = (LinearLayout) getActivity().findViewById(R.id.full_screen_content);
                fullScreen.setVisibility(View.GONE);
            }

        }
    }


}
