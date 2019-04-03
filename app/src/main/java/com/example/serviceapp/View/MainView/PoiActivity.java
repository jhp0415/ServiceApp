package com.example.serviceapp.View.MainView;

import android.content.Context;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.serviceapp.Adapter.ParentRecyclerAdapter;
import com.example.serviceapp.Adapter.ReviewRecyclerAdapter;
import com.example.serviceapp.BottomSheet.PoiInfoBottomSheet;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.facebook.CallbackManager;
import com.facebook.appevents.codeless.CodelessLoggingEventListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnResponseListener;
import com.kt.place.sdk.model.Autocomplete;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.net.RetrievePoiRequest;
import com.kt.place.sdk.util.PlaceClient;
import com.kt.place.sdk.util.PlaceManager;

public class PoiActivity extends AppCompatActivity
        implements View.OnClickListener{

    private MapHelper mapHelper;
    private PlaceClient placesClient;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Util util;

    private SupportMapFragment googleMapFragment;
    View bottomSheetView;
    private PoiInfoBottomSheet bottomSheet;

    // 페이스북 로그인
    private CallbackManager callbackManager; // Facebook manager

    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;
    public static final int REQUEST_EDIT_REVIEW = 3;
    public static final int REQUEST_DELETE_REVIEW = 4;

    // 툴바
    EditText editText;
    private String fbId;
    String poiId;
    String mode;
    Poi poi;
    Autocomplete autocomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        // intent 데이터 받기 -> fbId
        getIntentData();

        // fragment 설정
        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.googleMapFragment;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment,"visible");
        fragmentTransaction.commit();


        placesClient = PlaceManager.createClient();

        // bottom Sheet 설정
        bottomSheetView = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheet = new PoiInfoBottomSheet(getApplicationContext(), this, bottomSheetView);
        bottomSheet.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheet.setFbId(fbId);
        bottomSheet.setVisibility(false);

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editText = (EditText)toolbar.findViewById(R.id.toolbar_search);
        editText.setText(null);
        editText.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setResult(mode);
    }

    public void getIntentData() {
        Intent intent = getIntent();
        fbId = intent.getStringExtra("fb_id");
        poiId = intent.getStringExtra("poi_id");
        poi = (Poi) intent.getExtras().getSerializable("poi");
        mode = intent.getStringExtra("mode");
        Log.d("ddd", "PoiActivity getIntentData: fbId " + fbId);
    }

    public void setResult(String mode) {
        switch (mode) {
            case "poi":
                onFragmentResult(poi);

                break;
            case "autocomplete":
                modeAutocomplete(poiId);
//                onFragmentResultAutocomplete(poi);
                break;
        }
    }

    /**
     * 뒤로가기 했을때, 드로우 레이아웃 닫기
     */
    @Override
    public void onBackPressed() {
        finish();
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

    public String getFbId() {
        return fbId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void modeAutocomplete(String id) {
        RetrievePoiRequest request = new RetrievePoiRequest.RetrievePoiRequestBuilder(id).build();

        placesClient.getRetrievePoi(request, new OnResponseListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                onFragmentResultAutocomplete(poiResponse.getPois().get(0));
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
    }

    public void requestPoiSearchForAutocomplete(final String terms, int start) {
        Log.d("ddd", "autocomplete clicked item and Poi Search : " + terms);
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        PoiRequest request = new PoiRequest.PoiRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .setStart(start)
                .setNumberOfResults(10)
                .build();

        placesClient.getPoiSearch(request, new OnResponseListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                if(poiResponse.getPois().size() > 0) {
                    Log.d("ddd", "Poi Search Result : " + poiResponse.getPois().get(0).name + " " + poiResponse.getPois().get(0).branch);
                    bottomSheet.updateAutocompleteList(poiResponse.getPois());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    /**
     * 리사이클러뷰 아이템 클릭 이벤트 시 실행되는 함수
     * @param data
     */
    public void onFragmentResult(Poi data) {
//        util.hideKyeboard();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        editText.setText(data.name + " " + data.branch);

        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.point.lat,
                data.point.lng), data.name, data.branch, data.address.getFullAddressRoad());

        // TODO : 바텀 시트 업데이트
        bottomSheet.setVisibility(true);
        bottomSheet.updatePoiInfo(data);
    }

    public void onFragmentResultAutocomplete(Poi data) {
//        util.hideKyeboard();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

//        // TODO : 바텀 시트 업데이트
//        requestPoiSearchForAutocomplete(data.name, 0);
        // TODO : 바텀 시트 업데이트
        requestPoiSearchForAutocomplete(data.name + " " + data.branch, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_search:
                Log.d("ddd", "onClick: PoiActivity 종료");
                finish();
                break;
        }
    }
}
