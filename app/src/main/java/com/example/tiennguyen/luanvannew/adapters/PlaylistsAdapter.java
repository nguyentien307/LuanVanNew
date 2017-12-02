package com.example.tiennguyen.luanvannew.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.fragments.PlaylistFm;
import com.example.tiennguyen.luanvannew.fragments.PlaylistSongsFm;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.tiennguyen.luanvannew.fragments.PlaylistSongsFm.newInstance;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlaylistItem> arrPlaylists;
    SessionManagement session;

    public PlaylistsAdapter(Context context, ArrayList<PlaylistItem> arrPlaylists) {
        this.context = context;
        this.arrPlaylists = arrPlaylists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvPlaylistName, tvNumber;
        public ImageView ivBgCover, overflow;
        public CardView playlistCard;

        public ViewHolder(View view) {
            super(view);
            tvPlaylistName = (TextView) view.findViewById(R.id.tvTitle);
            playlistCard = (CardView) view.findViewById(R.id.playlist_card);
            ivBgCover = (ImageView) view.findViewById(R.id.ivBgCover);
            tvNumber = (TextView) view.findViewById(R.id.tv_number);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            ivBgCover.setOnClickListener(this);
            playlistCard.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            PlaylistSongsFm fragment = newInstance(arrPlaylists.get(getAdapterPosition()));
            ((AppCompatActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.playlist_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PlaylistItem playlistItem = arrPlaylists.get(position);
        holder.tvPlaylistName.setText(playlistItem.getName());
        holder.tvNumber.setText(playlistItem.getNumber() + " song(s)");
        // loading album cover using Glide library
        Glide.with(context).load(playlistItem.getImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(holder.ivBgCover);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, final int position) {
        session = new SessionManagement(context);
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_playlist, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_play_all:
                        Toast.makeText(context, "Play all", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_delete:
                        //Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                        ArrayList<PlaylistItem> arrItem = new ArrayList<PlaylistItem>();
                        if(session.getPlaylist() != "") {
                            String jsonPlaylists = session.getPlaylist();
                            try {
                                JSONArray arr = new JSONArray(jsonPlaylists);
                                for(int i = 0 ; i < arr.length(); i++){
                                    JSONObject jsonItem = arr.getJSONObject(i);
                                    PlaylistItem playlist = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                                    arrItem.add(playlist);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        arrItem.remove(position);
                        Gson gson = new Gson();
                        String jsonPlaylist = gson.toJson(arrItem);
                        session.setPlaylist(jsonPlaylist);

                        PlaylistFm fragment = PlaylistFm.newInstance("new");
                        ((AppCompatActivity)context).getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragment_container, fragment)
                                .commit();

                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return arrPlaylists.size();
    }


    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    public int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}