package com.example.serviceapp.Helper;

public class SaveData {
    public static SaveData instance;
    public static SaveData getInstance() {
        if(instance == null) {
            instance = new SaveData();
        }
        return instance;
    }

    public String currentAddress = "서울특별시 서초구 태봉로 151";

    public SaveData() {
        instance = this;
    }

    public void setCurrentAddress(String address) {
        this.currentAddress = address;
    }

    public String getCurrentAddress() {
        return this.currentAddress;
    }
}
