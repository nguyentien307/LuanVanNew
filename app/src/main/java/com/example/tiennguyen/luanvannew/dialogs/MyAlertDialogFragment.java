package com.example.tiennguyen.luanvannew.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.PlaylistsNameAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.fragments.PlaylistFm;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class MyAlertDialogFragment extends DialogFragment {

    public static final String TITLE = "dataKey";
    ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();
    SessionManagement session;
    PlaylistsNameAdapter playlistsAdapter;
    SongItem songItem;
    public static MyAlertDialogFragment newInstance(ArrayList<PlaylistItem> arrPlaylists, SongItem songItem) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TITLE, arrPlaylists);
        args.putSerializable("songItem", songItem);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        songItem = (SongItem) getArguments().getSerializable("songItem");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_playlist_box, null);
        LinearLayout btnNewPlaylist = (LinearLayout) view.findViewById(R.id.btnNewPlaylist);

        RecyclerView rcPlaylists = (RecyclerView) view.findViewById(R.id.rc_playlists);
        //recycler
        RecyclerView.LayoutManager playlistsLayoutManager;

        session = new SessionManagement(getContext());

        //ArrayList<PlaylistItem> arrPlaylists = ((MyApplication) getActivity().getApplication()).getArrPlaylists();
        //recycler view
        rcPlaylists.setHasFixedSize(true);
        rcPlaylists.setNestedScrollingEnabled(false);
        playlistsLayoutManager = new LinearLayoutManager(getContext());
        rcPlaylists.setLayoutManager(playlistsLayoutManager);
        preparePlaylists();
        playlistsAdapter = new PlaylistsNameAdapter(getContext(), getActivity(), arrPlaylists, MyAlertDialogFragment.this, songItem);
        rcPlaylists.setAdapter(playlistsAdapter);

        setCancelable(true);
        builder.setView(view);
        builder.setTitle(getResources().getString(R.string.add_to_playlist));
        final Dialog dialog = builder.create();

        btnNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlaylist();
            }
        });

        return dialog;

    }

    private void addNewPlaylist() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.create_playlist_box, null);
        final EditText etName = (EditText) alertLayout.findViewById(R.id.et_name);


        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getResources().getString(R.string.create_playlist));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton(getResources().getString(R.string.action_done), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString();

                ArrayList<String> arrName = new ArrayList<String>();
                Boolean isValid = true;

                if(session.getPlaylist() != "") {
                    String jsonPlaylists = session.getPlaylist();
                    try {
                        JSONArray arr = new JSONArray(jsonPlaylists);
                        for(int i = 0 ; i < arr.length(); i++){
                            JSONObject jsonItem = arr.getJSONObject(i);
                            String title = jsonItem.getString("name");
                            arrName.add(title);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(arrName.size()!=0){
                    for(int i = 0; i < arrName.size(); i++){
                        if(name.contentEquals(arrName.get(i))) {
                            isValid = false;
                            break;
                        }
                    }
                }

                if(!isValid){
                    Toast.makeText(getContext(), name + getResources().getString(R.string.exist_message), Toast.LENGTH_SHORT).show();
                }
                else {
                    PlaylistItem item = new PlaylistItem(name,"", R.drawable.hot_slider1, 0, "");
                    arrPlaylists.add(item);
                    Gson gson = new Gson();
                    String jsonPlaylist = gson.toJson(arrPlaylists);
                    session.setPlaylist(jsonPlaylist);
                    playlistsAdapter.notifyDataSetChanged();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void preparePlaylists() {
        if(session.getPlaylist() != "") {
            String jsonPlaylists = session.getPlaylist();
            try {
                JSONArray arr = new JSONArray(jsonPlaylists);
                for(int i = 0 ; i < arr.length(); i++){
                    JSONObject jsonItem = arr.getJSONObject(i);
                    PlaylistItem item = new PlaylistItem(jsonItem.getString("name"), jsonItem.getString("link"), jsonItem.getInt("img"), jsonItem.getInt("number"), jsonItem.getString("arrSongs"));
                    arrPlaylists.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
