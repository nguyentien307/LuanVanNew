package com.example.tiennguyen.luanvannew.dialogs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.R;

/**
 * Created by Quyen Hua on 11/5/2017.
 */

public class SearchDialog {

    private CustomLayoutInflater customLI;
    private Constants Constants;

    public SearchDialog(CustomLayoutInflater customLI) {
        this.customLI = customLI;
    }

    public void displaySearchDialog() {
        LayoutInflater inflater = customLI.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_search_title, null);
        final RadioButton song = (RadioButton) dialogLayout.findViewById(R.id.rbSongs);
        final RadioButton album = (RadioButton) dialogLayout.findViewById(R.id.rbALbums);
        final RadioButton artist = (RadioButton) dialogLayout.findViewById(R.id.rbArtists);
        final RadioButton composer = (RadioButton) dialogLayout.findViewById(R.id.rbComposers);

        Constants = new Constants();
        AlertDialog.Builder searchDialog = customLI.getAlertDialog();
        searchDialog.setView(dialogLayout);
        searchDialog.setTitle(Constants.SEARCH_TITLE);
        String title = customLI.getCheckedTitle();
        switch (title) {
            case "album":
                album.setChecked(true);
                break;
            case "artist":
                artist.setChecked(true);
                break;
            case "composer":
                composer.setChecked(true);
                break;
            default:
                song.setChecked(true);
                break;
        }
        searchDialog.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchTitle = "";
                if (song.isChecked()) {
                    searchTitle = song.getText().toString().toLowerCase();
                } else if (album.isChecked()) {
                    searchTitle = album.getText().toString().toLowerCase();
                } else if (artist.isChecked()) {
                    searchTitle = artist.getText().toString().toLowerCase();
                } else if (composer.isChecked()) {
                    searchTitle = composer.getText().toString().toLowerCase();
                }
                customLI.onResult(searchTitle);
            }
        });
        searchDialog.setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        searchDialog.create().show();
    }

    public interface CustomLayoutInflater {
        LayoutInflater getLayoutInflater();
        AlertDialog.Builder getAlertDialog();
        void onResult(String searchTitle);
        String getCheckedTitle();
    }
}
