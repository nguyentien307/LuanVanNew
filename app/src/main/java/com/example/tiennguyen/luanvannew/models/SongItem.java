package com.example.tiennguyen.luanvannew.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Quyen Hua on 11/4/2017.
 */

public class SongItem implements Serializable {
    private String title;
    private int views;
    private String link;
    private ArrayList<PersonItem> artist;
    private ArrayList<PersonItem> composer;
    private String linkLyric;


    private String linkImg;

    public SongItem(String title, int views, String link, ArrayList<PersonItem> artist, ArrayList<PersonItem> composer, String linkLyric, String linkImg) {
        this.title = title;
        this.views = views;
        this.link = link;
        this.artist = artist;
        this.composer = composer;
        this.linkLyric = linkLyric;
        this.linkImg = linkImg;
    }

    public SongItem(String title, ArrayList<PersonItem> artist, String link) {
        this.title = title;
        this.artist = artist;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArrayList<PersonItem> getArtist() {
        return artist;
    }

    public void setArtist(ArrayList<PersonItem> artist) {
        this.artist = artist;
    }

    public ArrayList<PersonItem> getComposer() {
        return composer;
    }

    public void setComposer(ArrayList<PersonItem> composer) {
        this.composer = composer;
    }

    public String getLinkLyric() {
        return linkLyric;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }
}
