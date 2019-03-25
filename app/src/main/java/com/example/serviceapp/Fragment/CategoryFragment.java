package com.example.serviceapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.Helper.PlacesApiHelper;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.NearbyPoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

import java.util.Map;

public class CategoryFragment extends Fragment implements View.OnClickListener {

    private static CategoryFragment instance;
    MainActivity activity;
    private MapHelper mapHelper;
    private Client placesClient;

    public static CategoryFragment newInstance(){
        if(instance == null) {
            return new CategoryFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        activity = (MainActivity)getActivity();
        mapHelper = MapHelper.getInstance();
        placesClient = new Client();

        ((Button)view.findViewById(R.id.category_btn1)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.category_btn2)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.category_btn3)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.category_btn4)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.category_btn5)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.category_btn6)).setOnClickListener(this);
        ((Button)view.findViewById(R.id.category_btn7)).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        MainActivity mainActivity = (MainActivity)getActivity();

        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, MapHelper.getGoogleMapFragment(), "visible");
        fragmentTransaction.addToBackStack(MapHelper.getGoogleMapFragment().getTag());
        fragmentTransaction.commit();

        switch (v.getId()){
            case R.id.category_btn1:
                requestCategoryData("편의점");
                break;
            case R.id.category_btn2:
                requestCategoryData("병원");
                break;
            case R.id.category_btn3:
                requestCategoryData("약국");
                break;
            case R.id.category_btn4:
                requestCategoryData("관광지");
                break;
            case R.id.category_btn5:
                requestCategoryData("주유소");
                break;
            case R.id.category_btn6:
                requestCategoryData("주차장");
                break;
            case R.id.category_btn7:
                requestCategoryData("은행");
                break;
        }
    }

    public void requestCategoryData(String categoryName) {
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
                for (final Poi poi : poiResponse.getPois()) {
                    LatLng result = new LatLng(poi.getPoint().getLat(), poi.getPoint().getLng());
                    mapHelper.setLocationMarker(result, poi.getName(), poi.getBranch(), poi.getAddress().getFullAddressRoad());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }
        });
    }



}
