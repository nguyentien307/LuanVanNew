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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.SongsAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/14/2017.
 */

public class AlbumSongsFm extends Fragment implements View.OnClickListener {
    //heder title
    private TextView tvHeaderTitle;
    //button back
    private LinearLayout llBackButton;
    private ImageView ivBackButton;
    //title
    private ImageView ivBgAvatar, ivAdd, ivPlay;
    private TextView tvAlbumName, tvAlbumSinger;
    // title singer va title composer
    private TextView tvTitleName;
    //Recycle view
    private RecyclerView rcSongs;
    private RecyclerView.LayoutManager songsLayoutManager;
    private SongsAdapter songsAdapter;
    private ArrayList<SongItem> arrSongs = new ArrayList<>();
    //InformationsAdapter singersAdapter, composersAdapter;

    private AlbumItem albumItem ;

    public static AlbumSongsFm newInstance(AlbumItem albumItem) {
        AlbumSongsFm contentFragment = new AlbumSongsFm();
        Bundle bundle = new Bundle();
        bundle.putSerializable("albumItem", albumItem);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            albumItem = (AlbumItem) getArguments().getSerializable("albumItem");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_album_songs, viewGroup, false);
        tvHeaderTitle = (TextView) view.findViewById(R.id.tv_header_title);
        tvHeaderTitle.setText("Album Songs");
        llBackButton = (LinearLayout) view.findViewById(R.id.ll_btn_back);
        ivBackButton = (ImageView) view.findViewById(R.id.iv_btn_back);
        llBackButton.setOnClickListener(this);
        ivBackButton.setOnClickListener(this);

        ivBgAvatar = (ImageView) view.findViewById(R.id.iv_bg_avatar);
        Glide.with(getContext()).load(albumItem.getLinkImg())
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
        tvAlbumName = (TextView) view.findViewById(R.id.tv_album_name);
        tvAlbumName.setText(albumItem.getName());
        tvAlbumSinger = (TextView) view.findViewById(R.id.tv_album_singer);
        ArrayList<PersonItem> singers =  albumItem.getSingers();
        String singerName = "";
        for (int singerIndex = 0; singerIndex < singers.size(); singerIndex++) {
            if (singerIndex == singers.size() - 1) {
                singerName += singers.get(singerIndex).getName();
            } else {
                singerName += singers.get(singerIndex).getName() + ", ";
            }
        }
        tvAlbumSinger.setText(singerName);

        tvTitleName = (TextView) view.findViewById(R.id.tv_title_name);
        tvTitleName.setText("List of Songs");



        //recycler view
        rcSongs = (RecyclerView) view.findViewById(R.id.rc_songs);
        rcSongs.setHasFixedSize(true);
        rcSongs.setNestedScrollingEnabled(false);
        songsLayoutManager = new LinearLayoutManager(getContext());
        rcSongs.setLayoutManager(songsLayoutManager);
        songsAdapter = new SongsAdapter(getContext(), getActivity(), arrSongs, Constants.ALBUM_CATEGORIES);
        rcSongs.setAdapter(songsAdapter);
        prepareSongs();

        return view;
    }

    private void prepareSongs() {
        JSONObject data = null;
        try {
            data = new JSONObject(Constants.SONG_DATA);

            JSONArray songList = data.getJSONArray("list");
            for(int songIndex = 0; songIndex < songList.length() ; songIndex++){
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_back:
            case R.id.iv_btn_back:{

                getActivity().getSupportFragmentManager().popBackStack();
                LinearLayout secondScreen  = (LinearLayout) getActivity().findViewById(R.id.fragment_container_second);
                secondScreen.setVisibility(View.GONE);

            }; break;

            case R.id.iv_add:{
                Toast.makeText(getContext(), "add", Toast.LENGTH_SHORT).show();
            }; break;
            case R.id.iv_play:{
                Toast.makeText(getContext(), "play", Toast.LENGTH_SHORT).show();
            }; break;

        }
    }
}
