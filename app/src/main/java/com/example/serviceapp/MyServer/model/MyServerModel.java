package com.example.serviceapp.MyServer.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kt.place.sdk.listener.OnResponseListener;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.net.RetrievePoiRequest;
import com.kt.place.sdk.util.PlaceClient;
import com.kt.place.sdk.util.PlaceManager;

public class MyServerModel {
    PlaceClient placeClient = PlaceManager.createClient();

    public interface poiRetrieveListener {
        public void onPoiRetrieveFinished(PoiResponse response);
        public void onPoiRetrieveFailure(Throwable t);
    }

    public void callpoiRetrieve(String poiId, final poiRetrieveListener onFinishedListener) {
        RetrievePoiRequest retrievePoiRequest = new RetrievePoiRequest.RetrievePoiRequestBuilder(poiId).build();
        placeClient.getRetrievePoi(retrievePoiRequest, new OnResponseListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                try {
                    onFinishedListener.onPoiRetrieveFinished(poiResponse);
                } catch (Exception e) {
                    Log.d("callpoiRetrieve", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("callpoiRetrieve", "fail");
            }
        });
    }
}