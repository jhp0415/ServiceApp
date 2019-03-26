package com.example.serviceapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.serviceapp.Login.POJO.sComment;
import com.example.serviceapp.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewRecyclerAdapter extends RecyclerView.Adapter<ReviewRecyclerAdapter.ViewHolder> {

    private List<sComment> items = new ArrayList<>();
    private Context mContext;

    public ReviewRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_review_item, parent, false);
        if(view == null) {
            Log.d("ddd", "view is null");
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //이부분 중요!! 검색 리스트를 나오게하기 위해 꼭 필요
    public void setFilter(List<sComment> items) {
        if(items.size() > 0) {
            this.items.clear();
            this.items.addAll(items);

            notifyDataSetChanged();     // 데이터 업데이트
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d("ddd", "title : " + items.get(position).getCaptionTitle());
        Log.d("ddd", "title : " + items.get(position).getCaptionBody());
        Log.d("ddd", "title : " + items.get(position).getUser().getName());
        holder.mItem = items.get(position);
        holder.mTitleText.setText(items.get(position).getCaptionTitle());
        holder.mBodyText.setText(items.get(position).getCaptionBody());
        holder.mNameText.setText(items.get(position).getUser().getName());
    }

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
            mUserImage = (ImageView) mView.findViewById(R.id.user_profile);
            mTitleText = (TextView) mView.findViewById(R.id.poi_name);
            mBodyText = (TextView) mView.findViewById(R.id.poi_distance);
            mNameText = (TextView) mView.findViewById(R.id.poi_category);
        }
    }
}
