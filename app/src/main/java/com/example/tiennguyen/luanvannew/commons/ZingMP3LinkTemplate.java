package com.example.tiennguyen.luanvannew.commons;

/**
 * Created by Quyen Hua on 11/22/2017.
 */

public class ZingMP3LinkTemplate {
    public static final String SEARCH_URL = "https://mp3.zing.vn/tim-kiem/bai-hat.html?q=";

    private String URL = "https://mp3.zing.vn/tim-kiem/";
    public String getSearchUrl(String songName, String title) {
        return this.URL + title + ".html?q=" + songName;
    }
}
