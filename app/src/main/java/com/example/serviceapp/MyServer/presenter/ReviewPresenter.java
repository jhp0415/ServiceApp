package com.example.serviceapp.MyServer.presenter;

import android.util.Log;

import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.MyServer.contract.ReviewContract;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;

import java.util.ArrayList;

public class ReviewPresenter implements ReviewContract.Presenter {
    MyServerServiceModel myserverServiceModel;
    ReviewContract.View reviewView;

    public ReviewPresenter(ReviewContract.View reviewView) {

        this.reviewView = reviewView;

        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void getReviewList(String poiId) {
        myserverServiceModel.callCurrentPlace(poiId, new MyServerServiceModel.callCurrentPlaceListener() {
            @Override
            public void onGetCurrentPlaceFinished(sPlaceWithComment response) {
                Log.d("ddd", "onGetCurrentPlaceFinished : 리뷰 리스트 call 성공");
                ArrayList<sComment> comments = (ArrayList<sComment>) response.getComments();
                reviewView.setReviewList(comments);
            }

            @Override
            public void onGetCurrentPlaceFailure(Throwable t) {
                Log.d("ddd", "onGetCurrentPlaceFinished : 리뷰 리스트 call 실패");
                reviewView.clearReviewList();
            }
        });
    }
}
