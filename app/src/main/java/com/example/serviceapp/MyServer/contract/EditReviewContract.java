package com.example.serviceapp.MyServer.contract;

import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;

public interface EditReviewContract {
    interface Presenter {

        public void submitEditReview(String fbId, String reviewId, String commentTitle, String commentBody);
    }

    interface View {

        public void submitEditFinished(sComment comment);
    }
}
