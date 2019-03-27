package com.example.serviceapp.Login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.serviceapp.Login.POJO.sPlaceWithComment;
import com.example.serviceapp.Login.contract.AddReviewContract;
import com.example.serviceapp.Login.presenter.AddReviewPresenter;
import com.example.serviceapp.R;

public class AddReviewActivity extends AppCompatActivity
        implements AddReviewContract.View {
    AddReviewPresenter presenter;
    private String poiId;
    private String fbId;
    private Button submitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public void submitReviewClick() {
        Log.d("ddd", "new 리뷰 제출 버튼 클릭");
        EditText commentTitle = (EditText) findViewById(R.id.commentTitle);
        EditText commentBody = (EditText) findViewById(R.id.commentBody);

        presenter.submitReview(fbId, poiId, commentTitle.getText().toString(), commentBody.getText().toString());
    }

    @Override
    public void submitFinished(sPlaceWithComment placeReview) {
        Intent intent = getIntent();
        intent.putExtra("place_review", placeReview);
        setResult(RESULT_OK, intent);
        finish();
    }
}