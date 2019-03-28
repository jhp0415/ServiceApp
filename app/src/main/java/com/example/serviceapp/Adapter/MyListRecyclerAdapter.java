package com.example.serviceapp.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Login.POJO.sPlace;
import com.example.serviceapp.Login.contract.MyListContract;
import com.example.serviceapp.Login.contract.OverviewContract;
import com.example.serviceapp.Login.model.RetrofitInterface;
import com.example.serviceapp.Login.presenter.MyListPresenter;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.model.Poi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyListRecyclerAdapter extends RecyclerView.Adapter<MyListRecyclerAdapter.ViewHolder>
        implements ItemTouchHelperListener {

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
        holder.mTitleText.setText(items.get(position).getpoiId());

        if(itemCnt > 0) {
            holder.mAddressText.setText(items.get(position).getPlacePicUrl().get(0));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트 호출
                //LatLng point = GpsHelper.getInstance().getCurrentLocation();
                presenter.getMyListPoi(items.get(position).getpoiId());
                //((MainActivity) mActivity).onFragmentResult();
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < 0 || fromPosition >= items.size() || toPosition < 0 || toPosition >= items.size()){
            return false;
        }
//        String fromItem = items.get(fromPosition);
//        items.remove(fromPosition);
//        items.add(toPosition, fromItem);
//
//        notifyItemMoved(fromPosition, toPosition);
        Log.d("ddd", "onMove...");
        return true;
    }

    @Override
    public void onItemRemove(int position) {
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

        public ViewHolder(View view) {
            super(view);
            mView = view;       // View 초기화
            mImageView = (ImageView) view.findViewById(R.id.mylist_image);
            mTitleText = (TextView) view.findViewById(R.id.mylist_name);
            mAddressText = (TextView) view.findViewById(R.id.mylist_address);
        }
    }
}