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

public class LoginFm extends Fragment implements TextWatcher, View.OnKeyListener, TextView.OnEditorActionListener, View.OnClickListener {

    SessionManagement session;
    AlertDialogManagement alert = new AlertDialogManagement();
    Constants Constants = new Constants();

    private String res = "";
    private EditText edEmail, edPassword;
    private TextView tvEmailInvalid, tvPasswordInvalid;
    private Button btnLogin;
    private boolean isInvalid = false;

    public static LoginFm newInstance(String name) {
        LoginFm contentFragment = new LoginFm();
        Bundle bundle = new Bundle();
        bundle.putString("page", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!= null)
            res = getArguments().getString("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_login, viewGroup, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        session = new SessionManagement(getContext(), new SessionManagement.HaveNotLoggedIn() {
            @Override
            public void haveNotLoggedIn() {

            }
        });
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edPassword = (EditText) view.findViewById(R.id.edPassword);
        tvEmailInvalid = (TextView) view.findViewById(R.id.tvEmailInvalid);
        tvPasswordInvalid = (TextView) view.findViewById(R.id.tvPasswordInvalid);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);

        edEmail.addTextChangedListener(this);
        edEmail.setOnKeyListener(this);
        edEmail.setOnEditorActionListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkEmailType(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void checkEmailType(CharSequence email) {
        if (email.toString().equals("")) {
            edEmail.setBackgroundResource(R.drawable.edittext_login_invalid_selector);
            tvEmailInvalid.setText(Constants.FIELD_REQUIRED);
            tvEmailInvalid.setVisibility(View.VISIBLE);
            isInvalid = true;
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setBackgroundResource(R.drawable.edittext_login_invalid_selector);
            tvEmailInvalid.setText(Constants.EMAIL_INVALID_TYPE);
            tvEmailInvalid.setVisibility(View.VISIBLE);
            isInvalid = true;
        } else {
            edEmail.setBackgroundResource(R.drawable.edittext_login_selector);
            tvEmailInvalid.setVisibility(View.GONE);
            isInvalid = false;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        switch (v.getId()) {
//            case R.id.edEmail:
        if(keyCode == KeyEvent.KEYCODE_ENTER) {
            Toast.makeText(getActivity(), edEmail.getText(), Toast.LENGTH_SHORT).show();
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
//                            Context.INPUT_METHOD_SERVICE); //tham chiếu tới INPUT service
//                    imm.hideSoftInputFromWindow(edPassword.getWindowToken(), 0); //ẩn bàn phím
        }
//                break;
//        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            //do here your stuff f
            Toast.makeText(getActivity(), edEmail.getText(), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                // Hiện tại không kiểm tra login, sẽ thực hiện khi có API request đăng nhập
                if (edEmail.equals("") || edPassword.equals("") || !isInvalid)
                    loginValidateion();
        }
    }

    private void loginValidateion() {
        String email = String.valueOf(edEmail.getText());
        String password = String.valueOf(edPassword.getText());

        // Check if username, password is filled
        if(email.trim().length() > 0 && password.trim().length() > 0){
            // For testing puspose username, password is checked with sample data
            // username = test
            // password = test
            if(email.equals("huathitoquyen0403@gmail.com") && password.equals("1111")){

                // Creating user login session
                // For testing i am stroing name, email as follow
                // Use user real data
                session.createLoginSession("huathitoquyen0403@gmail.com", "1111");

                Fragment fragment = new Fragment();
                switch (res) {
                    case "Playlist":
                        fragment = new PlaylistFm();
                        break;
                    case "Login":
                        fragment = new UserFm();
                        break;
                }
                preparePlaylist();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();

            } else {
                // username / password doesn't match
                alert.showAlertDialog(getActivity(), getResources().getString(R.string.login_fail), getResources().getString(R.string.error_message), false);
            }
        }else{
            // user didn't entered username or password
            // Show alert asking him to enter the details
            alert.showAlertDialog(getActivity(), getResources().getString(R.string.login_fail), getResources().getString(R.string.warning_message), false);
        }
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
}
