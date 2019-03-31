package com.example.serviceapp.MyServer.contract;


import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;

public interface AddReviewContract {

    interface Presenter {

        public void submitReview(String fbId, String poiId, String commentTitle, String commentBody);
    }

    interface View {

        public void submitFinished(sPlaceWithComment placeReview);
    }
}
