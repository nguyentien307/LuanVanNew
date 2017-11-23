package com.example.tiennguyen.luanvannew.sessions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.fragments.LoginFm;

import java.util.HashMap;

/**
 * Created by Quyen Hua on 11/15/2017.
 */

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Activity
    Activity activity;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "User infomation";

    private static final String PREF_NAME_1 = "Setting infomation";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    public static final String PLAY_IN_BACKGROUND = "play in background";

    public static final String CONTINUE_PLAY_WHEN_PLUG_PHONE = "continue play when plug phone";

    public static final String CONTINUE_PLAY_WHEN_REMOVE_PHONE = "continue play when remove phone";

    public static final String SAVE_HISTORY = "save history";

    public static final String AUTO_STOP_TIME = "auto stop time";

    public static final String CHECK_ALARM = "check alarm";

    HaveNotLoggedIn haveNotLoggedIn;

    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME_1, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Constructor
    public SessionManagement(Context context, HaveNotLoggedIn haveNotLoggedIn){
        this._context = context;
        this.haveNotLoggedIn = haveNotLoggedIn;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_EMAIL, email);

        // Storing email in pref
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            haveNotLoggedIn.haveNotLoggedIn();
        }
    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public interface HaveNotLoggedIn {
        void haveNotLoggedIn();
    }

    public boolean isCheckAlarm() {
        return pref.getBoolean(CHECK_ALARM, false);
    }

    public void setCheckAlarm(boolean checkAlarm) {
        editor.putBoolean(CHECK_ALARM, checkAlarm);
        editor.commit();
    }

    public Boolean getPlayInBackground() {
        return pref.getBoolean(PLAY_IN_BACKGROUND, true);
    }

    public void setPlayInBackground(Boolean playInBackground) {
        editor.putBoolean(PLAY_IN_BACKGROUND, playInBackground);
        editor.commit();
    }

    public Boolean getContinueWhenPlugPhone() {
        return pref.getBoolean(CONTINUE_PLAY_WHEN_PLUG_PHONE, true);
    }

    public void setContinueWhenPlugPhone(Boolean continueWhenPlugPhone) {
        editor.putBoolean(CONTINUE_PLAY_WHEN_PLUG_PHONE, continueWhenPlugPhone);
        editor.commit();
    }

    public Boolean getContinueWhenRemovePhone() {
        return pref.getBoolean(CONTINUE_PLAY_WHEN_REMOVE_PHONE, true);
    }

    public void setContinueWhenRemovePhone(Boolean continueWhenRemovePhone) {
        editor.putBoolean(CONTINUE_PLAY_WHEN_REMOVE_PHONE, continueWhenRemovePhone);
        editor.commit();
    }

    public Boolean getSaveHistory() {
        return pref.getBoolean(SAVE_HISTORY, true);
    }

    public void setSaveHistory(Boolean saveHistory) {
        editor.putBoolean(SAVE_HISTORY, saveHistory);
        editor.commit();
    }

    public int getAutoStopPlayMusicTime() {
        return pref.getInt(AUTO_STOP_TIME, -1);
    }

    public void setAutoStopPlayMusicTime(int autoStopPlayMusicTime) {
        editor.putInt(AUTO_STOP_TIME, autoStopPlayMusicTime);
        editor.commit();
    }
}
