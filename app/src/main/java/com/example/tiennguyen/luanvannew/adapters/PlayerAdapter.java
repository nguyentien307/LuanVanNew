package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiennguyen.luanvannew.MainActivity;
import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogManagement;
import com.example.tiennguyen.luanvannew.dialogs.MyAlertDialogFragment;
import com.example.tiennguyen.luanvannew.fragments.SongInfoFm;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import java.util.ArrayList;

/**
 * Created by Quyen Hua on 11/9/2017.
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>{
    ArrayList<SongItem> arrSongs;
    Context context;
    Activity activity;

    public PlayerAdapter(ArrayList<SongItem> arrSongs, Context context, Activity activity){
        this.arrSongs = arrSongs;
        this.context = context;
        this.activity = activity;
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
        holder.tvSongName.setText(arrSongs.get(position).getTitle());
        holder.tvArtistName.setText(StringUtils.getArtists(arrSongs.get(position).getArtist()));
        holder.tvIndex.setText(position + 1 +"");
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
        public TextView tvSongName, tvArtistName, tvIndex;
        public LinearLayout llAdd, llAbout;
        public LinearLayout llIndex;

        PlayerViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvArtistName = (TextView) itemView.findViewById(R.id.tv_artist_name);
            llAbout = (LinearLayout) itemView.findViewById(R.id.ll_about);
            llAdd = (LinearLayout) itemView.findViewById(R.id.ll_add);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);

            llAdd.setOnClickListener(this);
            llAbout.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_add: {
                    showDialog();
                }; break;
                case R.id.ll_about:{
                    Intent intent = new Intent(activity, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("player", true);
                    bundle.putSerializable("songItem", arrSongs.get(getAdapterPosition()));
                    bundle.putParcelableArrayList("arrArtist", arrSongs.get(getAdapterPosition()).getArtist());
                    bundle.putParcelableArrayList("arrComposer", arrSongs.get(getAdapterPosition()).getComposer());
                    intent.putExtra("data", bundle);
                    activity.setResult(Constants.RESULT_OK, intent);
                    activity.finish();
                }; break;
                default:
                    Intent playerService = new Intent(context, PlayerService.class);
                    playerService.putExtra("songIndex", getAdapterPosition());
                    playerService.putExtra("playNew", true);
                    context.startService(playerService);
                    break;
            }

        }

        private void showDialog(){
            SessionManagement session = new SessionManagement(context, new SessionManagement.HaveNotLoggedIn() {
                @Override
                public void haveNotLoggedIn() {
                    AlertDialogManagement alertDialod = new AlertDialogManagement(new AlertDialogManagement.ConfirmLogout() {
                        @Override
                        public void confirmLogout() {

                        }
                    });
                    alertDialod.showAlertDialog(context, Constants.ADD_PLAYLIST_TITLE, Constants.REQUEST_LOGIN, false);
                }
            });
            if (session.isLoggedIn()) {
                ArrayList<PlaylistItem> arrPlaylists = ((MyApplication) activity.getApplication()).getArrPlaylists();
                MyAlertDialogFragment dialog = MyAlertDialogFragment.newInstance(arrPlaylists, arrSongs.get(getAdapterPosition()));
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                dialog.show(manager, "fragment_alert");
            }
            else {
                session.checkLogin();
            }
        }
    }
}
