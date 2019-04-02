package com.example.serviceapp.BottomSheet;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.serviceapp.Adapter.MyListRecyclerAdapter;
import com.example.serviceapp.Adapter.RecyclerItemTouchHelper;
import com.example.serviceapp.Adapter.ReviewRecyclerAdapter;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.MyServer.POJO.sPlace;
import com.example.serviceapp.MyServer.contract.MyListContract;
import com.example.serviceapp.MyServer.presenter.MyListPresenter;
import com.example.serviceapp.R;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;

public class MyListBottomSheet
        implements MyListContract.View{
    private Context mContext;
    private Activity mActivity;
    private View view;
    public static BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout dynamicContent;
    private View wizardView;
    private View mylistView;
    private LinearLayout mainContent;

    // 즐겨찾기 목록 받아오기
    private MyListPresenter mylistPresenter;
    public ArrayList<sPlace> myList = new ArrayList<>();

    private RecyclerView poiReviewRecyclerView;
    private ReviewRecyclerAdapter poiReviewRecyclerViewAdapter;

    private String fbId;

    public static PoiInfoBottomSheet poiInfoBottomSheet;

    public MyListBottomSheet(Context context, Activity activity, View view) {
        this.mContext = context;
        this.mActivity = activity;
        this.view = view;

        poiInfoBottomSheet = new PoiInfoBottomSheet(context, activity, view);

        mylistPresenter = new MyListPresenter(this);
        mylistPresenter.getMyList(((MainActivity) mActivity).getFbId());

        dynamicContent = (LinearLayout) view.findViewById(R.id.dynamic_content);
        dynamicContent.removeAllViews();
        wizardView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_main, dynamicContent, false);
        dynamicContent.addView(wizardView);
        mainContent = (LinearLayout) wizardView.findViewById(R.id.main_content_tap);
        mylistView = LayoutInflater.from(mContext)
                .inflate(R.layout.content_main_wishlist, mainContent, false);
        mainContent.addView(mylistView);
    }

    public void setFbId(String id) {
        fbId = id;
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
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
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
        LinearLayout pleaseLoginText = (LinearLayout) mylistView.findViewById(R.id.please_login);
        pleaseLoginText.setVisibility(View.GONE);

        // 리사이클러뷰 초기화
        RecyclerView recyclerView = (RecyclerView) mylistView.findViewById(R.id.mylist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        MyListRecyclerAdapter mAdapter = new MyListRecyclerAdapter(mActivity, mylistPresenter);
        mAdapter.setFbid(((MainActivity) mActivity).getFbId());
        recyclerView.setAdapter(mAdapter);
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
}
