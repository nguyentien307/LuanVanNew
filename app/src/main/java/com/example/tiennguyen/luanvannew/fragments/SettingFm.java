package com.example.tiennguyen.luanvannew.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.R;

/**
 * Created by TIENNGUYEN on 11/11/2017.
 */

public class SettingFm extends Fragment {

    private View titleMusic, titleHistory;
    private TextView tvMusicTitle, tvHistoryTilte;

    private View switchPlayerBackground, switchPhone, switchHistory;

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
        tvMusicTitle.setText("Music player");
        titleHistory = view.findViewById(R.id.title_history);
        tvHistoryTilte = (TextView) titleHistory.findViewById(R.id.tv_title_name);
        tvHistoryTilte.setText("Search Settings");

        switchPlayerBackground = view.findViewById(R.id.swich_btn_1);
        RadioGroup groupBackground = (RadioGroup) switchPlayerBackground.findViewById(R.id.toggle);

        groupBackground.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.on) {
                            Toast.makeText(getActivity(), "background on", Toast.LENGTH_SHORT).show();
                        } else if (checkedId == R.id.off) {
                            Toast.makeText(getActivity(), "background of", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
        switchPhone = view.findViewById(R.id.swich_btn_2);
        RadioGroup groupPhone = (RadioGroup) switchPhone.findViewById(R.id.toggle);
        groupPhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.on) {
                    Toast.makeText(getActivity(), " on", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.off) {
                    Toast.makeText(getActivity(), "of", Toast.LENGTH_SHORT).show();
                }

            }

        });

        switchHistory = view.findViewById(R.id.swich_btn_3);
        RadioGroup groupHistory = (RadioGroup) switchHistory.findViewById(R.id.toggle);
        groupHistory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.on) {
                    Toast.makeText(getActivity(), "background on", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.off) {
                    Toast.makeText(getActivity(), "background of", Toast.LENGTH_SHORT).show();
                }

            }

        });

        //spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        ArrayAdapter<String> spinerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spiner_layout,
                getResources().getStringArray(R.array.spiner));
        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "choose" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
        return view;
    }
}