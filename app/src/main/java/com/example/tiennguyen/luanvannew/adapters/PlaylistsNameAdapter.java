package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.dialogs.MyAlertDialogFragment;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class PlaylistsNameAdapter extends RecyclerView.Adapter<PlaylistsNameAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<PlaylistItem> arrPlaylists;
    private MyAlertDialogFragment dialog;
    private SessionManagement session;
    private SongItem songItem;

    public PlaylistsNameAdapter(Context context, Activity activity, ArrayList<PlaylistItem> arrPlaylists, MyAlertDialogFragment dialog, SongItem songItem) {
        this.context = context;
        this.arrPlaylists = arrPlaylists;
        this.activity = activity;
        this.dialog = dialog;
        this.songItem = songItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvPlaylistName;

        public ViewHolder(View view) {
            super(view);
            tvPlaylistName = (TextView) view.findViewById(R.id.tvTitle);
            tvPlaylistName.setTextColor(Color.rgb(0, 0, 0));
            tvPlaylistName.setTextSize(16);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            dialog.dismiss();
            Toast.makeText(context,getAdapterPosition() + "", Toast.LENGTH_SHORT).show();
            //addToPlaylists(getAdapterPosition(), songItem);
            //Toast.makeText(context,"Added to " + arrPlaylists.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            session = new SessionManagement(context);
            ArrayList<PlaylistItem> newPlaylists = new ArrayList<>();

                String jsonPlaylists = session.getPlaylist();
                //Toast.makeText(getContext(), jsonPlaylists, Toast.LENGTH_SHORT).show();
                try {
                    JSONArray arr = new JSONArray(jsonPlaylists);
                    for(int i = 0 ; i < arr.length(); i++){
                        JSONObject jsonItem = arr.getJSONObject(i);
                        //JSONArray songs = jsonItem.getJSONArray("arrSongs");
                        PlaylistItem item = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                        newPlaylists.add(item);
                    }
                    //JSONObject item = new JSONObject(newPlaylists.get(position).getArrSongs());
                    PlaylistItem playlistItem = newPlaylists.get(getAdapterPosition());
                    JSONArray songs = new JSONArray(playlistItem.getArrSongs());
                    ArrayList<SongItem> arrSongs = new ArrayList<>();
                    for(int j = 0; j < songs.length(); j ++){
                        JSONObject song = songs.getJSONObject(j);
                        String title = song.getString("title");
                        int views = song.getInt("views");
                        String link = song.getString("link");
                        String linkLyric = song.getString("linkLyric");
                        String linkImg = song.getString("linkImg");
                        JSONArray singers = song.getJSONArray("artist");
                        JSONArray composers = song.getJSONArray("composer");
                        final ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                        final ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
                        for (int index = 0 ; index < singers.length(); index++){
                            JSONObject singer = singers.getJSONObject(index);
                            String singerHref = singer.getString("link");
                            String singerName = singer.getString("name");
                            int view = singer.getInt("views");
                            PersonItem singerItem = new PersonItem(singerName, singerHref, view);
                            arrSingers.add(singerItem);
                        }
                        for (int index = 0 ; index < composers.length(); index++){
                            JSONObject composer = composers.getJSONObject(index);
                            String composerHref = composer.getString("link");
                            String composerName = composer.getString("name");
                            int view = composer.getInt("views");
                            PersonItem composerItem = new PersonItem(composerName, composerHref, view);
                            arrComposers.add(composerItem);
                        }
                        SongItem item = new SongItem(title, views, link, arrSingers, arrComposers, linkLyric, linkImg );
                        arrSongs.add(item);
                    }
                    arrSongs.add(songItem);
                    Gson gson = new Gson();
                    String jsonSongs = gson.toJson(arrSongs);
//                    Toast.makeText(context,getAdapterPosition() + "", Toast.LENGTH_SHORT).show();
                    PlaylistItem newItem = new PlaylistItem(playlistItem.getName(), playlistItem.getLink(), playlistItem.getImg(), playlistItem.getNumber(), jsonSongs);
                    newPlaylists.set(getAdapterPosition(), newItem);
                    String jsonPlaylist = gson.toJson(newPlaylists);
                    session.setPlaylist(jsonPlaylist);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }

    private void addToPlaylists(int position, SongItem songItem) {
        session = new SessionManagement(context);
        ArrayList<PlaylistItem> newPlaylists = new ArrayList<>();
        if(session.getPlaylist() != "") {
            String jsonPlaylists = session.getPlaylist();
            //Toast.makeText(getContext(), jsonPlaylists, Toast.LENGTH_SHORT).show();
            try {
                JSONArray arr = new JSONArray(jsonPlaylists);
                for(int i = 0 ; i < arr.length(); i++){
                    JSONObject jsonItem = arr.getJSONObject(i);
                    //JSONArray songs = jsonItem.getJSONArray("arrSongs");
                    PlaylistItem item = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                    newPlaylists.add(item);
                }
                //JSONObject item = new JSONObject(newPlaylists.get(position).getArrSongs());
                PlaylistItem playlistItem = newPlaylists.get(position);
                JSONArray songs = new JSONArray(playlistItem.getArrSongs());
                ArrayList<SongItem> arrSongs = new ArrayList<>();
                for(int j = 0; j < songs.length(); j ++){
                    JSONObject song = songs.getJSONObject(j);
                    String title = song.getString("title");
                    int views = song.getInt("views");
                    String link = song.getString("link");
                    String linkLyric = song.getString("linkLyric");
                    String linkImg = song.getString("linkImg");
                    JSONArray singers = song.getJSONArray("artist");
                    JSONArray composers = song.getJSONArray("composer");
                    final ArrayList<PersonItem> arrSingers = new ArrayList<PersonItem>();
                    final ArrayList<PersonItem> arrComposers = new ArrayList<PersonItem>();
                    for (int index = 0 ; index < singers.length(); index++){
                        JSONObject singer = singers.getJSONObject(index);
                        String singerHref = singer.getString("link");
                        String singerName = singer.getString("name");
                        int view = singer.getInt("views");
                        PersonItem singerItem = new PersonItem(singerName, singerHref, view);
                        arrSingers.add(singerItem);
                    }
                    for (int index = 0 ; index < composers.length(); index++){
                        JSONObject composer = composers.getJSONObject(index);
                        String composerHref = composer.getString("link");
                        String composerName = composer.getString("name");
                        int view = composer.getInt("views");
                        PersonItem composerItem = new PersonItem(composerName, composerHref, view);
                        arrComposers.add(composerItem);
                    }
                    SongItem item = new SongItem(title, views, link, arrSingers, arrComposers, linkLyric, linkImg );
                    arrSongs.add(item);
                }
                arrSongs.add(songItem);
                Gson gson = new Gson();
                String jsonSongs = gson.toJson(arrSongs);
                Toast.makeText(context,position + "", Toast.LENGTH_SHORT).show();
                PlaylistItem newItem = new PlaylistItem(playlistItem.getName(), playlistItem.getLink(), playlistItem.getImg(), playlistItem.getNumber(), jsonSongs);
                newPlaylists.set(position, newItem);
                String jsonPlaylist = gson.toJson(newPlaylists);
                session.setPlaylist(jsonPlaylist);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
