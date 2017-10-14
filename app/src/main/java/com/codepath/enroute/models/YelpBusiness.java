package com.codepath.enroute.models;

import com.codepath.enroute.util.MapUtil;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by qunli on 10/14/17.
 */

public class YelpBusiness {
    double latitude;
    double longitude;
    String id;
    String name;
    String image_url;
    String url;
    int review_count;
    int rating;
    String price_level;
    String phone_number;
    String display_address;


    public static YelpBusiness fromJson(JSONObject jsonObject) throws JSONException {
        YelpBusiness aYelpBusiness = new YelpBusiness();
        aYelpBusiness.id = jsonObject.getString("id");
        aYelpBusiness.name = jsonObject.getString("name");
        aYelpBusiness.image_url = jsonObject.getString("image_url");
        aYelpBusiness.url = jsonObject.getString("url");
        aYelpBusiness.review_count = jsonObject.getInt("review_count");
        aYelpBusiness.rating = jsonObject.getInt("rating");
        aYelpBusiness.latitude = jsonObject.getJSONObject("coordinates").getDouble("latitude");
        aYelpBusiness.longitude = jsonObject.getJSONObject("coordinates").getDouble("longitude");
        aYelpBusiness.price_level = jsonObject.getString("price");
        aYelpBusiness.display_address = jsonObject.getJSONObject("location").getString("display_address");
        aYelpBusiness.phone_number = jsonObject.getString("phone");
        return aYelpBusiness;
    }
}
