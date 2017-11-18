package com.example.tiennguyen.luanvannew.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;
import com.example.tiennguyen.luanvannew.utils.Constants;

import java.util.ArrayList;

/**
 * Created by Quyen Hua on 11/9/2017.
 */

public class PlayerCollapseFm extends Fragment implements View.OnClickListener {

    private Constants CONSTANTS;
    Context ctx;
    private String songTitle;
    private String songArtist;
    private int songIndex;
    Intent playerService;

    public static LinearLayout rlPlayerCollapse;
    public static TextView tvTitleCol, tvArtistCol;
    public static ImageView imgTitleCol, btnPreviousCol, btnNextCol, btnPlayCol;
    public static PlayerCollapseFm newInstance(String title, String artist, int index) {
        PlayerCollapseFm contentFragment = new PlayerCollapseFm();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("artist", artist);
        bundle.putInt("index", index);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null) {
            songTitle = getArguments().getString("title");
            songArtist = getArguments().getString("artist");
            songIndex = getArguments().getInt("index");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_player_collapse, container, false);
        initViews(view);
        tvTitleCol.setText(songTitle);
        tvArtistCol.setText(songArtist);
        return view;
    }

    private void initViews(View view) {
        rlPlayerCollapse = (LinearLayout) view.findViewById(R.id.rlPlayerCollapse);
        tvTitleCol = (TextView) view.findViewById(R.id.tvTitleCol);
        tvArtistCol = (TextView) view.findViewById(R.id.tvArtistCol);
        imgTitleCol = (ImageView) view.findViewById(R.id.imgTitleCol);
        btnNextCol = (ImageView) view.findViewById(R.id.imgNextCol);
        btnPlayCol = (ImageView) view.findViewById(R.id.imgPlayCol);
        btnPreviousCol = (ImageView) view.findViewById(R.id.imgPreCol);

        rlPlayerCollapse.setOnClickListener(this);
        btnPlayCol.setOnClickListener(this);
        btnNextCol.setOnClickListener(this);
        btnPreviousCol.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlPlayerCollapse) {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("index", songIndex);
            bundle.putString("type", CONSTANTS.PLAYER_COLLAPSE);
            intent.putExtra("data", bundle);
            getActivity().startActivity(intent);
        }
    }
}
