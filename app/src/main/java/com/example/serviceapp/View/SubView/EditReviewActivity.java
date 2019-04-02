package com.example.serviceapp.View.SubView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.serviceapp.MyServer.POJO.sComment;
import com.example.serviceapp.MyServer.POJO.sPlaceWithComment;
import com.example.serviceapp.MyServer.contract.EditReviewContract;
import com.example.serviceapp.MyServer.presenter.EditReviewPresenter;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.Util;

public class EditReviewActivity extends Activity implements EditReviewContract.View {

    private int position;
    private String reviewId;
    private String fbId;
    private String title;
    private String body;

    private Button submitBtn;
    private Button cancleBtn;
    EditText commentTitle;
    EditText commentBody;

    EditReviewPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_review);

        getIntentData();

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

        presenter = new EditReviewPresenter(this);
    }

    public void getIntentData() {
        Intent intent = getIntent();
        position = (int) intent.getExtras().getInt("position");
        fbId = intent.getStringExtra("fb_id");
        reviewId = intent.getStringExtra("review_id");
        title = intent.getStringExtra("comment_title");
        body = intent.getStringExtra("comment_body");

        commentTitle = (EditText) findViewById(R.id.commentTitle);
        commentBody = (EditText) findViewById(R.id.commentBody);
        commentTitle.setText(title);
        commentBody.setText(body);
    }

    public void submitReviewClick() {
        Log.d("ddd", "수정한 리뷰 제출 버튼 클릭");
        presenter.submitEditReview(fbId, reviewId, commentTitle.getText().toString(), commentBody.getText().toString());
    }

    public void cancleReviewClick() {
        // 리뷰 수정 취소
        finish();
    }

    @Override
    public void submitEditFinished(sComment comment) {
        // 리스트 업데이트하고 끝낸다

        Intent intent = getIntent();
        intent.putExtra("position", position);
        intent.putExtra("update_review", comment);
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
