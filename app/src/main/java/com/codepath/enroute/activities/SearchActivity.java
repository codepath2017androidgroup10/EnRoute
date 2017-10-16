package com.codepath.enroute.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.codepath.enroute.R;
import com.codepath.enroute.connection.GoogleClient;
import com.codepath.enroute.databinding.ActivitySearchBinding;
import com.codepath.enroute.fragments.SettingFragment;
import com.codepath.enroute.util.PlacesUtil;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding mBinding;
    private String fromLocation = "";
    private String toLocation = "";
    private EditText etToLocation;
    private EditText etFromLocation;

    static final String KEY_FROM_LAT = "FROM_LAT";
    static final String KEY_TO_LAT = "TO_LAT";
    static final String KEY_FROM_LNG = "FROM_LNG";
    static final String KEY_TO_LNG = "TO_LNG";
    private final GoogleClient googleClient = GoogleClient.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        YelpClient.getInstance();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setContentView(R.layout.activity_search);
        setUpViews();
    }

    private void setUpViews() {
        // TODO: Change it to get text from binding
        etFromLocation = (EditText) findViewById(R.id.etFrom);
        etFromLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etFromLocation.setError("Please enter a location");
                }
            }
        });


        etToLocation = (EditText) findViewById(R.id.etTo);
        etToLocation.requestFocus();
        etToLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etToLocation.setError("Please enter a location");
                }
            }
        });

    }

    /*
    * Click Listener for Search button. Opens the map view on click.
    *
    * */

    public void onButtonSearch(View view) {
        fromLocation = etFromLocation.getEditableText().toString();
        toLocation = etToLocation.getEditableText().toString();
        Log.d("vvv: To location - " , toLocation);
        RequestParams params = new RequestParams();
        params.add("address",toLocation);
        final LatLng[] destinationCoordinates = new LatLng[1];
        googleClient.getLocationCoordinates(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(this.getClass().toString(), response.toString());
                try {
                    destinationCoordinates[0] = PlacesUtil.getCoordinatesOfPlaceFromJson(response);
                    sendToMap(destinationCoordinates);
                } catch (JSONException e) {
                    Log.e("ERROR", "Failed to get location coordinates", e);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(this.getClass().toString(), errorResponse.toString());
                setError();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }

    private void sendToMap(LatLng[] destinationCoordinates) {

        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra(KEY_TO_LAT, destinationCoordinates[0].latitude);
        intent.putExtra(KEY_TO_LNG, destinationCoordinates[0].longitude);
        startActivity(intent);
    }

    private void setError() {
        etToLocation.setError("Invalid address");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingFragment settingFragment = SettingFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            settingFragment.show(fm, "fragment_setting");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
