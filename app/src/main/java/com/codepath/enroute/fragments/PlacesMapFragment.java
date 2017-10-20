package com.codepath.enroute.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.enroute.Manifest;
import com.codepath.enroute.R;
import com.codepath.enroute.activities.DetailActivity;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.MapUtil;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.codepath.enroute.util.MapUtil.addMarker;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Maps Fragment class extending {@link Fragment} class.

 */
@RuntimePermissions
public class PlacesMapFragment extends PointsOfInterestFragment implements GoogleMap.OnMarkerClickListener{

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LatLng mCurrentLatLng;

    private List<LatLng> directionPoints;
    private MapView mapView;
    OnSearchDoneListener listener;

    public PlacesMapFragment() {
        // Required empty public constructor
    }

    public interface OnSearchDoneListener {
       public void notifyActivity(ArrayList<YelpBusiness> list);
    }

    public static PlacesMapFragment newInstance(String directionsJson, String encodedPolyLine) {
        PlacesMapFragment placesMapFragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putString("directionsJson", directionsJson);
        args.putString("points", encodedPolyLine);
        placesMapFragment.setArguments(args);
        return placesMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_placesmap, container, false);
        try {
            directionsJson = new JSONObject(getArguments().getString("directionsJson"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        directionPoints = MapUtil.decodePolyLine(getArguments().getString("points"));
        setUpViews(v, savedInstanceState);
        // Inflate the layout for this fragment
        return v;
    }

    private View setUpViews(View v, Bundle savedInstanceState) {
        mapView = (MapView) v.findViewById(R.id.mapview);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    map.getUiSettings().setMyLocationButtonEnabled(false);

                    loadMap(map);

                }
            });
        } else {
            Log.e(this.getClass().toString(), "Error - Map Fragment was null!!");
        }
        return v;
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        if (map != null) {
            // Map is ready
            map.setMyLocationEnabled(true);
            MapsInitializer.initialize(getContext());
            mCurrentLatLng = directionPoints.get(0);
            Log.d(this.getClass().toString(), "Map Fragment was loaded properly!");
            mCurrentLocation = new Location("");
            mCurrentLocation.setLatitude(mCurrentLatLng.latitude);
            mCurrentLocation.setLongitude(mCurrentLatLng.longitude);
            onLocationChanged(mCurrentLocation);
            PlacesMapFragmentPermissionsDispatcher.startLocationUpdatesWithCheck(this);
        } else {
            Log.e(this.getClass().toString(), "Error - Map was null!!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PlacesMapFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
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
        Marker fromMarker = addMarker(map, mCurrentLatLng, "Current Location", "", defaultMarker);
        zoomToLocation();
        drawDirections();
        //getYelpBusinesses(directionsJson);
        getYelpBusinesses();
    }

    @Override
    public void postYelpSearch() {
        BitmapDescriptor marker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        markBusinesses(marker);
       listener.notifyActivity(yelpBusinessList);
    }

    private void markBusinesses(BitmapDescriptor marker) {
        for (Map.Entry<LatLng, YelpBusiness> poi : mPointsOfInterest.entrySet()) {
            Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), marker);
            aMarker.setTag(poi.getValue());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnSearchDoneListener) context;
    }

    private void drawDirections() {
        Log.d("DEBUG", "Drawing directions");
        PolylineOptions lineOptions = new PolylineOptions();
        for (LatLng latLng : directionPoints) {
            lineOptions.add(latLng);
        }
        map.addPolyline(lineOptions);



        // Add marker for destination
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        Marker toMarker = addMarker(map, directionPoints.get(directionPoints.size() - 1), "", "", defaultMarker);
    }

    private void zoomToLocation() {

        double distance = distance(directionPoints.get(0).latitude,
                directionPoints.get(0).longitude,
                directionPoints.get(directionPoints.size()-1).latitude,
                directionPoints.get(directionPoints.size()-1).longitude);

        int zoomLevel = 15;

        //ZoomLevel
        //1 world
        //5 continent
        //10 city
        //15 street
        //20 buildings

        //1000,500,200,100,50,20,10,5,2,


        if (distance<0.2){
            zoomLevel = 15;
        }else if (distance<0.5){
            zoomLevel = 14;
        }else if (distance<1){
            zoomLevel = 13;
        }else if (distance<2){
            zoomLevel = 12;
        }else if (distance<5){
            zoomLevel = 11;
        }else if (distance<10){
            zoomLevel = 10;
        }else if (distance<20){
            zoomLevel = 9;
        }else if (distance<50){
            zoomLevel = 8;
        }else if (distance<100){
            zoomLevel = 7;
        }else if (distance<200){
            zoomLevel = 6;
        }else if (distance< 500) {
            zoomLevel = 5;
        }else if (distance< 1000){
            zoomLevel =  4;
        }else if (distance< 2000){
            zoomLevel = 3;
        }else if (distance<5000){
            zoomLevel = 2;
        }else if (distance<10000){
            zoomLevel =1;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, zoomLevel);
        map.animateCamera(cameraUpdate);
    }

    //The return value is distance in Miles.
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        //dist = dist * 1.609344;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        YelpBusiness aYelpBusiness = (YelpBusiness) marker.getTag();
        if (aYelpBusiness == null) {
            return false;
        }else{
            Intent detailActivity = new Intent(getContext(), DetailActivity.class);
            detailActivity.putExtra("YELP_BUSINESS", Parcels.wrap(aYelpBusiness));
            startActivity(detailActivity);
            return true;
        }
    }
}
