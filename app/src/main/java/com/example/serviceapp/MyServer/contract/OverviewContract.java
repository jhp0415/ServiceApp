package com.example.serviceapp.MyServer.contract;

import com.kt.place.sdk.model.Poi;

import java.util.List;

public interface OverviewContract {

    interface Presenter {
        public void getOverviewInfo(String poiId);
        public void addMyList(String fbId, String poiID);
    }

    interface View {

        public void setOverviewImage(List<String> poiImage);
        public void setOverviewInfo(Poi place);
        public void clearOverviewImage();
    }
}
