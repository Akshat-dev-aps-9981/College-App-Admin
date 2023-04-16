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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aksapps.collageappadmin.Models.TeacherData;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeacherActivity extends AppCompatActivity {
    private CircleImageView addTeacherImage;
    private TextInputEditText addTeacherName, addTeacherEmail, addTeacherPhone;
    private Spinner addTeacherCategory;
    private MaterialButton addTeacherBtn;

    private String category = "";

    private static final int REQUEST_CODE = 4;
    private Uri uri = null;
    private Bitmap bitmap = null;

    private String teacherName = "", teacherEmail = "", teacherPhone = "", downloadUrl = "";

    private ProgressDialog progressDialog;

    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        addTeacherImage = findViewById(R.id.add_teacher_image);
        addTeacherName = findViewById(R.id.add_teacher_name);
        addTeacherEmail = findViewById(R.id.add_teacher_email);
        addTeacherPhone = findViewById(R.id.add_teacher_phone);
        addTeacherCategory = findViewById(R.id.add_teacher_category);
        addTeacherBtn = findViewById(R.id.add_teacher_btn);

        progressDialog = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] items = new String[] {"Select Category", "Computer Science", "Mathematics", "Physics", "Chemistry"};
        addTeacherCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));

        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                category = addTeacherCategory.getSelectedItem().toString();
                // adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        addTeacherImage.setOnClickListener((view) -> {
            openGallery();
        });

        addTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        teacherName = addTeacherName.getText().toString();
        teacherEmail = addTeacherEmail.getText().toString();
        teacherPhone = addTeacherPhone.getText().toString();

        if (teacherName.isEmpty()) {
            addTeacherName.setError("Please Enter Teacher's Name.");
            addTeacherName.requestFocus();
        } else if (teacherEmail.isEmpty()) {
            addTeacherEmail.setError("Please Enter Teacher's Email.");
            addTeacherEmail.requestFocus();
        } else if (teacherPhone.isEmpty()) {
            addTeacherPhone.setError("Please Enter Teacher's Phone Number.");
            addTeacherPhone.requestFocus();
        } else if (category.equals("Select Category") || category.isEmpty()){
            Toast.makeText(this, "Please Provide Teacher's Category.", Toast.LENGTH_SHORT).show();
        } else if (bitmap == null) {
            teacherPhone = "+91" + addTeacherPhone.getText().toString();
            insertData();
        } else {
            teacherPhone = "+91" + addTeacherPhone.getText().toString();
            insertImage();
        }
    }

    private void insertImage() {
        progressDialog.setTitle("Uploading Image...");
        progressDialog.setMessage("Uploading Teacher's Image, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Teachers").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(AddTeacherActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    insertData();
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddTeacherActivity.this, "Image Uploading Failed. Exception: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertData() {
        progressDialog.setTitle("Uploading Notice...");
        progressDialog.setMessage("Uploading Notice Text, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbRef = reference.child(category);
        final String uniqueKey = dbRef.push().getKey();

        TeacherData teacherData = new TeacherData(teacherName, teacherEmail, teacherPhone, downloadUrl, uniqueKey);


        dbRef.child(uniqueKey).setValue(teacherData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(AddTeacherActivity.this, "Teacher Added.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddTeacherActivity.this, "Adding Teacher's Data Failed. Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
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
                addTeacherImage.setImageBitmap(bitmap);
        }
    }
}