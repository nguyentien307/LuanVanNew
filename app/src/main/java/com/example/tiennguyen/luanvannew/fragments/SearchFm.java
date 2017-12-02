package com.example.tiennguyen.luanvannew.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.AlbumsAdapter;
import com.example.tiennguyen.luanvannew.adapters.HistoryAdapter;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.commons.WriteData;
import com.example.tiennguyen.luanvannew.commons.ZingMP3LinkTemplate;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogClearHistory;
import com.example.tiennguyen.luanvannew.dialogs.SearchDialog;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.CheckInternet;
import com.example.tiennguyen.luanvannew.services.GetHtmlData;
import com.example.tiennguyen.luanvannew.services.GetPage;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class SearchFm extends Fragment implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {

    private String res = "";
    private Constants Constants = new Constants();
    private String[] arrHistory;
    private ArrayList<String> arrayListHistory;
    private EditText edSearch;
    private RecyclerView rcHistory;
    private LinearLayout llSearchCategories;
    private String searchTitle = "";
    private LinearLayout llClearHistory;
    private RecyclerView rcTopSongs;
    private RelativeLayout rlTopSong;
    private RecyclerView.LayoutManager songsLayoutManager;
    private SongsAdapter songsAdapter;
    private ArrayList<SongItem> arrSongs = new ArrayList<>();
    private HistoryAdapter historyAdapter;
    private TextView tvHisResult;
    private SessionManagement session;

    private ScrollView scrollView;
    // Searching
    private ScrollView svSearchingLayout;
    private ProgressBar pbSeachingLoading;
    private LinearLayout llSearching;
    private RecyclerView rcSearchingList;

    private AlbumsAdapter albumsAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static SearchFm newInstance(String name, String title) {
        SearchFm contentFragment = new SearchFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("title", title);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchTitle = getResources().getString(R.string.songs_title);
        if (getArguments()!= null) {
            res = getArguments().getString("name");
            searchTitle = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_search, viewGroup, false);
        setInitial(view);
        if (!CheckInternet.isConnected(getContext())) {
            rlTopSong.setVisibility(View.GONE);
        }
        showLists();
        if (res != "") {
            edSearch.setText(res, TextView.BufferType.NORMAL);
        }
        return view;
    }

    private void setInitial(View view) {
        edSearch = (EditText) view.findViewById(R.id.edSearch);
        llSearchCategories = (LinearLayout) view.findViewById(R.id.llSearchCategories);
        rcHistory = (RecyclerView) view.findViewById(R.id.rcHistory);
        rcHistory.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rcHistory.setLayoutManager(llm);
        llClearHistory = (LinearLayout) view.findViewById(R.id.llClearHistory);
        rcTopSongs = (RecyclerView) view.findViewById(R.id.rcTopSongView);
        rlTopSong = (RelativeLayout) view.findViewById(R.id.rlTopSongView);
        tvHisResult = (TextView) view.findViewById(R.id.tvHistoryResult);
        tvHisResult.setVisibility(View.GONE);
        session = new SessionManagement(getContext());

        scrollView = (ScrollView) view.findViewById(R.id.svHistory);

        //searching
        svSearchingLayout = (ScrollView) view.findViewById(R.id.svSearchingLayout);
        pbSeachingLoading = (ProgressBar) view.findViewById(R.id.pbSearchingLoading);
        llSearching = (LinearLayout) view.findViewById(R.id.llSearching);
        rcSearchingList = (RecyclerView) view.findViewById(R.id.rcSearchingList);
        rcSearchingList.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        rcSearchingList.setLayoutManager(llm2);
        svSearchingLayout.setVisibility(View.GONE);

        edSearch.addTextChangedListener(this);
        edSearch.setOnEditorActionListener(this);
        llSearchCategories.setOnClickListener(this);
        llClearHistory.setOnClickListener(this);
    }

    private void showLists() {
        showTopSongs();
        showHistory();
    }

    private void showTopSongs() {
        rcTopSongs.setHasFixedSize(true);
        rcTopSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcTopSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES, rcTopSongs);
        rcTopSongs.setAdapter(songsAdapter);
        prepareSongs();
    }

    private void showHistory() {
        StringUtils saveData = new StringUtils();
        String listHistory = saveData.readData(Constants.SUGGESTION_FILE, getContext());
        if (!listHistory.equals("")) {
            arrHistory = listHistory.split("\n");
        } else {
            arrHistory = new String[0];
        }
        arrayListHistory = new ArrayList<>();
        for (int i = 0; i < arrHistory.length; i++) {
            arrayListHistory.add(arrHistory[i]);
        }
        historyAdapter = new HistoryAdapter(arrayListHistory, getContext(), getActivity());
        rcHistory.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        rlTopSong.setVisibility(View.GONE);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (CheckInternet.isConnected(getContext())) {
            if (s.toString().equals("")) {
                scrollView.setVisibility(View.VISIBLE);
                svSearchingLayout.setVisibility(View.GONE);
            } else if (count % 2 == 0) {
                scrollView.setVisibility(View.GONE);
                svSearchingLayout.setVisibility(View.VISIBLE);
                showZingResult(String.valueOf(s));
            }
        } else {
            scrollView.setVisibility(View.VISIBLE);
            svSearchingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
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
                        edSearch.setHint(getResources().getString(R.string.searching_for) + " " + searchTitle);
                    }

                    @Override
                    public String getCheckedTitle() {
                        return searchTitle;
                    }
                });
                searchDialog.displaySearchDialog(getActivity());
                break;
            case R.id.llClearHistory:
                AlertDialogClearHistory alertDialogClearHistory = new AlertDialogClearHistory(new AlertDialogClearHistory.ConfirmClear() {
                    @Override
                    public void confirmClear(int position) {
                        saveData("", getActivity().MODE_PRIVATE);
                        showHistory();
                    }
                });
                alertDialogClearHistory.showConfirmClearDialog(getContext(), Constants.CLEAR_ALL, Constants.CLEAR_MESSAGE, -1);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            InputMethodManager imm = (InputMethodManager) edSearch.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
            String name = edSearch.getText().toString();
            if (!name.equals("")) {
                saveNewData(name);
                SearchResultFm fragment = new SearchResultFm();
                fragment = fragment.newInstance(name, searchTitle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        }
        return false;
    }

    private void saveNewData(String name) {
        boolean isUnique = true;
        if (arrHistory != null) {
            for (int i = 0; i < arrHistory.length; i++) {
                if (arrHistory[i].equals(name)) {
                    isUnique = false;
                    break;
                }
            }
        }
        if (isUnique) {
            if (session.getSaveHistory()) {
                saveData(name + "\n", getActivity().MODE_APPEND);
            }
        }
    }

    private void saveData(String data, final int mode) {
        WriteData writeData = new WriteData(new WriteData.GetFileOutputStream() {
            @Override
            public FileOutputStream getFileOutputStream() {
                FileOutputStream fos = null;
                try {
                    fos = getActivity().openFileOutput(Constants.SUGGESTION_FILE, mode);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return fos;
            }
        });
        writeData.saveData(data);
    }

    private void prepareSongs() {
        JSONObject data = null;
        try {
            data = new JSONObject(Constants.SONG_DATA);

            JSONArray songList = data.getJSONArray("list");
            for(int songIndex = 0; songIndex < 4; songIndex++){
                JSONObject song = songList.getJSONObject(songIndex);
                String title = song.getString("title");
                String img = song.getString("img");
                String href = song.getString("href");
                JSONArray singersJSON = song.getJSONArray("singers");
                ArrayList<PersonItem> arrSinger = new ArrayList<>();
                ArrayList<PersonItem> arrComposer = new ArrayList<>();
                for (int singerIndex = 0; singerIndex < singersJSON.length(); singerIndex++ ){
                    JSONObject singer = singersJSON.getJSONObject(singerIndex);
                    String singerName = singer.getString("singerName");
                    String singerHref = singer.getString("singerHref");
                    PersonItem singerItem = new PersonItem(singerName, singerHref, 200);
                    arrSinger.add(singerItem);
                }
                PersonItem composer = new PersonItem("Trịnh Công Sơn", "", 200);
                PersonItem composer1 = new PersonItem("Vũ Cát Tường", "", 200);
                arrComposer.add(composer);
                arrComposer.add(composer1);
                SongItem songItem = new SongItem(title,200, href, arrSinger, arrComposer, "", img);
                arrSongs.add(songItem);

            }

            songsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLoading(boolean b) {
        if (b == true) {
            llSearching.setVisibility(View.INVISIBLE);
            pbSeachingLoading.setVisibility(View.VISIBLE);
        } else {
            llSearching.setVisibility(View.VISIBLE);
            pbSeachingLoading.setVisibility(View.INVISIBLE);
        }
    }

    private void showZingResult(String data) {
        setLoading(true);
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
                    displayZingList(data);
                }
            }

            @Override
            public void dataDownloadFailed() {
                CheckInternet.goNoInternet(getContext(), R.id.svHistory);
            }
        });
    }

    private void displayAlbumList(Document document) {
        ArrayList<AlbumItem> arrList = new ArrayList<>();
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
        setAlbumAdapter(arrList);
    }

    private void setAlbumAdapter(ArrayList<AlbumItem> arrList) {
        albumsAdapter = new AlbumsAdapter(getContext(), arrList, Constants.VERTICAL_ALBUMS_LIST);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rcSearchingList.setLayoutManager(layoutManager);
        rcSearchingList.addItemDecoration(new AlbumsAdapter.GridSpacingItemDecoration(2, albumsAdapter.dpToPx(10), true));
        rcSearchingList.setItemAnimator(new DefaultItemAnimator());
        rcSearchingList.setNestedScrollingEnabled(false);
        rcSearchingList.setAdapter(albumsAdapter);
    }

    private void displayZingList(Document document) {
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
        SongsAdapter songsAdapter = new SongsAdapter(getContext(), getActivity(), arrList, Constants.SONG_CATEGORIES, rcSearchingList);
        rcSearchingList.setAdapter(songsAdapter);
//        songsAdapter.notifyItemRangeInserted(songsAdapter.getItemCount(), arrList.size() - 1);
    }
}
