package com.itstep.smakmobile.dto;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class ReviewRequest {
    private int rating;
    private String comment;

    public ReviewRequest(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}

