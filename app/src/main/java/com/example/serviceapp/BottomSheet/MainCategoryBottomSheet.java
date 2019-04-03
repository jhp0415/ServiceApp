package com.example.serviceapp.BottomSheet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.example.serviceapp.View.MainView.CategoryActivity;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnResponseListener;
import com.kt.place.sdk.net.GeocodeRequest;
import com.kt.place.sdk.net.GeocodeResponse;
import com.kt.place.sdk.util.PlaceClient;
import com.kt.place.sdk.util.PlaceManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainCategoryBottomSheet implements View.OnClickListener {
    private Context mContext;
    private Activity mActivity;
    private View view;
    private PlaceClient placesClient;
    public static BottomSheetBehavior bottomSheetBehavior;
    public View bottomSheetView;
    private LinearLayout dynamicContent;
    private View mainContent;

    @BindView(R.id.default_info) TextView textView;
    @BindView(R.id.nearby_img1) ImageView imageView1;
    @BindView(R.id.nearby_img2) ImageView imageView2;
    @BindView(R.id.nearby_img3) ImageView imageView3;
    @BindView(R.id.nearby_img4) ImageView imageView4;
    @BindView(R.id.nearby_img5) ImageView imageView5;
    @BindView(R.id.nearby_img6) ImageView imageView6;
    @BindView(R.id.nearby_img7) ImageView imageView7;
    @BindView(R.id.nearby_img8) ImageView imageView8;

    public MainCategoryBottomSheet(Context context, Activity activity, View view) {
        this.mContext = context;
        this.mActivity = activity;
        this.view = view;
        ButterKnife.bind(this, this.view);

        placesClient = PlaceManager.createClient();
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

    public void addBottomSheetContent(int id) {
        switch (id) {
            case 0:
                // 카테고리
                dynamicContent.removeAllViews();
                mainContent = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_nearby, dynamicContent, false);
                dynamicContent.addView(mainContent);
                ButterKnife.bind(this, mainContent);
                imageView1.setOnClickListener(this);
                imageView2.setOnClickListener(this);
                imageView3.setOnClickListener(this);
                imageView4.setOnClickListener(this);
                imageView5.setOnClickListener(this);
                imageView6.setOnClickListener(this);
                imageView7.setOnClickListener(this);
                imageView8.setOnClickListener(this);
                break;
            case 1:
                // Poi 검색 결과
                dynamicContent.removeAllViews();
                mainContent = LayoutInflater.from(mContext)
                        .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);
                dynamicContent.addView(mainContent);

                break;
        }
    }

    public View getView() {
        return view;
    }

    public void setBottomSheetHeight(float height) {
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, height, mContext.getResources().getDisplayMetrics()));
    }

    public void setBottomSheetState(String state) {
        switch (state) {
            case "COLLAPSED":
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case "EXPANDED":
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
        }
    }

    public int getBottomSheetState() {
        return bottomSheetBehavior.getState();
    }

    public void setVisibility(boolean flag) {
        if(flag)
            bottomSheetView.setVisibility(View.VISIBLE);
        else
            bottomSheetView.setVisibility(View.GONE);
    }


    public void setCurrentAddress(Location location) {
        // 현재 위치 주소
        requestGeocodeResult(location);
    }

    @Override
    public void onClick(View v) {
        Log.d("ddd", "CategoryBottomSheet Click Event");
        Intent intent = new Intent(mActivity, CategoryActivity.class);
        intent.putExtra("fb_id", ((MainActivity)mActivity).getFbId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (v.getId()) {
            case R.id.nearby_img1:
                intent.putExtra("seleted_category", "편의점");
                break;
            case R.id.nearby_img2:
                intent.putExtra("seleted_category", "병원");
                break;
            case R.id.nearby_img3:
                intent.putExtra("seleted_category", "약국");
                break;
            case R.id.nearby_img4:
                intent.putExtra("seleted_category", "관광지");
                break;
            case R.id.nearby_img5:
                intent.putExtra("seleted_category", "주유소");
                break;
            case R.id.nearby_img6:
                intent.putExtra("seleted_category", "주차장");
                break;
            case R.id.nearby_img7:
                intent.putExtra("seleted_category", "은행");
                break;
            case R.id.nearby_img8:
//                intent.putExtra("seleted_category", "편의점");
                // NO
                break;
        }
        if(mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).mainBottomSheet.setBottomSheetState("COLLAPSED");
        }
        mActivity.startActivity(intent);
    }

    public void requestGeocodeResult(Location location){
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        final GeocodeRequest request = new GeocodeRequest.GeocodeRequestBuilder(point.latitude, point.longitude).build();

        placesClient.getGeocode(request, new OnResponseListener<GeocodeResponse>() {
            @Override
            public void onSuccess(@NonNull GeocodeResponse geocodeResponse) {
//                String fullAddress = geocodeResponse.getGeocodeList().get(0).getParcelAddressList().get(0).getSiDo() + " "
//                        + geocodeResponse.getGeocodeList().get(0).getParcelAddressList().get(0).getSiGunGu() + " "
//                        + geocodeResponse.getGeocodeList().get(0).getParcelAddressList().get(0).getEupMyeonDong() + " ";
                textView.setText(geocodeResponse.getGeocodeList().get(0).roadAddressList.get(0).getFullStreetAddress());

            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }
        });
    }

}
