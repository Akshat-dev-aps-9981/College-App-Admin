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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

public class UploadPdfActivity extends AppCompatActivity {
    private MaterialCardView selectPdf;
    private TextView pdfTitleOutput;
    private TextInputEditText pdfTitleInput;
    private MaterialButton uploadPdfBtn;

    private String pdfName, title;

    private static final int REQUEST_CODE = 3;
    private Uri pdfData;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        selectPdf = findViewById(R.id.add_pdf);
        pdfTitleOutput = findViewById(R.id.pdf_title_output);
        pdfTitleInput = findViewById(R.id.pdf_title_input);
        uploadPdfBtn = findViewById(R.id.upload_pdf_btn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading PDF...");
        progressDialog.setMessage("Uploading PDF, Please Wait...");
        progressDialog.setIcon(R.drawable.launcher_icon);
        progressDialog.setCancelable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        uploadPdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = pdfTitleInput.getText().toString();
                if (title.isEmpty()) {
                    pdfTitleInput.setError("Enter PDF Title Here!");
                    pdfTitleInput.requestFocus();
                    Toast.makeText(UploadPdfActivity.this, "Please Enter PDF Title.", Toast.LENGTH_SHORT).show();
                } else if (pdfData  == null) {
                    Toast.makeText(UploadPdfActivity.this, "Please Select One PDF.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf();
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File."), REQUEST_CODE);
    }

    private void uploadPdf() {
        progressDialog.show();
        StorageReference reference = storageReference.child("pdf/" + pdfName + "-" + System.currentTimeMillis() + ".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(UploadPdfActivity.this, "An error occured. Please try again later. Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String downloadUrl) {
        String uniqueKey = databaseReference.child("pdf").push().getKey();
        HashMap<String, String> data = new HashMap<>();
        data.put("pdfTitle", title);
        data.put("pdfUrl", downloadUrl);

        databaseReference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(UploadPdfActivity.this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                progressDialog.dismiss();
                Toast.makeText(UploadPdfActivity.this, "PDF uploaded successfully.", Toast.LENGTH_SHORT).show();
                pdfTitleInput.setText("");
            }

        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(Exception e)
            {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(UploadPdfActivity.this, "Failed to upload pdf. Exception: " + e.toString(), Toast.LENGTH_LONG).show();
            }

        });
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            pdfData = data.getData();
            if(pdfData.toString().startsWith("content://"))
            {
                Cursor cursor = null;
                try{
                    cursor = UploadPdfActivity.this.getContentResolver().query(pdfData, null, null, null, null);
                    if(cursor != null && cursor.moveToFirst())
                    {
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if(pdfData.toString().startsWith("file://"))
            {
                pdfName = new File(pdfData.toString()).getName();
            }

            pdfTitleOutput.setText(pdfName);
        }
    }
}