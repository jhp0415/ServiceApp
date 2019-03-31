package com.example.serviceapp.MyServer.contract;


import com.example.serviceapp.MyServer.POJO.sPlaceOverview;

public interface AddPhotoContract {

    interface Presenter {

        public void submitPhoto(String poiId, String imagePath);
    }

    interface View {

        public void submitFinished(sPlaceOverview placeReview);
    }
}
