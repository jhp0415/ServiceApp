package com.example.serviceapp.MyServer.presenter;

import android.util.Log;

import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.MyServer.contract.AddReviewContract;
import com.example.serviceapp.MyServer.contract.EditReviewContract;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;

import java.util.HashMap;

public class EditReviewPresenter implements EditReviewContract.Presenter {
    EditReviewContract.View editReviewView;
    MyServerServiceModel myserverServiceModel;

    public EditReviewPresenter(EditReviewContract.View editReviewView) {
        this.editReviewView = editReviewView;

        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void submitEditReview(String fbId, String reviewId, String commentTitle, String commentBody) {
        Log.d("ddd", fbId);
        Log.d("ddd", reviewId);
        Log.d("ddd", commentTitle);
        Log.d("ddd", commentBody);

        myserverServiceModel.updatePlaceReview(fbId, reviewId, commentTitle, commentBody, new MyServerServiceModel.updatePlaceReviewListener() {

            @Override
            public void onUpdatePlaceReviewFinished(sComment response) {
                Log.d("ddd", "리뷰 수정 성공");
                editReviewView.submitEditFinished(response);
            }

            @Override
            public void onUpdatePlaceReviewFailure(Throwable t) {
                Log.d("ddd", "리뷰 제출 실패 : " + t.getMessage());
            }
        });
    }

}
