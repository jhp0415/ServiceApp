package com.example.serviceapp.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.serviceapp.Listener.OnGpsListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapHelper
        implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener{

    public static GoogleMap mGoogleMap;
    private int ZOOM_LEVEL = 17;
    private static MapHelper instance;
    public static SupportMapFragment googleMapFragment;

    public static MapHelper getInstance() {
        if (instance == null) {
            instance = new MapHelper();
        }
        return instance;
    }

    public MapHelper() {
        googleMapFragment = SupportMapFragment.newInstance();
        googleMapFragment.getMapAsync(this);
    }

    public static GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public static SupportMapFragment getGoogleMapFragment() {
        return googleMapFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        initializeLocation();
    }

    @SuppressLint("MissingPermission")
    public void initializeLocation() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);        // 지도에 현재위치 버튼 보이게
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);        // 지도에 줌버튼 보이게
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);               //나침반이 나타나도록 설정
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));     // 매끄럽게 이동함

        GpsHelper.getInstance().defaultCamera();
    }

    public void setLocationMarker(LatLng latLng, String title, String branch, String snippet) {
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title+branch).snippet(snippet).draggable(true));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
        mGoogleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(GpsHelper.getInstance().getCurrentLocation().latitude,
                        GpsHelper.getInstance().getCurrentLocation().longitude), ZOOM_LEVEL));
        return false;
    }


}
