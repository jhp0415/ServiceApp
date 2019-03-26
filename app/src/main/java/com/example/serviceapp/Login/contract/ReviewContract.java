package com.example.serviceapp.Login.contract;

import com.example.serviceapp.Login.POJO.sComment;

import java.util.List;

public interface ReviewContract {

    interface Presenter {

        public void getReviewList(String poiId);
    }

    interface View {

        public void setReviewList(List<sComment> comments);
    }
}
