package com.example.tiennguyen.luanvannew.commons;

import android.content.Context;

import com.example.tiennguyen.luanvannew.services.GetHtmlData;
import com.example.tiennguyen.luanvannew.services.GetPage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Quyen Hua on 11/23/2017.
 */

public class GetDataCodeFromZing {

    KeyCodeFromZing keyCodeFromZing;

    public GetDataCodeFromZing(KeyCodeFromZing keyCodeFromZing) {
        this.keyCodeFromZing = keyCodeFromZing;
    }

    public void getKeyFromZing(Context context, String data) {
        StringUtils convertedToUnsigned = new StringUtils();
        String name = convertedToUnsigned.convertedToUnsigned(data);
        GetPage gethtmlData = new GetPage(context);
        gethtmlData.execute(ZingMP3LinkTemplate.SEARCH_URL + name);
        gethtmlData.setDataDownloadListener(new GetPage.DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(Document data) {
                Elements item = data.select("div.item-song");
                String key = "ZGcGykHNhSmpAAZtHTDnZmTkWLFFdEhGz";
                String img = "https://zmp3-photo.zadn.vn/thumb/94_94/covers/6/e/6e7b90d96728c9ce1b4c2a104d622784_1507799020.jpg";
                if (item.size() > 0) {
                    key = item.get(0).attr("data-code");
                    img = item.get(0).select("img").attr("src");
                }
                keyCodeFromZing.keyCodeFromZing(key, img);
            }

            @Override
            public void dataDownloadFailed() {

            }
        });
    }

    public interface KeyCodeFromZing {
        void keyCodeFromZing(String key, String imgLink);
    }
}
