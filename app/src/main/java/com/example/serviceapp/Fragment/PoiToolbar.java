package com.example.serviceapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serviceapp.R;
import com.example.serviceapp.View.MainView.PoiInfoActivity;

public class PoiToolbar extends Fragment {

    public static PoiToolbar instance;
    PoiInfoActivity activity;
    Toolbar toolbar;
    public static PoiToolbar getInstance(){
        if(instance == null) {
            instance = new PoiToolbar();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        instance = this;

        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);
        toolbar = (Toolbar)view.findViewById(R.id.toolbar);

        activity = (PoiInfoActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        return view;
    }

    public void setTitle(String name) {
        // 선택된 카테고리 텍스트 보여주기
        TextView textView = toolbar.findViewById(R.id.toolbar_title);
        textView.setText(name);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // TODO: 뒤로가기 -> 이전 엑티비티
                activity.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
