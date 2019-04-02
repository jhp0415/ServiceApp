package com.example.serviceapp.View.SubView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.MyServer.contract.AddReviewContract;
import com.example.serviceapp.MyServer.presenter.AddReviewPresenter;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;

public class AddReviewActivity extends Activity
        implements AddReviewContract.View{

    AddReviewPresenter presenter;
    private String poiId;
    private String fbId;
    private Button submitBtn;
    private Button cancleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_review);

        Intent intent = getIntent();
        poiId = intent.getStringExtra("poi_id");
        fbId = intent.getStringExtra("fb_id");

        presenter = new AddReviewPresenter(this);

        submitBtn = (Button) findViewById(R.id.submit_review_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReviewClick();
            }
        });

        cancleBtn = (Button) findViewById(R.id.cancle_review_btn);
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleReviewClick();
            }
        });
    }

    public void submitReviewClick() {
        Log.d("ddd", "리뷰 제출 버튼 클릭");
        EditText commentTitle = (EditText) findViewById(R.id.commentTitle);
        EditText commentBody = (EditText) findViewById(R.id.commentBody);

        presenter.submitReview(fbId, poiId, commentTitle.getText().toString(), commentBody.getText().toString());
    }

    public void cancleReviewClick() {
        // 리뷰 제출 취소
        finish();
    }

    @Override
    public void submitFinished(sPlaceWithComment placeReview) {

        Intent intent = getIntent();
        intent.putExtra("place_review", placeReview);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
