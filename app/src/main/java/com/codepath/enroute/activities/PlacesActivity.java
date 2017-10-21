package com.codepath.enroute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.codepath.enroute.R;
import com.codepath.enroute.fragments.ListFragment;
import com.codepath.enroute.fragments.PlacesMapFragment;
import com.codepath.enroute.fragments.PointsOfInterestFragment;
import com.codepath.enroute.fragments.SettingFragment;
import com.codepath.enroute.models.YelpBusiness;

import java.util.ArrayList;

/*
* Activity with the map view showing route between current location and destination
*
* */

public class PlacesActivity extends AppCompatActivity implements PlacesMapFragment.OnSearchDoneListener{

    ArrayList<YelpBusiness> yelpBusinessArrayList;

    final String[] searchTerm=new String[1];
    String keyReponseJSON;
    String keyDirection;

//    PlacesMapFragment placesMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        keyReponseJSON = bundle.getString(SearchActivity.KEY_RESPONSE_JSON);
        keyDirection = bundle.getString(SearchActivity.KEY_DIRECTIONS);

        PlacesMapFragment placesMapFragment = PlacesMapFragment.newInstance(bundle.getString(SearchActivity.KEY_RESPONSE_JSON), bundle.getString(SearchActivity.KEY_DIRECTIONS));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeHolder, placesMapFragment);
        ft.commit();

//        try {
//            directionsJson = new JSONObject(bundle.getString(SearchActivity.KEY_RESPONSE_JSON));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        directionPoints = MapUtil.decodePolyLine(bundle.getString(SearchActivity.KEY_DIRECTIONS));

//        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
//            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
//            // is not null.
//            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//            mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//        }
//
//        if(savedInstanceState!= null && savedInstanceState.keySet().contains(KEY_POINTS_OF_INTEREST)){
//            mPointsOfInterest = savedInstanceState.getParcelable(KEY_POINTS_OF_INTEREST);
//        }else{
//            mPointsOfInterest = new HashMap<>();
//        }
//        // Get location here and get directions:
//
//        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(GoogleMap map) {
//                    loadMap(map);
//                }
//            });
//        } else {
//            Log.e(this.getClass().toString(), "Error - Map Fragment was null!!");
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_places, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here



                searchTerm[0] = query;
                searchView.clearFocus();
                //Intent i = new Intent(getApplicationContext(), DetailActivity.class);
 //               i.putExtra("q", query);
                PointsOfInterestFragment aFragment = (PointsOfInterestFragment)getSupportFragmentManager().findFragmentById(R.id.placeHolder);
                aFragment.setSearchTerm(query);
                aFragment.getYelpBusinesses();
                //startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    /*
    * Click listener for tool bar menu item to switch to list view
    *
    * */
    public void onClickSwitchView(MenuItem item) {
        Log.d(this.getClass().toString(), "Switching to List view");

//        ListFragment placesListFragment = ListFragment.newInstance(yelpBusinessArrayList);
//        ListFragment placesListFragment = ListFragment.newInstance(keyReponseJSON,keyDirection);
        ListFragment placesListFragment = ListFragment.newInstance(yelpBusinessArrayList, keyReponseJSON, keyDirection);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeHolder, placesListFragment);
        //ft.add(placesListFragment, "list_fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        // Display the connection status
//
//        if (mCurrentLocation != null) {
//            Log.d(this.getClass().toString(), "GPS location was found!");
//            mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//            zoomToLocation();
//        } else {
//            Log.e(this.getClass().toString(), "Current location was null, enable GPS on emulator!");
//        }
//        PlacesActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void notifyActivity(ArrayList<YelpBusiness> list) {
        yelpBusinessArrayList = list;
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

    /*
    *  Click handler for FAB
    * */

    boolean areOptionsShown = false;

    public void onFABClick(View view) {
        Log.d("FAB", "CLicked");
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_food);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_gas);

        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        //layoutParams1.rightMargin += (int) (fab2.getWidth() * 1.7);
        layoutParams1.bottomMargin += (int) (fab2.getHeight() * 1.7);
        fab2.setLayoutParams(layoutParams1);

        if (!areOptionsShown) {
            // Show menu items
            Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
            fab1.startAnimation(show_fab_1);
            fab1.setClickable(true);
            Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
            fab2.startAnimation(show_fab_2);
            fab2.setClickable(true);
            areOptionsShown = true;
        } else {
            // hide menu items
            Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
            fab1.startAnimation(hide_fab_1);
            fab1.setClickable(false);

            Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
            fab2.startAnimation(hide_fab_2);
            fab2.setClickable(false);

            layoutParams.rightMargin = (int) (fab1.getWidth());
            layoutParams.bottomMargin = (int) (fab1.getHeight());

            layoutParams1.bottomMargin = (int) (fab2.getHeight());
            areOptionsShown = false;
        }
    }

    public void onFoodMenuClicked(View view) {
        Log.d("DEBUG:", "Food Menu clicked");
        PointsOfInterestFragment aFragment = (PointsOfInterestFragment)getSupportFragmentManager().findFragmentById(R.id.placeHolder);
        aFragment.setSearchTerm("food");
        aFragment.getYelpBusinesses();
    }

    public void onGasMenuClicked(View view) {
        Log.d("DEBUG:", "Gas Menu clicked");
        PointsOfInterestFragment aFragment = (PointsOfInterestFragment)getSupportFragmentManager().findFragmentById(R.id.placeHolder);
        aFragment.setSearchTerm("gas");
        aFragment.getYelpBusinesses();
    }
}

