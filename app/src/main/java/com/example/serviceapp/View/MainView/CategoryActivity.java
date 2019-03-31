package com.example.serviceapp.View.MainView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.serviceapp.BottomSheet.CategoryBottomSheet;
import com.example.serviceapp.Fragment.CategoryToolbar;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.NearbyPoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

public class CategoryActivity extends AppCompatActivity {

    public String seletedCategory;
    public String fbId;
    public Client placesClient;
    public MapHelper mapHelper;
    public CategoryBottomSheet bottomSheet;
    private SupportMapFragment googleMapFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        placesClient = new Client();
        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.googleMapFragment;
        getIntentData();

        // 검색된 주변 카테고리 지도에 마커핀 찍기
        requestCategoryData(seletedCategory);

        // CategoryBottomSheet 셋팅
        View bottomSheetView = (LinearLayout) findViewById(R.id.bottom_sheet);

        bottomSheet = new CategoryBottomSheet(getApplicationContext(), this, bottomSheetView);
        bottomSheet.addBottomSheetContent(0);

        bottomSheet.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);


        // Category Toolbar로 교체
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container1, new CategoryToolbar(), "visible");
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "category");
        fragmentTransaction.commit();

        Log.d("ddd", "map id : " + googleMapFragment.getTag() + googleMapFragment.getId());
    }


    public String getSeletedCategory() {
        return seletedCategory;
    }

    public void getIntentData() {
        Intent intent = getIntent();
        fbId = intent.getStringExtra("fb_id");
        seletedCategory = intent.getStringExtra("seleted_category");
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
