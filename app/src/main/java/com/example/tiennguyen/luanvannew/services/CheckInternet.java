package com.example.tiennguyen.luanvannew.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.tiennguyen.luanvannew.fragments.NoInternetFm;

/**
 * Created by Quyen Hua on 12/2/2017.
 */

public class CheckInternet {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void goNoInternet(Context context, int view) {
        Fragment fragment = new NoInternetFm();
        ((AppCompatActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .replace(view, fragment)
                .commit();
    }
}
