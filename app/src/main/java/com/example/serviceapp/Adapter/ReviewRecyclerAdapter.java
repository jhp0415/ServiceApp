package com.example.serviceapp.Adapter;

import android.app.Activity;
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
    private Activity mActivity;

    public ReviewRecyclerAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_review_item, parent, false);
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

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(items.size() > 0) {
            holder.mItem = items.get(position);

            holder.mTitleText.setText(items.get(position).getCaptionTitle());
            holder.mBodyText.setText(items.get(position).getCaptionBody());
            holder.mNameText.setText(items.get(position).getUser().getName());
        } else {
            holder.mTitleText.setText("No Any Reivew");
        }
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
            mUserImage = (ImageView) view.findViewById(R.id.user_profile);
            mTitleText = (TextView) view.findViewById(R.id.user_comment_title);
            mBodyText = (TextView) view.findViewById(R.id.user_comment_body);
            mNameText = (TextView) view.findViewById(R.id.user_name);

            if(mTitleText == null) {
                Log.d("ddd", "mTitleText is null");
            }
        }
    }
}
