package com.codepath.enroute.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.MapUtil;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vidhya on 10/17/17.
 */

public class PointsOfInterestFragment extends Fragment {
    protected ArrayList<YelpBusiness> yelpBusinessList;
    protected YelpClient yelpClient;
    protected Map<LatLng,YelpBusiness> mPointsOfInterest;

    public PointsOfInterestFragment() {
        mPointsOfInterest = new HashMap<>();
        yelpBusinessList = new ArrayList<>();
    }


    protected void getYelpBusinesses(JSONObject response) {
        //TESTME Jim
        List<LatLng> googlePoints = null;
        try {
            googlePoints = MapUtil.getLatLngFromOverView(response, 1609);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //The following is an example how to use YelpApi.
        YelpClient client = YelpClient.getInstance();
        RequestParams params = new RequestParams();
        params.put("term", "food");
        params.put("radius", 1000);
        for (int i = 0; i < googlePoints.size(); i++) {

            params.put("latitude", googlePoints.get(i).latitude);
            params.put("longitude", googlePoints.get(i).longitude);
            client.getSearchResult(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        JSONArray yelpBusinesses = response.getJSONArray("businesses");
                        BitmapDescriptor icon =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                        for (int i = 0; i < yelpBusinesses.length(); i++) {
                            YelpBusiness aYelpBusiness = YelpBusiness.fromJson(yelpBusinesses.getJSONObject(i));

                            LatLng newLatLng = new LatLng(aYelpBusiness.getLatitude(),aYelpBusiness.getLongitude());

                            //Skip if there is a duplicate.
                            if (!mPointsOfInterest.containsKey(newLatLng)){
                                mPointsOfInterest.put(newLatLng,aYelpBusiness);
                                // Add data to arraylist
                            }}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(this.getClass().toString(), "Error fetching Yelp businesses: " + errorResponse.toString());
                    //super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });

        }
    }


}
