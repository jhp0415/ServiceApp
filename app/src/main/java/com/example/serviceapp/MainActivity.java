package com.example.serviceapp;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.serviceapp.Fragment.CategoryFragment;
import com.example.serviceapp.Fragment.FragmentToolbar;
import com.example.serviceapp.Fragment.SearchFragment;
import com.example.serviceapp.Helper.BottomSheetHelper;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.Helper.PlacesApiHelper;
import com.example.serviceapp.Listener.OnGpsListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.model.Suggest;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;
import com.kt.place.sdk.util.Manager;

import java.util.List;

public class MainActivity extends AppCompatActivity
                implements FragmentManager.OnBackStackChangedListener {

    private GpsHelper gpsHelper;
    private MapHelper mapHelper;
    private Client placesClient;

    private BottomSheetHelper bottomSheetHelper;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapHelper = new MapHelper();
        Manager.initialize(getApplicationContext(), "Bearer eb142d9027f84d51a4a20df8490e44bcf6fc7ef4dea64cae96a7fca282ebd8cc02764651");
        placesClient = new Client();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container1, FragmentToolbar.newInstance(), "visible");
        fragmentTransaction.replace(R.id.fragment_container2, mapHelper.googleMapFragment, "visible");
//        fragmentTransaction.addToBackStack("googleMapFragment");
        fragmentTransaction.commit();

        // Bottom Sheet 초기화
        bottomSheetHelper =
                new BottomSheetHelper(getApplicationContext(), this);
        bottomSheetHelper.addBottomSheetContent(0);

        gpsHelper = new GpsHelper();
//        gpsHelper.setGpsHelperInit(getApplicationContext(), this);
        fragmentManager.addOnBackStackChangedListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == gpsHelper.PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gpsHelper.isAccessFineLocation = true;
        }
        if (gpsHelper.isAccessFineLocation) {
            gpsHelper.mLocationPermissionGranted = true;
            gpsHelper.getDeviceLocation();
//            listener.onSuccessMapReady();
        }
    }

    public void setEditTextQuery(String query) {
        SearchFragment.getInstance().setEditTextQuery(query);
    }

    public void onFragmentResult(Poi data) {
//        hideKyeboard();
        mapHelper.getGoogleMap().clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getName(), data.getBranch(), data.getAddress().getFullAddressParcel());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, mapHelper.googleMapFragment, "visible");
        fragmentTransaction.addToBackStack("googleMapFragment");
        fragmentTransaction.commit();

        // TODO : 바텀 시트 업데이트
        bottomSheetHelper.updatePoiInfo(data);
    }

    public void onFragmentResultAutocomplete(Suggest data) {
//        hideKyeboard();
        mapHelper.getGoogleMap().clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getTerms(), "", "");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, mapHelper.googleMapFragment, "visible");
        fragmentTransaction.addToBackStack("googleMapFragment");
        fragmentTransaction.commit();

        // TODO : 바텀 시트 업데이트
        requestPoiSearch(data.getTerms(), 0);
    }

    public void requestPoiSearch(final String terms, int start) {
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
                    bottomSheetHelper.updateAutocompleteList(poiResponse.getPois());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    public void setRecycleView(List<Poi> pois){
        if(pois.size() > 0) {
            SearchFragment.getInstance().setRecyclerView(pois);
        }
    }

    public void setAutocompleteView(List<Suggest> suggests){
        if(suggests.size() > 0) {
            SearchFragment.getInstance().setAutocompleteView(suggests);
        }
    }

    @Override
    public void onBackStackChanged() {
        Log.d("ddd", "BackStackCount : " + fragmentManager.getBackStackEntryCount());
        Fragment currentFragment = fragmentManager.findFragmentByTag("visible");
        if(currentFragment instanceof SupportMapFragment) {
            Log.d("ddd", "currentFragment : googleMapFragment");
            bottomSheetHelper.setVisibility(true);
            if(fragmentManager.getBackStackEntryCount() == 0) {
                bottomSheetHelper.addBottomSheetContent(0);
            }
        } else if(currentFragment instanceof CategoryFragment) {
            Log.d("ddd", "currentFragment : CategoryFragment");
            bottomSheetHelper.setVisibility(false);
        } else if(currentFragment instanceof SearchFragment) {
            Log.d("ddd", "currentFragment : SearchFragment");
            bottomSheetHelper.setVisibility(false);
        } else {
            Log.d("ddd", "TAG : " + currentFragment.getTag());
            bottomSheetHelper.setVisibility(true);
            if(fragmentManager.getBackStackEntryCount() == 0) {
                bottomSheetHelper.addBottomSheetContent(0);
            }
        }

        for(int i=fragmentManager.getBackStackEntryCount() - 1; i>=0 ; i--) {
            Log.d("ddd", i + "번째 스택 : " + fragmentManager.getBackStackEntryAt(i).getName());
        }
        Log.d("ddd", "====================================================");
    }
}
