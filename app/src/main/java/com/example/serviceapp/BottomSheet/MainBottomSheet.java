package com.example.serviceapp.BottomSheet;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.serviceapp.Adapter.MyListRecyclerAdapter;
import com.example.serviceapp.Adapter.RecyclerItemTouchHelper;
import com.example.serviceapp.Adapter.ReviewRecyclerAdapter;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlace;
import com.example.serviceapp.MyServer.contract.MyListContract;
import com.example.serviceapp.MyServer.contract.OverviewContract;
import com.example.serviceapp.MyServer.contract.ReviewContract;
import com.example.serviceapp.MyServer.presenter.MyListPresenter;
import com.example.serviceapp.MyServer.presenter.OverviewPresenter;
import com.example.serviceapp.MyServer.presenter.ReviewPresenter;
import com.example.serviceapp.R;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.util.PlaceClient;
import com.kt.place.sdk.util.PlaceManager;

import java.util.ArrayList;
import java.util.List;

public class MainBottomSheet extends BottomSheetBehavior.BottomSheetCallback
        implements View.OnClickListener,
        MyListContract.View,
        OverviewContract.View,
        ReviewContract.View{

    // must need parameter
    private Context mContext;
    private Activity mActivity;
    private View rootView;

    // bottom sheet main resource
    public static BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout dynamicContent;
    private LinearLayout mainContent;
    private PlaceClient placesClient;

    // Main Bottom Sheet content
    private MainCategoryBottomSheet categoryBottomSheet;
    public static MyListBottomSheet myListBottomSheet;
    public ArrayList<sPlace> myList = new ArrayList<>();
    private View mylistView;

    // 즐겨찾기 목록 받아오기
    private MyListPresenter mylistPresenter;

    // 주변검색 버튼, 내 즐겨찾기 리스트 버튼
    ImageView button1;
    ImageView button2;
    private View wizardView;

    // 즐겨 찾기 클릭해서 poi 정보 출력하기
    private RecyclerView poiReviewRecyclerView;
    private ReviewRecyclerAdapter poiReviewRecyclerViewAdapter;
    private View poiView;
    public OverviewPresenter presenter;
    private ReviewPresenter reviewPresenter;

    public MainBottomSheet(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.rootView = (LinearLayout) mActivity.findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(rootView);
        bottomSheetBehavior.setBottomSheetCallback(this);
        dynamicContent = (LinearLayout) rootView.findViewById(R.id.dynamic_content);
        placesClient = PlaceManager.createClient();

        // 메인 바텀시트 셋팅
        dynamicContent.removeAllViews();
        wizardView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_main, dynamicContent, false);
        dynamicContent.addView(wizardView);

        // 메인 버튼
        button1 = (ImageView) mActivity.findViewById(R.id.content_main_btn_nearby);
        button2 = (ImageView) mActivity.findViewById(R.id.content_main_btn_mylist);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        poiView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);
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
            rootView.setVisibility(View.VISIBLE);
        else
            rootView.setVisibility(View.GONE);
    }

    public void addBottomSheetContent(int id) {
        // 메인 바텀시트 셋팅
        dynamicContent.removeAllViews();
        wizardView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_main, dynamicContent, false);
        dynamicContent.addView(wizardView);
        // 메인 버튼
        button1 = (ImageView) mActivity.findViewById(R.id.content_main_btn_nearby);
        button2 = (ImageView) mActivity.findViewById(R.id.content_main_btn_mylist);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        switch (id) {
            case 0:
                button1.setImageResource(R.drawable.icon_location_pins_mint);
                button2.setImageResource(R.drawable.icon_wishlist_gray);

                //TODO: 바텀시트 카테고리 검색하기
                mainContent = (LinearLayout) wizardView.findViewById(R.id.main_content_tap);
                View mainView = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_nearby, mainContent, false);
                // 카테고리 검색 리스너 달기
                categoryBottomSheet = new MainCategoryBottomSheet(mContext, mActivity, mainView);
                mainView = categoryBottomSheet.getView();
                mainContent.addView(mainView);
                break;
            case 1:
                button1.setImageResource(R.drawable.icon_location_pins_gray);
                button2.setImageResource(R.drawable.icon_wishlist_mint);
                // TODO: 즐겨찾기 리스트 출력
                mainContent = (LinearLayout) wizardView.findViewById(R.id.main_content_tap);
                mylistView = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_wishlist, mainContent, false);

                // 즐겨찾기 리스트 가져오기
                mylistPresenter = new MyListPresenter(this);
                mylistPresenter.getMyList(((MainActivity) mActivity).getFbId());

                mainContent.addView(mylistView);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_main_btn_nearby:
                // bottom sheet state 확인
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    setBottomSheetState("EXPANDED");
                }
                addBottomSheetContent(0);
                break;
            case R.id.content_main_btn_mylist:
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    setBottomSheetState("EXPANDED");
                }
                addBottomSheetContent(1);
                break;
        }
    }

    /**
     * 내 즐겨찾기 리스트 관련 함수들
     * @param response
     */
    @Override
    public void setMyList(ArrayList<sPlace> response) {
        // 즐겨찾기 리스트 저장
        myList = response;
        Log.d("ddd", "즐겨찾기 목록 가져와서 저장 완료");

        // 로그인하면 텍스트 대체하기
        TextView pleaseLoginText = (TextView) mylistView.findViewById(R.id.please_login);
        pleaseLoginText.setText("");

        // 리사이클러뷰 초기화
        RecyclerView recyclerView = (RecyclerView) mylistView.findViewById(R.id.mylist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        MyListRecyclerAdapter mAdapter = new MyListRecyclerAdapter(mActivity, mylistPresenter);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setFbid(((MainActivity) mActivity).getFbId());
        mAdapter.setFilter(response);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, mAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void setMyList(int position, String poiName) {

    }

    @Override
    public void setMyListPoi(Poi place) {
        ((MainActivity) mActivity).onFragmentResult(place);
    }

    @Override
    public void clearMyList() {

    }


    /**
     * 바텀 시트
     * @param view
     * @param i
     */
    @Override
    public void onStateChanged(@NonNull View view, int i) {

    }

    @Override
    public void onSlide(@NonNull View view, float v) {

    }

    public void updatePoiInfo(final Poi poi) {
        dynamicContent.removeAllViews();

        // TODO : bottom sheet 높이 조절
        setBottomSheetHeight(130.f);
        setBottomSheetState("COLLAPSED");

        Log.d("ddd", "ReviewRecyclerView 초기화");
        poiReviewRecyclerView = (RecyclerView) poiView.findViewById(R.id.review_recyclerview);
        poiReviewRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        poiReviewRecyclerView.setHasFixedSize(true);
        poiReviewRecyclerViewAdapter = new ReviewRecyclerAdapter(mActivity);
        poiReviewRecyclerView.setAdapter(poiReviewRecyclerViewAdapter);
        poiReviewRecyclerViewAdapter.setFbid(((MainActivity)mActivity).getFbId());
        dynamicContent.addView(poiView);

        // poi
        poiReviewRecyclerViewAdapter.setPoiInfo(poi);

        // Image, Review 불러오기
        presenter = new OverviewPresenter(this);
        presenter.getOverviewInfo(poi.id);

        // 리뷰 리스트 셋팅
        reviewPresenter = new ReviewPresenter(this);
        reviewPresenter.getReviewList(poi.id);
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
    public void setReviewList(int position, sComment comment) {
        poiReviewRecyclerViewAdapter.setUpdate(position, comment);
    }

    @Override
    public void setReviewList(int position) {
        poiReviewRecyclerViewAdapter.setDelete(position);
    }

    @Override
    public void clearReviewList() {
        poiReviewRecyclerViewAdapter.clear();
    }
}
