package com.example.serviceapp.Login.contract;


import com.example.serviceapp.Login.POJO.sPlaceWithComment;

public interface AddReviewContract {

    interface Presenter {

        public void submitReview(String fbId, String poiId, String commentTitle, String commentBody);
    }

    interface View {

        public void submitFinished(sPlaceWithComment placeReview);
    }
}
