package com.example.serviceapp.Login.presenter;

import com.example.serviceapp.Login.contract.MyServerContract;
import com.example.serviceapp.Login.POJO.sAccess;
import com.example.serviceapp.Login.model.MyServerModel;
import com.example.serviceapp.Login.model.MyServerServiceModel;

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
