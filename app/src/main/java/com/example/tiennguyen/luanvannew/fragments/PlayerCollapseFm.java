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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;

/**
 * Created by Quyen Hua on 11/9/2017.
 */

public class PlayerCollapseFm extends Fragment implements View.OnClickListener {

    private Constants CONSTANTS;
    Context ctx;
    private SongItem songItem;
    Intent playerService;

    public static LinearLayout rlPlayerCollapse;
    public static TextView tvTitleCol, tvArtistCol;
    public static LinearLayout llPreCol, llPlayCol, llNextCol;
    public static ImageView imgTitleCol, btnPreviousCol, btnNextCol, btnPlayCol;

    public static PlayerCollapseFm newInstance(SongItem songItem) {
        PlayerCollapseFm contentFragment = new PlayerCollapseFm();
        Bundle bundle = new Bundle();
        bundle.putSerializable("songItem", songItem);
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
            songItem = (SongItem) getArguments().getSerializable("songItem");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_player_collapse, container, false);
        initViews(view);
        tvTitleCol.setText(songItem.getTitle());
        tvArtistCol.setText(StringUtils.getArtists(songItem.getArtist()));
        Glide.with(getContext()).load(songItem.getLinkImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(imgTitleCol);
        return view;
    }

    private void initViews(View view) {
        rlPlayerCollapse = (LinearLayout) view.findViewById(R.id.rlPlayerCollapse);
        tvTitleCol = (TextView) view.findViewById(R.id.tvTitleCol);
        tvArtistCol = (TextView) view.findViewById(R.id.tvArtistCol);
        imgTitleCol = (ImageView) view.findViewById(R.id.imgTitleCol);
        llNextCol = (LinearLayout) view.findViewById(R.id.llNextCol);
        llPlayCol = (LinearLayout) view.findViewById(R.id.llPlayCol);
        llPreCol = (LinearLayout) view.findViewById(R.id.llPreCol);
        btnNextCol = (ImageView) view.findViewById(R.id.imgNextCol);
        btnPlayCol = (ImageView) view.findViewById(R.id.imgPlayCol);
        btnPreviousCol = (ImageView) view.findViewById(R.id.imgPreCol);

        rlPlayerCollapse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlPlayerCollapse) {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("type", CONSTANTS.PLAYER_COLLAPSE);
            bundle.putInt("index", PlayerService.currentSongIndex);
            intent.putExtra("data", bundle);
            getActivity().startActivity(intent);
        }
    }
}
