package com.example.serviceapp.Listener;

import com.google.android.gms.maps.GoogleMap;

public interface OnGpsListener {
    void onSuccessPermission();
    void onSuccessMapReady();
}
