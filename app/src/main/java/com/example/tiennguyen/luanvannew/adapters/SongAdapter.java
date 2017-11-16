package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.fragments.PlayerCollapseFm;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.commons.StringUtils;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/8/2017.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    ArrayList<SongItem> arrSongs;
    private Context ctx;
    private Activity activity;
    Boolean isInAlbum;

    public SongAdapter(ArrayList<SongItem> arrSongs, Context ctx, Boolean isInAlbum, Activity activity){
        this.arrSongs = arrSongs;
        this.ctx = ctx;
        this.isInAlbum = isInAlbum;
        this.activity = activity;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.song_card, parent, false);
        SongViewHolder svh = new SongViewHolder(itemView);
        return svh;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), "mystical.ttf");
        holder.tvSongTitle.setTypeface(typeface);
        holder.tvSongTitle.setText(arrSongs.get(position).getTitle());

        holder.tvSongSingers.setText(StringUtils.getArtists(arrSongs.get(position).getArtist()));
        if (arrSongs.get(position).getLinkImg() != "") {
            Glide.with(ctx)
                    .load(arrSongs.get(position).getLinkImg())
                    .centerCrop()
                    .placeholder(R.drawable.item_down)
                    .error(R.drawable.item_up)
                    .into(holder.imgSong);
        }
    }

    @Override
    public int getItemCount() {
        return arrSongs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvSongTitle;

        CardView cvSong;
        TextView tvSongSingers;
        ImageView imgSong;
        ImageView btnAdd;
        ImageView btnPlay;

        SongViewHolder(View itemView) {
            super(itemView);
            cvSong = (CardView) itemView.findViewById(R.id.cvSong);
            tvSongTitle = (TextView) itemView.findViewById(R.id.tvSongTitle);
            tvSongSingers = (TextView) itemView.findViewById(R.id.tvSongSinger);
            imgSong = (ImageView) itemView.findViewById(R.id.imgSong);
            btnAdd = (ImageView) itemView.findViewById(R.id.btnAdd);
            btnPlay = (ImageView) itemView.findViewById(R.id.btnPlay);

            btnPlay.setOnClickListener(this);
            btnAdd.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAdd: {
                    Toast.makeText(ctx,"Add", Toast.LENGTH_SHORT).show();
                }; break;

                case R.id.btnPlay: {
                    Toast.makeText(ctx, "play", Toast.LENGTH_SHORT).show();
                }; break;

                default:

                    LinearLayout llPlayerCol = (LinearLayout) activity.findViewById(R.id.llPlayerCollapse);
                    llPlayerCol.setVisibility(View.VISIBLE);
                    PlayerCollapseFm playerCollapseFm = new PlayerCollapseFm();
                    String title = arrSongs.get(getAdapterPosition()).getTitle();
                    String artist = StringUtils.getArtists(arrSongs.get(getAdapterPosition()).getArtist());
                    playerCollapseFm = playerCollapseFm.newInstance(title, artist);
                    FragmentTransaction ft = ((FragmentActivity)ctx).getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.llPlayerCollapse, playerCollapseFm).commit();

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("arrSong", arrSongs);
                    bundle.putInt("index", getAdapterPosition());
                    Intent intent = new Intent(ctx, PlayerActivity.class);
                    intent.putExtra("data", bundle);
                    ctx.startActivity(intent);
                    break;
            }
        }

        private ArrayList<ArrayList<PersonItem>> getArtistList() {
            ArrayList<ArrayList<PersonItem>> arrArtist = new ArrayList<>();
            for (int i = 0; i < arrSongs.size(); i++) {
                arrArtist.add(i, arrSongs.get(i).getArtist());
            }
            return arrArtist;
        }
    }
}