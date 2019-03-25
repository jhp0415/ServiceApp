package com.example.serviceapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.model.Suggest;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteRecyclerAdapter extends RecyclerView.Adapter<AutocompleteRecyclerAdapter.ViewHolder> {

    private List<Poi> items = new ArrayList<>();
    private Context mContext;

    public AutocompleteRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bottom_sheet_content_poi_info, parent, false);
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
        holder.mTitleText.setText(items.get(position).getName() + " " + items.get(position).getBranch());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트 호출
                ((MainActivity) mContext).onFragmentResult(holder.mItem);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Poi mItem;
        public final View mView;
        public final TextView mTitleText;

        public ViewHolder(View view) {
            super(view);
            mView = view;       // View 초기화
            mTitleText = (TextView) view.findViewById(R.id.poi_name);
        }
    }
}