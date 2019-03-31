package com.example.serviceapp.Helper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.net.AutocompleteRequest;
import com.kt.place.sdk.net.AutocompleteResponse;
import com.kt.place.sdk.net.GeocodeRequest;
import com.kt.place.sdk.net.GeocodeResponse;
import com.kt.place.sdk.net.NearbyPoiRequest;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

public class PlacesApiHelper {
    private static PlacesApiHelper instance;
    private Client placesClient;
    private GeocodeResponse geocodeResponseResult;
    private PoiResponse poiResponseResult;
    private AutocompleteResponse autocompleteResponseResult;
    private PoiResponse categoryResponseResult;

    public static PlacesApiHelper getInstance() {
        if(instance == null) {
            instance = new PlacesApiHelper();
        }
        return instance;
    }

    public PlacesApiHelper() {
        placesClient = new Client();
    }

    public GeocodeResponse requestGeocodeResult(){
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        final GeocodeRequest request = new GeocodeRequest.GeocodeRequestBuilder()
                .setLat(point.latitude)
                .setLng(point.longitude)
                .build();

        placesClient.getGeocode(request, new OnSuccessListener<GeocodeResponse>() {
            @Override
            public void onSuccess(@NonNull GeocodeResponse geocodeResponse) {
                geocodeResponseResult = geocodeResponse;
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
        return geocodeResponseResult;
    }

    public PoiResponse requestPoiSearch(final String terms, int start) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        PoiRequest request = new PoiRequest.PoiRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .setStart(start)
                .setNumberOfResults(10)
                .build();

        placesClient.getPoiSearch(request, new OnSuccessListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                if(poiResponse.getPois().size() > 0) {
                    poiResponseResult = poiResponse;
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
        return poiResponseResult;
    }

    public AutocompleteResponse requestAutocomplete(final String terms) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        AutocompleteRequest request = new AutocompleteRequest.AutocompleteRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .build();

        placesClient.getAutocomplete(request, new OnSuccessListener<AutocompleteResponse>() {
            @Override
            public void onSuccess(@NonNull AutocompleteResponse autocompleteResponse) {
                if(autocompleteResponse.getSuggestList().size() > 0) {
                    autocompleteResponseResult = autocompleteResponse;
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
        return autocompleteResponseResult;
    }

    public PoiResponse requestCategoryData(String categoryName) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        NearbyPoiRequest request = new NearbyPoiRequest.NearbyPoiRequestBuilder(500, point.latitude, point.longitude)
                .setCategoryName(categoryName)
                .setLat(point.latitude)
                .setNumberOfResults(10)
                .setLng(point.longitude)
                .build();

        placesClient.getNearbyPoiSearch(request, new OnSuccessListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull final PoiResponse poiResponse) {
//                for (final Poi poi : poiResponse.getPois()) {
//                    LatLng result = new LatLng(poi.getPoint().getLat(), poi.getPoint().getLng());
//                    mapHelper.setLocationMarker(result, poi.getName(), poi.getBranch(), poi.getAddress().getFullAddressRoad());
//                }
                categoryResponseResult = poiResponse;
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }
        });
        return categoryResponseResult;
    }

}
