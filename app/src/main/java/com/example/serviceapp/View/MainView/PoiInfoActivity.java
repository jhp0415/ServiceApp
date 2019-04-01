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

import com.example.serviceapp.BottomSheet.PoiInfoBottomSheet;
import com.example.serviceapp.Fragment.PoiToolbar;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.net.RetrievePoiRequest;
import com.kt.place.sdk.util.Client;

public class PoiInfoActivity extends AppCompatActivity {

    private String poiId;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapHelper mapHelper;
    private Client placesClient;
    private SupportMapFragment googleMapFragment;
    private PoiInfoBottomSheet bottomSheet;
    private Util util;
    public String fbId;

    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;

    private Poi poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_info);
        // intent 데이터 받기 -> fbId
        getIntentData();


        // fragment 설정
        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.googleMapFragment;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container1, new PoiToolbar(),"visible");
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment,"visible");
        fragmentTransaction.commit();

        placesClient = new Client();

        // bottom Sheet 설정
        View bottomSheetView = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheet = new PoiInfoBottomSheet(getApplicationContext(), this, bottomSheetView);
        bottomSheet.setFbId(fbId);
        bottomSheet.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheet.setVisibility(false);


        requestRetrievePoi(poiId);
    }

    public void getIntentData() {
        Intent intent = getIntent();
        fbId = intent.getStringExtra("fb_id");
        poiId = intent.getStringExtra("poi_id");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_PHOTO && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_photo");
            sPlaceOverview review = (sPlaceOverview) data.getSerializableExtra("place_overview");
            bottomSheet.setOverviewImage(review.getPlacePicUrl());
        }
        if (requestCode == REQUEST_ADD_REVIEW && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_review");
            sPlaceWithComment review = (sPlaceWithComment) data.getSerializableExtra("place_review");
            bottomSheet.setReviewList(review.getComments());
        }
    }

    public void onFragmentResult(Poi data) {

        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getName(), data.getBranch(), data.getAddress().getFullAddressParcel());


        Log.d("ddd", "map id : " + googleMapFragment.getTag() + googleMapFragment.getId());

        // TODO : 바텀 시트 업데이트
        bottomSheet.setVisibility(true);
        bottomSheet.updatePoiInfo(data);
    }

    public void requestRetrievePoi(String id) {
        RetrievePoiRequest request = new RetrievePoiRequest.RetrievePoiRequestBuilder().setId(id).build();

        placesClient.getRetrievePoi(request, new OnSuccessListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                poi = poiResponse.getPois().get(0);
                onFragmentResult(poi);
                PoiToolbar.getInstance().setTitle(poi.getName());
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
    }
}