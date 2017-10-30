package com.codepath.enroute.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import com.codepath.enroute.Manifest;
import com.codepath.enroute.R;
import com.codepath.enroute.connection.GoogleClient;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.databinding.ActivitySearchBinding;
import com.codepath.enroute.fragments.SettingFragment;
import com.codepath.enroute.models.Direction;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.R.id.input;
import static java.security.AccessController.getContext;

@RuntimePermissions
public class SearchActivity extends AppCompatActivity {

    public static final String KEY_DIRECTIONS = "Directions";
    public static final String KEY_RESPONSE_JSON = "JSON_RESPONSE";
    private ActivitySearchBinding mBinding;
    private String fromLocation = "";
    private String toLocation = "";
    private AutoCompleteTextView etToLocation;
    private AutoCompleteTextView etFromLocation;

    static final String KEY_FROM_LAT = "FROM_LAT";
    static final String KEY_TO_LAT = "TO_LAT";
    static final String KEY_FROM_LNG = "FROM_LNG";
    static final String KEY_TO_LNG = "TO_LNG";

    static final String KEY_ORIGIN = "ORIGIN";
    static final String KEY_DESTINATION = "DESTINATION";
    private final GoogleClient googleClient = GoogleClient.getInstance();
    private FusedLocationProviderClient mFusedLocationClient;
    SharedPreferences settingPreference;
    private Set<String> toHistory;
    private Set<String> fromHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        YelpClient.getInstance();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setContentView(R.layout.activity_search);
        etToLocation = findViewById(R.id.etTo);
        etFromLocation = findViewById(R.id.etFrom);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setUpViews();
    }

    private void setUpViews() {
        // TODO: Change it to get text from binding
        settingPreference = getSharedPreferences(String.valueOf(R.string.setting_preference), MODE_PRIVATE);
        toHistory = settingPreference.getStringSet("toHistory", new HashSet());
        fromHistory = settingPreference.getStringSet("fromHistory", new HashSet());
        fromSetAutoCompleteSource();
        toSetAutoCompleteSource();
        etFromLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etFromLocation.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etFromLocation.setError("Please enter location");
                }
            }
        });
        etToLocation.requestFocus();
        etToLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                etToLocation.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    etToLocation.setError("Please enter location");
                }
            }
        });
        etToLocation.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    SearchActivityPermissionsDispatcher.getCurrentLocationOfUserWithCheck(SearchActivity.this);
                    return true;
                }
                return false;
            }
        });



    }

    /*
    * Click Listener for Search button. Opens the map view on click.
    *
    * */

    private void fromSetAutoCompleteSource() {
//        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.etInput);
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, fromHistory.toArray(new String[fromHistory.size()]));
        etFromLocation.setAdapter(fromAdapter);
    }

    private void toSetAutoCompleteSource() {
//        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.etInput);
        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, toHistory.toArray(new String[toHistory.size()]));
        etToLocation.setAdapter(toAdapter);
    }


    public void onButtonSearch(View view) {
        fromLocation = etFromLocation.getEditableText().toString();
        toLocation = etToLocation.getEditableText().toString();
        SearchActivityPermissionsDispatcher.getCurrentLocationOfUserWithCheck(this);
    }

    private void toSavePrefs() {

        settingPreference.edit().putStringSet("toHistory", toHistory).commit();

    }

    private void fromSavePrefs() {

        settingPreference.edit().putStringSet("fromHistory", fromHistory).commit();

    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void getCurrentLocationOfUser() {
        if(fromLocation.equals("Current Location")) {
            // Get location from FusedLocationClient.
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                validateAddressAndGetDirections(location);
                            }
                        }
                    });
        } else {
            validateAddressAndGetDirections(null);
        }
    }

    private void validateAddressAndGetDirections(Location originLocation) {
        if (toLocation.equalsIgnoreCase(fromLocation)) {
            setError("Please enter different addresses");
            return;
        }
        RequestParams params = new RequestParams();
        if (originLocation == null) {
            params.add("origin", fromLocation);
        } else {
            params.add("origin", originLocation.getLatitude() + "," + originLocation.getLongitude());
        }
        params.add("destination", toLocation);
        final GoogleClient googleClient = GoogleClient.getInstance();
        googleClient.getDirections(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("PlacesActivity", "Response from Google for directions: " + response.toString());
                try {
                    String result = "";
                    result = Direction.fromJson(response);
                    if (result == null || result.isEmpty()) {
                        // Report error
                        setError("both");
                    } else if (result.equals("Invalid From")) {
                        setError("from");
                    } else if (result.equals("Invalid To")) {
                        setError("to");
                    } else {
                        sendToMap(result, response);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("PlacesActivity", "Response from Google for directions: JSON Array - " + response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("PlacesActivity", "Response from Google for directions: Str -" + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("ERROR:" + this.getClass().toString(), "Invalid address. " + throwable.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR:" + this.getClass().toString(), "Invalid address. " + errorResponse.toString());
                setError("both");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("ERROR:" + this.getClass().toString(), "Invalid address. " + throwable.toString());
            }
        });
    }

    private void sendToMap(String encodedPolyLine, JSONObject jsonResponse) {
        if (!toHistory.contains(toLocation)) {
            toHistory.add(toLocation);
            toSavePrefs();
            toSetAutoCompleteSource();
        }
        if (!fromHistory.contains(fromLocation)) {
            fromHistory.add(fromLocation);
            fromSavePrefs();
            fromSetAutoCompleteSource();
        }
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra(KEY_DIRECTIONS, encodedPolyLine);
        intent.putExtra(KEY_RESPONSE_JSON, jsonResponse.toString());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setError(String error) {
        if (error.equals("both")) {
            etToLocation.setError("Invalid address");
            etFromLocation.setError("Invalid address");
        } else if (error.equals("from")) {
            etFromLocation.setError("Invalid address");
        } else if (error.equals("to")) {
            etToLocation.setError("Invalid address");
        } else {
            etToLocation.setError(error);
        }

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
        // as you specify yelpBusinessArrayList parent activity in AndroidManifest.xml.
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
