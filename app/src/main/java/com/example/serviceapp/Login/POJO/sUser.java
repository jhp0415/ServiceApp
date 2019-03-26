package com.example.serviceapp.Login.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class sUser implements Serializable {
    @SerializedName("places")
    @Expose
    private List<String> places = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("fbId")
    @Expose
    private String fbId;
    @SerializedName("userProfileUrl")
    @Expose
    private String userProfileUrl;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getUserProfileUrl() {
        return this.userProfileUrl;
    }

    public String getName() {
        return this.name;
    }
}