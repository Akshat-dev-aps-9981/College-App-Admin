package com.aksapps.collageappadmin.Activities;

import static com.aksapps.collageappadmin.Models.Constants.TOPIC;

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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aksapps.collageappadmin.API.APIUtilities;
import com.aksapps.collageappadmin.Models.NoticeData;
import com.aksapps.collageappadmin.Models.NotificationData;
import com.aksapps.collageappadmin.Models.PushNotification;
import com.aksapps.collageappadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadNoticeActivity extends AppCompatActivity {
    private MaterialCardView addImage;
    private static final int REQUEST_CODE = 1;
    private Uri uri = null;
    private Bitmap bitmap = null;
    private ImageView noticeImageView;
    private TextInputEditText noticeTitle;
    private MaterialButton uploadNoticeBtn;
    private RadioGroup authorRadio;
    private MaterialRadioButton defAuthor, manAuthor;
    private TextInputLayout authorNameLayout;
    private TextInputEditText authorName;

    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;
    private String downloadUrl = "", title = "", authorname = "Collage App Admin";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        addImage = findViewById(R.id.add_image);
        noticeImageView = findViewById(R.id.notice_image_view);
        noticeTitle = findViewById(R.id.notice_title);
        uploadNoticeBtn = findViewById(R.id.upload_notice_button);
        authorRadio = findViewById(R.id.author_radio);
        defAuthor = findViewById(R.id.def_author_radio);
        manAuthor = findViewById(R.id.man_author_radio);
        authorNameLayout = findViewById(R.id.author_name_layout);
        authorName = findViewById(R.id.author_name);

        progressDialog = new ProgressDialog(this);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        defAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        manAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        uploadNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authorNameLayout.getVisibility() == View.GONE && authorName.getVisibility() == View.GONE && noticeTitle.getText().toString().isEmpty()) {
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();
                } else if (authorNameLayout.getVisibility() == View.VISIBLE && authorName.getVisibility() == View.VISIBLE && authorName.getText().toString().isEmpty()) {
                    authorName.setError("Author Name must be specified.");
                    authorName.requestFocus();
                }
                else if (authorNameLayout.getVisibility() == View.GONE && authorName.getVisibility() == View.GONE && bitmap == null) {
                    uploadData();
                } else if (authorNameLayout.getVisibility() == View.GONE && authorName.getVisibility() == View.GONE && bitmap != null){
                    uploadImage();
                } else if (authorNameLayout.getVisibility() == View.VISIBLE && authorName.getVisibility() == View.VISIBLE && bitmap != null){
                    uploadImageWithAuthor();
                } else if (authorNameLayout.getVisibility() == View.VISIBLE && authorName.getVisibility() == View.VISIBLE && bitmap == null){
                    uploadDataWithAuthor();
                } else {
                    Toast.makeText(UploadNoticeActivity.this, "An unexpected error occured.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void sendNotif(String authorname, String title) {
        if (!authorname.isEmpty() && !title.isEmpty()) {
            PushNotification notification = new PushNotification(new NotificationData(authorname, title), TOPIC);
            APIUtilities.getClient().sendNotification(notification).enqueue(new Callback<PushNotification>() {
                @Override
                public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UploadNoticeActivity.this, "Successfully Sent Notification.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UploadNoticeActivity.this, "An error occured while sending Notification.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PushNotification> call, Throwable t) {
                    Toast.makeText(UploadNoticeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadDataWithAuthor() {
        progressDialog.setTitle("Uploading Notice...");
        progressDialog.setMessage("Uploading Notice Text, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbRef = reference.child("Notice");
        final String uniqueKey = dbRef.push().getKey();

        authorname = authorName.getText().toString();
        title = noticeTitle.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calForTime.getTime());

        NoticeData noticeData = new NoticeData(authorname, title, downloadUrl, date, time, uniqueKey);

        dbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                sendNotif(authorname, title);
                Toast.makeText(UploadNoticeActivity.this, "Notice Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadNoticeActivity.this, "Uploading Notice Data Failed. Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageWithAuthor() {
        progressDialog.setTitle("Uploading Image...");
        progressDialog.setMessage("Uploading Notice Image, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Notice").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadNoticeActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    uploadDataWithAuthor();
                                }
                            });
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNoticeActivity.this, "Image Uploading Failed. Exception: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage() {
        progressDialog.setTitle("Uploading Image...");
        progressDialog.setMessage("Uploading Notice Image, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalImage = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Notice").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadNoticeActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(UploadNoticeActivity.this, "Image Uploading Failed. Exception: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData() {
        progressDialog.setTitle("Uploading Notice...");
        progressDialog.setMessage("Uploading Notice Text, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbRef = reference.child("Notice");
        final String uniqueKey = dbRef.push().getKey();

        title = noticeTitle.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calForTime.getTime());

        NoticeData noticeData = new NoticeData(authorname, title, downloadUrl, date, time, uniqueKey);

        dbRef.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                sendNotif(authorname, title);
                Toast.makeText(UploadNoticeActivity.this, "Notice Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadNoticeActivity.this, "Uploading Notice Data Failed. Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onRadioButtonClicked(@NonNull View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.def_author_radio:
                if (checked) {
                    authorNameLayout.setVisibility(View.GONE);
                    authorName.setVisibility(View.GONE);
                }
                break;
            case R.id.man_author_radio:
                if (checked) {
                    authorNameLayout.setVisibility(View.VISIBLE);
                    authorName.setVisibility(View.VISIBLE);
                }
                break;
        }
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
                noticeImageView.setImageBitmap(bitmap);

        }
    }
}