package com.example.tiennguyen.luanvannew;

import android.app.Application;

import com.example.tiennguyen.luanvannew.models.PlaylistItem;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class MyApplication extends Application {

    private Boolean isLogin = false;

    private ArrayList<PlaylistItem> arrPlaylists;

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

    public ArrayList<PlaylistItem> getArrPlaylists() {
        return arrPlaylists;
    }

    public void setArrPlaylists(ArrayList<PlaylistItem> arrPlaylists) {
        this.arrPlaylists = arrPlaylists;
    }
}
