package com.codepath.enroute.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by vidhya on 10/14/17.
 */

public class PointEnRoute {
    double latitude;
    double longitude;
    String address;
    String nameOfPlace;
    LatLng latLng;
    String description;
    boolean openNow;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNameOfPlace() {
        return nameOfPlace;
    }

    public void setNameOfPlace(String nameOfPlace) {
        this.nameOfPlace = nameOfPlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public LatLng getLatLng() {
        this.latLng = new LatLng(this.getLatitude(), this.getLongitude());
        return this.latLng;
    }

}
