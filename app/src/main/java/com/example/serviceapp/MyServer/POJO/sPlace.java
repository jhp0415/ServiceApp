package com.example.serviceapp.MyServer.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class sPlace {
    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("__v")
    @Expose
    private int __v;
    @SerializedName("poiId")
    @Expose
    private String poiId;
    @SerializedName("placePicUrl")
    @Expose
    private List<String> placePicUrl = null;
    @SerializedName("comments")
    @Expose
    private List<String> comments = null;

    public List<String> getPlacePicUrl() {
        return placePicUrl;
    }

    public String getpoiId() {
        return poiId;
    }
}