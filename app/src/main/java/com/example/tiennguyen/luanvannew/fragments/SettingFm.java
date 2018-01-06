package com.example.tiennguyen.luanvannew.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.dialogs.SearchDialog;
import com.example.tiennguyen.luanvannew.dialogs.TimerDialog;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;
import com.example.tiennguyen.luanvannew.changelanguage.ChangeLanguageActivity;
import com.example.tiennguyen.luanvannew.commons.Constants;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class SettingFm extends Fragment implements View.OnClickListener {

    private View titleMusic, titleHistory, titleLanguage;
    private TextView tvMusicTitle, tvHistoryTilte, tvLanguageTitle;

    private View switchPlayerBackground, switchPlugPhone, switchHistory, switchRemovePhone;
    private Button changeLanguage;
    SessionManagement session;

    private String res = "";

    public static SettingFm newInstance(String name) {
        SettingFm contentFragment = new SettingFm();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        contentFragment.setArguments(bundle);

        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            res = getArguments().getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_setting, viewGroup, false);

        titleMusic = view.findViewById(R.id.title_music);
        tvMusicTitle = (TextView) titleMusic.findViewById(R.id.tv_title_name);
        tvMusicTitle.setText(R.string.title_player_setting);
        titleHistory = view.findViewById(R.id.title_history);
        tvHistoryTilte = (TextView) titleHistory.findViewById(R.id.tv_title_name);
        tvHistoryTilte.setText(R.string.title_search_setting);
        titleLanguage = view.findViewById(R.id.title_language);
        tvLanguageTitle = (TextView) titleLanguage.findViewById(R.id.tv_title_name);
        tvLanguageTitle.setText(R.string.title_language_setting);
        changeLanguage = (Button) view.findViewById(R.id.button_change_language);
        changeLanguage.setOnClickListener(this);

        session = new SessionManagement(getContext());

        setPlayingBackground(view);
        setPlugHeadPhone(view);
        setRemoveHeadPhone(view);
        setHistory(view);
        setAutoStopTime(view);
        return view;
    }

    private void setPlayingBackground(View view) {
        switchPlayerBackground = view.findViewById(R.id.swich_btn_1);
        RadioGroup groupBackground = (RadioGroup) switchPlayerBackground.findViewById(R.id.toggle);
        RadioButton btnOn = (RadioButton) switchPlayerBackground.findViewById(R.id.on);
        RadioButton btnOff = (RadioButton) switchPlayerBackground.findViewById(R.id.off);
        if (session.getPlayInBackground()) {
            btnOn.setChecked(true);
            btnOff.setChecked(false);
        } else {
            btnOn.setChecked(false);
            btnOff.setChecked(true);
        }

        groupBackground.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean isPlayInBackground = true;
                if (checkedId == R.id.on) {
                    isPlayInBackground = true;
                    Toast.makeText(getActivity(), "Turn on play in background", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.off) {
                    isPlayInBackground = false;
                    Toast.makeText(getActivity(), "Turn off play in background", Toast.LENGTH_SHORT).show();
                }
                session.setPlayInBackground(isPlayInBackground);
            }

        });
    }

    private void setPlugHeadPhone(View view) {
        switchPlugPhone = view.findViewById(R.id.swich_btn_2);
        RadioGroup groupPhone = (RadioGroup) switchPlugPhone.findViewById(R.id.toggle);
        RadioButton btnOn = (RadioButton) switchPlugPhone.findViewById(R.id.on);
        RadioButton btnOff = (RadioButton) switchPlugPhone.findViewById(R.id.off);
        if (session.getContinueWhenPlugPhone()) {
            btnOn.setChecked(true);
            btnOff.setChecked(false);
        } else {
            btnOn.setChecked(false);
            btnOff.setChecked(true);
        }
        groupPhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean isContinueWhenPlugPhone = true;
                if (checkedId == R.id.on) {
                    isContinueWhenPlugPhone = true;
                    Toast.makeText(getActivity(), "Continue play music", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.off) {
                    isContinueWhenPlugPhone = false;
                    Toast.makeText(getActivity(), "Stop play music", Toast.LENGTH_SHORT).show();
                }
                session.setContinueWhenPlugPhone(isContinueWhenPlugPhone);
            }

        });
    }

    private void setRemoveHeadPhone(View view) {
        switchRemovePhone = view.findViewById(R.id.swich_btn);
        RadioGroup groupRemovePhone = (RadioGroup) switchRemovePhone.findViewById(R.id.toggle);
        RadioButton btnOn = (RadioButton) switchRemovePhone.findViewById(R.id.on);
        RadioButton btnOff = (RadioButton) switchRemovePhone.findViewById(R.id.off);
        if (session.getContinueWhenRemovePhone()) {
            btnOn.setChecked(true);
            btnOff.setChecked(false);
        } else {
            btnOn.setChecked(false);
            btnOff.setChecked(true);
        }
        groupRemovePhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean isContinueWhenRemovePhone = true;
                if (checkedId == R.id.on) {
                    isContinueWhenRemovePhone = true;
                    Toast.makeText(getActivity(), "Continue play music", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.off) {
                    isContinueWhenRemovePhone = false;
                    Toast.makeText(getActivity(), "Stop play music", Toast.LENGTH_SHORT).show();
                }
                session.setContinueWhenRemovePhone(isContinueWhenRemovePhone);
            }

        });

    }

    private void setHistory(View view) {
        switchHistory = view.findViewById(R.id.swich_btn_3);
        RadioGroup groupHistory = (RadioGroup) switchHistory.findViewById(R.id.toggle);
        RadioButton btnOn = (RadioButton) switchHistory.findViewById(R.id.on);
        RadioButton btnOff = (RadioButton) switchHistory.findViewById(R.id.off);
        if (session.getSaveHistory()) {
            btnOn.setChecked(true);
            btnOff.setChecked(false);
        } else {
            btnOn.setChecked(false);
            btnOff.setChecked(true);
        }
        groupHistory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean isSaveHistory = true;
                if (checkedId == R.id.on) {
                    isSaveHistory = true;
                    Toast.makeText(getActivity(), "Save history", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.off) {
                    isSaveHistory = false;
                    Toast.makeText(getActivity(), "Don't save history", Toast.LENGTH_SHORT).show();
                }
                session.setSaveHistory(isSaveHistory);
            }

        });
    }

    private void setAutoStopTime(View view) {
        LinearLayout llTimer = (LinearLayout) view.findViewById(R.id.llTimer);
        final TextView tvTimer = (TextView) view.findViewById(R.id.tvTimer);
        int autoStopTime = session.getAutoStopPlayMusicTime();
        if (autoStopTime > 0) {
            tvTimer.setText(autoStopTime + "'");
        } else {
            tvTimer.setText("None");
        }
        llTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimerDialog timerDialog = new TimerDialog(new TimerDialog.CustomLayoutInflater() {
                    @Override
                    public LayoutInflater getLayoutInflater() {
                        return getActivity().getLayoutInflater();
                    }

                    @Override
                    public AlertDialog.Builder getAlertDialog() {
                        AlertDialog.Builder timerTitleDialog = new AlertDialog.Builder(getActivity());
                        return timerTitleDialog;
                    }

                    @Override
                    public void onResult(String title) {
                        if (!title.equals("None")) {
                            tvTimer.setText(title);
                            int time = Integer.parseInt(title.split("'", 2)[0]);
                            session.setAutoStopPlayMusicTime(time);
                        } else {
                            tvTimer.setText(title);
                            session.setAutoStopPlayMusicTime(-1);
                        }
                    }

                    @Override
                    public String getCheckedTitle() {
                        return tvTimer.getText().toString();
                    }
                });
                timerDialog.displayTimerDialog(getActivity());
            }
        });
//        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
//
//        final String[] spinnerArr = getResources().getStringArray(R.array.spiner);
//        ArrayAdapter<String> spinerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spiner_layout,
//                spinnerArr);
//        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(spinerAdapter);
//        int id;
//        int autoStopTime = session.getAutoStopPlayMusicTime();
//        for (id = 0; id < spinnerArr.length; id++) {
//            if (autoStopTime == -1) {
//                break;
//            } else if (spinnerArr[id].equals(autoStopTime + " min")) {
//                break;
//            }
//        }
//        spinner.setSelection(id);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                int time = -1;
//                if (!spinnerArr[position].equals("None"))
//                    time = Integer.parseInt(spinnerArr[position].split(" ", 2)[0]);
//                session.setAutoStopPlayMusicTime(time);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                return;
//            }
//        });
    }

    public void openLanguageScreen() {
        Intent intent = new Intent(getContext(), ChangeLanguageActivity.class);
        startActivityForResult(intent, Constants.RequestCode.CHANGE_LANGUAGE);
    }

    @Override
    public void onClick(View v) {
        openLanguageScreen();
    }
}
