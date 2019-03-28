package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.model.Suggest;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter
{

    private List<Poi> items = new ArrayList<>();
    private List<Suggest> itemsHearder = new ArrayList<>();
    private Activity mActivity;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private HeaderViewHolder headerViewHolder;

    public RecyclerAdapter(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == 0) {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_poi_header_layout, parent, false);
            holder = new HeaderViewHolder(view);
        }
        else {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_poi_item, parent, false);
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder itemViewHolder = (ViewHolder) holder;
            itemViewHolder.mItem = items.get(position);
            itemViewHolder.mTitleText.setText(items.get(position).getName() + " " + items.get(position).getBranch());
            itemViewHolder.mDescriptionText.setText(items.get(position).getAddress().getFullAddressParcel());
            itemViewHolder.mDistance.setText(String.valueOf((int) Math.round(items.get(position).getDistance())));
            itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mActivity).onFragmentResult(itemViewHolder.mItem);
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            headerViewHolder = (HeaderViewHolder) holder;
            if(itemsHearder.size() > 0) {
                if(itemsHearder.size() < 3) {
                    for(int i = 0; i<itemsHearder.size();i++) {
                        headerViewHolder.mItem[i] = itemsHearder.get(0);
                        headerViewHolder.mTitleText[i].setText(itemsHearder.get(i).getTerms());
                    }
                } else {
                    for(int i = 0; i<3;i++) {
                        headerViewHolder.mItem[i] = itemsHearder.get(0);
                        headerViewHolder.mTitleText[i].setText(itemsHearder.get(i).getTerms());
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //이부분 중요!! 검색 리스트를 나오게하기 위해 꼭 필요
    public void setFilter(List<Poi> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();     // 데이터 업데이트
    }

    // 다음 리스트 받아오기
    public void setFilterPlus(List<Poi> items) {
        this.items.addAll(items);
        notifyDataSetChanged();     // 데이터 업데이트
    }

    public void setHeaderFilter(List<Suggest> items) {
        this.itemsHearder.clear();
        if(items.size() > 0) {
            if(items.size() < 3) {
                for (int index = 0; index < items.size(); index++) {
                    this.itemsHearder.add(items.get(index));
                }
            } else {
                for (int index = 0; index < 3; index++) {
                    this.itemsHearder.add(items.get(index));
                }
            }
        }
        notifyDataSetChanged();     // 데이터 업데이트
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public Poi mItem;
        public final View mView;
        public final TextView mTitleText;
        public final TextView mDescriptionText;
        public final TextView mDistance;

        public ViewHolder(View view) {
            super(view);
            mView = view;       // View 초기화
            mTitleText = (TextView) view.findViewById(R.id.title);
            mDescriptionText = (TextView) view.findViewById(R.id.description);
            mDistance = (TextView) view.findViewById(R.id.distance);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener  {
        public Suggest[] mItem = new Suggest[3];
        public final View mView;
        public final TextView[] mTitleText = new TextView[3];
        public final int[] id = {
                R.id.suggest1,
                R.id.suggest2,
                R.id.suggest3
        };

        public HeaderViewHolder(View headerView) {
            super(headerView);
            mView = headerView;       // View 초기화
            for(int i=0;i<3;i++) {
                mTitleText[i] = (TextView) mView.findViewById(id[i]);
                mTitleText[i].setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.suggest1:
                    ((MainActivity) mActivity).onFragmentResultAutocomplete(headerViewHolder.mItem[0]);
                    break;
                case R.id.suggest2:
                    ((MainActivity) mActivity).onFragmentResultAutocomplete(headerViewHolder.mItem[1]);
                    break;
                case R.id.suggest3:
                    ((MainActivity) mActivity).onFragmentResultAutocomplete(headerViewHolder.mItem[2]);
                    break;
            }
        }
    }
}