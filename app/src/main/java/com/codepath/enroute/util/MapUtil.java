package com.codepath.enroute.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidhya on 10/13/17.
 */


public class MapUtil {

    // Source: http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java

    /*
    * Method takes an encoded polyline, decodes it and gives back a list of LatLng.
    *
    * */
    public static List<LatLng> decodePolyLine(String encodedPolyLine) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encodedPolyLine.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPolyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedPolyLine.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }



    //The following gets the LatLng from the detailed googleMapResponse.
    //I don't think it is necessary though.
    //We can just use the overview version. ( see below )
    public static void getLatLng(JSONObject googleMapResponse, int distance) throws JSONException {

        //distince is in meter.
        //1 mile equal to 1609 meters.

        JSONArray routes = googleMapResponse.getJSONArray("routes");

        for (int i = 0; i < routes.length(); i++) {
            JSONObject routnN = routes.getJSONObject(i);
            JSONArray routeSteps = routnN.getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

            for (int j = 0; j < routeSteps.length(); j++) {
                JSONObject stepDetail = routeSteps.getJSONObject(j);
                int distanceMeters = stepDetail.getJSONObject("distance").getInt("value");


                String routePolylines = stepDetail.getJSONObject("polyline").getString("points");
                List<LatLng> c = MapUtil.decodePolyLine(routePolylines);

                int totalHops = c.size();
                //for every "distinct" meters, pick one point.
                int hops = totalHops * distance / distanceMeters;


                for (int k = 0; k < c.size(); k = k + hops) {
                    System.out.println("DEBUG2:" + c.get(k).latitude + ":" + c.get(k).longitude);
                }
            }
        }
    }

    public static List<LatLng> getLatLngFromOverView(JSONObject googleMapResponse, int distance) throws JSONException {

        //distince is in meter.
        //1 mile equal to 1609 meters.
        List<LatLng> returnResult = new ArrayList<>();
        String overview_polyline_points = googleMapResponse.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
        int distanceMeters =              googleMapResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

        List<LatLng> c = MapUtil.decodePolyLine(overview_polyline_points);

        int totalHops = c.size();
        //for every "distinct" meters, pick one point.
        float hops = totalHops * distance / distanceMeters;

        if (hops<1){
            hops =1.0f;
        }


        //Some kind of optimization
        //The idea is the following,
        //I will check the start point and then incrementally check along the way.
        //The factor below (1.3) can be adjusted so that the far away from starting point, the big the gap it is.
        //So when factor is set to 1.3, we are check 1,1,2,2,3,4,6,8,10,13,17,23,30,39.....
        //Usually it check less than 10 points.
        for (int k = 0; k < c.size(); k = k + (int)hops) {
            System.out.println("DEBUG1:" + c.get(k).latitude + "," + c.get(k).longitude);
            returnResult.add(new LatLng(c.get(k).latitude,c.get(k).longitude));
            hops *=1.3;
        }

        return returnResult;
    }


    //The following is no used. Just keep for reference.
    //Will delete later ( JIM )
    //TODO
    public static List<LatLng> decode(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    public static Marker addMarker(GoogleMap map, LatLng point, String title,
                                   String snippet,
                                   BitmapDescriptor icon) {
        // Creates and adds marker to the map
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(icon);
        Marker marker = map.addMarker(options);
        marker.setDraggable(true);
        return marker;
    }
}
