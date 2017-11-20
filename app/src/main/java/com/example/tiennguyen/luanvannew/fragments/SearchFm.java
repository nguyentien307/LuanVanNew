package com.example.tiennguyen.luanvannew.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.HistoryAdapter;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.commons.WriteData;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogClearHistory;
import com.example.tiennguyen.luanvannew.dialogs.SearchDialog;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static SearchFm newInstance(String name) {
        SearchFm contentFragment = new SearchFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            res = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_search, viewGroup, false);
        setInitial(view);
        showLists();
        if (res != "") {
            edSearch.setText(res, TextView.BufferType.EDITABLE);
        }
//        edSearch.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                InputMethodManager keyboard = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                keyboard.showSoftInput(edSearch, res.length());
//            }
//        },50);
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
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.SONG_CATEGORIES);
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
        ArrayList<String> newString = new ArrayList<>();
        for (int i = 0; i < arrayListHistory.size(); i++) {
            if (arrayListHistory.get(i).contains(s)) {
                newString.add(arrayListHistory.get(i));
            }
        }
        historyAdapter = new HistoryAdapter(newString, getContext(), getActivity());
        rcHistory.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        if(newString.size() == 0) {
            tvHisResult.setVisibility(View.VISIBLE);
            tvHisResult.setText(Constants.NO_KEY);
        } else {
            tvHisResult.setVisibility(View.GONE);
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
                        edSearch.setHint("Searching for " + searchTitle);
                    }

                    @Override
                    public String getCheckedTitle() {
                        return searchTitle;
                    }
                });
                searchDialog.displaySearchDialog();
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
                fragment = fragment.newInstance(name);
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
        for (int i = 0; i < arrHistory.length; i++) {
            if (arrHistory[i].equals(name)){
                isUnique = false;
                break;
            }
        }
        if (isUnique) {
            saveData(name + "\n", getActivity().MODE_APPEND);
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
                ArrayList<PersonItem> arrSinger = new ArrayList<PersonItem>();
                ArrayList<PersonItem> arrComposer = new ArrayList<PersonItem>();
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
}
