package com.codepath.enroute.models;

import com.codepath.enroute.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<String> categoriesList = new ArrayList<>();
    ArrayList<String> photosList = new ArrayList<>();
    ArrayList<ArrayList<OpenHour>> openHourSummary = new ArrayList<>();

    public ArrayList<ArrayList<OpenHour>> getOpenHourSummary() {
        return openHourSummary;
    }

    public void setOpenHourSummary(ArrayList<ArrayList<OpenHour>> openHourSummary) {
        this.openHourSummary = openHourSummary;
    }

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

    public List<String> getCategoriesList() {
        return categoriesList;
    }

    public ArrayList<String> getPhotosList() { return photosList;}

    public void setCategoriesList(List<String> categoriesList) {
        this.categoriesList = categoriesList;
    }

    public void setPhotosList(ArrayList<String> photosList){
        this.photosList=photosList;
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
            aYelpBusiness.price_level ="";
        }
        aYelpBusiness.display_address = "";
        JSONArray addressArray = jsonObject.getJSONObject("location").getJSONArray("display_address");
        for (int i=0;i<addressArray.length();i++){
            aYelpBusiness.display_address+=addressArray.getString(i);
            if (i == 0) {
                aYelpBusiness.display_address += ",";
            }
            aYelpBusiness.display_address += " ";
        }
        aYelpBusiness.phone_number = jsonObject.getString("phone");

//        aYelpBusiness.openNow = !jsonObject.getBoolean("is_closed");
        //Mark, why we divide this by 3.3?
        aYelpBusiness.distance = jsonObject.getDouble("distance") / 1609;
        JSONArray jsonArray = jsonObject.getJSONArray("categories");
        aYelpBusiness.categories = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            aYelpBusiness.categories += jsonArray.getJSONObject(i).getString("title");
            aYelpBusiness.categoriesList.add(jsonArray.getJSONObject(i).getString("title"));
            if (i < jsonArray.length() - 1) {
                aYelpBusiness.categories += ", ";
            }
        }

        if (jsonObject.has("photos")) {
            JSONArray jsonPhotoArray = jsonObject.getJSONArray("photos");

            for (int i = 0; i < jsonPhotoArray.length(); i++) {

                aYelpBusiness.photosList.add(jsonPhotoArray.getString(i));

            }
        }

        aYelpBusiness.setDescription(aYelpBusiness.getCategories());
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

    public static Map<String, Integer> loadCategoryIcons() {
        Map<String, Integer> mCategoryIconMap = new HashMap<>();
        mCategoryIconMap.put("placeholder_food", R.drawable.ic_food_placeholder);
        mCategoryIconMap.put("american", R.drawable.ic_category_american);
        mCategoryIconMap.put("american (new)", R.drawable.ic_category_american);
        mCategoryIconMap.put("american (traditional)", R.drawable.ic_category_american);
        mCategoryIconMap.put("japanese", R.drawable.ic_catgory_japanese);
        mCategoryIconMap.put("asianfusion", R.drawable.ic_category_asian);
        mCategoryIconMap.put("thai", R.drawable.ic_category_asian);
        mCategoryIconMap.put("filipino", R.drawable.ic_category_asian);
        mCategoryIconMap.put("breakfast & brunch", R.drawable.ic_category_breakfast);
        mCategoryIconMap.put("breakfast", R.drawable.ic_category_breakfast);
        mCategoryIconMap.put("sandwiches", R.drawable.ic_category_sandwich);
        mCategoryIconMap.put("vegetarian", R.drawable.ic_category_veg);
        mCategoryIconMap.put("vegan", R.drawable.ic_category_veg);
        mCategoryIconMap.put("pizza", R.drawable.ic_category_pizza);
        mCategoryIconMap.put("seafood", R.drawable.ic_category_seafood);
        mCategoryIconMap.put("bakeries", R.drawable.ic_category_bakery);
        mCategoryIconMap.put("donuts", R.drawable.ic_category_donut);
        mCategoryIconMap.put("chinese", R.drawable.ic_category_chinese);
        mCategoryIconMap.put("italian", R.drawable.ic_category_italian);
        mCategoryIconMap.put("vietnamese", R.drawable.ic_category_vietnamese);
        mCategoryIconMap.put("gas", R.drawable.ic_placeholder_gas);
        mCategoryIconMap.put("coffee", R.drawable.ic_coffee_placeholder);
        mCategoryIconMap.put("coffee & tea", R.drawable.ic_coffee_placeholder);
        mCategoryIconMap.put("tea", R.drawable.ic_category_tea);
        mCategoryIconMap.put("mexican", R.drawable.ic_category_mexican);
        return mCategoryIconMap;
    }

    public boolean isCategoryKnown(String searchTerm) {
        Map<String, Integer> map = loadCategoryIcons();
        return map.containsKey(searchTerm);
    }
}
