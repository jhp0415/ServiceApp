package com.example.serviceapp.BottomSheet;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.serviceapp.Fragment.SearchFragment;
import com.example.serviceapp.Fragment.SearchToolbar;
import com.example.serviceapp.Helper.BottomSheetHelper;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.GeocodeRequest;
import com.kt.place.sdk.net.GeocodeResponse;
import com.kt.place.sdk.net.NearbyPoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class CategoryBottomSheet
        implements View.OnClickListener {
    private Context mContext;
    private Activity mActivity;
    private View view;
    private Client placesClient;

    @BindView(R.id.default_info) TextView textView;
    @BindView(R.id.nearby_img1) ImageView imageView1;
    @BindView(R.id.nearby_img2) ImageView imageView2;
    @BindView(R.id.nearby_img3) ImageView imageView3;
    @BindView(R.id.nearby_img4) ImageView imageView4;
    @BindView(R.id.nearby_img5) ImageView imageView5;
    @BindView(R.id.nearby_img6) ImageView imageView6;
    @BindView(R.id.nearby_img7) ImageView imageView7;
    @BindView(R.id.nearby_img8) ImageView imageView8;

    public CategoryBottomSheet(Context context, Activity activity, View view) {
        this.mContext = context;
        this.mActivity = activity;
        this.view = view;
        ButterKnife.bind(this, this.view);

        placesClient = new Client();
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        imageView6.setOnClickListener(this);
        imageView7.setOnClickListener(this);
        imageView8.setOnClickListener(this);

        GpsHelper.getInstance().setOnCurrentAddressListener(listener);
    }

    GpsHelper.OnCurrentAddress listener = new GpsHelper.OnCurrentAddress() {
        @Override
        public void onCurrentAddress(Location location) {
            setCurrentAddress(location);
        }
    };


    public View getView() {
        return view;
    }

    public void setCurrentAddress(Location location) {
        // 현재 위치 주소

        requestGeocodeResult(location);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nearby_img1:
                requestCategoryData("편의점");
                break;
            case R.id.nearby_img2:
                requestCategoryData("병원");
                break;
            case R.id.nearby_img3:
                requestCategoryData("약국");
                break;
            case R.id.nearby_img4:
                requestCategoryData("관광지");
                break;
            case R.id.nearby_img5:
                requestCategoryData("주유소");
                break;
            case R.id.nearby_img6:
                requestCategoryData("주차장");
                break;
            case R.id.nearby_img7:
                requestCategoryData("은행");
                break;
            case R.id.nearby_img8:
                // NO
                break;
        }
        BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
        FragmentManager fragmentManager = ((MainActivity) mActivity).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container1, SearchToolbar.getInstance(),"visible");
        fragmentTransaction.replace(R.id.fragment_container2, MapHelper.getMapInstance(),"visible");
        fragmentTransaction.addToBackStack("Category");
        fragmentTransaction.commit();
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
                    MapHelper.getInstance(mContext, mActivity).setLocationMarker(result, poi.getName(), poi.getBranch(), poi.getAddress().getFullAddressRoad());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }
        });
    }


    public void requestGeocodeResult(Location location){
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        final GeocodeRequest request = new GeocodeRequest.GeocodeRequestBuilder()
                .setLat(point.latitude)
                .setLng(point.longitude)
                .build();

        placesClient.getGeocode(request, new OnSuccessListener<GeocodeResponse>() {
            @Override
            public void onSuccess(@NonNull GeocodeResponse geocodeResponse) {
                String fullAddress = geocodeResponse.getGeocodeList().get(0).getParcelAddressList().get(0).getSiDo() + " "
                        + geocodeResponse.getGeocodeList().get(0).getParcelAddressList().get(0).getSiGunGu() + " "
                        + geocodeResponse.getGeocodeList().get(0).getParcelAddressList().get(0).getEupMyeonDong() + " ";
                textView.setText(fullAddress);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
    }


}
