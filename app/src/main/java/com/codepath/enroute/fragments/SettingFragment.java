package com.codepath.enroute.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.codepath.enroute.R;

import static android.content.Context.MODE_PRIVATE;

public class SettingFragment extends DialogFragment{

    SharedPreferences settingPreference;
    ImageView ivBack;
    RadioButton radio_detour_miles;
    RadioButton radio_detour_time;
    Button btnAdvanced;
    LinearLayout llAdvanced;
    RadioButton radio_realtime_yes;
    RadioButton radio_realtime_no;
    RadioButton radio_range_5;
    RadioButton radio_range_10;
    RadioButton radio_range_15;
    RadioButton radio_min_rating_1;
    RadioButton radio_min_rating_2;
    RadioButton radio_min_rating_3;
    RadioButton radio_max_detour_5miles;
    RadioButton radio_max_detour_10miles;
    RadioButton radio_choice_car;
    RadioButton radio_choice_bike;
    RadioButton radio_choice_foot;
    Button btnReset;
    Button btnAccess;
    Button btnHistory;
    String detourChoices;
    String realtime;
    int range;
    int rating;
    int maxDetourMiles;
    String transporationChoices;



    public SettingFragment() {

    }

    public static SettingFragment newInstance() {

        return new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiateView(view);
        setupListner();

    }
    public void initiateView(View view) {
        ivBack = view.findViewById(R.id.ivBack);
        radio_detour_miles = view.findViewById(R.id.radio_detour_miles);
        radio_detour_time = view.findViewById(R.id.radio_detour_time);
        btnAdvanced = view.findViewById(R.id.btnAdvanced);
        llAdvanced = view.findViewById(R.id.llAdvanced);
        radio_realtime_yes = view.findViewById(R.id.radio_realtime_yes);
        radio_realtime_no = view.findViewById(R.id.radio_realtime_no);
        radio_range_5 = view.findViewById(R.id.radio_range_5);
        radio_range_10 = view.findViewById(R.id.radio_range_10);
        radio_range_15 = view.findViewById(R.id.radio_range_15);
        radio_min_rating_1 = view.findViewById(R.id.radio_min_rating_1);
        radio_min_rating_2 = view.findViewById(R.id.radio_min_rating_2);
        radio_min_rating_3 = view.findViewById(R.id.radio_min_rating_3);
        radio_max_detour_5miles = view.findViewById(R.id.radio_max_detour_5miles);
        radio_max_detour_10miles = view.findViewById(R.id.radio_max_detour_10miles);
        radio_choice_car = view.findViewById(R.id.radio_choice_car);
        radio_choice_bike = view.findViewById(R.id.radio_choice_bike);
        radio_choice_foot = view.findViewById(R.id.radio_choice_foot);
        btnReset = view.findViewById(R.id.btnReset);
        btnAccess = view.findViewById(R.id.btnAccess);
        btnHistory = view.findViewById(R.id.btnHistory);

        settingPreference = getContext().getSharedPreferences(String.valueOf(R.string.setting_preference), MODE_PRIVATE);
        detourChoices = settingPreference.getString("detourChoices", "time");
        realtime = settingPreference.getString("realtime", "no");
        range = settingPreference.getInt("range", 5);
        rating = settingPreference.getInt("rating", 1);
        maxDetourMiles = settingPreference.getInt("maxDetourMiles", 10);
        transporationChoices = settingPreference.getString("transporationChoices", "car");

        if (detourChoices.equals("miles")) {
            radio_detour_miles.setChecked(true);
        }
        else {
            radio_detour_time.setChecked(true);
        }
        if (realtime.equals("yes")) {
            radio_realtime_yes.setChecked(true);
        }
        else {
            radio_realtime_no.setChecked(true);
        }
        if (range == 5) {
            radio_range_5.setChecked(true);
        }
        else if (range == 10) {
            radio_range_10.setChecked(true);
        }
        else {
            radio_range_15.setChecked(true);
        }
        if (rating == 1) {
            radio_min_rating_1.setChecked(true);
        }
        else if (rating == 2) {
            radio_min_rating_2.setChecked(true);
        }
        else {
            radio_min_rating_3.setChecked(true);
        }
        if (maxDetourMiles == 5) {
            radio_max_detour_5miles.setChecked(true);
        }
        else {
            radio_max_detour_10miles.setChecked(true);
        }
        if (transporationChoices.equals("car")) {
            radio_choice_car.setChecked(true);
        }
        else if (transporationChoices.equals("bike")) {
            radio_choice_bike.setChecked(true);
        }
        else {
            radio_choice_foot.setChecked(true);
        }

    }
    public void setupListner() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radio_detour_miles.isChecked()) {
                    detourChoices = "miles";
                }
                else {
                    detourChoices = "time";
                }
                settingPreference.edit().putString("detourChoices",detourChoices).commit();

                if (radio_realtime_yes.isChecked()) {
                    realtime = "yes";
                }
                else {
                    realtime = "no";
                }
                settingPreference.edit().putString("realtime",realtime).commit();

                if (radio_range_5.isChecked()) {
                    range = 5;
                }
                else if (radio_range_10.isChecked()) {
                    range = 10;
                }
                else {
                    range = 15;
                }
                settingPreference.edit().putInt("range",range).commit();

                if (radio_min_rating_1.isChecked()) {
                    rating = 1;
                }
                else if (radio_min_rating_2.isChecked()) {
                    rating = 2;
                }
                else {rating = 3;}
                settingPreference.edit().putInt("rating",rating).commit();

                if (radio_max_detour_5miles.isChecked()) {
                    maxDetourMiles = 5;
                }
                else {
                    maxDetourMiles = 10;
                }
                settingPreference.edit().putInt("maxDetourMiles",maxDetourMiles).commit();

                if (radio_choice_car.isChecked()) {
                    transporationChoices = "car";
                }
                else if (radio_choice_bike.isChecked()) {
                    transporationChoices = "bike";
                }
                else {
                    transporationChoices = "foot";
                }
                settingPreference.edit().putString("transporationChoices",transporationChoices).commit();


                dismiss();
            }
        });

        btnAdvanced.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnAdvanced.getText().toString().equals("show advanced features")){
                        llAdvanced.setVisibility(View.VISIBLE);
                        btnAdvanced.setText("hide advanced features");}
                    else {
                        llAdvanced.setVisibility(View.INVISIBLE);
                        btnAdvanced.setText("show advanced features");
                    }


                }
            });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingPreference.edit().clear().commit();
                radio_detour_time.setChecked(true);
                radio_realtime_no.setChecked(true);
                radio_range_5.setChecked(true);
                radio_min_rating_1.setChecked(true);
                radio_max_detour_10miles.setChecked(true);
                radio_choice_car.setChecked(true);

            }
        });
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "please grant access", Toast.LENGTH_LONG).show();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "your history is erased", Toast.LENGTH_LONG).show();
            }
        });
    }
 /*   public void onDetourChoicesClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        String detourChoices = "";
        switch(view.getId()) {
            case R.id.radio_detour_miles:
                if (checked)
                    detourChoices = "miles";
                    break;
            case R.id.radio_detour_time:
                if (checked)
                    detourChoices = "time";
                    break;
        }
        settingPreference.edit().putString("detourChoices",detourChoices).commit();
    }

    public void onRealtimeClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        String realtime = "";
        switch(view.getId()) {
            case R.id.radio_realtime_yes:
                if (checked)
                    realtime = "yes";
                break;
            case R.id.radio_realtime_no:
                if (checked)
                    realtime = "no";
                break;
        }
        settingPreference.edit().putString("realtime",realtime).commit();
    }

    public void onRangeClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int  range = 0;
        switch(view.getId()) {
            case R.id.radio_range_5:
                if (checked)
                    range = 5;
                break;
            case R.id.radio_range_10:
                if (checked)
                    range = 10;
                break;
            case R.id.radio_range_15:
                if (checked)
                    range = 15;
                break;
        }
        settingPreference.edit().putInt("range",range).commit();
    }
    public void onRatingClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int  rating = 0;
        switch(view.getId()) {
            case R.id.radio_min_rating_1:
                if (checked)
                    rating = 5;
                break;
            case R.id.radio_min_rating_2:
                if (checked)
                    rating = 2;
                break;
            case R.id.radio_min_rating_3:
                if (checked)
                    rating = 3;
                break;
        }
        settingPreference.edit().putInt("rating",rating).commit();
    }

    public void onMaxDetourClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int  maxDetourMiles = 0;
        switch(view.getId()) {
            case R.id.radio_max_detour_5miles:
                if (checked)
                    maxDetourMiles = 5;
                break;
            case R.id.radio_max_detour_10miles:
                if (checked)
                    maxDetourMiles = 10;
                break;
        }
        settingPreference.edit().putInt("maxDetourMiles",maxDetourMiles).commit();
    }

    public void onTransportationClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        String transporationChoices = "";
        switch(view.getId()) {
            case R.id.radio_choice_car:
                if (checked)
                    transporationChoices = "car";
                break;
            case R.id.radio_choice_bike:
                if (checked)
                    transporationChoices = "bike";
                break;
            case R.id.radio_choice_foot:
                if (checked)
                    transporationChoices = "foot";
                break;
        }
        settingPreference.edit().putString("transporationChoices",transporationChoices).commit();
    }*/

}
