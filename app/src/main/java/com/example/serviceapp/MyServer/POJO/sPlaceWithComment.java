package com.example.serviceapp.MyServer.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class sPlaceWithComment implements Serializable {
    @SerializedName("placePicUrl")
    @Expose
    private List<String> placePicUrl = null;
    @SerializedName("comments")
    @Expose
    private List<sComment> comments = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("poiId")
    @Expose
    private String poiId;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public List<String> getPlacePicUrl() {
        return placePicUrl;
    }

    public List<sComment> getComments() {
        return comments;
    }

    public String getpoiId() {
        return poiId;
    }
}