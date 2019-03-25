package com.example.serviceapp.Helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class GpsHelper extends Service
        implements LocationListener {

    public static GpsHelper instance;
    private GoogleMap mGoogleMap;
    private Activity activity;

    // GPS
    private Context mContext;
    private LatLng defaultPoint = new LatLng(37.57248123626738, 126.97783713788459);        //kT광화문west
    private LocationManager locationManager;
    public boolean isAccessFineLocation = false;
    public boolean isAccessCoarseLocation = false;
    public boolean mLocationPermissionGranted = false;
    public final int PERMISSION_REQUEST_CODE = 1;
    private boolean isGPSEnabled = false;       // 현재 GPS 사용유무
    private boolean isNetworkEnabled = false;       // 네트워크 사용유무
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;     // 최소 GPS 정보 업데이트 거리 1미터
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;      // 최소 GPS 정보 업데이트 시간 밀리세컨(1분)
    private Location mLastKnownLocation = null;
    private int ZOOM_LEVEL = 15;

    private final String TAG = "ddd";

    // BottomSheet에 현재 위치 주소를 표시하기 위한 리스너
    public interface OnCurrentAddress {
        void onCurrentAddress(Location location);
    }
    public OnCurrentAddress callbackListener;
    public void setOnCurrentAddressListener(OnCurrentAddress callback) {
        this.callbackListener = callback;
    }

    public static GpsHelper getInstance() {
        if (instance == null) {
            instance = new GpsHelper();
        }
        return instance;
    }

    public GpsHelper() {
    }

    public void setGpsHelperInit(Context context, GoogleMap googleMap, Activity activity) {
        this.mContext = context;
        this.mGoogleMap = googleMap;
        this.activity = activity;

        // GPS 초기화
        this.locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);     // GPS 정보 가져오기
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// 현재 네트워크 상태 값 알아오기
    }

    public void requestPermission() {
        if (mGoogleMap == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            receivedPermission();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    public void receivedPermission() {
        if(mLocationPermissionGranted) {
            getDeviceLocation();
            MapHelper.initializeMap();
            setCameraOnLocation();
        }
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation() {
        if (mLocationPermissionGranted) {     // 권한 있을 때,
            Log.d("ddd", isNetworkEnabled + " ");
            if (isNetworkEnabled) {     // 네트워크 위치 정보 사용 가능 여부
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    mLastKnownLocation = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            Log.d("ddd", isGPSEnabled + " ");
            if (isGPSEnabled) {     // GPS 위치 정보 사용 가능 여부
                if (mLastKnownLocation == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        mLastKnownLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        }
    }

    public void setCameraOnLocation() {
        // Set the map's camera position to the current location of the device.
        if (mLastKnownLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), ZOOM_LEVEL));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPoint, ZOOM_LEVEL));
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    // 현재 위치 데이터 받아오기
    public LatLng getCurrentLocation() {
        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        return latLng;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        Log.d(TAG, "onLocationChanged: mLastKnownLocation -> " + mLastKnownLocation.getLatitude() + ", " +
                mLastKnownLocation.getLongitude());

        if(callbackListener != null) {
            callbackListener.onCurrentAddress(mLastKnownLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
