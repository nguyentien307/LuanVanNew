package com.example.tiennguyen.luanvannew;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;

import com.example.tiennguyen.luanvannew.fragments.MusicFm;
import com.example.tiennguyen.luanvannew.fragments.PlaylistFm;
import com.example.tiennguyen.luanvannew.fragments.SearchFm;
import com.example.tiennguyen.luanvannew.fragments.SettingFm;
import com.example.tiennguyen.luanvannew.fragments.UserFm;
import com.example.tiennguyen.luanvannew.helpers.BottomNavigationViewHelper;
import com.example.tiennguyen.luanvannew.helpers.CustomTypefaceSpan;
import com.example.tiennguyen.luanvannew.utils.Constants;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigation ;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBottomNavigation();

    }

    private void setBottomNavigation() {
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);

        fragment = UserFm.newInstance("new");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        Menu m = bottomNavigation.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "isadora.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        LinearLayout secondScreen  = (LinearLayout) findViewById(R.id.fragment_container_second);
        secondScreen.setVisibility(View.GONE);
        LinearLayout fullScreen  = (LinearLayout) findViewById(R.id.full_screen_content);
        fullScreen.setVisibility(View.GONE);
        switch (item.getItemId()){
            case R.id.action_user:{
                fragment = UserFm.newInstance("new");
            }break;
            case R.id.action_music:{
                fragment = MusicFm.newInstance(Constants.TAB_HOT);
            }break;
            case R.id.action_search:{
                fragment = new SearchFm();
            }break;
            case R.id.action_playlist:{
                fragment = PlaylistFm.newInstance("new");
            }break;
            case R.id.action_setting:{
                fragment = SettingFm.newInstance("new");
            }break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return false;
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        int id;
        if(fragment instanceof UserFm) id = R.id.action_user;
        else if (fragment instanceof PlaylistFm){
            id = R.id.action_playlist;
        }else return;
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigationView.getMenu().findItem(id).setChecked(true);
    }
}
