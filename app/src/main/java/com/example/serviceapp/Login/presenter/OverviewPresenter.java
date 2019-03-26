package com.example.serviceapp.Login.presenter;

import com.example.serviceapp.Login.POJO.sPlaceWithComment;
import com.example.serviceapp.Login.contract.OverviewContract;
import com.example.serviceapp.Login.model.MyServerModel;
import com.example.serviceapp.Login.model.MyServerServiceModel;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.PoiResponse;

import java.util.HashMap;
import java.util.List;

public class OverviewPresenter implements OverviewContract.Presenter {
    MyServerModel myserverModel;
    MyServerServiceModel myserverServiceModel;
    OverviewContract.View overviewView;

    public OverviewPresenter(OverviewContract.View overviewView) {

        this.overviewView = overviewView;

        myserverModel = new MyServerModel();
        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void getOverviewInfo(String poiId) {
        myserverServiceModel.callCurrentPlace(poiId, new MyServerServiceModel.callCurrentPlaceListener() {
            @Override
            public void onGetCurrentPlaceFinished(sPlaceWithComment response) {
                List<String> imageUrls = response.getPlacePicUrl();
                overviewView.setOverviewImage(imageUrls);
            }

            @Override
            public void onGetCurrentPlaceFailure(Throwable t) {
                // TODO: onGetCurrentPlaceFailure
            }
        });

        myserverModel.callpoiRetrieve(poiId, new MyServerModel.poiRetrieveListener() {
            @Override
            public void onPoiRetrieveFinished(PoiResponse response) {
                Poi poi = response.getPois().get(0);
                overviewView.setOverviewInfo(poi);
            }

            @Override
            public void onPoiRetrieveFailure(Throwable t) {
                // TODO: onPoiRetrieveFailure
            }
        });
    }

    @Override
    public void addMyListCurrentPlace(String fbId, String poiID) {
        myserverServiceModel.addMyListList(fbId, poiID, new MyServerServiceModel.addMyListListener() {
            @Override
            public void onAddMyListFinished(HashMap<String, Object> response) {
                // TODO: onAddMyListFinished
            }

            @Override
            public void onAddMyListFailure(Throwable t) {
                // TODO: onAddMyListFailure
            }
        });
    }
}
