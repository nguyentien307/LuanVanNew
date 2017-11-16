package com.example.tiennguyen.luanvannew.models;

import java.io.Serializable;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class CategoryItem implements Serializable {
    private String name;
    private String link;
    private int image;

    public CategoryItem(String name, String link, int image) {
        this.name = name;
        this.link = link;
        this.image = image;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}