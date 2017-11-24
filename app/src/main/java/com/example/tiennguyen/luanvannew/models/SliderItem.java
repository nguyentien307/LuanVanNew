package com.example.tiennguyen.luanvannew.models;

/**
 * Created by TIENNGUYEN on 11/20/2017.
 */

public class SliderItem {
    private String img;
    private String title;
    private String href;

    public SliderItem(String img, String title, String href) {
        this.img = img;
        this.title = title;
        this.href = href;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
