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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.fragments.PlayerCollapseFm;
import com.example.tiennguyen.luanvannew.fragments.SongInfoFm;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.dialogs.MyAlertDialogFragment;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/8/2017.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    ArrayList<SongItem> arrSongs;
    private Context context;
    private String style;
    private Activity activity;


    public SongsAdapter(Context context, Activity activity, ArrayList<SongItem> arrSongs, String style){
        this.arrSongs = arrSongs;
        this.context = context;
        this.style = style;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvSongName, tvArtistName, tvViews, tvIndex;
        public ImageView ivAvatar, ivAdd, ivAbout, ivDelete ;
        public LinearLayout llAdd, llAbout;
        public LinearLayout llIndex;

        ViewHolder(View itemView) {
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
                        ArrayList<ArrayList<PersonItem>> listArtist = new ArrayList<>();
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView ;
        if(style == Constants.SONGS_LIST_IN_PLAYLIST){
            itemView = inflater.inflate(R.layout.song_item_playlist, parent, false);
        }else {
            itemView = inflater.inflate(R.layout.song_item, parent, false);
        }
        ViewHolder svh = new ViewHolder(itemView);
        return svh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvSongName.setText(arrSongs.get(position).getTitle());
        holder.tvViews.setText("Views: "+arrSongs.get(position).getViews());

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
            holder.tvArtistName.setText(singerName);
        } else holder.tvArtistName.setText("khong co");

        if(style == Constants.SONG_CATEGORIES || style == Constants.SONGS_LIST_IN_PLAYLIST) {
            if (arrSongs.get(position).getLinkImg() != "") {
                Glide.with(context)
                        .load(arrSongs.get(position).getLinkImg())
                        .centerCrop()
                        .placeholder(R.drawable.item_up)
                        .error(R.drawable.item_up)
                        .into(holder.ivAvatar);
            }
        }
        else if (style == Constants.ALBUM_CATEGORIES || style.equals(Constants.PLAYER_ACTIVITY)){
            holder.tvIndex.setText(position + 1 +"");
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
                .setMessage("Do you want to Delete")
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
        ArrayList<PlaylistItem> arrPlaylists = ((MyApplication) activity.getApplication()).getArrPlaylists();
        MyAlertDialogFragment dialog = MyAlertDialogFragment.newInstance(arrPlaylists);
        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
        dialog.show(manager, "fragment_alert");

    }


}
