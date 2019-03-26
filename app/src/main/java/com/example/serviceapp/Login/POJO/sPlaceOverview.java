package com.example.serviceapp.Login.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class sPlaceOverview implements Serializable {
    @SerializedName("placePicUrl")
    @Expose
    private List<String> placePicUrl = null;
    @SerializedName("comments")
    @Expose
    private List<String> comments = null;

    public List<String> getPlacePicUrl() {
        return placePicUrl;
    }

    public List<String> getComments() {
        return comments;
    }
}
