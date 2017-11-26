package com.codepath.enroute.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.enroute.Manifest;
import com.codepath.enroute.R;
import com.codepath.enroute.activities.DetailActivity;
import com.codepath.enroute.adapters.CustomInfoWindowAdapter;
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
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Maps Fragment class extending {@link Fragment} class.

 */
@RuntimePermissions
public class PlacesMapFragment extends PointsOfInterestFragment implements GoogleMap.OnMarkerClickListener{

    private static final String KEY_LOCATION = "location";
    private static final String KEY_POINTS_OF_INTEREST = "poi";
    private static final String KEY_ZOOM_LEVEL = "zoom_level" ;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LatLng mCurrentLatLng;

    //TODO this is a hack to get the right zoom level.
    private static float mZoomLevel;
    final float[] zoomLevel = new float[1];
    float zoomLevelToSave = 15;

//    private List<YelpBusiness> mList;

    private List<LatLng> directionPoints;
    private MapView mapView;
    OnSearchDoneListener listener;
    Context mContext;

    public PlacesMapFragment() {
        // Required empty public constructor
    }

    public interface OnSearchDoneListener {
       public void notifyActivity(ArrayList<YelpBusiness> list);
    }



    public void setBusinessList(ArrayList<YelpBusiness> list) {
        yelpBusinessList = list;
    }

    public static PlacesMapFragment newInstance(String directionsJson, String encodedPolyLine) {
        PlacesMapFragment placesMapFragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putString("directionsJson", directionsJson);
        args.putString("points", encodedPolyLine);
        placesMapFragment.setArguments(args);
        return placesMapFragment;
    }

    public static PlacesMapFragment newInstance(ArrayList<YelpBusiness> list, String directionsJson, String encodedPolyLine) {
        PlacesMapFragment placesMapFragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putString("directionsJson", directionsJson);
        args.putString("points", encodedPolyLine);
        placesMapFragment.setArguments(args);
        placesMapFragment.yelpBusinessList = list;
        return placesMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (savedInstanceState != null) {
            zoomLevelToSave = savedInstanceState.getFloat(KEY_ZOOM_LEVEL);
            Log.d("PMF onCreate", "zoomLevel " + zoomLevelToSave);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            zoomLevelToSave = savedInstanceState.getFloat(KEY_ZOOM_LEVEL);
            Log.d("PMF onActivityCreated", "zoomLevel " + zoomLevelToSave);
        }
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Log.d("PMF onActivityCreated", "cur latlng  " + mCurrentLocation.getLatitude() + mCurrentLocation.getLongitude());
        }

        if(savedInstanceState!= null && savedInstanceState.keySet().contains(KEY_POINTS_OF_INTEREST)){
            mPointsOfInterest = savedInstanceState.getParcelable(KEY_POINTS_OF_INTEREST);
        }else{
            mPointsOfInterest = new HashMap<>();
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_LOCATION, mCurrentLocation);
        outState.putFloat(KEY_ZOOM_LEVEL, zoomLevelToSave);
        super.onSaveInstanceState(outState);
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
        if (map != null) {
            // Map is ready
            map.setOnMarkerClickListener(this);
            map.setMyLocationEnabled(true);
            map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    zoomLevel[0] = map.getCameraPosition().zoom;
                    Log.d("oncam idle : Zoom Level", ""+ zoomLevel[0]);
                    placeMarkersWithZoomLevel(zoomLevel[0]);
                }
            });
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
        if (mCurrentLocation != null) {
            mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            onLocationChanged(mCurrentLocation);
            PlacesMapFragmentPermissionsDispatcher.startLocationUpdatesWithCheck(this);
        }
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        mZoomLevel = map.getCameraPosition().zoom;
        super.onPause();
    }



    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
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
        mCurrentLocation = location;
        zoomToLocation();
        drawDirections(mCurrentLocation);
        // Load some location on load of the fragment.
        if (yelpBusinessList.size() == 0) {
            setSearchTerm("");
            getYelpBusinesses();
        }
    }

    @Override
    public void postYelpSearch() {
       markBusinesses();
       listener.notifyActivity(yelpBusinessList);
    }

    public void markBusinesses() {

        map.clear();
        placeMarkersWithZoomLevel(getZoomLevel());
        CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(getContext(), searchTerm);
        map.setInfoWindowAdapter(infoWindowAdapter);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                YelpBusiness aYelpBusiness = (YelpBusiness) marker.getTag();
                if (aYelpBusiness != null) {
                    Intent detailActivity = new Intent(getContext(), DetailActivity.class);
                     detailActivity.putExtra("YELP_BUSINESS", Parcels.wrap(aYelpBusiness));
                    startActivity(detailActivity);
                }
            }
        });
    }

    private void placeMarkersWithZoomLevel(float zoom) {

        map.clear();
        drawDirections(mCurrentLocation);
        BitmapDescriptor marker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);

//       if(!mPointsOfInterest.isEmpty()) {
//            for (Map.Entry<LatLng, YelpBusiness> poi : mPointsOfInterest.entrySet()) {
//
//                if (zoom < 12) {
//                    if (searchTerm.equals("gas")) {
//                        Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.teal_dot));
//                        aMarker.setTag(poi.getValue());
//                    } else if (searchTerm.equals("coffee")) {
//                        Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.orange_dot));
//                        aMarker.setTag(poi.getValue());
//                    } else {
//                        Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.orange_dot));
//                        aMarker.setTag(poi.getValue());
//                    }
////                    Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
////                    aMarker.setTag(poi.getValue());
//                } else if (zoom >= 12) {
//                    if (searchTerm.equals("gas")) {
//                        Marker aMarker = MapUtil.addGasMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), getContext());
//                        aMarker.setTag(poi.getValue());
//                    } else if (searchTerm.equals("coffee")) {
//                        Marker aMarker = MapUtil.addCoffeeMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), getContext());
//                        aMarker.setTag(poi.getValue());
//                    } else {
//                        Marker aMarker = MapUtil.addRestaurantMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), getContext());
//                        aMarker.setTag(poi.getValue());
//                    }
//                }
////                else if (zoom >= 10 && zoom < 13){
////                    Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), marker2);
////                    aMarker.setTag(poi.getValue());
////                } else if (zoom >= 13){
////                    Marker aMarker = MapUtil.addMarker(map, poi.getKey(), poi.getValue().getName(), poi.getValue().getDescription(), marker);
////                    aMarker.setTag(poi.getValue());
////                }
//            }
//        }



        if (yelpBusinessList != null && yelpBusinessList.size() > 0) {
            for (int i = 0; i < yelpBusinessList.size(); i++) {
                YelpBusiness yB = yelpBusinessList.get(i);
                if (zoom < 11) {

                    if (searchTerm.equals("gas")) {
                        //if (yB.getGasPrice()>0) {
                            Marker aMarker = MapUtil.addMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.teal_dot));
                            aMarker.setTag(yB);


                        //}
                    } else if (searchTerm.equals("coffee") || searchTerm.equals("restaurant")  ) {
                        Marker aMarker = MapUtil.addMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.orange_dot));
                        aMarker.setTag(yB);
                    } else {
                        Marker aMarker = MapUtil.addMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), BitmapDescriptorFactory.fromResource(R.drawable.orange_dot));
                        aMarker.setTag(yB);
                    }
                } else if (zoom >= 11) {
                    if (searchTerm.equals("gas")) {
                        //if (yB.getGasPrice()>0) {
                            Marker aMarker = MapUtil.addGasMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), getContext(),yB.getGasPrice());
                            aMarker.setTag(yB);
                        //}
                    } else if (searchTerm.equals("coffee") || (searchTerm.equals("tea"))) {
                        Marker aMarker = MapUtil.addCoffeeMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), getContext());
                        aMarker.setTag(yB);
                    } else if (searchTerm.equals("restaurant") || yB.isCategoryKnown(searchTerm)){
                        Marker aMarker = MapUtil.addRestaurantMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), getContext());
                        aMarker.setTag(yB);
                    } else {
                        Marker aMarker = MapUtil.addDefaultMarker(map, yB.getLatLng(), yB.getName(), yB.getDescription(), getContext());
                        aMarker.setTag(yB);
                    }
                }
            }
        }


    }

    private float getZoomLevel() {
        return map.getCameraPosition().zoom;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnSearchDoneListener) context;
        mContext = context;
    }

    private void drawDirections(Location location) {
        //Log.d("DEBUG", "Drawing directions");
        PolylineOptions lineOptions = new PolylineOptions();
        for (LatLng latLng : directionPoints) {
            lineOptions.add(latLng);
        }

        lineOptions = lineOptions.color(ContextCompat.getColor(mContext, R.color.colorPrimary));

        map.addPolyline(lineOptions);
        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getContext().getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void zoomToLocation() {



        double distance = distance(directionPoints.get(0).latitude,
                directionPoints.get(0).longitude,
                directionPoints.get(directionPoints.size()-1).latitude,
                directionPoints.get(directionPoints.size()-1).longitude);

        float zoomLevel = zoomLevelToSave;

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

        CameraUpdate cameraUpdate;
        if(mZoomLevel!=0) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, mZoomLevel);
        }else{
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, zoomLevel);
        }
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
        if (marker.getTag() != null) {
            marker.showInfoWindow();
        }
        return true;
    }





}
