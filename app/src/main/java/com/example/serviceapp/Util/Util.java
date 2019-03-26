package com.example.serviceapp.Util;

import android.widget.EditText;

import com.example.serviceapp.R;

public class Util {

    public static String changeMeterToKilometer(double meter){
        String result = "";
        if(meter > 1000)
            result = String.format("%.2f",(meter / 1000)) + "km";
        else
            result = Math.round(meter) + "m";

        return result;
    }

//    private void hideKyeboard(){
//        EditText editText = container1.findViewById(R.id.toolbar_search);
//        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//    }
}
