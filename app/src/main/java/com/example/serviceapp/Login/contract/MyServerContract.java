package com.example.serviceapp.Login.contract;


import com.example.serviceapp.Login.POJO.sAccess;

public interface MyServerContract {

    /*interface Model {

        interface OnFinishedListener {
            public void onSignCheckFinished(sAccess response);
            public void onSignCheckFailure(Throwable t);
        }

        //public void callNearby(OnFinishedListener onFinishedListener, LatLng curPos, String type);
        //public void callPlaceInfo(OnFinishedListener onFinishedListener, PlacesClient placesClient, String placeId);
        public void callSignCheck(OnFinishedListener onFinishedListener, String accessToken);
    }*/

    interface Presenter {

        //public void getNearby(LatLng curPos, String type);
        //public void getPlaceInfo(PlacesClient placesClient, String placeId);
        public void getSignCheck(String accessToken);
    }

    interface View {

         //public void drawNearbyMarker(HashMap<LatLng,String> list);
         //public void drawPlaceInfo(Place place, Bitmap placePhoto);
         public void setFbInfo(sAccess response);
    }
}
