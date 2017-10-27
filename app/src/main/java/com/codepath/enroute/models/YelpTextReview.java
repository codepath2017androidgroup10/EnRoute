package com.codepath.enroute.models;

/**
 * Created by qunli on 10/20/17.
 * Thsi comes from Firebase
 */

public class YelpTextReview {
    private String text;


    public YelpTextReview() {
    }

    public YelpTextReview(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
