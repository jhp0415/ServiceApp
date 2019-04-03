package com.example.serviceapp.Adapter;

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
import com.kt.place.sdk.model.Poi;

import java.util.List;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<ChildRecyclerViewAdapter.MyViewHolder> {

    SubSelectionInterface subSelectionInterface;
    private List<Poi> categoryList;
    private Context context;
    private int clickedPosition = -1;

    public ChildRecyclerViewAdapter(Context context, List<Poi> categoryList, int clickedPosition, SubSelectionInterface subSelectionInterface) {
        this.categoryList = categoryList;
        this.context = context;
        this.clickedPosition = clickedPosition;
        this.subSelectionInterface = subSelectionInterface;
    }

    @Override
    public ChildRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_poi_child_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChildRecyclerViewAdapter.MyViewHolder holder, final int position) {

        holder.mName.setText(categoryList.get(position).name);
        holder.mAddress.setText(String.valueOf(categoryList.get(position).address.getFullAddressRoad()));

        if (position == clickedPosition) {
            holder.mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.mName.setTypeface(null, Typeface.BOLD);
            holder.mAddress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.mAddress.setTypeface(null, Typeface.BOLD);
        } else {
            holder.mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.mName.setTypeface(null, Typeface.NORMAL);
            holder.mAddress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.mAddress.setTypeface(null, Typeface.NORMAL);
        }

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedPosition != position) {
                    clickedPosition = position;
                    subSelectionInterface.onsubselection(position);
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mAddress, mName;
        public RelativeLayout row;

        public MyViewHolder(View view) {
            super(view);
            mAddress = (TextView) view.findViewById(R.id.poi_child_address);
            mName = (TextView) view.findViewById(R.id.poi_child_name);
            row = (RelativeLayout) view.findViewById(R.id.poi_child_layout);
        }
    }

    public interface SubSelectionInterface {
        void onsubselection(int position);
    }
}