package com.example.serviceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.serviceapp.Fragment.SearchFragment;
import com.example.serviceapp.Fragment.SearchToolbar;
import com.example.serviceapp.Helper.BottomSheetHelper;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.Login.POJO.sAccess;
import com.example.serviceapp.Login.POJO.sPlaceOverview;
import com.example.serviceapp.Login.POJO.sPlaceWithComment;
import com.example.serviceapp.Login.contract.MyServerContract;
import com.example.serviceapp.Login.presenter.MyServerPresenter;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.model.Suggest;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;
import com.kt.place.sdk.util.Manager;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
                implements FragmentManager.OnBackStackChangedListener,
        NavigationView.OnNavigationItemSelectedListener,
        MyServerContract.View {

    private GpsHelper gpsHelper;
    private MapHelper mapHelper;
    private Client placesClient;
    private SupportMapFragment googleMapFragment;
    private BottomSheetHelper bottomSheetHelper;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    MyServerContract.View myServerView = this;

    // 페이스북 로그인
    private CallbackManager callbackManager; // Facebook manager
    private sAccess fbInfo; //Facebook Access info
    private String fbId;
    private String fbToken;
    private String clickedPoiId;
    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsHelper = new GpsHelper();
        mapHelper = new MapHelper(getApplicationContext(), this);
        googleMapFragment = mapHelper.getMapInstance();


        Manager.initialize(getApplicationContext(), "Bearer eb142d9027f84d51a4a20df8490e44bcf6fc7ef4dea64cae96a7fca282ebd8cc02764651");
        placesClient = new Client();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container1, FragmentToolbar.newInstance(), "visible");
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "visible");
        fragmentTransaction.commit();
        fragmentManager.addOnBackStackChangedListener(this);

        // Bottom Sheet 초기화
        bottomSheetHelper =
                new BottomSheetHelper(getApplicationContext(), this);
        bottomSheetHelper.addBottomSheetContent(0);

        // 네비게이션 드로워
        FrameLayout linearLayout = (FrameLayout) findViewById(R.id.fragment_container1);
        LayoutInflater layoutInflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.fragment_toolbar, linearLayout, true);

        Toolbar toolbar = (Toolbar)linearLayout.findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 네비게이션 헤더 뷰
        View nav_header_view = navigationView.getHeaderView(0);
        Button loginButton = (Button)nav_header_view.findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 페이스북 로그인
                facebookLoginOnClick();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 툴바 검색창
        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, SearchToolbar.getInstance(),"visible");
                fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(),"visible");
                fragmentTransaction.addToBackStack("SearchFragment");
                fragmentTransaction.commit();
            }
        });


        // 의준 오빠 서버 로그인하기
        myServerLogin();
    }

    /**
     * 툴바 왼쪽 홈버튼
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 툴바 오른쪽 옵션 메뉴. 현재는 사용안할 예정
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(fragmentManager.getBackStackEntryCount() == 0) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param menuItem
     * @return
     * 드로우 레이아웃 안에 네비게이션 아이템 클릭 이벤트
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_home:
                // TODO: 홈 화면으로 이동하기
                fragmentManager.popBackStack(0, fragmentManager.getBackStackEntryCount() - 1);
                break;
            case R.id.nav_wishlist:
                bottomSheetHelper.addBottomSheetContent(1);
                bottomSheetHelper.setBottomSheetState("EXPANDED");
                break;
            case R.id.nav_search:
                // TODO: 검색 화면으로 이동하기
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, SearchToolbar.getInstance(),"visible");
                fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(),"visible");
                fragmentTransaction.addToBackStack("SearchFragment");
                fragmentTransaction.commit();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 뒤로가기 했을때, 드로우 레이아웃 닫기
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if(BottomSheetHelper.getInstance(getApplicationContext(), this).getBottomSheetState() == BottomSheetBehavior.STATE_EXPANDED) {
            BottomSheetHelper.getInstance(getApplicationContext(), this).setBottomSheetState("COLLAPSED");
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 페이스북 로그인 실행 결과 받기
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_PHOTO && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_photo");
            sPlaceOverview review = (sPlaceOverview) data.getSerializableExtra("place_overview");
            bottomSheetHelper.setOverviewImage(review.getPlacePicUrl());
        }
        if (requestCode == REQUEST_ADD_REVIEW && resultCode == RESULT_OK) {
            Log.d("ddd", "ActivityResult : Request_add_review");
            sPlaceWithComment review = (sPlaceWithComment) data.getSerializableExtra("place_review");
            bottomSheetHelper.setReviewList(review.getComments());
        }
    }

    public String getFbId() {
        return fbId;
    }


    public void setFbInfo(sAccess info) {
        fbId = info.getFbId();
        fbInfo = info;
    }
    /**
     * 페이스북 로그인하기
     */
    public void facebookLoginOnClick(){
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult result) {
                GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            fbId = user.optString("id");
                            fbToken = result.getAccessToken().getToken();
                            setResult(RESULT_OK);
                            MyServerPresenter presenter = new MyServerPresenter(myServerView);
                            presenter.getSignCheck(fbToken);
                            Log.d("ddd", "MainActivity : facebook login success : " + fbId);
                            // 로그인 버튼 상태 바꾸기 로그인->로그아웃
                            View nav_header_view = navigationView.getHeaderView(0);
                            Button loginButton = (Button)nav_header_view.findViewById(R.id.login_btn);
                            loginButton.setText("로그아웃");
                            // TODO: 로그인 이미지 바꾸기
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                //finish();
            }
            @Override
            public void onCancel() {
                //finish();
            }
        });
    }

    public void myServerLogin() {
        // 페이스북 로그인 여부 체크
        callbackManager = CallbackManager.Factory.create();

        //SharedPreferences fbPref = getPreferences(MODE_PRIVATE);
        //String access_token = fbPref.getString("access_token", null);
        //long expires = fbPref.getLong("access_expires", 0);

        //if(access_token == null || expires == 0) {
        // TODO: access token expire 처리

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken == null) {
            SharedPreferences fbPref = getPreferences(MODE_PRIVATE);
            fbToken = fbPref.getString("access_token", null);
        }
        else {
            fbToken = accessToken.getToken();
        }

        if(fbToken == null) {
            //setContentView(R.layout.activity_main);
        }
        else {
            fbInfo = new sAccess();
            Log.d("access_token", fbToken);
            // TODO: 의준오빠 서버 로그인하기
            MyServerPresenter presenter = new MyServerPresenter(this);
            presenter.getSignCheck(fbToken);

            Log.d("ddd", "MainActivity : get facebook token : " + fbToken);
            // 로그인 버튼 상태 바꾸기 로그인->로그아웃
            View nav_header_view = navigationView.getHeaderView(0);
            Button loginButton = (Button)nav_header_view.findViewById(R.id.login_btn);
            loginButton.setText("로그아웃");
        }
    }

    /**
     * GPS 권한 받기
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == gpsHelper.PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gpsHelper.isAccessFineLocation = true;
        }
        if (gpsHelper.isAccessFineLocation) {
            gpsHelper.mLocationPermissionGranted = true;
            gpsHelper.receivedPermission();
        }
    }

    /**
     * 프레그먼트 간의 통신을 위해
     * @param query
     */
    public void setEditTextQuery(String query) {
        SearchFragment.getInstance().setEditTextQuery(query);
    }

    public void onFragmentResult(Poi data) {
//        hideKyeboard();
        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getName(), data.getBranch(), data.getAddress().getFullAddressParcel());

        Fragment currentFragment = fragmentManager.findFragmentByTag("visible");
        if(currentFragment instanceof SupportMapFragment) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "visible");
            fragmentTransaction.addToBackStack("googleMapFragment");
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "visible");
            fragmentTransaction.addToBackStack("googleMapFragment");
            fragmentTransaction.commit();
        }

        // TODO : 바텀 시트 업데이트
        bottomSheetHelper.updatePoiInfo(data);
    }

    public void onFragmentResultAutocomplete(Suggest data) {
//        hideKyeboard();
        mapHelper.mGoogleMap.clear();
        mapHelper.setLocationMarker(new LatLng(data.getPoint().getLat(),
                data.getPoint().getLng()), data.getTerms(), "", "");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, googleMapFragment, "visible");
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

    @Override
    public void onBackStackChanged() {
        Log.d("ddd", "BackStackCount : " + fragmentManager.getBackStackEntryCount());
        Fragment currentFragment = fragmentManager.findFragmentByTag("visible");
        if(currentFragment instanceof SupportMapFragment) {
            Log.d("ddd", "currentFragment : googleMapFragment");
            bottomSheetHelper.setVisibility(true);
            if(fragmentManager.getBackStackEntryCount() == 0) {
                bottomSheetHelper.setBottomSheetHeight(75.f);
                bottomSheetHelper.addBottomSheetContent(0);
            }
        } else if(currentFragment instanceof SearchFragment) {
            Log.d("ddd", "currentFragment : SearchFragment");
            bottomSheetHelper.setVisibility(false);
        } else {
            Log.d("ddd", "TAG : else case");
            bottomSheetHelper.setVisibility(true);
            if(fragmentManager.getBackStackEntryCount() == 0) {
                mapHelper.onMyLocationButtonClick();
                bottomSheetHelper.addBottomSheetContent(0);
            }
        }

        for(int i=fragmentManager.getBackStackEntryCount() - 1; i>=0 ; i--) {
            Log.d("ddd", i + "번째 스택 : " + fragmentManager.getBackStackEntryAt(i).getName());
        }
        Log.d("ddd", "====================================================");
    }
}
