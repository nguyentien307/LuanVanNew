package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogManagement;
import com.example.tiennguyen.luanvannew.dialogs.MyAlertDialogFragment;
import com.example.tiennguyen.luanvannew.fragments.PlayerCollapseFm;
import com.example.tiennguyen.luanvannew.fragments.SongInfoFm;
import com.example.tiennguyen.luanvannew.interfaces.OnLoadMoreListener;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/8/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<SongItem> arrSongs;
    private Context context;
    private String style;
    private Activity activity;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }


    public SongsAdapter(Context context, Activity activity, ArrayList<SongItem> arrSongs, String style, RecyclerView recyclerView){
        this.arrSongs = arrSongs;
        this.context = context;
        this.style = style;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvSongName, tvArtistName, tvViews, tvIndex;
        public ImageView ivAvatar, ivAdd, ivAbout, ivDelete ;
        public LinearLayout llAdd, llAbout;
        public LinearLayout llIndex;

        SongViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tv_song_name);
            tvArtistName = (TextView) itemView.findViewById(R.id.tv_artist_name);
            tvViews = (TextView) itemView.findViewById(R.id.tv_views);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            ivAbout = (ImageView) itemView.findViewById(R.id.iv_about);
            llAbout = (LinearLayout) itemView.findViewById(R.id.ll_about);
            if (style == Constants.SONGS_LIST_IN_PLAYLIST){
                ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
                ivDelete.setOnClickListener(this);
            } else {
                ivAdd = (ImageView) itemView.findViewById(R.id.iv_add);
                llAdd = (LinearLayout) itemView.findViewById(R.id.ll_add);
                llIndex = (LinearLayout) itemView.findViewById(R.id.ll_index);
                tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
                if (style == Constants.ALBUM_CATEGORIES) {
                    ivAvatar.setVisibility(View.GONE);
                } else {
                    llIndex.setVisibility(View.GONE);
                }
                ivAdd.setOnClickListener(this);
                llAdd.setOnClickListener(this);
            }

            ivAbout.setOnClickListener(this);
            llAbout.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_add:
                case R.id.iv_add: {
                    showDialog();
                }; break;

                case R.id.ll_about:
                case R.id.iv_about:{
                    //Toast.makeText(context,"About", Toast.LENGTH_SHORT).show();
                    LinearLayout secondScreen  = (LinearLayout) activity.findViewById(R.id.fragment_container_second);
                    secondScreen.setVisibility(View.VISIBLE);
                    Fragment fragment = SongInfoFm.newInstance(arrSongs.get(getAdapterPosition()));
                    transaction(R.id.fragment_container_second, fragment);

                }; break;

                case R.id.iv_delete: {
                    //Toast.makeText(context,"Delete", Toast.LENGTH_SHORT).show();
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                }; break;

                default: {
                    LinearLayout llPlayerCol = (LinearLayout) activity.findViewById(R.id.llPlayerCollapse);
                    llPlayerCol.setVisibility(View.VISIBLE);
                    PlayerCollapseFm playerCollapseFm = PlayerCollapseFm.newInstance(arrSongs.get(getAdapterPosition()));
                    FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.llPlayerCollapse, playerCollapseFm).commit();

                    Bundle bundle = new Bundle();
                    if (style.equals(Constants.SONG_CATEGORIES)) {
                        bundle.putSerializable("songItem", arrSongs.get(getAdapterPosition()));
                        bundle.putParcelableArrayList("arrArtist", arrSongs.get(getAdapterPosition()).getArtist());
                        bundle.putParcelableArrayList("arrComposer", arrSongs.get(getAdapterPosition()).getComposer());
                    } else {
                        bundle.putParcelableArrayList("arrSong", arrSongs);
                        for (int i = 0; i < arrSongs.size(); i++) {
                            bundle.putParcelableArrayList("arrArtist" + i, arrSongs.get(i).getArtist());
                            bundle.putParcelableArrayList("arrComposer" + i, arrSongs.get(i).getComposer());
                        }
                        bundle.putInt("index", getAdapterPosition());
                    }
                    bundle.putString("type", style);
                    Intent intent = new Intent(activity, PlayerActivity.class);
                    intent.putExtra("data", bundle);
                    activity.startActivityForResult(intent, com.example.tiennguyen.luanvannew.commons.Constants.REQUEST_CODE);
                }break;
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return arrSongs.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ITEM) {
            if(style == Constants.SONGS_LIST_IN_PLAYLIST){
                view = LayoutInflater.from(activity).inflate(R.layout.song_item_playlist, parent, false);
            }else {
                view = LayoutInflater.from(activity).inflate(R.layout.song_item, parent, false);
            }
            return new SongViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SongViewHolder) {
            SongViewHolder songViewHolder = (SongViewHolder) holder;
            songViewHolder.tvSongName.setText(arrSongs.get(position).getTitle());
            songViewHolder.tvViews.setText("Views: "+arrSongs.get(position).getViews());

            ArrayList<PersonItem> singers = arrSongs.get(position).getArtist();
            String singerName = "";
            for (int singerIndex = 0; singerIndex < singers.size(); singerIndex++) {
                if (singerIndex == singers.size() - 1) {
                    singerName += singers.get(singerIndex).getName();
                } else {
                    singerName += singers.get(singerIndex).getName() + ", ";
                }
            }
            if (singerName != "") {
                songViewHolder.tvArtistName.setText(singerName);
            } else songViewHolder.tvArtistName.setText("khong co");

            if(style == Constants.SONG_CATEGORIES || style == Constants.SONGS_LIST_IN_PLAYLIST) {
                if (arrSongs.get(position).getLinkImg() != "") {
                    Glide.with(context)
                            .load(arrSongs.get(position).getLinkImg())
                            .centerCrop()
                            .placeholder(R.drawable.item_up)
                            .error(R.drawable.item_up)
                            .into(songViewHolder.ivAvatar);
                }
            }
            else if (style == Constants.ALBUM_CATEGORIES || style.equals(Constants.PLAYER_ACTIVITY)){
                songViewHolder.tvIndex.setText(position + 1 +"");
            }

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount() {
        return arrSongs == null ? 0 : arrSongs.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void transaction(int idLayout, Fragment fragment){
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(idLayout, fragment)
                .commit();
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage(Constants.DELETE_CONFIRM)
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

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
            MyAlertDialogFragment dialog = MyAlertDialogFragment.newInstance(arrPlaylists);
            FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            dialog.show(manager, "fragment_alert");
        }
        else {
            session.checkLogin();
        }
    }
}
