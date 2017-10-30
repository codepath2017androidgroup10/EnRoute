package com.codepath.enroute.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.codepath.enroute.R;
import com.codepath.enroute.fragments.ListFragment;
import com.codepath.enroute.fragments.PlacesMapFragment;
import com.codepath.enroute.fragments.PointsOfInterestFragment;
import com.codepath.enroute.models.YelpBusiness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
* Activity with the map view showing route between current location and destination
*
* */

public class PlacesActivity extends AppCompatActivity implements PlacesMapFragment.OnSearchDoneListener, ListFragment.OnSearchDoneListener{

    ArrayList<YelpBusiness> yelpBusinessArrayList;

    final String[] searchTerm=new String[1];
    String keyReponseJSON;
    String keyDirection;
    SharedPreferences settingPreference;
    private Set<String> searchHistory;
    SearchView searchView;
    Boolean isMapFragment;
    ListFragment placesListFragment;
    PlacesMapFragment placesMapFragment;

//    PlacesMapFragment placesMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        keyReponseJSON = bundle.getString(SearchActivity.KEY_RESPONSE_JSON);
        keyDirection = bundle.getString(SearchActivity.KEY_DIRECTIONS);

        placesMapFragment = PlacesMapFragment.newInstance(bundle.getString(SearchActivity.KEY_RESPONSE_JSON), bundle.getString(SearchActivity.KEY_DIRECTIONS));
//        placesListFragment = ListFragment.newInstance(yelpBusinessArrayList, keyReponseJSON, keyDirection);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeHolder, placesMapFragment);
        ft.commit();
        isMapFragment = true;
    }
/*
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_places, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        settingPreference = getSharedPreferences(String.valueOf(R.string.setting_preference), MODE_PRIVATE);
        searchHistory = settingPreference.getStringSet("searchHistory", new HashSet());
        setAutoCompleteSource();
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
                searchView.setQuery("", false);
                searchView.clearFocus();
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
    private void setAutoCompleteSource() {
//        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.etInput);
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, searchHistory.toArray(new String[searchHistory.size()]));

    }
    /*
    * Click listener for tool bar menu item to switch to list view
    *
    * */
    public void onClickSwitchView(MenuItem item) {
        Log.d(this.getClass().toString(), "Switching to List view");

//        ListFragment placesListFragment = ListFragment.newInstance(yelpBusinessArrayList);
//        ListFragment placesListFragment = ListFragment.newInstance(keyReponseJSON,keyDirection);
        if (isMapFragment) {
//            if (placesListFragment == null) {
                if (yelpBusinessArrayList == null) {
                    yelpBusinessArrayList = new ArrayList<>();
                }
//                if (placesListFragment == null) {placesListFragment = ListFragment.newInstance(yelpBusinessArrayList, keyReponseJSON, keyDirection);}

//            }
            placesListFragment = ListFragment.newInstance(yelpBusinessArrayList, keyReponseJSON, keyDirection);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(R.id.placeHolder, placesListFragment);
        //ft.add(placesListFragment, "list_fragment");
            ft.addToBackStack(null);
            ft.commit();
//            placesListFragment.setBusinessList(yelpBusinessArrayList);
//            placesListFragment.updateList();
            item.setIcon(R.drawable.ic_map_black_24dp);
            isMapFragment = false;
        }
        else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
 //           placesMapFragment = PlacesMapFragment.newInstance(yelpBusinessArrayList, keyReponseJSON, keyDirection);
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            ft.replace(R.id.placeHolder, placesMapFragment);
            ft.commit();
 //           placesMapFragment.setBusinessList(yelpBusinessArrayList);
//            placesMapFragment.markBusinesses();
            isMapFragment = true;
            item.setIcon(R.drawable.ic_view_list_black_24dp);
        }
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
/*        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingFragment settingFragment = SettingFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            settingFragment.show(fm, "fragment_setting");
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*
    *  Click handler for FAB
    * */

    boolean areOptionsShown = false;

    public void onFABClick(View view) {
        Log.d("FAB", "CLicked");

        FloatingActionButton fab0 = (FloatingActionButton) findViewById(R.id.fab_base);

        FrameLayout.LayoutParams layoutParams0 = (FrameLayout.LayoutParams) fab0.getLayoutParams();

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_food);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();


        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_gas);

        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) fab2.getLayoutParams();


        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_coffee);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab3.getLayoutParams();

        if (!areOptionsShown) {
            // Show menu items

            layoutParams.rightMargin = (int) (layoutParams0.rightMargin)+ (int) (fab1.getWidth() * 1.7);
            layoutParams.bottomMargin = (int) layoutParams0.bottomMargin + (int) (fab1.getHeight() * 0.25);
            fab1.setLayoutParams(layoutParams);

            Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
            fab1.startAnimation(show_fab_1);
            fab1.setClickable(true);

            layoutParams1.rightMargin = (int) (layoutParams0.rightMargin)+ (int) (fab2.getWidth() * 0.25);
            layoutParams1.bottomMargin = (int) layoutParams0.bottomMargin + (int) (fab2.getHeight() * 1.7);
            fab2.setLayoutParams(layoutParams1);

            Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
            fab2.startAnimation(show_fab_2);
            fab2.setClickable(true);

            layoutParams2.rightMargin = (int) (layoutParams0.rightMargin)+ (int) (fab3.getWidth() * 1.5);
            layoutParams2.bottomMargin = (int) layoutParams0.bottomMargin+ (int) (fab3.getHeight() * 1.5);
            fab3.setLayoutParams(layoutParams2);

            Animation show_fab3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
            fab3.startAnimation(show_fab3);
            fab3.setClickable(true);

            areOptionsShown = true;
        } else {
            // hide menu items
            Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
            fab1.startAnimation(hide_fab_1);
            fab1.setClickable(false);

            Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
            fab2.startAnimation(hide_fab_2);
            fab2.setClickable(false);

            Animation hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fav3_hide);
            fab3.startAnimation(hide_fab_3);
            fab3.setClickable(false);

            layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
            layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);

            layoutParams1.rightMargin -= (int) (fab2.getWidth() * 0.25);
            layoutParams1.bottomMargin -= (int) (fab2.getHeight() * 1.7);

            layoutParams2.rightMargin -= (int) (fab3.getWidth() * 1.5);
            layoutParams2.bottomMargin -= (int) (fab3.getHeight() * 1.5);

            areOptionsShown = false;
        }
    }

    public void onFoodMenuClicked(View view) {
        Log.d("DEBUG:", "Food Menu clicked");
        PointsOfInterestFragment aFragment = (PointsOfInterestFragment)getSupportFragmentManager().findFragmentById(R.id.placeHolder);
        aFragment.setSearchTerm("restaurant");
        aFragment.getYelpBusinesses();
    }

    public void onGasMenuClicked(View view) {
        Log.d("DEBUG:", "Gas Menu clicked");
        PointsOfInterestFragment aFragment = (PointsOfInterestFragment)getSupportFragmentManager().findFragmentById(R.id.placeHolder);
        aFragment.setSearchTerm("gas");
        aFragment.getYelpBusinesses();
    }

    public void onCafeMenuClicked(View view) {
        Log.d("DEBUG:", "Coffee Menu clicked");
        PointsOfInterestFragment aFragment = (PointsOfInterestFragment)getSupportFragmentManager().findFragmentById(R.id.placeHolder);
        aFragment.setSearchTerm("coffee");
        aFragment.getYelpBusinesses();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

