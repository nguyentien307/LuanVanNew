package com.example.tiennguyen.luanvannew.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogManagement;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class UserFm extends Fragment implements View.OnClickListener {

    private Constants CONSTANTS;
    private String res = "";
    private LinearLayout llLogout, llMyPlaylist;
    private ImageView imgAvatar;
    private TextView tvName, tvPhoneNumber, tvEmail, tvNickChat, tvLevel;

    // Session Manager Class
    SessionManagement session;

    public static UserFm newInstance(String name) {
        UserFm contentFragment = new UserFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            res = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_user, viewGroup, false);
        checkLogin();
        initViews(view);
        preparePlaylist();
        viewUserProfile();
        return view;
    }

    private void preparePlaylist() {
        ArrayList<PlaylistItem> arrPlaylists = new ArrayList<>();
        PlaylistItem item0 = new PlaylistItem("Nhạc yêu thích", "", R.drawable.hot_slider1, 19);
        arrPlaylists.add(item0);
        PlaylistItem item1 = new PlaylistItem("Nhạc buồn", "", R.drawable.hot_slider2, 10);
        arrPlaylists.add(item1);
        PlaylistItem item2 = new PlaylistItem("Nhạc sôi động", "", R.drawable.hot_slider3, 15);
        arrPlaylists.add(item2);

        ((MyApplication) getActivity().getApplication()).setArrPlaylists(arrPlaylists);
    }

    private void checkLogin() {
        session = new SessionManagement(getContext(), new SessionManagement.CheckLogin() {
            @Override
            public void checkLogin() {
                replaceFragment("Login");
            }
        });
        session.checkLogin();
    }

    private void initViews(View view) {
        llLogout = (LinearLayout) view.findViewById(R.id.llLogout);
        llMyPlaylist = (LinearLayout) view.findViewById(R.id.llMyPlaylist);
        imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvPhoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvNickChat = (TextView) view.findViewById(R.id.tvNickChat);
        tvLevel = (TextView) view.findViewById(R.id.tvLevel);

        llLogout.setOnClickListener(this);
        llMyPlaylist.setOnClickListener(this);
    }

    private void viewUserProfile() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llLogout:
                AlertDialogManagement dialog = new AlertDialogManagement(new AlertDialogManagement.ConfirmLogout() {
                    @Override
                    public void confirmLogout() {
                        session.logoutUser();
                        replaceFragment("Login");
                    }
                });
                dialog.showConfirmLogoutDialog(getContext(), "Logout", "Do you want to logout?");
                break;
            case R.id.llMyPlaylist:
                replaceFragment("Playlist");
                break;
            default:
                break;
        }
    }

    private void replaceFragment(String page) {
        Fragment fragment = new Fragment();
        switch (page) {
            case "Login":
                fragment = LoginFm.newInstance(page);
                break;
            case "Playlist":
                fragment = new PlaylistFm();
                break;
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
