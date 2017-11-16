package com.example.tiennguyen.luanvannew.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.SongAdapter;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.utils.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.BaseURI;
import com.example.tiennguyen.luanvannew.services.GetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultFm extends Fragment implements View.OnFocusChangeListener {
    private EditText edSearchView;
    private ProgressBar pbSearchLoading;
    private LinearLayout llSearchResult;
    private TextView tvSongNum;
    private RecyclerView rcSearchResult;
    private LinearLayoutManager llm;

    private ArrayList<SongItem> songArr;
    private SongsAdapter songsAdapter;
    private String data = "";
    private int startSearchIndex = 0;
    private boolean isLoading = false;

    private Constants CONSTANTS;

    public static SearchResultFm newInstance(String name) {
        SearchResultFm contentFragment = new SearchResultFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            data = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_search_result, viewGroup, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initialView(view);
        showResult();
    }

    private void initialView(View view) {
        pbSearchLoading = (ProgressBar) view.findViewById(R.id.pbSearchLoading);
        llSearchResult = (LinearLayout) view.findViewById(R.id.llSearchResult);
        tvSongNum = (TextView) view.findViewById(R.id.tvSongNum);
        rcSearchResult = (RecyclerView) view.findViewById(R.id.rcSearchList);
        rcSearchResult.setHasFixedSize(true);
        llm = new LinearLayoutManager(getContext());
        rcSearchResult.setLayoutManager(llm);

        edSearchView = (EditText) view.findViewById(R.id.edSearch);

        edSearchView.setOnFocusChangeListener(this);
//        flSearchProperty = (FrameLayout) findViewById(R.id.fl_search_property);

    }

    private void showResult() {
        setLoading(true);
        Bundle bundle = getArguments();
        edSearchView.setText(data, TextView.BufferType.SPANNABLE);
        StringUtils convertedToUnsigned = new StringUtils();
        String name = convertedToUnsigned.convertedToUnsigned(data);
        BaseURI baseURI = new BaseURI();
        GetData getData = new GetData(getContext());
        getData.execute(baseURI.getSearchedSong(name, "song", startSearchIndex + 1, startSearchIndex + 10));
        startSearchIndex += 10;
        getData.setDataDownloadListener(new GetData.DataDownloadListener() {

            @Override
            public void dataDownloadedSuccessfully(JSONObject data) {
                setLoading(false);
                displayList(data, false);
            }

            @Override
            public void dataDownloadFailed() {
            }
        });
    }

//    public void customLoadMoreDataFromApi(int offset) {
//        ArrayList<SongItem> moreItems = getMoreData();
//        songArr.addAll(moreItems);
//
//        int curSize = songAdapter.getItemCount();
//        songAdapter.notifyItemRangeInserted(curSize, songArr.size() - 1);
//    }
//
//    public ArrayList<SongItem> getMoreData() {
//        ArrayList<SongItem> footerSongArray = new ArrayList<>();
//        BaseURI baseURI = new BaseURI();
//        GetData getData = new GetData(getContext());
//        getData.execute(baseURI.getSearchedSong(data, "song", startSearchIndex, startSearchIndex + 10));
//        startSearchIndex += 10;
//        getData.setDataDownloadListener(new GetData.DataDownloadListener() {
//
//            @Override
//            public void dataDownloadedSuccessfully(JSONObject data) {
//
//            }
//
//            @Override
//            public void dataDownloadFailed() {
//            }
//        });
//        return footerSongArray;
//    }

    private ArrayList<SongItem> displayList(JSONObject data, boolean isMore) {
        songArr = new ArrayList<>();
        try {
            String numFound = data.getString("total");
            JSONArray songList = data.getJSONArray("list");
            for(int i = 0; i < songList.length(); i++) {
                JSONObject jsObject = songList.getJSONObject(i);
                ArrayList<PersonItem> artists = getSingerList(jsObject);
                SongItem item = new SongItem(jsObject.getString("title"),123, jsObject.getString("href"), artists, null, "", "");
                songArr.add(item);
            }
            if (isMore == false) {
                setAdapter(songArr, numFound);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return songArr;
    }

    private ArrayList<PersonItem> getSingerList(JSONObject jsObject) {
        ArrayList<PersonItem> artists = new ArrayList<>();
        try {
            JSONArray jsArr = jsObject.getJSONArray("singers");
            for (int i = 0; i < jsArr.length(); i++) {
                JSONObject object = jsArr.getJSONObject(i);
                PersonItem item = new PersonItem(object.getString("singerName"), object.getString("singerHref"), 142);
                artists.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  artists;
    }

    private void setAdapter(ArrayList<SongItem> searchingArray, String numFound) {
        tvSongNum.setText("Have " + numFound + " results is founded for '" + data + "'");
        songsAdapter = new SongsAdapter(getContext(), getActivity(), songArr, CONSTANTS.SONGS_LIST_WITHOUT_IMAGE);
        rcSearchResult.setAdapter(songsAdapter);

        songsAdapter.notifyItemRangeInserted(songsAdapter.getItemCount(), searchingArray.size() - 1);
    }

    private void setLoading(boolean b) {
        if (b == true) {
            llSearchResult.setVisibility(View.INVISIBLE);
            pbSearchLoading.setVisibility(View.VISIBLE);
        } else {
            llSearchResult.setVisibility(View.VISIBLE);
            pbSearchLoading.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        SearchFm fragment = new SearchFm();
        fragment = fragment.newInstance(String.valueOf(edSearchView.getText()));
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
