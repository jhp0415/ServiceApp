package com.example.serviceapp.MyServer.contract;

import com.example.serviceapp.MyServer.POJO.sComment;

public interface DeleteReviewContract {
    interface Presenter {

        public void submitEditReview(String fbId, String reviewId, String poiId);
    }

    interface View {

        public void submitEditFinished();
    }
}
