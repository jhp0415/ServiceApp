package com.example.serviceapp.View.MainView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.serviceapp.Fragment.SearchFragment;
import com.example.serviceapp.Helper.GpsHelper;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;
import com.google.android.gms.maps.model.LatLng;
import com.kt.place.sdk.listener.OnResponseListener;
import com.kt.place.sdk.model.Autocomplete;
import com.kt.place.sdk.model.Poi;
import com.kt.place.sdk.net.AutocompleteRequest;
import com.kt.place.sdk.net.AutocompleteResponse;
import com.kt.place.sdk.net.PoiRequest;
import com.kt.place.sdk.net.PoiResponse;
import com.kt.place.sdk.util.PlaceClient;
import com.kt.place.sdk.util.PlaceManager;

import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements TextWatcher, View.OnClickListener {

    private PlaceClient placesClient;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    public String fbId;

    // 툴바
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getIntentData();
        placesClient = PlaceManager.createClient();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, SearchFragment.getInstance(),"visible");
        fragmentTransaction.commit();

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        editText = (EditText)toolbar.findViewById(R.id.toolbar_search);
        editText.setText(null);
        editText.setOnClickListener(this);
        editText.addTextChangedListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 키보드
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Util util = new Util(getApplicationContext(), this);
        util.showKyeboard(editText);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public void getIntentData() {
        Intent intent = getIntent();
        fbId = intent.getStringExtra("fb_id");
        Log.d("ddd", "SearchActivity onCreate: fbId : " + fbId);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        hideKyeboard();
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() == 0) {

        } else {
            setEditTextQuery(s.toString());
            requestPoiSearch2(s.toString(), 0);
            requestAutocomplete(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
    }

    public void setEditTextQuery(String query) {
        SearchFragment.getInstance().setEditTextQuery(query);
    }

    public void requestPoiSearch2(final String terms, int start) {
        LatLng point = GpsHelper.getInstance().getCurrentLocation();
        PoiRequest request = new PoiRequest.PoiRequestBuilder(terms)
                .setLat(point.latitude)
                .setLng(point.longitude)
                .setStart(start)
                .setNumberOfResults(10)
                .build();

        placesClient.getPoiSearch(request, new OnResponseListener<PoiResponse>() {
            @Override
            public void onSuccess(@NonNull PoiResponse poiResponse) {
                if(poiResponse.getPois().size() > 0) {
                    setRecycleView(poiResponse.getPois());
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

        placesClient.getAutocomplete(request, new OnResponseListener<AutocompleteResponse>() {
            @Override
            public void onSuccess(@NonNull AutocompleteResponse autocompleteResponse) {
                if(autocompleteResponse.getAutocompleteList().size() > 0) {
                    setAutocompleteView(autocompleteResponse.getAutocompleteList());
                }
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.d("ddd", throwable.getMessage());
            }
        });
    }

    /**
     * 검색결과 리사이클러뷰 셋팅
     * @param pois
     */
    public void setRecycleView(List<Poi> pois){
        if(pois.size() > 0) {
            SearchFragment.getInstance().setRecyclerView(pois);
        }
    }

    public void setRecycleViewPlus(List<Poi> pois){
        if(pois.size() > 0) {
            SearchFragment.getInstance().setRecyclerViewPlus(pois);
        }
    }

    public void setAutocompleteView(List<Autocomplete> suggests){
        if(suggests.size() > 0) {
            SearchFragment.getInstance().setAutocompleteView(suggests);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_search:
//                Fragment currentFragment = fragmentManager.findFragmentByTag("visible");
//                if (!(currentFragment instanceof SearchFragment)) {
//                    Intent intent = new Intent(this, PoiActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }

                break;
        }
    }
}
