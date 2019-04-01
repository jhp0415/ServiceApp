package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.serviceapp.BottomSheet.CategoryBottomSheet;
import com.example.serviceapp.BottomSheet.PoiInfoBottomSheet;
import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.example.serviceapp.View.MainView.CategoryActivity;
import com.example.serviceapp.View.SubView.AddPhotoActivity;
import com.example.serviceapp.View.SubView.AddReviewActivity;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;
import java.util.List;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter {

    private List<sComment> items = new ArrayList<>();
    private Activity mActivity;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;
    public static final int REQUEST_ADD_PHOTO = 1;
    public static final int REQUEST_ADD_REVIEW = 2;

    private Poi poi;
    private List<String> poiImages = new ArrayList<>();
    private String fbId;

    public ReviewRecyclerAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == 0) {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_review_header, parent, false);
            holder = new ReviewRecyclerAdapter.HeaderViewHolder(view);
        }
        else {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_review_item, parent, false);
            holder = new ReviewRecyclerAdapter.ViewHolder(view);
        }
        return holder;
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    //이부분 중요!! 검색 리스트를 나오게하기 위해 꼭 필요
    public void setFilter(List<sComment> items) {
        if(items.size() > 0) {
            this.items.clear();
            this.items.addAll(items);
        }
        notifyDataSetChanged();     // 데이터 업데이트
    }

    public void setPoiInfo(Poi poi) {
        this.poi = poi;
        notifyDataSetChanged();
    }

    public void setImageResource(List<String> poiImages) {
        this.poiImages.clear();
        this.poiImages.addAll(poiImages);
        notifyItemChanged(0);
    }

    public void clear() {
        // 리뷰 클리어
        items.clear();
        notifyDataSetChanged();
    }

    public void imageUrlClear() {
        // 이미지 url 클리어
        poiImages.clear();
        notifyDataSetChanged();
    }

    public void setFbid(String id) {
        fbId = id;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if( holder instanceof ViewHolder) {
            final ViewHolder reviewHolder = (ViewHolder) holder;
            reviewHolder.mItem = items.get(position - 1);

            if(items.get(position - 1).getUser().getUserProfileUrl() != null) {
                Glide.with(mActivity.getApplicationContext())
                        .applyDefaultRequestOptions(
                                new RequestOptions().error(R.drawable.ic_broken_image_black_24dp))
                        .load(items.get(position - 1).getUser().getUserProfileUrl())
                        .apply(RequestOptions.circleCropTransform().override(300, 300))
                        .into(reviewHolder.mUserImage);
            }
            reviewHolder.mTitleText.setText(items.get(position - 1).getCaptionTitle());
            reviewHolder.mBodyText.setText(items.get(position - 1).getCaptionBody());
            reviewHolder.mNameText.setText(items.get(position - 1).getUser().getName());

            // 리뷰 수정 기능
            reviewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    // 내 리뷰일때만
//                    if(reviewHolder.mItem.getUser().getName() == fbInfo.getName()) {
//                        // 수정 창 열기
//                        Intent intent = new Intent(mActivity, EditReviewActivity.class);
//                        intent.putExtra("title", reviewHolder.mTitleText.getText());
//                        intent.putExtra("body", reviewHolder.mBodyText.getText());
//                        mActivity.startActivityForResult(intent, REQUST_EDIT_REVIEW);
//                    }
                    return false;
                }
            });

        } else if (position == TYPE_HEADER && holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            // 헤더에 poi 정보 출력하기
            if(poi != null) {
                headerViewHolder.textView1.setText(poi.getName() + poi.getBranch());
                if (poi.getDistance() != null) {
//                    headerViewHolder.textView2.setText(String.valueOf((int) Math.round(poi.getDistance())) + "km");
                    Util util = new Util(mActivity.getApplicationContext(), mActivity);
                    headerViewHolder.textView2.setText(util.changeMeterToKilometer((int) Math.round(poi.getDistance())));
                }
                headerViewHolder.textView3.setText(poi.getCategory().getMasterName());
                headerViewHolder.textView4.setText(poi.getAddress().getFullAddressParcel());
                if (poi.getPhones().getRepresentation() != null)
                    if (poi.getPhones().getRepresentation().size() > 0)
                        headerViewHolder.textView5.setText(poi.getPhones().getRepresentation().get(0));
            }
            // 슬라이드 이미지뷰
            if(poiImages != null) {
                headerViewHolder.mDemoSlider.removeAllSliders();
                for (String poiImage : poiImages) {
                    TextSliderView sliderView = new TextSliderView(mActivity.getApplicationContext());
                    sliderView.image(poiImage)
                            .setProgressBarVisible(true);
                    headerViewHolder.mDemoSlider.addSlider(sliderView);
                }
                headerViewHolder.mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                headerViewHolder.mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            }
        }
    }

    // 리뷰
    public class ViewHolder extends RecyclerView.ViewHolder {
        public sComment mItem;
        public final View mView;
        public final ImageView mUserImage;
        public final TextView mTitleText;
        public final TextView mBodyText;
        public final TextView mNameText;

        public ViewHolder(View view) {
            super(view);
            mView = view;       // View 초기화
            mUserImage = (ImageView) view.findViewById(R.id.user_profile);
            mTitleText = (TextView) view.findViewById(R.id.user_comment_title);
            mBodyText = (TextView) view.findViewById(R.id.user_comment_body);
            mNameText = (TextView) view.findViewById(R.id.user_name);

            if(mTitleText == null) {
                Log.d("ddd", "mTitleText is null");
            }
        }
    }

    // poi, image
    public class HeaderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final View mView;
        public final TextView textView1;
        public final TextView textView2;
        public final TextView textView3;
        public final TextView textView4;
        public final TextView textView5;

        public final SliderLayout mDemoSlider;
        public final LinearLayout addPhotoBtn;
        public final LinearLayout addListBtn;
        public final Button addReviewBtn;

        public HeaderViewHolder(View headerView) {
            super(headerView);
            mView = headerView;       // View 초기화
            textView1 = (TextView) mView.findViewById(R.id.poi_name);
            textView2 = (TextView) mView.findViewById(R.id.poi_distance);
            textView3 = (TextView) mView.findViewById(R.id.poi_category);
            textView4 = (TextView) mView.findViewById(R.id.poi_address);
            textView5 = (TextView) mView.findViewById(R.id.poi_phone);

            mDemoSlider = mView.findViewById(R.id.poiImageSlider);

            addPhotoBtn = (LinearLayout) mView.findViewById(R.id.add_photo);
            addListBtn = (LinearLayout) mView.findViewById(R.id.add_mylist);
            addReviewBtn = (Button) mView.findViewById(R.id.add_review);

            addPhotoBtn.setOnClickListener(this);
            addListBtn.setOnClickListener(this);
            addReviewBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_photo:
                    Intent intent = new Intent(mActivity, AddPhotoActivity.class);
                    intent.putExtra("poi_id", poi.getId());
                    intent.putExtra("fb_id", fbId);
                    mActivity.startActivityForResult(intent, REQUEST_ADD_PHOTO);
                    break;
                case R.id.add_mylist:
                    Log.d("ddd", "즐겨찾기 추가 버튼 클릭");
                    ImageView star = (ImageView) addListBtn.findViewById(R.id.addMyList);
                    star.setImageResource(R.drawable.icon_star);
                    if (mActivity instanceof CategoryActivity) {
                        ((CategoryBottomSheet.instance).presenter).addMyList(fbId, poi.getId());
                    } else {
                        ((PoiInfoBottomSheet.instance).presenter).addMyList(fbId, poi.getId());
                    }
                    break;
                case R.id.add_review:
                    //데이터 담아서 팝업(액티비티) 호출
                    Intent intent2 = new Intent(mActivity, AddReviewActivity.class);
                    intent2.putExtra("poi_id", poi.getId());
                    intent2.putExtra("fb_id", fbId);
                    mActivity.startActivityForResult(intent2, REQUEST_ADD_REVIEW);
                    break;
            }
        }
    }
}
