package com.example.serviceapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.Helper.PlacesApiHelper;
import com.example.serviceapp.MainActivity;
import com.example.serviceapp.R;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnSuccessListener;
import com.kt.place.sdk.net.AutocompleteRequest;
import com.kt.place.sdk.net.AutocompleteResponse;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.Client;

public class SearchToolbar extends Fragment implements TextWatcher {
    private static SearchToolbar instance;
    MainActivity activity;
    Client placesClient;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LatLng currentPoint;
    EditText editText;
    InputMethodManager inputMethodManager;

    public static SearchToolbar newInstance(){
        if(instance == null) {
            instance = new SearchToolbar();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchtoolbar, container, false);
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar);
        placesClient = new Client();
        activity = (MainActivity)getActivity();
        inputMethodManager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        currentPoint = GpsHelper.getInstance().getCurrentLocation();
        fragmentManager = activity.getSupportFragmentManager();

        editText = (EditText)toolbar.findViewById(R.id.toolbar_search);
        editText.addTextChangedListener(this);
        setHasOptionsMenu(true);
        activity = (MainActivity)getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        return view;
    }

    private void hideKyeboard(){
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKyeboard();
        switch (item.getItemId()){
            case android.R.id.home:
                fragmentManager.popBackStack();
//                editText.setText(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        if(s.length() == 0){
//            Fragment fragment = fragmentManager.findFragmentByTag("visible");
////            Log.d("ddd", fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName());
//            if(fragment instanceof CategoryFragment) {
//                fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container2, CategoryFragment.newInstance(), "visible");
//                fragmentTransaction.commit();
//            } else {
//                fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container2, CategoryFragment.newInstance(), "visible");
//                fragmentTransaction.addToBackStack("CategoryFragment");
//                fragmentTransaction.commit();
//            }
//        }
//
//        if(s.length() > 0) {
//            activity.setEditTextQuery(s.toString());
//            Fragment fragment = fragmentManager.findFragmentByTag("visible");
//            if(fragment instanceof SearchFragment) {
//                fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(), "visible");
//                fragmentTransaction.commit();
//            }else {
//                fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(), "visible");
//                fragmentTransaction.addToBackStack("SearchFragment");
//                fragmentTransaction.commit();
//            }
//
//            requestPoiSearch(s.toString(), 0);
//            requestAutocomplete(s.toString());
//        }
        if(s.length() == 0) {

        } else {
            activity.setEditTextQuery(s.toString());
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(), "visible");
//        fragmentTransaction.addToBackStack("SearchFragment");
//        fragmentTransaction.commit();
            requestPoiSearch(s.toString(), 0);
            requestAutocomplete(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
    }

    public void requestPoiSearch(final String terms, int start) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        PoiRequest request = new PoiRequest.PoiRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .setStart(start)
                .setNumberOfResults(10)
                .build();

        placesClient.getPoiSearch(request, new OnSuccessListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                if(poiResponse.getPois().size() > 0) {
                    activity.setRecycleView(poiResponse.getPois());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    public void requestAutocomplete(final String terms) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        AutocompleteRequest request = new AutocompleteRequest.AutocompleteRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .build();

        placesClient.getAutocomplete(request, new OnSuccessListener<AutocompleteResponse>() {
            @Override
            public void onSuccess(@NonNull AutocompleteResponse autocompleteResponse) {
                if(autocompleteResponse.getSuggestList().size() > 0) {
                    activity.setAutocompleteView(autocompleteResponse.getSuggestList());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }
}
