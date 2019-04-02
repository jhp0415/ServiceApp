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
import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnResponseListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.net.RetrievePoiRequest;
import com.kt.place.sdk.util.PlaceClient;
import com.kt.place.sdk.util.PlaceManager;

public class PoiInfoActivity extends AppCompatActivity {

    private String poiId;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapHelper mapHelper;
    private PlaceClient placesClient;
    private SupportMapFragment googleMapFragment;
    private PoiInfoBottomSheet bottomSheet;
    private Util util;
    public String fbId;

    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;
    public static final int REQUEST_EDIT_REVIEW = 3;
    public static final int REQUEST_DELETE_REVIEW = 4;

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

        placesClient = PlaceManager.createClient();

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
        // 사진
        if (requestCode == REQUEST_ADD_PHOTO && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_photo");
            sPlaceOverview review = (sPlaceOverview) data.getSerializableExtra("place_overview");
            bottomSheet.setOverviewImage(review.getPlacePicUrl());
        }
        // 리뷰
        if (requestCode == REQUEST_ADD_REVIEW && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_review");
            sPlaceWithComment review = (sPlaceWithComment) data.getSerializableExtra("place_review");
            bottomSheet.setReviewList(review.getComments());
        }
        if (requestCode == REQUEST_EDIT_REVIEW && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_review");
            sComment comment = (sComment) data.getSerializableExtra("update_review");
            int position = data.getExtras().getInt("position");
            bottomSheet.setReviewList(position, comment);
        }
        if (requestCode == REQUEST_DELETE_REVIEW && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_review");
            int position = data.getExtras().getInt("position");
            bottomSheet.setReviewList(position);
        }
    }

    public void onFragmentResult(Poi data) {
        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.point.lat,
                data.point.lng), data.name, data.branch, data.address.getFullAddressRoad());

        // TODO : 바텀 시트 업데이트
        bottomSheet.setVisibility(true);
        bottomSheet.updatePoiInfo(data);
    }

    public void requestRetrievePoi(String id) {
        RetrievePoiRequest request = new RetrievePoiRequest.RetrievePoiRequestBuilder(id).build();

        placesClient.getRetrievePoi(request, new OnResponseListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                poi = poiResponse.getPois().get(0);
                onFragmentResult(poi);
                PoiToolbar.getInstance().setTitle(poi.name + " " + poi.branch);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
    }
}
