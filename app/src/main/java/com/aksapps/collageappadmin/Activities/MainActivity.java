package com.aksapps.collageappadmin.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aksapps.collageappadmin.R;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MaterialCardView uploadNotice, addGalleryImage, addEbook, faculty, deleteNotice, logOut;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("Login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin", "false").equals("false")) {
            openLogin();
        }

        uploadNotice = findViewById(R.id.add_notice);
        addGalleryImage = findViewById(R.id.add_gallery_image);
        addEbook = findViewById(R.id.add_ebook);
        faculty = findViewById(R.id.faculty);
        deleteNotice = findViewById(R.id.delete_notice);
        logOut = findViewById(R.id.logout_btn);

        uploadNotice.setOnClickListener(this);
        addGalleryImage.setOnClickListener(this);
        addEbook.setOnClickListener(this);
        faculty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);
        logOut.setOnClickListener(this);
    }

    private void openLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.add_notice:
                intent = new Intent(MainActivity.this, UploadNoticeActivity.class);
                startActivity(intent);
                break;

            case R.id.add_gallery_image:
                intent = new Intent(MainActivity.this, UploadImageActivity.class);
                startActivity(intent);
                break;

            case R.id.add_ebook:
                intent = new Intent(MainActivity.this, UploadPdfActivity.class);
                startActivity(intent);
                break;

            case R.id.faculty:
                intent = new Intent(MainActivity.this, UpdateFacultyActivity.class);
                startActivity(intent);
                break;

            case R.id.delete_notice:
                intent = new Intent(MainActivity.this, DeleteNoticeActivity.class);
                startActivity(intent);
                break;

            case R.id.logout_btn:
                editor.putString("isLogin", "false");
                editor.commit();
                openLogin();
                break;
        }
    }
}