package com.example.tiennguyen.luanvannew.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.PlaylistsNameAdapter;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class MyAlertDialogFragment extends DialogFragment {

    public static final String TITLE = "dataKey";
    ArrayList<PlaylistItem> arrPlaylists;

    public static MyAlertDialogFragment newInstance(ArrayList<PlaylistItem> arrPlaylists) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TITLE, arrPlaylists);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        arrPlaylists = getArguments().getParcelableArrayList(TITLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_playlist_box, null);

        RecyclerView rcPlaylists = (RecyclerView) view.findViewById(R.id.rc_playlists);
        //recycler
        RecyclerView.LayoutManager playlistsLayoutManager;
        PlaylistsNameAdapter playlistsAdapter;
        ArrayList<PlaylistItem> arrPlaylists = ((MyApplication) getActivity().getApplication()).getArrPlaylists();
        //recycler view
        rcPlaylists.setHasFixedSize(true);
        rcPlaylists.setNestedScrollingEnabled(false);
        playlistsLayoutManager = new LinearLayoutManager(getContext());
        rcPlaylists.setLayoutManager(playlistsLayoutManager);
        playlistsAdapter = new PlaylistsNameAdapter(getContext(), getActivity(), arrPlaylists, MyAlertDialogFragment.this);
        rcPlaylists.setAdapter(playlistsAdapter);

        setCancelable(false);

        builder.setView(view);
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        return dialog;

    }
}