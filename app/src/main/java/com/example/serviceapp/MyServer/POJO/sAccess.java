package com.example.serviceapp.MyServer.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class sAccess {
    @SerializedName("fbId")
    @Expose
    private String fbId;
    @SerializedName("userProfileUrl")
    @Expose
    private String userProfileUrl;
    @SerializedName("name")
    @Expose
    private String name;

    public String getFbId() {
        return fbId;
    }

    public String getUserProfileUrl() {
        return userProfileUrl;
    }

    public String getName() {
        return name;
    }
}
