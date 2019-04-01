package com.example.serviceapp.View.SubView;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.serviceapp.MyServer.contract.DeleteReviewContract;
import com.example.serviceapp.MyServer.presenter.DeleteReviewPresenter;
import com.example.serviceapp.R;

public class DeleteReviewActivity extends AppCompatActivity
        implements DeleteReviewContract.View {

    private int position;
    private String poiId;
    private String fbId;
    private String reviewId;

    DeleteReviewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_review);
        getIntentData();

        // 바로 삭제
        presenter = new DeleteReviewPresenter(this);
        presenter.submitEditReview(fbId, reviewId, poiId);
    }

    public void getIntentData() {
        Intent intent = getIntent();
        position = (int) intent.getExtras().getInt("position");
        fbId = intent.getStringExtra("fb_id");
        reviewId = intent.getStringExtra("review_id");
    }


    @Override
    public void submitEditFinished() {
        // 리스트 업데이트하고 끝낸다
        Intent intent = getIntent();
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
