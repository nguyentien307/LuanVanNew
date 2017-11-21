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

        GetPage getSongStylePage = new GetPage(getContext());
        getSongStylePage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements styles = data.select("div.widget-content ul li");
                for(Element style:styles){
                    String title = style.select("h2 a").text();
                    String href = style.select("a").attr("href");
                    String img = style.select("a img").attr("src");
                    CategoryItem item = new CategoryItem(title, href, R.drawable.category1);
                    arrCategories.add(item);
                }

                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getSongStylePage.execute(Constants.SONG_CATEGORIES_PAGE);
    }

    private void prepareSongCategories() {
        GetPage getSongCategoryPage = new GetPage(getContext());
        getSongCategoryPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements categories = data.select("div.tab-menu ul li");
                for(int i = 0; i < 6; i++){
                    Element category = categories.get(i);
                    String title = category.select("a").text();
                    String href = category.select("a").attr("href");
                    CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                    arrCategories.add(item);
                }
                Elements expandCate = data.select("div.tab-menu div.dropdown ul li");
                for(Element category:expandCate){
                    String title = category.select("a").text();
                    String href = category.select("a").attr("href");
                    CategoryItem item = new CategoryItem(title, href, R.drawable.category2);
                    arrCategories.add(item);
                }

                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getSongCategoryPage.execute(Constants.SONG_CATEGORIES_PAGE);
    }

    private void prepareAlbumCategories() {
        GetPage getAlbumCategoryPage = new GetPage(getContext());
        getAlbumCategoryPage.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Element cateVietnam = data.select("div.fn-scrollbar ul.data-list li").first();
                Elements categories = cateVietnam.select("ul li");
                for(Element category:categories){
                    String title = category.select("a").text();
                    String href = category.select("a").attr("href");
                    CategoryItem item = new CategoryItem(title, href, R.drawable.category1);
                    arrCategories.add(item);
                }
                categoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
        getAlbumCategoryPage.execute(Constants.ALBUM_CATEGORIES_PAGE);
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