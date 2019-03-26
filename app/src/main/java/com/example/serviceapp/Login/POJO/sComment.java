package com.example.serviceapp.Login.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class sComment implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("poiId")
    @Expose
    private String poiId;
    @SerializedName("user")
    @Expose
    private sUser user;
    @SerializedName("captionTitle")
    @Expose
    private String captionTitle;
    @SerializedName("captionBody")
    @Expose
    private String captionBody;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public sUser getUser() {
        return user;
    }

    public String getCaptionTitle() {
        return captionTitle;
    }

    public String getCaptionBody() {
        return captionBody;
    }
}
