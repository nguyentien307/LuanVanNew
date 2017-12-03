package com.example.tiennguyen.luanvannew.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.tiennguyen.luanvannew.R;

import java.net.URL;

/**
 * Created by Quyen Hua on 12/3/2017.
 */

public class ImageBigmap {

    public static Bitmap getDefaultAlbumArt(String img) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeStream(new java.net.URL(img).openStream(), null, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }
}
