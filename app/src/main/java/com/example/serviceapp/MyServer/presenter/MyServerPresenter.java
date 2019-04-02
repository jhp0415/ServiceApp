package com.example.serviceapp.MyServer.presenter;

import android.util.Log;

import com.example.serviceapp.MyServer.POJO.sAccess;
import com.example.serviceapp.MyServer.contract.MyServerContract;
import com.example.serviceapp.MyServer.model.MyServerModel;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;

public class MyServerPresenter implements MyServerContract.Presenter {

    MyServerContract.View myserverView;
    MyServerModel myserverModel;
    MyServerServiceModel myserverServiceModel;
    private String TAG = MyServerPresenter.class.getName();
    public MyServerPresenter(MyServerContract.View myserverView) {

        this.myserverView = myserverView;

//        myserverModel = new MyServerModel();
        myserverServiceModel = new MyServerServiceModel();
    }

    @Override
    public void getSignCheck(String accessToken) {
        myserverServiceModel.callSignCheck(accessToken, new MyServerServiceModel.callSignCheckListener() {
            @Override
            public void onSignCheckFinished(sAccess response) {
                Log.d(TAG, "onSignCheckFinished: ");
                myserverView.setFbInfo(response);
            }

            @Override
            public void onSignCheckFailure(Throwable t) {
                Log.d(TAG, "onSignCheckFailure: ");
                t.printStackTrace();
            }
        });
    }
}
