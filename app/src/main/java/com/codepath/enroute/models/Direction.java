package com.codepath.enroute.models;

import com.codepath.enroute.util.MapUtil;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by vidhya on 10/13/17.
 */

public class Direction {

    String polyLine;

    public String getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(String polyLine) {
        this.polyLine = polyLine;
    }

    /*
    * Gives a list of LatLng values decoded from the Overview Polyline representing the route
    * */
    public static List<LatLng> fromJson(JSONObject jsonObject) throws JSONException {
        JSONObject routesJSON = jsonObject.getJSONArray("routes").optJSONObject(0);
        if (routesJSON == null) {
            return null;
        } else {
            JSONObject overViewJson = routesJSON.getJSONObject("overview_polyline");
            return MapUtil.decodePolyLine(overViewJson.getString("points"));
        }
    }
}
