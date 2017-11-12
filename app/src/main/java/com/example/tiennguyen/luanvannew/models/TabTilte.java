package com.example.tiennguyen.luanvannew.models;

import android.view.View;
import android.widget.TextView;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class TabTilte {
    private View view;
    private TextView tv;
    private boolean checked;

    public TabTilte(View view, TextView tv, boolean checked) {
        this.view = view;
        this.tv = tv;
        this.checked = checked;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
