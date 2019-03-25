package com.example.serviceapp.BottomSheet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.serviceapp.Helper.BottomSheetHelper;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.MapHelper;
import com.example.serviceapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.NearbyPoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

import butterknife.BindView;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;

public class CategoryBottomSheet implements View.OnClickListener{
    private Context mContext;
    private View view;
    private Client placesClient;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;

    public CategoryBottomSheet(Context context, View view) {
        this.mContext = context;
        this.view = view;
        placesClient = new Client();

        imageView1 = view.findViewById(R.id.nearby_img1);
        imageView2 = view.findViewById(R.id.nearby_img2);
        imageView3 = view.findViewById(R.id.nearby_img3);
        imageView4 = view.findViewById(R.id.nearby_img4);
        imageView5 = view.findViewById(R.id.nearby_img5);
        imageView6 = view.findViewById(R.id.nearby_img6);
        imageView7 = view.findViewById(R.id.nearby_img7);
        imageView8 = view.findViewById(R.id.nearby_img8);

        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        imageView6.setOnClickListener(this);
        imageView7.setOnClickListener(this);
        imageView8.setOnClickListener(this);
    }

    public void setView(View view) {
        this.view = view;
        view.setOnClickListener(this);
    }
    public View getView() {
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nearby_img1:
                Log.d("ddd", "image Clicked");
                requestCategoryData("편의점");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img2:
                requestCategoryData("병원");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img3:
                requestCategoryData("약국");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img4:
                requestCategoryData("관광지");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img5:
                requestCategoryData("주유소");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img6:
                requestCategoryData("주차장");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img7:
                requestCategoryData("은행");
                BottomSheetHelper.bottomSheetBehavior.setState(STATE_COLLAPSED);
                break;
            case R.id.nearby_img8:
                // NO
                break;
        }
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
                    MapHelper.getInstance().setLocationMarker(result, poi.getName(), poi.getBranch(), poi.getAddress().getFullAddressRoad());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }
        });
    }
}
