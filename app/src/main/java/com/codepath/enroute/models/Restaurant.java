package com.codepath.enroute.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bear&bear on 10/13/2017.
 */

public class Restaurant {

    public String name;
    public String id;
    public String imageUrl;
    public double rating;
    public double distance;
    public String price;
    public boolean isClosed;
    public int reviewCount;
    public LatLng coordinates;
    public String categories;
    public String address;

    public static Restaurant fromJSON(JSONObject jsonObject) {

        Restaurant restaurant = new Restaurant();
        try {
            restaurant.name = jsonObject.getString("name");
            restaurant.id = jsonObject.getString("id");
            restaurant.imageUrl = jsonObject.getString("image_url");
            restaurant.rating = jsonObject.getDouble("rating");
            restaurant.distance = jsonObject.getDouble("distance") / 3.3 / 1600;
            restaurant.price = jsonObject.getString("price");
            restaurant.isClosed = jsonObject.getBoolean("is_closed");
            restaurant.reviewCount = jsonObject.getInt("review_count");
            restaurant.coordinates = new LatLng(jsonObject.getJSONObject("coordinates").getDouble("latitude"),jsonObject.getJSONObject("coordinates").getDouble("longitude"));
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            restaurant.categories = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                restaurant.categories += jsonArray.getJSONObject(i).getString("title");
                if (i < jsonArray.length() - 1) {
                    restaurant.categories += ", ";
                }
            }
//            restaurant.categories = json.getString("alias") + ", " + json.getString("title");
            restaurant.address = jsonObject.getJSONObject("location").getString("address1") + ", " + jsonObject.getJSONObject("location").getString("city");

        }  catch (JSONException e){
            e.printStackTrace();
        }
        return restaurant;
    }

    public static ArrayList<Restaurant> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                restaurants.add(Restaurant.fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return restaurants;
    }

}
