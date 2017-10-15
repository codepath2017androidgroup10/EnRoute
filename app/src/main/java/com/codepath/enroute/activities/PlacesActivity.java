package com.codepath.enroute.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.enroute.Manifest;
import com.codepath.enroute.R;
import com.codepath.enroute.connection.GoogleClient;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.models.Direction;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.MapUtil;
import com.codepath.enroute.models.PointEnRoute;
import com.codepath.enroute.util.MapUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


/*
* Activity with the map view showing route between current location and destination
*
* */
@RuntimePermissions
public class PlacesActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LatLng mCurrentLatLng;
    private long UPDATE_INTERVAL = 60000 * 3;  /* 60 secs  * 3 */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static String KEY_LOCATION = "location";
    private final static String KEY_POINTS_OF_INTEREST = "points_of_interest";

    private LatLng testLatLng = new LatLng(37.37, -122.03); // TODO: Get location from previous activity through intent
    private List<LatLng> directionPoints;

    //This should contain a list of Points Of Interest;
    private Map<LatLng,YelpBusiness> mPointsOfInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }

        if(savedInstanceState!= null && savedInstanceState.keySet().contains(KEY_POINTS_OF_INTEREST)){
            mPointsOfInterest = savedInstanceState.getParcelable(KEY_POINTS_OF_INTEREST);
        }else{
            mPointsOfInterest = new HashMap<>();
        }
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Log.e(this.getClass().toString(), "Error - Map Fragment was null!!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_places, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            Log.d(this.getClass().toString(), "GPS location was found!");
            mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            zoomToLocation();
        } else {
            Log.e(this.getClass().toString(), "Current location was null, enable GPS on emulator!");
        }
        PlacesActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_LOCATION, mCurrentLocation);
        //outState.putParcelable(KEY_POINTS_OF_INTEREST,mPointsOfInterest);
        super.onSaveInstanceState(outState);
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Log.d(this.getClass().toString(), "Map Fragment was loaded properly!");
            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            Marker toMarker = MapUtil.addMarker(map, testLatLng, "Sunnyvale Caltrain station", "Train station", defaultMarker);
            PlacesActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            PlacesActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
        } else {
            Log.e(this.getClass().toString(), "Error - Map was null!!");
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        if (map != null) {
            // Disbled inspection for missing permission
            map.setMyLocationEnabled(true);
            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
            locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        onLocationChanged(location);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(this.getClass().toString(), "Error trying to get last gps location");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PlacesActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setInterval(UPDATE_INTERVAL);
        // mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        mCurrentLocation = location;
        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Marker fromMarker = MapUtil.addMarker(map, mCurrentLatLng, "Current Location", "", defaultMarker);
        RequestParams params = new RequestParams();

        params.add("origin", location.getLatitude() + "," + location.getLongitude());
        params.add("destination", testLatLng.latitude + "," + testLatLng.longitude);

        final GoogleClient googleClient = GoogleClient.getInstance();


        zoomToLocation();

        googleClient.getDirections(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("PlacesActivity", "Response from Google for directions: " + response.toString());
                try {
                    directionPoints = Direction.fromJson(response);
                    drawDirections();


                    //TESTME Jim
                    List<LatLng> googlePoints = MapUtil.getLatLngFromOverView(response, 1609);
                    //The following is an example how to use YelpApi.
                    YelpClient client = YelpClient.getInstance();
                    RequestParams params = new RequestParams();
                    params.put("term", "food");
                    params.put("radius", 1000);
                    for (int i = 0; i < googlePoints.size(); i++) {

                        params.put("latitude", googlePoints.get(i).latitude);
                        params.put("longitude", googlePoints.get(i).longitude);
                        client.getSearchResult(params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);

                                try {
                                    JSONArray yelpBusinesses = response.getJSONArray("businesses");
                                    BitmapDescriptor icon =
                                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                                    for (int i = 0; i < yelpBusinesses.length(); i++) {
                                        YelpBusiness aYelpBusiness = YelpBusiness.fromJson(yelpBusinesses.getJSONObject(i));
                                        mPointsOfInterest.put(new LatLng(aYelpBusiness.getLatitude(),aYelpBusiness.getLongitude()),aYelpBusiness);
                                        MapUtil.addMarker(map, new LatLng(aYelpBusiness.getLatitude(),aYelpBusiness.getLongitude()), aYelpBusiness.getName(), "No Description yet", icon);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                super.onSuccess(statusCode, headers, response);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                super.onSuccess(statusCode, headers, responseString);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                            }
                        });

                    }

                    //getPointsOfInterest();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("PlacesActivity", errorResponse.toString());
                //TODO: handle failure
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

    private void getPointsOfInterest() {
        YelpClient yelpClient = YelpClient.getInstance();
//        RequestParams params = new RequestParams();
//        params.put("latitude", mCurrentLocation.getLatitude());
//        params.put("longitude", mCurrentLocation.getLongitude());
        BitmapDescriptor icon =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        List<PointEnRoute> pointsOfInterestList = yelpClient.getPointsOfInterestEnRoute();
        for (PointEnRoute point : pointsOfInterestList) {
            MapUtil.addMarker(map, point.getLatLng(), point.getNameOfPlace(), point.getDescription(), icon);
        }

    }

    private void drawDirections() {
        PolylineOptions lineOptions = new PolylineOptions();
        for (LatLng latLng : directionPoints) {
            lineOptions.add(latLng);
        }
        map.addPolyline(lineOptions);
    }

    /*
    * Click listener for tool bar menu item to switch to list view
    *
    * */
    public void onClickSwitchView(MenuItem item) {
        Log.d(this.getClass().toString(), "Switching to List view");
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(intent);
    }

    private void zoomToLocation() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 15);
        map.animateCamera(cameraUpdate);
    }
}
