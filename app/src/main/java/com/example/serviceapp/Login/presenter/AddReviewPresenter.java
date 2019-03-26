package com.example.serviceapp.Login.presenter;


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
        myserverServiceModel.addPlaceReview(fbId, poiId, commentTitle, commentBody, new MyServerServiceModel.addPlaceReviewListener() {

            @Override
            public void onAddPlaceReviewFinished(sPlaceWithComment response) {
                addReviewView.submitFinished(response);
            }

            @Override
            public void onAddPlaceReviewFailure(Throwable t) {

            }
        });
    }
}
