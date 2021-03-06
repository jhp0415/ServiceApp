package com.example.serviceapp.MyServer.contract;


import com.example.serviceapp.MyServer.POJO.sPlace;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;

public interface MyListContract {

    interface Presenter {

        public void getMyList(String fbId);
        public void getMyListPoi(String poiId);
        public void deleteMyList(String fbId, String poiId);
    }

    interface View {
        public void setMyList(ArrayList<sPlace> response);
        public void setMyList(int position, String poiName);
        public void setMyListPoi(Poi place);
        public void clearMyList();
    }
}
