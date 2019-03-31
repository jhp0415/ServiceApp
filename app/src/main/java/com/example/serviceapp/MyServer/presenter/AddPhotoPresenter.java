package com.example.serviceapp.MyServer.presenter;

import android.util.Log;

import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.contract.AddPhotoContract;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;

import java.io.File;

public class AddPhotoPresenter implements AddPhotoContract.Presenter {
    AddPhotoContract.View addPhotoView;
    MyServerServiceModel myserverServiceModel;

    public AddPhotoPresenter(AddPhotoContract.View addPhotoView) {
        this.addPhotoView = addPhotoView;

        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void submitPhoto(String poiId, String imagePath) {
        File file = new File(imagePath);
        myserverServiceModel.addPlacePhoto(poiId, file, new MyServerServiceModel.addPlacePhotoListener() {
            @Override
            public void onAddPlacePhotoFinished(sPlaceOverview response) {
                addPhotoView.submitFinished(response);
            }

            @Override
            public void onAddPlacePhotoFailure(Throwable t) {
                Log.d("submitPhoto", t.getMessage().toString());
            }
        });
    }
}
