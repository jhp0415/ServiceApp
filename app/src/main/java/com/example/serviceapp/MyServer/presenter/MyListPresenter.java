package com.example.serviceapp.MyServer.presenter;

import android.util.Log;

import com.example.serviceapp.MyServer.POJO.sPlace;
import com.example.serviceapp.MyServer.contract.MyListContract;
import com.example.serviceapp.MyServer.model.MyServerModel;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.PoiResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class MyListPresenter implements MyListContract.Presenter {
    MyListContract.View myListView;
    MyServerServiceModel myserverServiceModel;
    MyServerModel myserverModel;
    ArrayList<String> poiNames;
    HashMap<sPlace, String> poiContent;

    public MyListPresenter(MyListContract.View myListView) {
        this.myListView = myListView;

        myserverServiceModel = new MyServerServiceModel();
        myserverModel = new MyServerModel();
    }

    @Override
    public void getMyList(String fbId) {
        myserverServiceModel.callMyPlaceList(fbId, new MyServerServiceModel.callMyPlaceListListener() {
            @Override
            public void onGetMyPlaceListFinished(ArrayList<sPlace> sPlaceList) {
                Log.d("ddd", "onGetMyPlaceListFinished: ");
                myListView.setMyList(sPlaceList);
                for(int i = 0; i < sPlaceList.size(); i++) {
//                    getPoiNameByRetrieve(i, sPlaceList.get(i));
                }
            }

            @Override
            public void onGetMyPlaceListFailure(Throwable t) {
                Log.d("ddd", "onGetMyPlaceListFailure: ");
                myListView.clearMyList();
            }
        });
    }

    private void getPoiNameByRetrieve(final int position, sPlace place) {
        myserverModel.callpoiRetrieve(place.getpoiId(), new MyServerModel.poiRetrieveListener() {
            @Override
            public void onPoiRetrieveFinished(PoiResponse response) {
                Poi poi = response.getPois().get(0);
                String poiName = poi.getName() + " " + poi.getBranch();
//                poiContent.put(place, poiName);
                myListView.setMyList(position, poiName);
            }

            @Override
            public void onPoiRetrieveFailure(Throwable t) {

            }
        });
    }

    @Override
    public void getMyListPoi(String poiId) {
        myserverModel.callpoiRetrieve(poiId, new MyServerModel.poiRetrieveListener() {
            @Override
            public void onPoiRetrieveFinished(PoiResponse response) {
                myListView.setMyListPoi(response.getPois().get(0));
            }

            @Override
            public void onPoiRetrieveFailure(Throwable t) {

            }
        });
    }

    @Override
    public void deleteMyList(String fbId, String poiId) {
        myserverServiceModel.delMyPlace(fbId, poiId, new MyServerServiceModel.delMyPlaceListener() {
            @Override
            public void onDelMyPlaceFinished(ArrayList<sPlace> response) {
                Log.d("deleteMyplace", "ondeleteMyplaceFinished: ");
                myListView.setMyList(response);
                for(int i = 0; i < response.size(); i++) {
                    getPoiNameByRetrieve(i, response.get(i));
                }
            }

            @Override
            public void onDelMyPlaceFailure(Throwable t) {
                Log.d("deleteMyplace", "ondeleteMyplaceFailure: ");
            }
        });
    }
}
