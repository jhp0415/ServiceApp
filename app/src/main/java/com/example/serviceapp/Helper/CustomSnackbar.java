package com.example.serviceapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serviceapp.R;
import com.example.serviceapp.View.SubView.DeleteReviewActivity;
import com.example.serviceapp.View.SubView.EditReviewActivity;

public class CustomSnackbar {
    Snackbar snackbar;

    int position;
    Context mContext;
    Activity mActivity;
    View view;
    String fbId;
    String reviewId;
    String poiId;
    String commentTitle;
    String commnetBody;

    TextView textViewOne;
    TextView textViewTwo;

    public static final int REQUEST_EDIT_REVIEW = 3;
    public static final int REQUEST_DELETE_REVIEW = 4;

    public CustomSnackbar(final int position, final Context context, Activity activity, View rootView,
                          final String fbId, final String reviewId, final String poiId,
                          final String commentTitle, final String commentBody) {
        this.position = position;
        this.mContext = context;
        this.mActivity = activity;
        this.view = rootView;
        this.fbId = fbId;
        this.reviewId = reviewId;
        this.poiId = poiId;
        this.commentTitle = commentTitle;
        this.commnetBody = commentBody;

        // Create the Snackbar
        snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        // Get the Snackbar's layout view
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Inflate our custom view
        final LayoutInflater inflater = LayoutInflater.from(context);
        View snackView = inflater.inflate(R.layout.review_snackbar_edit_action, null);
        // Configure the view
        textViewOne = (TextView) snackView.findViewById(R.id.snackbar_edit_btn);
        textViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditSnackbar(position, fbId, reviewId, commentTitle, commnetBody);
            }
        });
        textViewTwo = (TextView) snackView.findViewById(R.id.snackbar_delete_btn);
        textViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeleteSnackbar(position, fbId, reviewId, poiId);
            }
        });

        //If the view is not covering the whole snackbar layout, add this line
        layout.setPadding(0,0,0,0);

        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);
        // Show the Snackbar
//        snackbar.show();
    }

    public void show() {
        // Show the Snackbar
        snackbar.show();
    }

    public void setEditSnackbar(int position, final String fbId, final String reviewId, final String commentTitle, final String commentBody) {
        Intent intent = new Intent(mActivity, EditReviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("position", position);
        intent.putExtra("fb_id", fbId);
        intent.putExtra("review_id", reviewId);
        intent.putExtra("comment_title", commentTitle);
        intent.putExtra("comment_body", commentBody);
        mActivity.startActivityForResult(intent, REQUEST_EDIT_REVIEW);
    }

    public void setDeleteSnackbar(int position, final String fbId, final String reviewId, final String poiId) {
        Intent intent = new Intent(mActivity, DeleteReviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("position", position);
        intent.putExtra("fb_id", fbId);
        intent.putExtra("review_id", reviewId);
        intent.putExtra("poi_id", poiId);
        mActivity.startActivityForResult(intent, REQUEST_DELETE_REVIEW);
    }
}