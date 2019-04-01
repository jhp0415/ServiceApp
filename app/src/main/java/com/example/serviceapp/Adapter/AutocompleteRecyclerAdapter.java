package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.example.serviceapp.View.MainView.PoiActivity;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteRecyclerAdapter extends RecyclerView.Adapter<AutocompleteRecyclerAdapter.ViewHolder> {

    private List<Poi> items = new ArrayList<>();
    private Activity mActivity;

    public AutocompleteRecyclerAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_poi_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //이부분 중요!! 검색 리스트를 나오게하기 위해 꼭 필요
    public void setFilter(List<Poi> items) {
        if(items.size() > 0) {
            this.items.clear();
            this.items.addAll(items);

            notifyDataSetChanged();     // 데이터 업데이트
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = items.get(position);
        holder.mTitleText.setText(items.get(position).name + " " + items.get(position).branch);
        if (items.get(position).distance != null) {
            Util util = new Util(mActivity.getApplicationContext(), mActivity);
            holder.mDistanceText.setText(util.changeMeterToKilometer((int) Math.round(items.get(position).distance)));
        }
        holder.mCategoryText.setText(items.get(position).category.masterName);
        holder.mAddressText.setText(items.get(position).address.getFullAddressParcel());
        if (items.get(position).phones.representation != null)
            if (items.get(position).phones.representation.size() > 0)
                holder.mPhoneText.setText(items.get(position).phones.representation.get(0));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트 호출
                ((PoiActivity) mActivity).onFragmentResult(holder.mItem);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Poi mItem;
        public final View mView;
        public final TextView mTitleText;
        public final TextView mDistanceText;
        public final TextView mCategoryText;
        public final TextView mAddressText;
        public final TextView mPhoneText;

        public ViewHolder(View view) {
            super(view);
            mView = view;       // View 초기화
            mTitleText = (TextView) view.findViewById(R.id.poi_name);
            mDistanceText = (TextView) view.findViewById(R.id.poi_distance);
            mCategoryText = (TextView) view.findViewById(R.id.poi_category);
            mAddressText = (TextView) view.findViewById(R.id.poi_address);
            mPhoneText = (TextView) view.findViewById(R.id.poi_phone);
        }
    }
}