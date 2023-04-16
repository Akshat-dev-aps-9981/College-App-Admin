package com.aksapps.collageappadmin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aksapps.collageappadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImageActivity extends AppCompatActivity {
    private Spinner imageCategory;
    private MaterialCardView addGalleryImageB;
    private MaterialButton uploadImage;
    private ImageView galleryImageView;
    private String category = "";

    private DatabaseReference reference;
    private StorageReference storageReference;

    private String downloadUrl;

    private Uri uri = null;
    private Bitmap bitmap = null;

    private ProgressDialog progressDialog;

    private static final int REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        imageCategory = findViewById(R.id.image_category);
        addGalleryImageB = findViewById(R.id.add_gallery_image_b);
        uploadImage = findViewById(R.id.upload_image_btn);
        galleryImageView = findViewById(R.id.gallery_image_view);

        reference = FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("Gallery");

        progressDialog =  new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.setMessage("Uploading Image, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);

        String[] items = new String[] {"Select Category", "Convocation", "Independence Day", "Other Events"};
        imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));

        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = imageCategory.getSelectedItem().toString();
              // adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        addGalleryImageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null) {
                    Toast.makeText(UploadImageActivity.this, "Please Upload an Image.", Toast.LENGTH_SHORT).show();
                } else if (category.equals("Select Category") || category.isEmpty())  {
                    Toast.makeText(UploadImageActivity.this, "Please Select One Valid Category.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    uploadImage();
                }
            }
        });
    }

    private void uploadImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadImageActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    downloadUrl = String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadImageActivity.this, "Image Uploading Failed. Exception: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        progressDialog.setMessage("Uploading Image Data, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.show();
        reference = reference.child(category);
        final String uniqueKey = reference.push().getKey();

        reference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(UploadImageActivity.this, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadImageActivity.this, "Uploading Image Failed. Reason: " + e.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null)
                galleryImageView.setImageBitmap(bitmap);
        }
    }
}