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
import com.example.tiennguyen.luanvannew.adapters.InformationsAdapter;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;

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
        tvHeaderTitle.setText("Song Information");
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
        tvSingersTitle.setText("Artists");
        titleComposers = view.findViewById(R.id.title_composers);
        tvComposersTilte = (TextView) titleComposers.findViewById(R.id.tv_title_name);
        tvComposersTilte.setText("Composers");

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
                Toast.makeText(getContext(), "add", Toast.LENGTH_SHORT).show();
            }; break;
            case R.id.iv_play:{
                Toast.makeText(getContext(), "play", Toast.LENGTH_SHORT).show();
            }; break;

        }
    }
}
