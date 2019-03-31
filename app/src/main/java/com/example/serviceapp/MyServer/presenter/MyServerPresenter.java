package com.example.serviceapp.MyServer.presenter;

import com.example.serviceapp.MyServer.POJO.sAccess;
import com.example.serviceapp.MyServer.contract.MyServerContract;
import com.example.serviceapp.MyServer.model.MyServerModel;
import com.example.serviceapp.MyServer.model.MyServerServiceModel;

public class MyServerPresenter implements MyServerContract.Presenter {

    MyServerContract.View myserverView;
    MyServerModel myserverModel;
    MyServerServiceModel myserverServiceModel;

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
                myserverView.setFbInfo(response);
            }

            @Override
            public void onSignCheckFailure(Throwable t) {
            }
        });
    }
}
