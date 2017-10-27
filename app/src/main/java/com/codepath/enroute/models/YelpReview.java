package com.codepath.enroute.models;

/**
 * Created by qunli on 10/20/17.
 * Thsi comes from Firebase
 */

public class YelpReview {
    private String text;
    private String photoUrl;

    public YelpReview() {
    }

    public YelpReview(String text, String photoUrl) {
        this.text = text;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
