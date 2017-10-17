package com.codepath.enroute.models;

import org.json.JSONException;
import org.json.JSONObject;

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
    public static String fromJson(JSONObject jsonObject) throws JSONException {
        JSONObject routesJSON = jsonObject.getJSONArray("routes").optJSONObject(0);
        if (routesJSON == null) {
            return null;
        } else {
            JSONObject overViewJson = routesJSON.getJSONObject("overview_polyline");
            return overViewJson.optString("points") == null ? "" : overViewJson.optString("points")  ;
            //return MapUtil.decodePolyLine(overViewJson.getString("points"));
        }
    }
}
