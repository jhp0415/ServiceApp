package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.example.serviceapp.View.MainView.PoiActivity;
import com.kt.place.sdk.model.Autocomplete;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;
import java.util.List;

;

public class RecyclerAdapter extends RecyclerView.Adapter
{

    private List<Poi> items = new ArrayList<>();
    private List<Autocomplete> itemsHearder = new ArrayList<>();
    private Activity mActivity;

    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    private HeaderViewHolder headerViewHolder;

    private String fbId;

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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder itemViewHolder = (ViewHolder) holder;
            itemViewHolder.mItem = items.get(position);
            itemViewHolder.mTitleText.setText(items.get(position).name + " " + items.get(position).branch);
            itemViewHolder.mDescriptionText.setText(items.get(position).address.getFullAddressRoad());
            Util util = new Util(mActivity.getApplicationContext(), mActivity);
            itemViewHolder.mDistance.setText(util.changeMeterToKilometer((int) Math.round(items.get(position).distance)));
            itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, PoiActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.d("ddd", "RecyclerView onCreateView: fbId : " + fbId);
                    intent.putExtra("fb_id", fbId);
                    intent.putExtra("mode", "poi");
                    intent.putExtra("poi_id", items.get(position).id);
                    mActivity.startActivity(intent);
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            headerViewHolder = (HeaderViewHolder) holder;
            if(itemsHearder.size() > 0) {
                if(itemsHearder.size() < 3) {
                    for(int i = 0; i<itemsHearder.size();i++) {
                        headerViewHolder.mItem[i] = itemsHearder.get(0);
                        headerViewHolder.mTitleText[i].setText(itemsHearder.get(i).terms);
                    }
                } else {
                    for(int i = 0; i<3;i++) {
                        headerViewHolder.mItem[i] = itemsHearder.get(0);
                        headerViewHolder.mTitleText[i].setText(itemsHearder.get(i).terms);
                    }
                }
            }
        }
    }

    public void setFbId(String id) {
        fbId = id;
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

    public void setHeaderFilter(List<Autocomplete> items) {
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
        public Autocomplete[] mItem = new Autocomplete[3];
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
            Intent intent = new Intent(mActivity, PoiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("mode", "autocomplete");
            switch (v.getId()) {
                case R.id.suggest1:
//                    ((PoiActivity) mActivity).onFragmentResultAutocomplete(headerViewHolder.mItem[0]);
                    intent.putExtra("poi_id", headerViewHolder.mItem[0].poiId);
                    intent.putExtra("fb_id", fbId);
                    break;
                case R.id.suggest2:
//                    ((PoiActivity) mActivity).onFragmentResultAutocomplete(headerViewHolder.mItem[1]);
                    intent.putExtra("poi_id", headerViewHolder.mItem[0].poiId);
                    intent.putExtra("fb_id", fbId);
                    break;
                case R.id.suggest3:
//                    ((PoiActivity) mActivity).onFragmentResultAutocomplete(headerViewHolder.mItem[2]);
                    intent.putExtra("poi_id", headerViewHolder.mItem[0].poiId);
                    intent.putExtra("fb_id", fbId);
                    break;
            }
            mActivity.startActivity(intent);
        }
    }
}