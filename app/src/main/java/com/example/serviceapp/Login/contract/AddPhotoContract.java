package com.example.serviceapp.Login.contract;

import com.example.serviceapp.Login.POJO.sPlaceOverview;

public interface AddPhotoContract {

    interface Presenter {

        public void submitPhoto(String poiId, String imagePath);
    }

    interface View {

        public void submitFinished(sPlaceOverview placeReview);
    }
}
