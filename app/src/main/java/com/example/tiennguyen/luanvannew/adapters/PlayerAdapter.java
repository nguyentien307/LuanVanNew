package com.example.tiennguyen.luanvannew.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;
import com.example.tiennguyen.luanvannew.commons.StringUtils;

import java.util.ArrayList;

/**
 * Created by Quyen Hua on 11/9/2017.
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>{
    ArrayList<SongItem> arrSongs;
    private Context ctx;

    public PlayerAdapter(ArrayList<SongItem> arrSongs, Context ctx){
        this.arrSongs = arrSongs;
        this.ctx = ctx;
    }

    @Override
    public PlayerAdapter.PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.song_player_item, parent, false);
        PlayerAdapter.PlayerViewHolder svh = new PlayerAdapter.PlayerViewHolder(itemView);
        return svh;
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {
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

    public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvSongTitle;
        TextView tvIndex;

        CardView cvSong;
        TextView tvSongSingers;
        ImageView imgSong;
        ImageView btnAdd;
        ImageView btnPlay;

        PlayerViewHolder(View itemView) {
            super(itemView);
            cvSong = (CardView) itemView.findViewById(R.id.cvSongPlayer);
            tvSongTitle = (TextView) itemView.findViewById(R.id.tvSongTitlePlayer);
            tvSongSingers = (TextView) itemView.findViewById(R.id.tvSongSingerPlayer);
            imgSong = (ImageView) itemView.findViewById(R.id.imgSongPlayer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                default:
                    Intent playerService = new Intent(ctx, PlayerService.class);
                    playerService.putExtra("songIndex", getAdapterPosition());
                    ctx.startService(playerService);
                    break;
            }

        }
    }
}
