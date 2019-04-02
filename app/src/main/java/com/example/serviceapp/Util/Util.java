package com.example.serviceapp.Util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.serviceapp.R;

public class Util {

    InputMethodManager inputMethodManager;
    Context mContext;
    Activity mActivity;
    FrameLayout container1;
    public Util(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        inputMethodManager = (InputMethodManager)this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        container1 = (FrameLayout)this.mActivity.findViewById(R.id.fragment_container1);
    }

    public static String changeMeterToKilometer(double meter){
        String result = "";
        if(meter > 1000)
            result = String.format("%.2f",(meter / 1000)) + "km";
        else
            result = Math.round(meter) + "m";

        return result;
    }

    public void hideKyeboard(){
        EditText editText = mActivity.findViewById(R.id.toolbar_search);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showKyeboard(EditText editText) {
        inputMethodManager.showSoftInput(editText, 0);
    }
}
