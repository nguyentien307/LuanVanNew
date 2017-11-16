package com.example.tiennguyen.luanvannew.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class AlbumItem implements Serializable {
    private String name;
    private String link;
    private ArrayList<PersonItem> singers;
    private String linkImg;
    private int views;

    public AlbumItem(String name, String link, String linkImg, int views, ArrayList<PersonItem> singers) {
        this.name = name;
        this.link = link;
        this.linkImg = linkImg;
        this.views = views;
        this.singers = singers;
    }

    public ArrayList<PersonItem> getSingers() {
        return singers;
    }

    public void setSingers(ArrayList<PersonItem> singers) {
        this.singers = singers;
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

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}