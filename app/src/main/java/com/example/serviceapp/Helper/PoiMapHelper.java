package com.example.serviceapp.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.serviceapp.View.MainView.CategoryActivity;
import com.example.serviceapp.View.MainView.PoiActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

public class PoiMapHelper
        implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback,
        OnMyLocationButtonClickListener {

    public static MapHelper instance;
    public static GoogleMap mGoogleMap;
    private Context mContext;
    private Activity mActivity;
    public SupportMapFragment googleMapFragment;

    // GoogleMap
    public static int ZOOM_LEVEL = 17;


    public PoiMapHelper(Context context, Activity activity) {
        googleMapFragment = SupportMapFragment.newInstance();
        googleMapFragment.getMapAsync(this);

        this.mContext = context;
        this.mActivity = activity;
    }


    public static MapHelper getInstance(Context context, Activity activity) {
        if (instance == null) {
            instance = new MapHelper(context, activity);
        }
        return instance;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        GpsHelper.getInstance().setGpsHelperInit(mContext, this.mGoogleMap, mActivity);
        GpsHelper.getInstance().requestPermission();
    }

    @SuppressLint("MissingPermission")
    public static void initializeMap() {
        if (GpsHelper.getInstance().mLocationPermissionGranted) {
            MapHelper.mGoogleMap.setMyLocationEnabled(true);
            MapHelper.mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            MapHelper.mGoogleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(GpsHelper.getInstance().getCurrentLocation().latitude,
                                    GpsHelper.getInstance().getCurrentLocation().longitude), ZOOM_LEVEL));
                    return false;
                }
            });
            MapHelper.mGoogleMap.getUiSettings().setZoomControlsEnabled(true);        // 지도에 줌버튼 보이게
            MapHelper.mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
            MapHelper. mGoogleMap.getUiSettings().setCompassEnabled(true);               //나침반이 나타나도록 설정
            MapHelper.mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(MapHelper.ZOOM_LEVEL));     // 매끄럽게 이동함
        } else {
            Log.d("ddd", "현재위치를 받아올 수 없습니다.");
            MapHelper.mGoogleMap.setMyLocationEnabled(false);
            MapHelper.mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    public void setLocationMarker(LatLng latLng, String title, String branch, String snippet) {
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title+branch).snippet(snippet).draggable(true));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.setOnMarkerClickListener(this);
    }

    public void setLocationMarker(LatLng latLng, String title) {
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title).draggable(true));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String term = marker.getTitle();
        LatLng currentPoint = GpsHelper.getInstance().getCurrentLocation();
        PoiRequest request = new PoiRequest.PoiRequestBuilder(term)
                .setLat(currentPoint.latitude)
                .setLng(currentPoint.longitude)
                .setNumberOfResults(1)
                .build();

        Client placesClient = new Client();
        placesClient.getPoiSearch(request, new OnSuccessListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                if(poiResponse.getPois().size() > 0) {
                    // 마커 클릭해서 POI 정보 bottom sheet에 나타내기
//                    BottomSheetHelper.getInstance(mContext, mActivity).updatePoiInfo(poiResponse.getPois().get(0));
                    if(mActivity instanceof CategoryActivity) {
                        ((CategoryActivity) mActivity).bottomSheet.updatePoiInfo(poiResponse.getPois().get(0));

                    } else if(mActivity instanceof PoiActivity){

                    }

                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
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
