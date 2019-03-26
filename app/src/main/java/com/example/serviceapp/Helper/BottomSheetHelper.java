package com.example.serviceapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.serviceapp.Adapter.AutocompleteRecyclerAdapter;
import com.example.serviceapp.Adapter.ReviewRecyclerAdapter;
import com.example.serviceapp.BottomSheet.CategoryBottomSheet;
import com.example.serviceapp.Login.POJO.sComment;
import com.example.serviceapp.Login.contract.OverviewContract;
import com.example.serviceapp.Login.contract.ReviewContract;
import com.example.serviceapp.Login.presenter.OverviewPresenter;
import com.example.serviceapp.Login.presenter.ReviewPresenter;
import com.example.serviceapp.Login.view.AddPhotoActivity;
import com.example.serviceapp.Login.view.AddReviewActivity;
import com.example.serviceapp.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.util.Client;

import java.util.List;


public class BottomSheetHelper extends BottomSheetBehavior.BottomSheetCallback
        implements View.OnClickListener,
        OverviewContract.View,
        ReviewContract.View{

    private Context mContext;
    private View view;
    private Activity mActivity;
    public static BottomSheetBehavior bottomSheetBehavior;
    public LinearLayout bottomSheet;
    private LinearLayout dynamicContent;
    private LinearLayout mainContent;
    private Client placesClient;

    private CategoryBottomSheet categoryBottomSheet;
    private View poiView;

    OverviewPresenter presenter;
    private ReviewPresenter reviewPresenter;
    private String fbId;
    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;

    public BottomSheetHelper(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.view = (LinearLayout) mActivity.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(view);
        bottomSheetBehavior.setBottomSheetCallback(this);
        dynamicContent = (LinearLayout) view.findViewById(R.id.dynamic_content);
        placesClient = new Client();

        poiView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);
    }

    public void addBottomSheetContent(int id) {
        switch (id) {
            case 0:
                // TODO : bottom sheet 높이 조절
                bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 80.f, mContext.getResources().getDisplayMetrics()));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                dynamicContent.removeAllViews();
                View wizardView = LayoutInflater.from(mContext)
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
                categoryBottomSheet = new CategoryBottomSheet(mContext, mActivity, mainView);
                mainView = categoryBottomSheet.getView();
                mainContent.addView(mainView);
                break;
            case 1:

                break;
        }
    }

    public void updatePoiInfo(final Poi poi) {
        dynamicContent.removeAllViews();
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);

        // TODO : bottom sheet 높이 조절
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 150.f, mContext.getResources().getDisplayMetrics()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        TextView textView1 = (TextView) poiView.findViewById(R.id.poi_name);
        TextView textView2 = (TextView) poiView.findViewById(R.id.poi_distance);
        TextView textView3 = (TextView) poiView.findViewById(R.id.poi_category);
        TextView textView4 = (TextView) poiView.findViewById(R.id.poi_address);
        TextView textView5 = (TextView) poiView.findViewById(R.id.poi_phone);
        textView1.setText(poi.getName() + poi.getBranch());
        textView2.setText(String.valueOf((int) Math.round(poi.getDistance())) + "km");
        textView3.setText(poi.getCategory().getMasterName());
        textView4.setText(poi.getAddress().getFullAddressParcel());
        if (poi.getPhones().getRepresentation() != null)
            if (poi.getPhones().getRepresentation().size() > 0)
                textView5.setText(poi.getPhones().getRepresentation().get(0));

        // Image, Review 불러오기
        presenter = new OverviewPresenter(this);
        presenter.getOverviewInfo(poi.getId());

        // 리뷰 리스트 셋팅
        reviewPresenter = new ReviewPresenter(this);
        reviewPresenter.getReviewList(poi.getId());

        // 사진, 리뷰 버튼 리스너
        LinearLayout addPhotoBtn = (LinearLayout) poiView.findViewById(R.id.add_photo);
        LinearLayout addListBtn = (LinearLayout) poiView.findViewById(R.id.add_mylist);
        Button addReviewBtn = (Button) poiView.findViewById(R.id.add_review);

        // 사진 추가 버튼
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddPhotoActivity.class);
                intent.putExtra("poi_id", poi.getId());
                intent.putExtra("fb_id", fbId);
                mActivity.startActivityForResult(intent, REQUEST_ADD_PHOTO);
            }
        });
        // 즐겨찾기 추가 버튼
        addListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addMyListCurrentPlace(fbId, poi.getId());
            }
        });
        // 리뷰 추가 버튼
        addReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, AddReviewActivity.class);
                intent.putExtra("poi_id", poi.getId());
                intent.putExtra("fb_id", fbId);
                mActivity.startActivityForResult(intent, REQUEST_ADD_REVIEW);
            }
        });



        dynamicContent.addView(poiView);
    }

    public void updateAutocompleteList(List<Poi> list) {
        dynamicContent.removeAllViews();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_autocomplete_list, dynamicContent, false);

        // TODO : bottom sheet 높이 조절
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 400.f, mContext.getResources().getDisplayMetrics()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_DRAGGING: {
                break;
            }
            case BottomSheetBehavior.STATE_SETTLING: {
                break;
            }
            case BottomSheetBehavior.STATE_EXPANDED: {
                break;
            }
            case BottomSheetBehavior.STATE_COLLAPSED: {
                break;
            }
            case BottomSheetBehavior.STATE_HIDDEN: {
                break;
            }
        }
    }
    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        // 1이면 완전 펼쳐진 상태, 0이면 peekHeight인 상태, -1이면 숨김 상태
    }

    @Override
    public void onClick(View v) {
        View  mainView;
        switch (v.getId()) {
            case R.id.content_main_btn_nearby:
                addBottomSheetContent(0);
                break;
            case R.id.content_main_btn_tmp:
                mainContent.removeAllViews();
                mainView = LayoutInflater.from(mContext)
                        .inflate(R.layout.content_main_wishlist, mainContent, false);
                mainContent.addView(mainView);
                break;
        }
    }


    /**
     * 내 서버에 등록된 사진과 리뷰 정보 가져와서 셋팅하기
     * @param
     */
    @Override
    public void setOverviewImage(List<String> poiImages) {
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);
//        ImageView imageView = view.findViewById(R.id.poiImage);
        SliderLayout mDemoSlider = poiView.findViewById(R.id.poiImageSlider);
        mDemoSlider.removeAllSliders();

        for(String poiImage : poiImages) {
            TextSliderView sliderView = new TextSliderView(mContext);

            Log.d("ddd", "setOverviewImage URL: " + poiImage);
            sliderView.image(poiImage)
                    .setProgressBarVisible(true);
            mDemoSlider.addSlider(sliderView);

        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
    }

    @Override
    public void setOverviewInfo(Poi place) {
//        String placeName = place.getName() + " " + place.getBranch();
//        TextView placeNameView = getView().findViewById(R.id.poiName);
//        placeNameView.setText(placeName);
    }

    @Override
    public void setReviewList(List<sComment> comments) {
        Log.d("ddd", "setReviewList : 리뷰 리스트 출력하기");
        for(sComment s : comments) {
            Log.d("ddd", s.getUser().getName() + " " + s.getCaptionTitle());
        }
//        View view = LayoutInflater.from(mContext)
//                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);

        // 리사이클러뷰 초기화
        RecyclerView recyclerView = (RecyclerView) poiView.findViewById(R.id.reviewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        ReviewRecyclerAdapter mAdapter = new ReviewRecyclerAdapter(mActivity);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setFilter(comments);

//        dynamicContent.addView(poiView);
    }
}
