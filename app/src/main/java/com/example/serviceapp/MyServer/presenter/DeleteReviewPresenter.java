package com.example.serviceapp.MyServer.presenter;

import android.util.Log;

import com.example.serviceapp.MyServer.contract.DeleteReviewContract;
import com.example.serviceapp.MyServer.contract.EditReviewContract;
import com.example.serviceapp.MyServer.contract.ReviewContract;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;

import java.util.HashMap;

public class DeleteReviewPresenter implements DeleteReviewContract.Presenter {
    DeleteReviewContract.View deleteReviewView;
    MyServerServiceModel myserverServiceModel;

    public DeleteReviewPresenter(DeleteReviewContract.View deleteReviewView) {
        this.deleteReviewView = deleteReviewView;

        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void submitEditReview(String fbId, String reviewId, String poiId) {
        myserverServiceModel.deletePlaceReview(reviewId, fbId, poiId, new MyServerServiceModel.deletePlaceReviewListener() {
            @Override
            public void onDeletePlaceReviewFinished(HashMap<String, Object> response) {
                Log.d("ddd", "리뷰 삭제 성공 : ");
                deleteReviewView.submitEditFinished();
            }

            @Override
            public void onDeletePlaceReviewFailure(Throwable t) {
                Log.d("ddd", "리뷰 삭제 실패 : " + t.getMessage());
            }
        });
    }
}
