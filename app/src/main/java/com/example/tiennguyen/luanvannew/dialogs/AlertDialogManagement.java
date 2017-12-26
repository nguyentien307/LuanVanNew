package com.example.tiennguyen.luanvannew.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.tiennguyen.luanvannew.R;

/**
 * Created by Quyen Hua on 11/15/2017.
 */

public class AlertDialogManagement {

    private ConfirmLogout confirmLogout;

    public AlertDialogManagement() {
    }

    public AlertDialogManagement(ConfirmLogout confirmLogout) {
        this.confirmLogout = confirmLogout;
    }

    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if(status)
            // Setting alert dialog icon
            alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton(context.getResources().getString(R.string.action_OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showConfirmLogoutDialog(Context context, String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        alertDialog.setPositiveButton(context.getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.setNegativeButton(context.getResources().getString(R.string.action_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmLogout.confirmLogout();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public interface ConfirmLogout {
        void confirmLogout();
    }
}
