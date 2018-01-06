package com.example.tiennguyen.luanvannew.dialogs;

import android.app.Activity;
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

    public SearchDialog(CustomLayoutInflater customLI) {
        this.customLI = customLI;
    }

    public void displaySearchDialog(Activity activity) {
        LayoutInflater inflater = customLI.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_search_title, null);
        final RadioButton song = (RadioButton) dialogLayout.findViewById(R.id.rbSongs);
        final RadioButton album = (RadioButton) dialogLayout.findViewById(R.id.rbALbums);

        AlertDialog.Builder searchDialog = customLI.getAlertDialog();
        searchDialog.setView(dialogLayout);
        searchDialog.setTitle(activity.getResources().getString(R.string.searching_for));
        String title = customLI.getCheckedTitle();
        switch (title) {
            case "albums":
                album.setChecked(true);
                break;
            default:
                song.setChecked(true);
                break;
        }
        searchDialog.setPositiveButton(activity.getResources().getString(R.string.action_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchTitle = "";
                if (song.isChecked()) {
                    searchTitle = song.getText().toString().toLowerCase();
                } else if (album.isChecked()) {
                    searchTitle = album.getText().toString().toLowerCase();
                }
                customLI.onResult(searchTitle);
            }
        });
        searchDialog.setNegativeButton(activity.getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
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
