package com.example.tiennguyen.luanvannew.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.tiennguyen.luanvannew.R;

/**
 * Created by Quyen Hua on 11/5/2017.
 */

public class TimerDialog {

    private CustomLayoutInflater customLI;

    public TimerDialog(CustomLayoutInflater customLI) {
        this.customLI = customLI;
    }

    public void displayTimerDialog(Activity activity) {
        LayoutInflater inflater = customLI.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.timer_dialog, null);
        final RadioButton rb30 = (RadioButton) dialogLayout.findViewById(R.id.rb30);
        final RadioButton rb60 = (RadioButton) dialogLayout.findViewById(R.id.rb60);
        final RadioButton rb90 = (RadioButton) dialogLayout.findViewById(R.id.rb90);
        final EditText edTimer = (EditText) dialogLayout.findViewById(R.id.edTimer);

        final String[] newValue = {""};
        final int oldValue = 0;

        edTimer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int newValueInt = Integer.valueOf(charSequence.toString());
                if (newValueInt < 200) {
                    newValue[0] = String.valueOf(newValueInt);
                } else {
                    edTimer.setText(newValue[0]);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        AlertDialog.Builder searchDialog = customLI.getAlertDialog();
        searchDialog.setView(dialogLayout);
        searchDialog.setTitle(activity.getResources().getString(R.string.timer));
        String title = customLI.getCheckedTitle();
        switch (title) {
            case "30'":
                rb30.setChecked(true);
                break;
            case "60'":
                rb60.setChecked(true);
                break;
            case "90'":
                rb90.setChecked(true);
                break;
        }
        searchDialog.setPositiveButton(activity.getResources().getString(R.string.action_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchTitle = "";
                if (!newValue[0].equals("")) {
                    searchTitle = newValue[0] + "'";
                } else if (rb30.isChecked()) {
                    searchTitle = rb30.getText().toString();
                } else if (rb60.isChecked()) {
                    searchTitle = rb60.getText().toString();
                } else if (rb90.isChecked()) {
                    searchTitle = rb90.getText().toString();
                }
                customLI.onResult(searchTitle);
            }
        });
        searchDialog.setNegativeButton(activity.getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customLI.onResult("None");
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
