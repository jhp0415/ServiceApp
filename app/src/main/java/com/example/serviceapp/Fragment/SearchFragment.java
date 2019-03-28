package com.example.serviceapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.serviceapp.Adapter.AutocompleteRecyclerAdapter;
import com.example.serviceapp.Adapter.RecyclerAdapter;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.model.Suggest;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

import java.util.List;


public class SearchFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private static SearchFragment instance;
    private Client placesClient;
    private MainActivity activity;
    private RecyclerAdapter mAdapter;
    private AutocompleteRecyclerAdapter mAutocompleteAdapter;
    private RecyclerView recyclerView;
    private RecyclerView autocompleteRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int mLastKnowIndex = -1; // 현재까지 보여준 poi 인덱스
    private int mNextStartIndex = 0;    // 다음 호출할 poi 번호
    private int numberOfResults = 10;

    public String finalTerms = "";

    public static SearchFragment getInstance(){
        if(instance == null) {
            instance = new SearchFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchlist, parentViewGroup, false);

        activity = (MainActivity)getActivity();
        placesClient = new Client();

        // RecyclerView 초기화
        recyclerView = (RecyclerView) view.findViewById(R.id.poi_search_recycler2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        // SwipeRefreshLayout 초기화
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;

                if (lastVisibleItemPosition == itemTotalCount) {
                    // 다음 리스트 받아오기
                    mLastKnowIndex = mLastKnowIndex + numberOfResults;
                    mNextStartIndex = mLastKnowIndex + 1;
                    recyclerView.scrollToPosition(0);

                    requestPoiSearch(finalTerms, mNextStartIndex);
                }
            }
        });
        return view;
    }

    public void setEditTextQuery(String query) {
        finalTerms = query;
    }

    @Override
    public void onRefresh() {
        // 새로고침 코드
        mNextStartIndex = 0;
        mLastKnowIndex = -1;
        recyclerView.scrollToPosition(0);
        // 새로고침 종료
        swipeRefreshLayout.setRefreshing(false);
        Log.d("ddd", "새로고침");
    }

    public void setRecyclerView(List<Poi> pois){
        mAdapter.setFilter(pois);
    }

    public void setRecyclerViewPlus(List<Poi> pois){
        mAdapter.setFilterPlus(pois);
    }

    public void setAutocompleteView(List<Suggest> suggests){
//        mAutocompleteAdapter.setFilter(suggests);
        mAdapter.setHeaderFilter(suggests);
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
                    activity.setRecycleViewPlus(poiResponse.getPois());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }
}