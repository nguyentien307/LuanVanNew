package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.dialogs.MyAlertDialogFragment;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class PlaylistsNameAdapter extends RecyclerView.Adapter<PlaylistsNameAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<PlaylistItem> arrPlaylists;
    private MyAlertDialogFragment dialog;

    public PlaylistsNameAdapter(Context context, Activity activity, ArrayList<PlaylistItem> arrPlaylists, MyAlertDialogFragment dialog) {
        this.context = context;
        this.arrPlaylists = arrPlaylists;
        this.activity = activity;
        this.dialog = dialog;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvPlaylistName;

        public ViewHolder(View view) {
            super(view);
            tvPlaylistName = (TextView) view.findViewById(R.id.tvTitle);
            tvPlaylistName.setTextColor(Color.rgb(0, 0, 0));
            tvPlaylistName.setTextSize(13);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
            Toast.makeText(context,"Added to " + arrPlaylists.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();

        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.playlist_item_name, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PlaylistItem playlistItem = arrPlaylists.get(position);
        holder.tvPlaylistName.setText(playlistItem.getName());
    }

    @Override
    public int getItemCount() {
        return arrPlaylists.size();
    }

}
