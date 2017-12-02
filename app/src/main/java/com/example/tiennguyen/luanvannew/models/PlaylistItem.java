package com.example.tiennguyen.luanvannew.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by TIENNGUYEN on 11/15/2017.
 */

public class PlaylistItem implements Serializable, Parcelable {
    private String name;
    private String link;
    private int img;
    private int number;
    private String arrSongs;

    public PlaylistItem(String name, String link, int img, int number) {
        this.name = name;
        this.link = link;
        this.img = img;
        this.number = number;
    }
    public PlaylistItem(String name, String link, int img, int number, String arrSongs) {
        this.name = name;
        this.img = img;
        this.link = link;
        this.arrSongs = arrSongs;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public PlaylistItem(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<PlaylistItem> CREATOR = new Parcelable.Creator<PlaylistItem>() {
        public PlaylistItem createFromParcel(Parcel in) {
            return new PlaylistItem(in);
        }

        public PlaylistItem[] newArray(int size) {

            return new PlaylistItem[size];
        }

    };

    public String getArrSongs() {
        return arrSongs;
    }

    public void setArrSongs(String arrSongs) {
        this.arrSongs = arrSongs;
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        link = in.readString();
        img = in.readInt();
        number = in.readInt();
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(link);
        dest.writeInt(img);
        dest.writeInt(number);
    }
}
