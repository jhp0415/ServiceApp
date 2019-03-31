package com.example.serviceapp.BottomSheet;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.serviceapp.Adapter.AutocompleteRecyclerAdapter;
import com.example.serviceapp.Adapter.ReviewRecyclerAdapter;
import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.contract.OverviewContract;
import com.example.serviceapp.MyServer.contract.ReviewContract;
import com.example.serviceapp.MyServer.presenter.OverviewPresenter;
import com.example.serviceapp.MyServer.presenter.ReviewPresenter;
import com.example.serviceapp.R;
import com.kt.place.sdk.model.Poi;

import java.util.List;

public class PoiInfoBottomSheet
        implements OverviewContract.View,
        ReviewContract.View {
    public static PoiInfoBottomSheet instance;
    public static PoiInfoBottomSheet getInstance(Context context, Activity activity, View view) {
        if(instance == null) {
            instance = new PoiInfoBottomSheet(context, activity, view);
        }
        return instance;
    }
    private Context mContext;
    private Activity mActivity;
    private View view;

    public static BottomSheetBehavior bottomSheetBehavior;
    public View bottomSheetView;
    private LinearLayout dynamicContent;
    private View poiView;

    public OverviewPresenter presenter;
    private ReviewPresenter reviewPresenter;

    private RecyclerView poiReviewRecyclerView;
    private ReviewRecyclerAdapter poiReviewRecyclerViewAdapter;

    private String fbId;

    public PoiInfoBottomSheet(Context context, Activity activity, View view) {
        this.mContext = context;
        this.mActivity = activity;
        this.bottomSheetView = view;
        instance = this;

        dynamicContent = (LinearLayout) bottomSheetView.findViewById(R.id.dynamic_content);

        poiView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);
    }

    public void setFbId(String id) {
        fbId = id;
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

    public void updatePoiInfo(final Poi poi) {
        dynamicContent.removeAllViews();

        // TODO : bottom sheet 높이 조절
        setBottomSheetHeight(150.f);
        setBottomSheetState("COLLAPSED");

        Log.d("ddd", "ReviewRecyclerView 초기화");
        poiReviewRecyclerView = (RecyclerView) poiView.findViewById(R.id.review_recyclerview);
        poiReviewRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        poiReviewRecyclerView.setHasFixedSize(true);
        poiReviewRecyclerViewAdapter = new ReviewRecyclerAdapter(mActivity);
        poiReviewRecyclerView.setAdapter(poiReviewRecyclerViewAdapter);
        poiReviewRecyclerViewAdapter.setFbid(fbId);
        dynamicContent.addView(poiView);

        // poi
        poiReviewRecyclerViewAdapter.setPoiInfo(poi);

        // Image, Review 불러오기
        presenter = new OverviewPresenter(this);
        presenter.getOverviewInfo(poi.getId());

        // 리뷰 리스트 셋팅
        reviewPresenter = new ReviewPresenter(this);
        reviewPresenter.getReviewList(poi.getId());
    }

    public void updateAutocompleteList(List<Poi> list) {
        dynamicContent.removeAllViews();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_autocomplete_list, dynamicContent, false);

        // TODO : bottom sheet 높이 조절
        setVisibility(true);
        setBottomSheetHeight(400.f);
        setBottomSheetState("COLLAPSED");

        // 리사이클러뷰 초기화
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.autocomplete_search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        AutocompleteRecyclerAdapter mAdapter = new AutocompleteRecyclerAdapter(mActivity);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setFilter(list);

        dynamicContent.addView(view);
    }

    /**
     * 내 서버에 등록된 사진과 리뷰 정보 가져와서 셋팅하기
     * @param
     */
    @Override
    public void setOverviewImage(List<String> poiImages) {
        poiReviewRecyclerViewAdapter.setImageResource(poiImages);
    }

    @Override
    public void setOverviewInfo(Poi place) {

    }

    @Override
    public void clearOverviewImage() {
        poiReviewRecyclerViewAdapter.imageUrlClear();
    }

    @Override
    public void setReviewList(List<sComment> comments) {
        poiReviewRecyclerViewAdapter.setFilter(comments);
    }

    @Override
    public void clearReviewList() {
        poiReviewRecyclerViewAdapter.clear();
    }
}
