package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.commons.ZingMP3LinkTemplate;
import com.example.tiennguyen.luanvannew.dialogs.SearchDialog;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.BaseURI;
import com.example.tiennguyen.luanvannew.services.GetData;
import com.example.tiennguyen.luanvannew.services.GetHtmlData;
import com.example.tiennguyen.luanvannew.services.GetPage;
import com.example.tiennguyen.luanvannew.services.XMLDomParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class SearchResultFm extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {
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

    private LinearLayout llSearchCategories;
    private String searchTitle = "";

    private AlbumsAdapter albumsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static SearchResultFm newInstance(String name, String title) {
        SearchResultFm contentFragment = new SearchResultFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("title", title);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null) {
            data = getArguments().getString("name");
            searchTitle = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_search_result, viewGroup, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        initialView(view);
//        showResult();
        showZingResult();
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

        llSearchCategories = (LinearLayout) view.findViewById(R.id.llSearchCategories);

        edSearchView.setOnFocusChangeListener(this);
        llSearchCategories.setOnClickListener(this);
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
        tvSongNum.setText(getResources()
                .getString(R.string.title_search_result_1) + " " + numFound + " " + getResources().getString(R.string.title_search_result_2) + " '" + data + "'");
        songsAdapter = new SongsAdapter(getContext(), getActivity(), songArr, Constants.SONG_CATEGORIES, rcSearchResult);
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
        fragment = fragment.newInstance(String.valueOf(edSearchView.getText()), searchTitle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showZingResult() {
        setLoading(true);
        edSearchView.setText(data, TextView.BufferType.SPANNABLE);
        StringUtils convertedToUnsigned = new StringUtils();
        String name = convertedToUnsigned.convertedToUnsigned(data);
        GetPage gethtmlData = new GetPage(getContext());
        String title = searchTitle.equals("albums") ? "playlist" : "bai-hat";
        ZingMP3LinkTemplate zingMP3LinkTemplate = new ZingMP3LinkTemplate();
        String url = zingMP3LinkTemplate.getSearchUrl(name, title);
        gethtmlData.execute(url);
        gethtmlData.setDataDownloadListener(new GetPage.DataDownloadListener() {

            @Override
            public void dataDownloadedSuccessfully(Document data) {
                setLoading(false);
                if (searchTitle.equals("albums")) {
                    displayAlbumList(data);
                } else {
                    displayZingList(data, false);
                }
            }

            @Override
            public void dataDownloadFailed() {
            }
        });
    }

    private void displayAlbumList(Document document) {
        ArrayList<AlbumItem> arrList = new ArrayList<>();
        String totalResult = document.select("div.sta-result").select("span").text();
        Elements div = document.select("div.title-album");
        for (int i = 0; i < div.size(); i++) {
            String img = div.get(i).select("img").attr("src");
            String title = div.get(i).select("h2").select("a").text();
            Elements arrArtists = div.get(i).select("h3").select("a");
            ArrayList<PersonItem> arrArtist = new ArrayList<>();
            for (int j = 0; j < arrArtists.size(); j++) {
                arrArtist.add(new PersonItem(arrArtists.get(j).text(), "", 157523));
            }
            arrList.add(new AlbumItem(title, "", img, 101022, arrArtist));
        }
        setAlbumAdapter(arrList, totalResult);
    }

    private void setAlbumAdapter(ArrayList<AlbumItem> arrList, String total) {
        tvSongNum.setText(getResources()
                .getString(R.string.title_search_result_1) + " " + total + " " + getResources().getString(R.string.title_search_result_2) + " '" + data + "'");
        albumsAdapter = new AlbumsAdapter(getContext(), arrList, Constants.VERTICAL_ALBUMS_LIST);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rcSearchResult.setLayoutManager(layoutManager);
        rcSearchResult.addItemDecoration(new AlbumsAdapter.GridSpacingItemDecoration(2, albumsAdapter.dpToPx(10), true));
        rcSearchResult.setItemAnimator(new DefaultItemAnimator());
        rcSearchResult.setNestedScrollingEnabled(false);
        rcSearchResult.setAdapter(albumsAdapter);
    }

    private void displayZingList(Document document, boolean b) {
        ArrayList<SongItem> arrList = new ArrayList<>();
        String totalResult = document.select("div.sta-result").select("span").text();
        Elements div = document.select("div.item-song");
        for (int i = 0; i < div.size(); i++) {
            String data_code = div.get(i).attr("data-code");
            String img = div.get(i).select("img").attr("src");
            String titleAndArtists = div.get(i).select("h3").select("a").attr("title");
            String[] arrTitleArtists = titleAndArtists.split(" - ", 2);
            String[] arrArtists = arrTitleArtists[1].split(", ");
            ArrayList<PersonItem> arrArtist = new ArrayList<>();
            for (int j = 0; j < arrArtists.length; j++) {
                arrArtist.add(new PersonItem(arrArtists[j], "", 157523));
            }
            arrList.add(new SongItem(arrTitleArtists[0], 1437659, data_code, arrArtist, null, "", img));
        }
        setZingAdapter(totalResult, arrList);
    }

    private void setZingAdapter(String totalResult, ArrayList<SongItem> arrList) {
        tvSongNum.setText(getResources()
                .getString(R.string.title_search_result_1) + " " + totalResult + " " + getResources().getString(R.string.title_search_result_2) + " '" + data + "'");
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrList, Constants.SONG_CATEGORIES, rcSearchResult);
        rcSearchResult.setAdapter(songsAdapter);
//        songsAdapter.notifyItemRangeInserted(songsAdapter.getItemCount(), arrList.size() - 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSearchCategories:
                SearchDialog searchDialog = new SearchDialog(new SearchDialog.CustomLayoutInflater() {
                    @Override
                    public LayoutInflater getLayoutInflater() {
                        return getActivity().getLayoutInflater();
                    }

                    @Override
                    public AlertDialog.Builder getAlertDialog() {
                        AlertDialog.Builder searchTitleDialog = new AlertDialog.Builder(getActivity());
                        return searchTitleDialog;
                    }

                    @Override
                    public void onResult(String title) {
                        searchTitle = title;
                        edSearchView.setHint(getResources().getString(R.string.searching_for) + " " + searchTitle);
                    }

                    @Override
                    public String getCheckedTitle() {
                        return searchTitle;
                    }
                });
                searchDialog.displaySearchDialog(getActivity());
                break;
            default:
                break;
        }
    }
}
