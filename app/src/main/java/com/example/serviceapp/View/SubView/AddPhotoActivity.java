package com.example.serviceapp.View.SubView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.serviceapp.MyServer.POJO.sPlaceOverview;
import com.example.serviceapp.MyServer.contract.AddPhotoContract;
import com.example.serviceapp.MyServer.presenter.AddPhotoPresenter;
import com.example.serviceapp.R;
import com.example.serviceapp.Util.RealPathUtil;

public class AddPhotoActivity extends AppCompatActivity implements AddPhotoContract.View,
        ActivityCompat.OnRequestPermissionsResultCallback{

    AddPhotoPresenter presenter;
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_WRITE_PERMISSION = 1;
    private ImageView selectPhotoButton;
    private Uri selectedImage;
    private String poiId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        Intent intent = getIntent();
        poiId = intent.getStringExtra("poi_id");

        selectPhotoButton = (ImageView) findViewById(R.id.selectPhoto);

        presenter = new AddPhotoPresenter(this);
    }

    public void selectPhoto(View v) {
        // TODO: ASK PERMISSION
        requestPermission();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("selectImg", String.valueOf(requestCode));
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_IMAGE) {
            selectedImage = data.getData();
            // TODO: Image URI check
            selectPhotoButton.setImageURI(selectedImage);
        }
    }

    public void submitPhotoClick(View v) {
        presenter.submitPhoto(poiId, RealPathUtil.getRealPath(getApplicationContext(), selectedImage));
    }

    @Override
    public void submitFinished(sPlaceOverview placeOverview) {
        Intent intent = getIntent();
        intent.putExtra("place_overview", placeOverview);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            openGallery();
        }
    }
}
