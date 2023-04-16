package com.aksapps.collageappadmin.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.aksapps.collageappadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateTeacherActivity extends AppCompatActivity {
    private CircleImageView updateTeacherImage;
    private TextInputEditText updateTeacherName, updateTeacherEmail, updateTeacherPhone;
    private MaterialButton updateTeacherBtn, deleteTeacherBtn;

    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private static final int REQUEST_CODE = 5;
    private Uri imageUri = null;
    private Bitmap bitmap = null;

    private String category = "", uniqueKey = "";

    private String name = "", email = "", image = "", phone = "", downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        image = getIntent().getStringExtra("image");


        uniqueKey = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");

        progressDialog = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        updateTeacherImage = findViewById(R.id.update_teacher_image);
        updateTeacherName = findViewById(R.id.update_teacher_name);
        updateTeacherEmail = findViewById(R.id.update_teacher_email);
        updateTeacherPhone = findViewById(R.id.update_teacher_phone);
        updateTeacherBtn = findViewById(R.id.update_teacher_btn);
        deleteTeacherBtn = findViewById(R.id.delete_teacher_btn);

        try {
            Picasso.get().load(image).into(updateTeacherImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateTeacherName.setText(name);
        updateTeacherEmail.setText(email);
        updateTeacherPhone.setText(phone);

        updateTeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        updateTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = updateTeacherName.getText().toString();
                email = updateTeacherEmail.getText().toString();
                phone = updateTeacherPhone.getText().toString();
                checkValidations();
            }
        });

        deleteTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }

    private void deleteData() {
        progressDialog.setTitle("Deleting Data...");
        progressDialog.setMessage("Deleting Teacher's Data, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();
        reference.child(category).child(uniqueKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if (!task.isSuccessful()) {
                  progressDialog.dismiss();
                  Toast.makeText(UpdateTeacherActivity.this, "Deleting Data Failed. Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
              } else {
                  progressDialog.dismiss();
                  Toast.makeText(UpdateTeacherActivity.this, "Data deleted successfully.", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFacultyActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(intent);
                  finish();
              }
            }
        });
    }

    private void checkValidations() {
        if (name.isEmpty()) {
            updateTeacherName.setError("Please Enter Teacher's Name.");
            updateTeacherName.requestFocus();
        } else if (email.isEmpty()) {
            updateTeacherEmail.setError("Please Enter Teacher's Email.");
            updateTeacherEmail.requestFocus();
        } else if (phone.isEmpty()) {
            updateTeacherPhone.setError("Please Enter Teacher's Phone Number.");
            updateTeacherPhone.requestFocus();
        } else if (bitmap == null) {
            phone = "+91" + updateTeacherPhone.getText().toString();
            updateData(image);
        } else  {
            phone = "+91" + updateTeacherPhone.getText().toString();
            uploadImage();
        }
    }

    private void uploadImage() {
        progressDialog.setTitle("Updating Image...");
        progressDialog.setMessage("Updating Teacher's Image, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Teachers").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UpdateTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadUrl);
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateTeacherActivity.this, "Image Uploading Failed. Exception: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateData(String s) {
        progressDialog.setTitle("Updating Data...");
        progressDialog.setMessage("Updating Teacher's Data, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("phone", phone);
        hashMap.put("image", s);

        reference.child(category).child(uniqueKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "Data updated successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateTeacherActivity.this, UpdateFacultyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateTeacherActivity.this, "An error occured while updating data. Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {
                updateTeacherImage.setImageBitmap(bitmap);
            }
        }
    }
}