package com.example.serviceapp.MyServer.contract;


import com.example.serviceapp.MyServer.POJO.sComment;

import java.util.List;

public interface ReviewContract {

    interface Presenter {

        public void getReviewList(String poiId);
    }

    interface View {

        public void setReviewList(List<sComment> comments);
        public void setReviewList(int position, sComment comment);
        public void setReviewList(int position);
        public void clearReviewList();
    }
}
