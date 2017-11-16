package com.example.tiennguyen.luanvannew.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Quyen Hua on 11/4/2017.
 */

public class PersonItem implements Serializable, Parcelable {
    private String name;
    private String link;
    private int views;

    public PersonItem(String name, String link, int views) {
        this.name = name;
        this.link = link;
        this.views = views;
    }

    protected PersonItem(Parcel in) {
        name = in.readString();
        link = in.readString();
        views = in.readInt();
    }

    public static final Creator<PersonItem> CREATOR = new Creator<PersonItem>() {
        @Override
        public PersonItem createFromParcel(Parcel in) {
            return new PersonItem(in);
        }

        @Override
        public PersonItem[] newArray(int size) {
            return new PersonItem[size];
        }
    };

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

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(link);
        dest.writeInt(views);
    }
}
