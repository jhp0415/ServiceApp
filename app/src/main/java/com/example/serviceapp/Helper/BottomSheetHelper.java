package com.example.serviceapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
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

import com.example.serviceapp.Adapter.AutocompleteRecyclerAdapter;
import com.example.serviceapp.Adapter.ItemTouchHelperCallback;
import com.example.serviceapp.Adapter.MyListRecyclerAdapter;
import com.example.serviceapp.Adapter.ReviewRecyclerAdapter;
import com.example.serviceapp.BottomSheet.CategoryBottomSheet;
import com.example.serviceapp.Login.POJO.sComment;
import com.example.serviceapp.Login.POJO.sPlace;
import com.example.serviceapp.Login.contract.MyListContract;
import com.example.serviceapp.Login.contract.OverviewContract;
import com.example.serviceapp.Login.contract.ReviewContract;
import com.example.serviceapp.Login.presenter.MyListPresenter;
import com.example.serviceapp.Login.presenter.OverviewPresenter;
import com.example.serviceapp.Login.presenter.ReviewPresenter;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.util.Client;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetHelper extends BottomSheetBehavior.BottomSheetCallback
        implements View.OnClickListener,
        OverviewContract.View,
        ReviewContract.View,
        MyListContract.View {
    private static BottomSheetHelper instance;
    public static BottomSheetHelper getInstance(Context context, Activity activity) {
        if(instance == null) {
            instance = new BottomSheetHelper(context, activity);
        }
        return instance;
    }
    private Context mContext;
    private View rootView;
    private Activity mActivity;
    public static BottomSheetBehavior bottomSheetBehavior;
    public LinearLayout bottomSheet;
    private LinearLayout dynamicContent;
    private LinearLayout mainContent;
    private Client placesClient;

    private CategoryBottomSheet categoryBottomSheet;
    private View poiView;
    private View mylistView;

    public OverviewPresenter presenter;
    private ReviewPresenter reviewPresenter;
    private MyListPresenter mylistPresenter;
    private String fbId;
    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;

    ImageView button1;
    ImageView button2;

    private  RecyclerView poiReviewRecyclerView;
    private ReviewRecyclerAdapter poiReviewRecyclerViewAdapter;

    public BottomSheetHelper(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.rootView = (LinearLayout) mActivity.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(rootView);
        bottomSheetBehavior.setBottomSheetCallback(this);
        dynamicContent = (LinearLayout) rootView.findViewById(R.id.dynamic_content);
        placesClient = new Client();

        poiView = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_poi_info, dynamicContent, false);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.content_main_btn_nearby:
                // bottom sheet state 확인
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    setBottomSheetState("EXPANDED");
                } else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    setBottomSheetState("COLLAPSED");
                }
                addBottomSheetContent(0);
                break;
            case R.id.content_main_btn_mylist:
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    setBottomSheetState("EXPANDED");
                } else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    setBottomSheetState("COLLAPSED");
                }
                addBottomSheetContent(1);
                break;
        }
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

    public void addBottomSheetContent(int id) {
        dynamicContent.removeAllViews();
        View wizardView = LayoutInflater.from(mContext)
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
                categoryBottomSheet = new CategoryBottomSheet(mContext, mActivity, mainView);
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

        dynamicContent.addView(poiView);

//        TextView textView1 = (TextView) poiView.findViewById(R.id.poi_name);
//        TextView textView2 = (TextView) poiView.findViewById(R.id.poi_distance);
//        TextView textView3 = (TextView) poiView.findViewById(R.id.poi_category);
//        TextView textView4 = (TextView) poiView.findViewById(R.id.poi_address);
//        TextView textView5 = (TextView) poiView.findViewById(R.id.poi_phone);
//        textView1.setText(poi.getName() + poi.getBranch());
//        if(poi.getDistance() != null) {
//            textView2.setText(String.valueOf((int) Math.round(poi.getDistance())) + "km");
//        }
//        textView3.setText(poi.getCategory().getMasterName());
//        textView4.setText(poi.getAddress().getFullAddressParcel());
//        if (poi.getPhones().getRepresentation() != null)
//            if (poi.getPhones().getRepresentation().size() > 0)
//                textView5.setText(poi.getPhones().getRepresentation().get(0));

        // poi
        poiReviewRecyclerViewAdapter.setPoiInfo(poi);

        // Image, Review 불러오기
        presenter = new OverviewPresenter(this);
        presenter.getOverviewInfo(poi.getId());

        // 리뷰 리스트 셋팅
        reviewPresenter = new ReviewPresenter(this);
        reviewPresenter.getReviewList(poi.getId());



//        // 사진, 리뷰 버튼 리스너
//        LinearLayout addPhotoBtn = (LinearLayout) poiView.findViewById(R.id.add_photo);
//        LinearLayout addListBtn = (LinearLayout) poiView.findViewById(R.id.add_mylist);
//        Button addReviewBtn = (Button) poiView.findViewById(R.id.add_review);
//
//        // 사진 추가 버튼
//        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, AddPhotoActivity.class);
//                intent.putExtra("poi_id", poi.getId());
//                intent.putExtra("fb_id", ((MainActivity) mActivity).getFbId());
//                mActivity.startActivityForResult(intent, REQUEST_ADD_PHOTO);
//            }
//        });
//        // 즐겨찾기 추가 버튼
//        addListBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("ddd", "즐겨찾기에 추가 버튼 클릭");
//                presenter.addMyList(((MainActivity) mActivity).getFbId(), poi.getId());
//            }
//        });
//        // 리뷰 추가 버튼
//        addReviewBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //데이터 담아서 팝업(액티비티) 호출
//                Intent intent = new Intent(mActivity, AddReviewActivity.class);
//                intent.putExtra("poi_id", poi.getId());
//                intent.putExtra("fb_id", ((MainActivity) mActivity).getFbId());
//                mActivity.startActivityForResult(intent, REQUEST_ADD_REVIEW);
//            }
//        });

//        dynamicContent.addView(poiView);
    }

    public void updateAutocompleteList(List<Poi> list) {
        dynamicContent.removeAllViews();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.bottom_sheet_content_autocomplete_list, dynamicContent, false);

        // TODO : bottom sheet 높이 조절
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

    public void setVisibility(boolean flag) {
        if(flag)
            rootView.setVisibility(View.VISIBLE);
        else
            rootView.setVisibility(View.GONE);
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


    /**
     * 내 서버에 등록된 사진과 리뷰 정보 가져와서 셋팅하기
     * @param
     */
    @Override
    public void setOverviewImage(List<String> poiImages) {
//        SliderLayout mDemoSlider = poiView.findViewById(R.id.poiImageSlider);
//        mDemoSlider.removeAllSliders();
//
//        for(String poiImage : poiImages) {
//            TextSliderView sliderView = new TextSliderView(mContext);
//
//            sliderView.image(poiImage)
//                    .setProgressBarVisible(true);
//            mDemoSlider.addSlider(sliderView);
//        }
//        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        poiReviewRecyclerViewAdapter.setImageResource(poiImages);
    }

    @Override
    public void clearOverviewImage() {
//        Log.d("ddd", "이미지 슬라이드 초기화");
//        SliderLayout mDemoSlider = poiView.findViewById(R.id.poiImageSlider);
//        mDemoSlider.removeAllSliders();
        poiReviewRecyclerViewAdapter.imageUrlClear();
    }

    @Override
    public void setOverviewInfo(Poi place) {
//        String placeName = place.getName() + " " + place.getBranch();
//        TextView placeNameView = getView().findViewById(R.id.poiName);
//        placeNameView.setText(placeName);
    }

    @Override
    public void setReviewList(List<sComment> comments) {
        // 리사이클러뷰 초기화
//        poiReviewRecyclerView = (RecyclerView) poiView.findViewById(R.id.review_recyclerview);
//        poiReviewRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
//        poiReviewRecyclerView.setHasFixedSize(true);
//        poiReviewRecyclerViewAdapter = new ReviewRecyclerAdapter(mActivity);
//        poiReviewRecyclerView.setAdapter(poiReviewRecyclerViewAdapter);
        poiReviewRecyclerViewAdapter.setFilter(comments);
    }

    @Override
    public void clearReviewList() {
        Log.d("ddd", "리뷰 리스트 초기화");
        // 리사이클러뷰 초기화
        poiReviewRecyclerViewAdapter.clear();
    }

    /**
     * 내 즐겨찾기 리스트 리사이클러뷰에 붙이기
     * @param response
     */
    @Override
    public void setMyList(ArrayList<sPlace> response) {
        TextView pleaseLoginText = (TextView) mylistView.findViewById(R.id.please_login);
        pleaseLoginText.setText("");

        // 리사이클러뷰 초기화
        RecyclerView recyclerView = (RecyclerView) mylistView.findViewById(R.id.mylist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setHasFixedSize(true);
        MyListRecyclerAdapter mAdapter = new MyListRecyclerAdapter(mActivity, mylistPresenter);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setFilter(response);

        // 리사이클러뷰 swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
