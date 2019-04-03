package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.serviceapp.R;
import com.example.serviceapp.View.MainView.PoiActivity;
import com.kt.place.sdk.model.Poi;

import java.util.List;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.MyViewHolder> {

    SubSelectionInterface subSelectionInterface;
    private List<Poi> childList;
    private Activity mActivity;
    private int clickedPosition = -1;

    public ChildRecyclerAdapter(Activity activity, List<Poi> childList, int clickedPosition, SubSelectionInterface subSelectionInterface) {
        this.childList = childList;
        this.mActivity = activity;
        this.clickedPosition = clickedPosition;
        this.subSelectionInterface = subSelectionInterface;
    }

    @Override
    public ChildRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_poi_child_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChildRecyclerAdapter.MyViewHolder holder, final int position) {
        holder.mItem = childList.get(position);
        if(childList.get(position).subName != null){
            holder.mName.setText(childList.get(position).name + " " + childList.get(position).branch + " " + childList.get(position).subName);
        } else {
            holder.mName.setText(childList.get(position).name + " " + childList.get(position).branch);
        }

        holder.mAddress.setText(String.valueOf(childList.get(position).address.getFullAddressRoad()));

        holder.childItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedPosition != position) {
                    clickedPosition = position;
                    subSelectionInterface.onsubselection(position);
                    notifyDataSetChanged();
                    ((PoiActivity) mActivity).onFragmentResult(holder.mItem);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Poi mItem;
        public TextView mAddress, mName;
        public RelativeLayout childItemView;

        public MyViewHolder(View view) {
            super(view);
            mAddress = (TextView) view.findViewById(R.id.poi_child_address);
            mName = (TextView) view.findViewById(R.id.poi_child_name);
            childItemView = (RelativeLayout) view.findViewById(R.id.poi_child_layout);
        }
    }

    public interface SubSelectionInterface {
        void onsubselection(int position);
    }
}