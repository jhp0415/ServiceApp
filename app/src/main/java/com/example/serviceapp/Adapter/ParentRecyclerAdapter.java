package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.example.serviceapp.View.MainView.PoiActivity;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;
import java.util.List;

public class ParentRecyclerAdapter extends RecyclerView.Adapter<ParentRecyclerAdapter.ViewHolder> {

    private List<Poi> items = new ArrayList<>();
    private Activity mActivity;
    private int clickedPosition = -1;
    private int childClickedPosition = -1;

    public ParentRecyclerAdapter(Activity activity, int clickedPosition) {
        this.mActivity = activity;
        this.clickedPosition = clickedPosition;
    }

    @Override
    public ParentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_content_parent_child_list, parent, false);
        return new ParentRecyclerAdapter.ViewHolder(view);
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
    public void onBindViewHolder(final ParentRecyclerAdapter.ViewHolder holder, final int position) {
        holder.mItem = items.get(position);
        if(items.get(position).subName != null) {
            holder.mTitleText.setText(items.get(position).name + " " + items.get(position).branch + " " + items.get(position).subName);
        } else {
            holder.mTitleText.setText(items.get(position).name + " " + items.get(position).branch);
        }

        holder.mAddressText.setText(items.get(position).address.getFullAddressParcel());
        Util util = new Util(mActivity.getApplicationContext(), mActivity);
        holder.mDistance.setText(util.changeMeterToKilometer((int) Math.round(items.get(0).distance)));

        LinearLayoutManager manager = new LinearLayoutManager(mActivity.getApplicationContext());
        ChildRecyclerAdapter adapter = new ChildRecyclerAdapter(mActivity,
                items.get(position).children, -1, new ChildRecyclerAdapter.SubSelectionInterface() {
            @Override
            public void onsubselection(int position) {
                childClickedPosition = position;
            }
        });
        holder.chlidRecyclerView.setLayoutManager(manager);
        holder.chlidRecyclerView.setAdapter(adapter);
        holder.chlidRecyclerView.setVisibility(View.VISIBLE);

        holder.parentItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedPosition != position) {
                    clickedPosition = position;
                    childClickedPosition = -1;
                    notifyDataSetChanged();
                    ((PoiActivity) mActivity).onFragmentResult(holder.mItem);
                }
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Poi mItem;
        public final TextView mTitleText;
        public final TextView mAddressText;
        public final TextView mDistance;

        public RecyclerView chlidRecyclerView;
        public LinearLayout parentItemView;

        public ViewHolder(View view) {
            super(view);
            mTitleText = (TextView) view.findViewById(R.id.poi_parent_name);
            mAddressText = (TextView) view.findViewById(R.id.poi_parent_address);
            mDistance = (TextView) view.findViewById(R.id.poi_parent_distance);

            chlidRecyclerView = (RecyclerView) view.findViewById(R.id.child_recyclerview);
            parentItemView = (LinearLayout) view.findViewById(R.id.parent_row);
        }
    }
}