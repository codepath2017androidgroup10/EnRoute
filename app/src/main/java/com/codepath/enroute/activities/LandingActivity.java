package com.codepath.enroute.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.codepath.enroute.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_landing);
    }

    /*
    * Click Listener for Search button. Opens the map view on click.
    *
    * */

    public void onButtonSearch(View view) {

    }
}
