package com.example.tiennguyen.luanvannew.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.adapters.InformationsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogManagement;
import com.example.tiennguyen.luanvannew.dialogs.MyAlertDialogFragment;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/14/2017.
 */

public class SongInfoFm extends Fragment implements View.OnClickListener {
    //heder title
    private TextView tvHeaderTitle;
    //button back
    private LinearLayout llBackButton;
    private ImageView ivBackButton;
    //title
    private ImageView ivBgAvatar, ivAdd, ivPlay;
    private TextView tvSongName;
    // title singer va title composer
    private View titleSingers, titleComposers;
    private TextView tvSingersTitle, tvComposersTilte;
    //Recycle view
    RecyclerView rcSingers, rcComposers;
    ArrayList<PersonItem> arrSingers = new ArrayList<>();
    ArrayList<PersonItem> arrComposers = new ArrayList<>();
    RecyclerView.LayoutManager singersLayoutManager, composersLayoutManager;
    //InformationsAdapter singersAdapter, composersAdapter;

    private SongItem songItem ;

    public static SongInfoFm newInstance(SongItem songItem) {
        SongInfoFm contentFragment = new SongInfoFm();
        Bundle bundle = new Bundle();
        bundle.putSerializable("songItem", songItem);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            songItem = (SongItem) getArguments().getSerializable("songItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_song_info, viewGroup, false);
        tvHeaderTitle = (TextView) view.findViewById(R.id.tv_header_title);
        tvHeaderTitle.setText(getResources().getString(R.string.song_info_title));
        llBackButton = (LinearLayout) view.findViewById(R.id.ll_btn_back);
        ivBackButton = (ImageView) view.findViewById(R.id.iv_btn_back);
        llBackButton.setOnClickListener(this);
        ivBackButton.setOnClickListener(this);

        ivBgAvatar = (ImageView) view.findViewById(R.id.iv_bg_avatar);
        Glide.with(getContext()).load(songItem.getLinkImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(ivBgAvatar);
        ivAdd = (ImageView) view.findViewById(R.id.iv_add);
        ivPlay = (ImageView) view.findViewById(R.id.iv_play);

        ivAdd.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        tvSongName = (TextView) view.findViewById(R.id.tv_song_name);
        tvSongName.setText(songItem.getTitle());

        titleSingers = view.findViewById(R.id.title_singers);
        tvSingersTitle = (TextView) titleSingers.findViewById(R.id.tv_title_name);
        tvSingersTitle.setText(getResources().getString(R.string.artists_title));
        titleComposers = view.findViewById(R.id.title_composers);
        tvComposersTilte = (TextView) titleComposers.findViewById(R.id.tv_title_name);
        tvComposersTilte.setText(getResources().getString(R.string.composers_title));

        //recycler view
        rcSingers = (RecyclerView) view.findViewById(R.id.rc_singers);
        rcSingers.setHasFixedSize(true);
        rcSingers.setNestedScrollingEnabled(false);
        singersLayoutManager = new LinearLayoutManager(getContext());
        rcSingers.setLayoutManager(singersLayoutManager);
//        singersAdapter = new InformationsAdapter(arrSingers, getContext());
//        rcSingers.setAdapter(singersAdapter);
        prepareSingers();


        rcComposers = (RecyclerView) view.findViewById(R.id.rc_composers);
        rcComposers.setHasFixedSize(true);
        rcComposers.setNestedScrollingEnabled(false);
        composersLayoutManager = new LinearLayoutManager(getContext());
        rcComposers.setLayoutManager(composersLayoutManager);
//        composersAdapter = new InformationsAdapter(arrComposers, getContext());
//        rcComposers.setAdapter(composersAdapter);
        prepareComposers();


        return view;
    }
    private void prepareSingers() {
        arrSingers = songItem.getArtist();
        //singersAdapter.notifyDataSetChanged();
        InformationsAdapter singersAdapter = new InformationsAdapter(arrSingers, getContext());
        rcSingers.setAdapter(singersAdapter);
    }

    private void prepareComposers() {
        arrComposers = songItem.getComposer();
        //composersAdapter.notifyDataSetChanged();
        InformationsAdapter composersAdapter = new InformationsAdapter(arrComposers, getContext());
        rcComposers.setAdapter(composersAdapter);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
            case R.id.iv_btn_back:{
//                LinearLayout secondScreen  = (LinearLayout) getActivity().findViewById(R.id.fragment_container_second);
//                secondScreen.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().popBackStack();
            }; break;

            case R.id.iv_add:{
                showDialog();
            }; break;
            case R.id.iv_play:{
                playSong();
            }; break;
        }
    }

    private void showDialog(){
        SessionManagement session = new SessionManagement(getContext(), new SessionManagement.HaveNotLoggedIn() {
            @Override
            public void haveNotLoggedIn() {
                AlertDialogManagement alertDialog = new AlertDialogManagement(new AlertDialogManagement.ConfirmLogout() {
                    @Override
                    public void confirmLogout() {

                    }
                });
                alertDialog.showAlertDialog(getContext(), getResources().getString(R.string.add_to_playlist), getResources().getString(R.string.request_login), false);
            }
        });
        if (session.isLoggedIn()) {
            ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();
            if(session.getPlaylist() != "") {
                String jsonPlaylists = session.getPlaylist();
                try {
                    JSONArray arr = new JSONArray(jsonPlaylists);
                    for(int i = 0 ; i < arr.length(); i++){
                        JSONObject jsonItem = arr.getJSONObject(i);
                        PlaylistItem item = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                        arrPlaylists.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            MyAlertDialogFragment dialog = MyAlertDialogFragment.newInstance(arrPlaylists, songItem);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            dialog.show(manager, "fragment_alert");
        }
        else {
            session.checkLogin();
        }
    }

    private void playSong() {
        LinearLayout llPlayerCol = (LinearLayout) getActivity().findViewById(R.id.llPlayerCollapse);
        llPlayerCol.setVisibility(View.VISIBLE);
        PlayerCollapseFm playerCollapseFm = PlayerCollapseFm.newInstance(songItem);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.llPlayerCollapse, playerCollapseFm).commit();

        ArrayList<SongItem> arrSongs = new ArrayList<>();
        arrSongs.add(songItem);
        Bundle bundle = new Bundle();
        bundle.putSerializable("songItem", songItem);
        bundle.putParcelableArrayList("arrSong", arrSongs);
        bundle.putParcelableArrayList("arrArtist", songItem.getArtist());
        bundle.putParcelableArrayList("arrComposer", songItem.getComposer());
        bundle.putString("type", Constants.SONG_CATEGORIES);
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putExtra("data", bundle);
        startActivityForResult(intent, com.example.tiennguyen.luanvannew.commons.Constants.REQUEST_CODE);
    }
}
