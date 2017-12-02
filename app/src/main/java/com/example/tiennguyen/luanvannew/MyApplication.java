package com.example.tiennguyen.luanvannew;

import android.app.Application;

import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class MyApplication extends Application {

    private Boolean isLogin = false;



    private Boolean isChangeLanguage = false;
    public Boolean getChangeLanguage() {
        return isChangeLanguage;
    }

    public void setChangeLanguage(Boolean changeLanguage) {
        isChangeLanguage = changeLanguage;
    }

    private ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();

    private ArrayList<SongItem> arrayPlayer;

    private Boolean isAlbumOrCategory = true;

    public Boolean getAlbumOrCategory() {
        return isAlbumOrCategory;
    }

    public void setAlbumOrCategory(Boolean albumOrCategory) {
        isAlbumOrCategory = albumOrCategory;
    }

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

    public ArrayList<SongItem> getArrayPlayer() {
        return arrayPlayer;
    }

    public void setArrayPlayer(ArrayList<SongItem> arrayPlayer) {
        this.arrayPlayer = arrayPlayer;
    }


    private static MyApplication sInstance;
    private Gson mGSon;

    public static MyApplication self() {
        return sInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mGSon = new Gson();
    }

    public Gson getGSon() {
        return mGSon;
    }
}
