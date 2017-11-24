package com.codepath.enroute.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import org.parceler.Parcel;
/**
 * Created by bear&bear on 11/23/2017.
 */
@Parcel
public class OpenHour {

    String day;
    String start;
    String end;
    Boolean is_overnight;

    public OpenHour(){

    }

    public String getDAy() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Boolean getIs_overnight() {
        return is_overnight;
    }

    public void setIs_overnight(Boolean is_overnight) {
        this.is_overnight = is_overnight;
    }

    public static OpenHour fromJSON(JSONObject jsonObject) throws JSONException {
        OpenHour openHour = new OpenHour();
        openHour.day = jsonObject.getString("day");
        openHour.end = jsonObject.getString("end");
        openHour.start = jsonObject.getString("start");
        openHour.is_overnight = jsonObject.getBoolean("is_overnight");
        return openHour;
    }

    public static ArrayList<OpenHour> fromJSONArray(JSONArray jsonArray) {
        ArrayList<OpenHour> openHOurs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                openHOurs.add(OpenHour.fromJSON(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return openHOurs;
    }


}
