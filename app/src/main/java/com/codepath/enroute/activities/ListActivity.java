package com.codepath.enroute.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codepath.enroute.R;
import com.codepath.enroute.adapters.RestaurantAdapter;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.view.View.Y;
import static java.security.AccessController.getContext;

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
        restaurants = new ArrayList<>();
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
        fetchData();
    }
    public void fetchData() {
        yelpClient = YelpClient.getInstance();
        RequestParams params = new RequestParams();
        params.put("term","restaurants");
        params.put("location","880 West Maude Avenue, Sunnyvale, CA");
        yelpClient.getSearchResult(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ArrayList<YelpBusiness> list = new ArrayList<>();
                try {
                    list = YelpBusiness.fromJSONArray(response.getJSONArray("businesses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                restaurants.clear();
                restaurants.addAll(list);
                restaurantAdapter.notifyDataSetChanged();
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
        });


    }
}
