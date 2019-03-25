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

import com.example.serviceapp.Listener.OnGpsListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class GpsHelper extends Service
        implements LocationListener,
        GoogleMap.OnMyLocationButtonClickListener{

    private Context mContext;
    private Activity mActivity;
    private static GpsHelper instance;
    private OnGpsListener onGpsListener;

    // GPS
    private LatLng defaultPoint = new LatLng(37.57248123626738, 126.97783713788459);        //kT광화문west
    private LocationManager locationManager;
//    public final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
//    public final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    public boolean isAccessFineLocation = false;
    public boolean isAccessCoarseLocation = false;
    public boolean mLocationPermissionGranted = false;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private boolean isGPSEnabled = false;       // 현재 GPS 사용유무
    private boolean isNetworkEnabled = false;       // 네트워크 사용유무
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;     // 최소 GPS 정보 업데이트 거리 1미터
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;      // 최소 GPS 정보 업데이트 시간 밀리세컨(1분)
    private Location mLastKnownLocation = null;
    private int ZOOM_LEVEL = 15;

    private final String TAG = "ddd";

    public static GpsHelper getInstance() {
        if(instance == null) {
            instance = new GpsHelper();
        }
        return instance;
    }

    public GpsHelper() {

    }

    public void setGpsHelperInit(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;

        // GPS 초기화
        this.locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);     // GPS 정보 가져오기
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);// 현재 네트워크 상태 값 알아오기

        // 권한 요청
        requestGpsPermission();
    }

    private void requestGpsPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();

        } else {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation() {
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

    public void defaultCamera() {
        // Set the map's camera position to the current location of the device.
//        if (mLastKnownLocation != null) {
//            MapHelper.getInstance().getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(mLastKnownLocation.getLatitude(),
//                            mLastKnownLocation.getLongitude()), ZOOM_LEVEL));
//        } else {
//            Log.d(TAG, "Current location is null. Using defaults.");
//            MapHelper.getInstance().getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPoint, ZOOM_LEVEL));
//            MapHelper.getInstance().getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
//        }

        if (mLastKnownLocation == null) {
            MapHelper.getInstance().getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(37.4714301,
                            127.0295863), ZOOM_LEVEL));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            MapHelper.getInstance().getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPoint, ZOOM_LEVEL));
            MapHelper.getInstance().getGoogleMap().getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    // 현재 위치 데이터 받아오기
    public LatLng getCurrentLocation() {
        Location location = mLastKnownLocation;
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng latLng = new LatLng(37.4714301, 127.0295863);
        return latLng;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        Log.d(TAG, "onLocationChanged: mLastKnownLocation -> " + mLastKnownLocation.getLatitude() + ", " +
                mLastKnownLocation.getLongitude());
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
    public boolean onMyLocationButtonClick() {
        MapHelper.getInstance().getGoogleMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), ZOOM_LEVEL));
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setGpsListener(OnGpsListener listener) {
        this.onGpsListener = listener;
    }

}
