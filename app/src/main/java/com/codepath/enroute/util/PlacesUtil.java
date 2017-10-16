package com.codepath.enroute.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to hold utility methods related to location and places.
 *
 * @author Valli Vidhya Venkatesan
 */
public class PlacesUtil {

    public static LatLng getCoordinatesOfPlaceFromJson(JSONObject jsonObject) throws JSONException {
        JSONObject jObj = jsonObject.getJSONArray("results").getJSONObject(0);
        JSONObject j = jObj.getJSONObject("geometry").getJSONObject("location");
        return new LatLng(j.getDouble("lat"), j.getLong("lng"));
    }
}
