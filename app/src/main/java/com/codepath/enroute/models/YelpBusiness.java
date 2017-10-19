package com.codepath.enroute.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by qunli on 10/14/17.
 */

@Parcel
public class YelpBusiness {
    double latitude;
    double longitude;
    String id;
    String name;
    String image_url;
    String url;
    int review_count;
    double rating;
    String price_level;
    String phone_number;
    String display_address;
    boolean openNow;
    LatLng latLng;
    String description;
    String categories;

    //Mark: Do we really need distance here?
    public YelpBusiness() {

    }

    public double distance;

    public String getDescription() {
        return description;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public String getCategories() {
        return categories;
    }

    public double getDistance() {
        return distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getUrl() {
        return url;
    }

    public int getReview_count() {
        return review_count;
    }

    public double getRating() {
        return rating;
    }

    public String getPrice_level() {
        return price_level;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPrice_level(String price_level) {
        this.price_level = price_level;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setDisplay_address(String display_address) {
        this.display_address = display_address;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_address() {
        return display_address;
    }

    public LatLng getLatLng() {
        this.latLng = new LatLng(this.getLatitude(), this.getLongitude());
        return this.latLng;
    }

    public static YelpBusiness fromJson(JSONObject jsonObject) throws JSONException {
        YelpBusiness aYelpBusiness = new YelpBusiness();
        aYelpBusiness.id = jsonObject.getString("id");
        aYelpBusiness.name = jsonObject.getString("name");
        aYelpBusiness.image_url = jsonObject.getString("image_url");
        aYelpBusiness.url = jsonObject.getString("url");
        aYelpBusiness.review_count = jsonObject.getInt("review_count");
        aYelpBusiness.rating = jsonObject.getDouble("rating");
        aYelpBusiness.latitude = jsonObject.getJSONObject("coordinates").getDouble("latitude");
        aYelpBusiness.longitude = jsonObject.getJSONObject("coordinates").getDouble("longitude");
        if (jsonObject.has("price")) {
            aYelpBusiness.price_level = jsonObject.getString("price");
        }else
        {
            aYelpBusiness.price_level ="NA";
        }
        aYelpBusiness.display_address = "";
        JSONArray addressArray = jsonObject.getJSONObject("location").getJSONArray("display_address");
        for (int i=0;i<addressArray.length();i++){
            aYelpBusiness.display_address+=addressArray.getString(i)+" ";
        }
        aYelpBusiness.phone_number = jsonObject.getString("phone");

        aYelpBusiness.openNow = !jsonObject.getBoolean("is_closed");
        //Mark, why we divide this by 3.3?
        aYelpBusiness.distance = jsonObject.getDouble("distance") / 1609;
        JSONArray jsonArray = jsonObject.getJSONArray("categories");
        aYelpBusiness.categories = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            aYelpBusiness.categories += jsonArray.getJSONObject(i).getString("title");
            if (i < jsonArray.length() - 1) {
                aYelpBusiness.categories += ", ";
            }
        }
        aYelpBusiness.latLng = new LatLng(jsonObject.getJSONObject("coordinates").getDouble("latitude"),jsonObject.getJSONObject("coordinates").getDouble("longitude"));
        return aYelpBusiness;
    }


    public static ArrayList<YelpBusiness> fromJSONArray(JSONArray jsonArray) {
        ArrayList<YelpBusiness> YelpBusinesses = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                YelpBusinesses.add(YelpBusiness.fromJson(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return YelpBusinesses;
    }
}
