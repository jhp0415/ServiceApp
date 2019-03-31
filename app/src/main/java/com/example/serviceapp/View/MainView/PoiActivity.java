package com.example.serviceapp.View.MainView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.serviceapp.BottomSheet.PoiInfoBottomSheet;
import com.example.serviceapp.Fragment.SearchFragment;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.facebook.CallbackManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.model.Suggest;
import com.kt.place.sdk.net.AutocompleteRequest;
import com.kt.place.sdk.net.AutocompleteResponse;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

import java.util.List;

public class PoiActivity extends AppCompatActivity
        implements TextWatcher, View.OnClickListener{

    private MapHelper mapHelper;
    private Client placesClient;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Util util;

    private SupportMapFragment googleMapFragment;
    private PoiInfoBottomSheet bottomSheet;

    // 페이스북 로그인
    private CallbackManager callbackManager; // Facebook manager
    private String fbId;
    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;

    // 툴바
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        Log.d("ddd", "PoiActivity 재실행");
        // intent 데이터 받기 -> fbId
        getIntentData();

        // fragment 설정
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(),"visible");
        fragmentTransaction.commit();

        util = new Util(getApplicationContext(), this);
        placesClient = new Client();

        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.googleMapFragment;

        // bottom Sheet 설정
        View bottomSheetView = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheet = new PoiInfoBottomSheet(getApplicationContext(), this, bottomSheetView);
        bottomSheet.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheet.setVisibility(false);

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editText = (EditText)toolbar.findViewById(R.id.toolbar_search);
        editText.setText(null);
        editText.setOnClickListener(this);
        editText.addTextChangedListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void getIntentData() {
        Intent intent = getIntent();
        fbId = intent.getStringExtra("fb_id");
    }

    /**
     * 뒤로가기 했을때, 드로우 레이아웃 닫기
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = fragmentManager.findFragmentByTag("visible");
        if(fragment instanceof SearchFragment) {
            finish();
        } else {
            Intent intent = new Intent(this, PoiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

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
//        hideKyeboard();
        switch (item.getItemId()){
            case android.R.id.home:
                Fragment fragment = fragmentManager.findFragmentByTag("visible");
                if(fragment instanceof SearchFragment) {
                    finish();
                } else {
                    Intent intent = new Intent(this, PoiActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() == 0) {

        } else {
            setEditTextQuery(s.toString());
            requestPoiSearch2(s.toString(), 0);
            requestAutocomplete(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
    }

    /**
     * 프레그먼트 간의 통신을 위해
     * @param query
     */
    public void setEditTextQuery(String query) {
        SearchFragment.getInstance().setEditTextQuery(query);
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
                    bottomSheet.updateAutocompleteList(poiResponse.getPois());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    public void requestPoiSearch2(final String terms, int start) {
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
                    setRecycleView(poiResponse.getPois());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    public void requestAutocomplete(final String terms) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        AutocompleteRequest request = new AutocompleteRequest.AutocompleteRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .build();

        placesClient.getAutocomplete(request, new OnSuccessListener<AutocompleteResponse>() {
            @Override
            public void onSuccess(@NonNull AutocompleteResponse autocompleteResponse) {
                if(autocompleteResponse.getSuggestList().size() > 0) {
                    setAutocompleteView(autocompleteResponse.getSuggestList());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    /**
     * 검색결과 리사이클러뷰 셋팅
     * @param pois
     */
    public void setRecycleView(List<Poi> pois){
        if(pois.size() > 0) {
            SearchFragment.getInstance().setRecyclerView(pois);
        }
    }

    public void setRecycleViewPlus(List<Poi> pois){
        if(pois.size() > 0) {
            SearchFragment.getInstance().setRecyclerViewPlus(pois);
        }
    }

    public void setAutocompleteView(List<Suggest> suggests){
        if(suggests.size() > 0) {
            SearchFragment.getInstance().setAutocompleteView(suggests);
        }
    }

    /**
     * 리사이클러뷰 아이템 클릭 이벤트 시 실행되는 함수
     * @param data
     */
    public void onFragmentResult(Poi data) {
        util.hideKyeboard();

        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.googleMapFragment;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "visible");
        fragmentTransaction.commit();

        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getName(), data.getBranch(), data.getAddress().getFullAddressParcel());

        // TODO : 바텀 시트 업데이트
        bottomSheet.setVisibility(true);
        bottomSheet.updatePoiInfo(data);
    }

    public void onFragmentResultAutocomplete(Suggest data) {
        util.hideKyeboard();

        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.googleMapFragment;

        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getTerms(), "", "");

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "visible");
        fragmentTransaction.commit();

        // TODO : 바텀 시트 업데이트
        requestPoiSearch(data.getTerms(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_search:
                Fragment currentFragment = fragmentManager.findFragmentByTag("visible");
                if (!(currentFragment instanceof SearchFragment)) {
                    Intent intent = new Intent(this, PoiActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
        }
    }
}
