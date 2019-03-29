package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.serviceapp.Login.POJO.sPlace;
import com.example.serviceapp.Login.model.MyServerModel;
import com.example.serviceapp.Login.presenter.MyListPresenter;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.kt.place.sdk.net.PoiResponse;

import java.util.ArrayList;
import java.util.List;

public class MyListRecyclerAdapter extends RecyclerView.Adapter<MyListRecyclerAdapter.ViewHolder>
        implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{


    private List<sPlace> items = new ArrayList<>();
    private Activity mActivity;
    private MyListPresenter presenter;

    public MyListRecyclerAdapter(Activity activity, MyListPresenter presenter) {
        this.mActivity = activity;
        this.presenter = presenter;
    }

    @Override
    public MyListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_mylist_item, parent, false);
        return new MyListRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //이부분 중요!! 검색 리스트를 나오게하기 위해 꼭 필요
    public void setFilter(List<sPlace> items) {
        if(items.size() > 0) {
            this.items.clear();
            this.items.addAll(items);

            notifyDataSetChanged();     // 데이터 업데이트
        }
    }

    @Override
    public void onBindViewHolder(final MyListRecyclerAdapter.ViewHolder holder, final int position) {
        holder.mItem = items.get(position);
        String thumbnail = null;
        int itemCnt = items.get(position).getPlacePicUrl().size();

        if(itemCnt > 0) {
            thumbnail = holder.mItem.getPlacePicUrl().get(0);
        }

        Glide.with(mActivity.getApplicationContext())
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_broken_image_black_24dp)
                        .error(R.drawable.ic_broken_image_black_24dp)
                ).load(thumbnail)
                .apply(RequestOptions.circleCropTransform().override(300,300))
                .into(holder.mImageView);

        MyServerModel myserverModel = new MyServerModel();
        myserverModel.callpoiRetrieve(items.get(position).getpoiId(), new MyServerModel.poiRetrieveListener() {
            @Override
            public void onPoiRetrieveFinished(PoiResponse response) {
                holder.mTitleText.setText(response.getPois().get(0).getName());
                holder.mAddressText.setText(response.getPois().get(0).getAddress().getFullAddressParcel());
            }

            @Override
            public void onPoiRetrieveFailure(Throwable t) {

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getMyListPoi(items.get(position).getpoiId());
            }
        });
    }

//    @Override
//    public boolean onItemMove(int fromPosition, int toPosition) {
//        if(fromPosition < 0 || fromPosition >= items.size() || toPosition < 0 || toPosition >= items.size()){
//            return false;
//        }
////        String fromItem = items.get(fromPosition);
////        items.remove(fromPosition);
////        items.add(toPosition, fromItem);
////
////        notifyItemMoved(fromPosition, toPosition);
//        Log.d("ddd", "onMove...");
//        return true;
//    }
//
//    @Override
//    public void onItemRemove(int position) {
//        Log.d("ddd", "서버 즐겨찾기 목록에서 제거하기");
//        // TODO:즐겨찾기에서 제거하기
//        presenter.deleteMyList(((MainActivity) mActivity).getFbId(), items.get(position).getpoiId());
//
//        items.remove(position);
//        notifyItemRemoved(position);
//    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MyListRecyclerAdapter.ViewHolder) {

            // backup of removed item for undo purpose
            final sPlace deletedItem = items.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            removeItem(viewHolder.getAdapterPosition());
        }
    }

    public void removeItem(int position) {
        Log.d("ddd", "서버 즐겨찾기 목록에서 제거하기");
        // TODO:즐겨찾기에서 제거하기
        presenter.deleteMyList(((MainActivity) mActivity).getFbId(), items.get(position).getpoiId());

        items.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public sPlace mItem;
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleText;
        public final TextView mAddressText;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;
        public ViewHolder(View view) {
            super(view);
            mView = view;       // View 초기화
            mImageView = (ImageView) view.findViewById(R.id.mylist_image);
            mTitleText = (TextView) view.findViewById(R.id.mylist_name);
            mAddressText = (TextView) view.findViewById(R.id.mylist_address);

            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }
}