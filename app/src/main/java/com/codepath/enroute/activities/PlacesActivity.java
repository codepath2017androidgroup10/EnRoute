package com.codepath.enroute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.codepath.enroute.R;
import com.codepath.enroute.fragments.ListFragment;
import com.codepath.enroute.fragments.PlacesMapFragment;
import com.codepath.enroute.fragments.SettingFragment;
import com.codepath.enroute.models.YelpBusiness;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.codepath.enroute.fragments.ListFragment.newInstance;

/*
* Activity with the map view showing route between current location and destination
*
* */

public class PlacesActivity extends AppCompatActivity implements PlacesMapFragment.OnSearchDoneListener{

    ArrayList<YelpBusiness> yelpBusinessArrayList;
//    PlacesMapFragment placesMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

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
//                placesMapFragment.yelpBusinessList.clear();
//                placesMapFragment.setQuery(new String[]{"food"});
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
        //FrameLayout fragmentLayout = new FrameLayout(this);
        // set the layout params to fill the activity
        //fragmentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // set an id to the layout
        //fragmentLayout.setId(1000); // some positive integer
        // set the layout as Activity content
        //setContentView(fragmentLayout);


        ListFragment placesListFragment = ListFragment.newInstance(yelpBusinessArrayList);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeHolder, placesListFragment);
        //ft.add(placesListFragment, "list_fragment");
        ft.addToBackStack(null);
        ft.commit();

//        Intent intent = new Intent(this, ListActivity.class);
//        ArrayList<YelpBusiness> list = new ArrayList<>();
//        list.addAll(yelpBusinessArrayList);
//        intent.putExtra("list", Parcels.wrap(list));
//        startActivity(intent);
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
}

