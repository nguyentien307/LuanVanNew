package com.example.tiennguyen.luanvannew.models;

import java.io.Serializable;

/**
 * Created by Quyen Hua on 11/4/2017.
 */

public class PersonItem implements Serializable {
    private String name;
    private String link;
    private int views;

    public PersonItem(String name, String link, int views) {
        this.name = name;
        this.link = link;
        this.views = views;
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

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }
}
