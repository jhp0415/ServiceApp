package com.example.serviceapp.Login.presenter;


import android.util.Log;

import com.example.serviceapp.Login.POJO.sPlaceWithComment;
import com.example.serviceapp.Login.contract.AddReviewContract;
import com.example.serviceapp.Login.model.MyServerServiceModel;

public class AddReviewPresenter implements AddReviewContract.Presenter {
    AddReviewContract.View addReviewView;
    MyServerServiceModel myserverServiceModel;

    public AddReviewPresenter(AddReviewContract.View addReviewView) {
        this.addReviewView = addReviewView;

        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void submitReview(String fbId, String poiId, String commentTitle, String commentBody) {
        Log.d("ddd", fbId);
        Log.d("ddd", poiId);
        Log.d("ddd", commentTitle);
        Log.d("ddd", commentBody);

        myserverServiceModel.addPlaceReview(fbId, poiId, commentTitle, commentBody, new MyServerServiceModel.addPlaceReviewListener() {

            @Override
            public void onAddPlaceReviewFinished(sPlaceWithComment response) {
                Log.d("ddd", "리뷰 제출 성공");
                addReviewView.submitFinished(response);
            }

            @Override
            public void onAddPlaceReviewFailure(Throwable t) {
                Log.d("ddd", "리뷰 제출 실패 : " + t.getMessage());
            }
        });
    }
}
