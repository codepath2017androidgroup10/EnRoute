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

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.Toast;

import com.codepath.enroute.Manifest;
import com.codepath.enroute.R;
import com.codepath.enroute.adapters.PlaceAutocompleteAdapter;
import com.codepath.enroute.connection.GoogleClient;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.databinding.ActivitySearchBinding;
import com.codepath.enroute.fragments.SettingFragment;
import com.codepath.enroute.models.Direction;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import static com.codepath.enroute.R.id.autocomplte_to_place;
import static java.security.AccessController.getContext;


@RuntimePermissions
public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "SearchActivity";
    public static final String KEY_DIRECTIONS = "Directions";
    public static final String KEY_RESPONSE_JSON = "JSON_RESPONSE";
    private ActivitySearchBinding mBinding;
    private String fromLocation = "";
    private String toLocation = "";
    //private AutoCompleteTextView etToLocation;
    //private AutoCompleteTextView etFromLocation;

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
 //   SupportPlaceAutocompleteFragment autocompleteFragment;

    protected GoogleApiClient mGoogleApiClient;

    ArrayAdapter<String> fromAdapter;
    ArrayAdapter<String> toAdapter;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(37.3639514,-121.9311315 ), new LatLng(37.6213171, -122.3811494));


    private PlaceAutocompleteAdapter mFromAdapter;
    private PlaceAutocompleteAdapter mToAdapter;
    private AutoCompleteTextView mAutocompleteViewFrom;
    private AutoCompleteTextView mAutocompleteViewTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();



        Fabric.with(this, new Crashlytics());
        YelpClient.getInstance();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setContentView(R.layout.activity_search);

        mAutocompleteViewFrom = (AutoCompleteTextView)
                findViewById(R.id.autocomplte_from_place);
        mAutocompleteViewTo = (AutoCompleteTextView)
                findViewById(R.id.autocomplte_to_place);

        //etToLocation = findViewById(R.id.etTo);
        //etFromLocation = findViewById(R.id.etFrom);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setUpViews();

//        autocompleteFragment = new SupportPlaceAutocompleteFragment();
//        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
//
//        fm.beginTransaction().replace(R.id.autoComplete,autocompleteFragment).commit();
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//        });



        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteViewFrom.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewTo.setOnItemClickListener(mAutocompleteClickListener);

        mAutocompleteViewFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){

                    if (mAutocompleteViewFrom.getText().length()==0){
                        mAutocompleteViewFrom.setAdapter(fromAdapter);
                    }

                    ((AutoCompleteTextView)v).showDropDown();
                }
            }
        });

        mAutocompleteViewTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){

                    if (mAutocompleteViewTo.getText().length()==0){
                        mAutocompleteViewTo.setAdapter(toAdapter);
                    }

                    ((AutoCompleteTextView)v).showDropDown();
                }
            }
        });

        mFromAdapter = new PlaceAutocompleteAdapter(SearchActivity.this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);


        mAutocompleteViewFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("DEBUGME",s+":"+start+":"+count+":"+after);
                if (count>1) {
                    return;
                }
                if (s.length()>3)  {
                    if (!mAutocompleteViewFrom.getAdapter().equals(mFromAdapter)) {
                        mAutocompleteViewFrom.setAdapter(mFromAdapter);
                    }

                }else if (s.length()==0){
                    mAutocompleteViewFrom.setAdapter(fromAdapter);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s==null || s.length()==0){
                    mAutocompleteViewFrom.setAdapter(fromAdapter);
                }
            }
        });




        mToAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);


        mAutocompleteViewTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count>1) {
                    return;
                }
                if (s.length()>3) {
                    if (!mAutocompleteViewTo.getAdapter().equals(mToAdapter)) {
                        mAutocompleteViewTo.setAdapter(mToAdapter);
                    }else if (s.length()==0){
                        mAutocompleteViewTo.setAdapter(toAdapter);
                    }

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s==null || s.length()==0){
                    mAutocompleteViewTo.setAdapter(toAdapter);
                }
            }
        });
    }

    private void setUpViews() {
        // TODO: Change it to get text from binding

        settingPreference = getSharedPreferences(String.valueOf(R.string.setting_preference), MODE_PRIVATE);
        //settingPreference = getPreferences( MODE_PRIVATE);
        toHistory = settingPreference.getStringSet("toHistory", new HashSet());
        fromHistory = settingPreference.getStringSet("fromHistory", new HashSet());
        fromSetAutoCompleteSource();
        toSetAutoCompleteSource();
//        etFromLocation.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                etFromLocation.setError(null);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.toString().isEmpty()) {
//                    etFromLocation.setError("Please enter location");
//                }
//            }
//        });
//        etToLocation.requestFocus();
//        etToLocation.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                etToLocation.setError(null);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (editable.toString().isEmpty()) {
//                    etToLocation.setError("Please enter location");
//                }
//            }
//        });
//        etToLocation.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    SearchActivityPermissionsDispatcher.getCurrentLocationOfUserWithCheck(SearchActivity.this);
//                    return true;
//                }
//                return false;
//            }
//        });



    }

    /*
    * Click Listener for Search button. Opens the map view on click.
    *
    * */

    private void fromSetAutoCompleteSource() {
        fromAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, fromHistory.toArray(new String[fromHistory.size()]));
        //etFromLocation.setAdapter(fromAdapter);
        mAutocompleteViewFrom.setAdapter(fromAdapter);
    }

    private void toSetAutoCompleteSource() {
        toAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, toHistory.toArray(new String[toHistory.size()]));
        //etToLocation.setAdapter(toAdapter);
        mAutocompleteViewTo.setAdapter(toAdapter);
    }


    public void onButtonSearch(View view) {
//        fromLocation = etFromLocation.getEditableText().toString();
//        toLocation = etToLocation.getEditableText().toString();
        fromLocation = mAutocompleteViewFrom.getEditableText().toString();
        toLocation = mAutocompleteViewTo.getEditableText().toString();
        SearchActivityPermissionsDispatcher.getCurrentLocationOfUserWithCheck(this);
    }

    private void toSavePrefs() {

        SharedPreferences.Editor editor = settingPreference.edit();

        editor.putStringSet("toHistory", toHistory);
        editor.apply();
        //editor.commit();

    }

    private void fromSavePrefs() {
        SharedPreferences.Editor editor = settingPreference.edit();
        editor.putStringSet("fromHistory", fromHistory);
        editor.apply();
        //editor.commit();

    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void getCurrentLocationOfUser() {
        if(!fromLocation.equals("Current Location")) {
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
//            etToLocation.setError("Invalid address");
//            etFromLocation.setError("Invalid address");
            mAutocompleteViewFrom.setError("Invalid address");
            mAutocompleteViewTo.setError("Invalid address");
        } else if (error.equals("from")) {
            //etFromLocation.setError("Invalid address");
            mAutocompleteViewFrom.setError("Invalid address");
        } else if (error.equals("to")) {
            //etToLocation.setError("Invalid address");
            mAutocompleteViewTo.setError("Invalid address");
        } else {
            //etToLocation.setError(error);
            mAutocompleteViewTo.setError("Invalid address");
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("SEARCH ACTIVITY", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */

            if (parent.getAdapter() instanceof PlaceAutocompleteAdapter) {

            final AutocompletePrediction item = (AutocompletePrediction)parent.getAdapter().getItem(position);
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

                Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
            }
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

//            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));

            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

}
