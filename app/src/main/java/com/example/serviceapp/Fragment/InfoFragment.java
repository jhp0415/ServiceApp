package com.example.serviceapp.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serviceapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoFragment extends Fragment {
    public static InfoFragment newInstance(){
        return new InfoFragment();
    }


    @BindView(R.id.tv_infoName) TextView name;
    @BindView(R.id.tv_infoAddr) TextView addr;
    @BindView(R.id.tv_infoPhone) TextView phone;
    @BindView(R.id.tv_infoDistance) TextView distance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeinfo, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    public void setPlaceInfo(String name, String addr, String phone, double distance){
        this.name.setText(name);
        this.addr.setText(addr);
        this.phone.setText(phone);
        this.distance.setText(changeMeterToKilometer(distance));
    }

    private String changeMeterToKilometer(double meter){
        String result = "";
        if(meter > 1000)
            result = String.format("%.2f",(meter / 1000)) + "km";
        else
            result = Math.round(meter) + "m";

        return result;
    }
}
