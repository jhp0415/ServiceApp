package com.example.serviceapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.serviceapp.Adapter.AutocompleteRecyclerAdapter;
import com.example.serviceapp.Adapter.RecyclerAdapter;
import com.example.serviceapp.BottomSheet.CategoryBottomSheet;
import com.example.serviceapp.Fragment.CategoryFragment;
import com.example.serviceapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.NearbyPoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

import java.util.List;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;


public class BottomSheetHelper extends BottomSheetBehavior.BottomSheetCallback
        implements View.OnClickListener{
    private Context mContext;
    private View view;
    private Activity mActivity;
    public static BottomSheetBehavior bottomSheetBehavior;
    public LinearLayout bottomSheet;
    private LinearLayout dynamicContent;
    private LinearLayout mainContent;
    private Client placesClient;

    public BottomSheetHelper(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.view = (LinearLayout) mActivity.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(this);
        dynamicContent = (LinearLayout) view.findViewById(R.id.dynamic_content);
        placesClient = new Client();
    }

    public void addBottomSheetContent(int id) {
        View wizardView;
        switch (id) {
            case 0:
                dynamicContent.removeAllViews();
                wizardView = LayoutInflater.from(mContext)
                        .inflate(R.layout.bottom_sheet_content_main, dynamicContent, false);
                dynamicContent.addView(wizardView);

                Button button1 = (Button) mActivity.findViewById(R.id.content_main_btn_nearby);
                Button button2 = (Button) mActivity.findViewById(R.id.content_main_btn_tmp);
                button1.setOnClickListener(this);
                button2.setOnClickListener(this);

                mainContent = (LinearLayout) wizardView.findViewById(R.id.main_content_tap);
                View mainView = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_nearby, mainContent, false);
                // 카테고리 검색 리스너 달기
                CategoryBottomSheet categoryBottomSheet = new CategoryBottomSheet(mContext, mainView);
                mainView = categoryBottomSheet.getView();

                mainContent.addView(mainView);

                break;
            case 1:

                break;
            case 2:

                break;
        }

    }

    public void updatePoiInfo(Poi poi) {
        dynamicContent.removeAllViews();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);
        TextView textView1 = (TextView) view.findViewById(R.id.poi_name);
        TextView textView2 = (TextView) view.findViewById(R.id.poi_distance);
        TextView textView3 = (TextView) view.findViewById(R.id.poi_category);
        TextView textView4 = (TextView) view.findViewById(R.id.poi_address);
        TextView textView5 = (TextView) view.findViewById(R.id.poi_phone);
        textView1.setText(poi.getName() + poi.getBranch());
        textView2.setText(String.valueOf((int) Math.round(poi.getDistance())) + "km");
        textView3.setText(poi.getCategory().getMasterName());
        textView4.setText(poi.getAddress().getFullAddressParcel());
        if (poi.getPhones().getRepresentation() != null)
            if (poi.getPhones().getRepresentation().size() > 0)
                textView5.setText(poi.getPhones().getRepresentation().get(0));
        dynamicContent.addView(view);
    }

    public void updateAutocompleteList(List<Poi> list) {
        dynamicContent.removeAllViews();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_autocomplete_list, dynamicContent, false);

        // 리사이클러뷰 초기화
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.autocomplete_search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        AutocompleteRecyclerAdapter mAdapter = new AutocompleteRecyclerAdapter(mActivity);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setFilter(list);


        dynamicContent.addView(view);
    }

    public void setVisibility(boolean flag) {
        if(flag)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

    public View getBottomSheetView() {
        return view;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_DRAGGING: {
//                Log.d("ddd", "DRAGGING");
                break;
            }
            case BottomSheetBehavior.STATE_SETTLING: {
//                Log.d("ddd", "SETTLING");
                break;
            }
            case BottomSheetBehavior.STATE_EXPANDED: {
//                Log.d("ddd", "EXPANDED");
                break;
            }
            case BottomSheetBehavior.STATE_COLLAPSED: {
//                Log.d("ddd", "COLLAPSED");
                break;
            }
            case BottomSheetBehavior.STATE_HIDDEN: {
//                Log.d("ddd", "HIDDEN");
                break;
            }
        }
    }
    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        // 1이면 완전 펼쳐진 상태, 0이면 peekHeight인 상태, -1이면 숨김 상태
//        Log.i("TAG", "slideOffset " + (slideOffset));;
    }

    @Override
    public void onClick(View v) {
        View  mainView;
        switch (v.getId()) {
            case R.id.content_main_btn_nearby:
                mainContent.removeAllViews();
                mainView = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_nearby, mainContent, false);
                mainContent.addView(mainView);
                break;
            case R.id.content_main_btn_tmp:
                mainContent.removeAllViews();
                mainView = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_tmp, mainContent, false);
                mainContent.addView(mainView);
                break;
        }
    }

}
