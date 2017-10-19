package com.codepath.enroute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codepath.enroute.R;
import com.codepath.enroute.adapters.RestaurantAdapter;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    RecyclerView rvRestaurants;
    ArrayList<YelpBusiness> restaurants;
    RestaurantAdapter restaurantAdapter;
    YelpClient yelpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        rvRestaurants = findViewById(R.id.rvRestaurants);
        Parcelable parcelable = (Parcelable) getIntent().getExtras().get("list");
        restaurants = (ArrayList<YelpBusiness>) Parcels.unwrap(parcelable);
        restaurantAdapter = new RestaurantAdapter(this, restaurants);
        rvRestaurants.setAdapter(restaurantAdapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(rvRestaurants).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                YelpBusiness yelpBusiness = restaurants.get(position);
                Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                i.putExtra("YELP_BUSINESS", Parcels.wrap(yelpBusiness));
                startActivity(i);
            }
        });
    }
}
