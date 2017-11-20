package com.example.tiennguyen.luanvannew.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.tiennguyen.luanvannew.R;

/**
 * Created by Quyen Hua on 11/15/2017.
 */

public class AlertDialogClearHistory {

    private ConfirmClear confirmClear;

    public AlertDialogClearHistory() {
    }

    public AlertDialogClearHistory(ConfirmClear confirmClear) {
        this.confirmClear = confirmClear;
    }

    public void showAlertDialog(Context context, String title, String message,
                                int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showConfirmClearDialog(Context context, String title, String message, final int position) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmClear.confirmClear(position);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public interface ConfirmClear {
        void confirmClear(int position);
    }
}
