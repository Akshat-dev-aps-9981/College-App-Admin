package com.aksapps.collageappadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aksapps.collageappadmin.Adapters.NoticeAdapter;
import com.aksapps.collageappadmin.Models.NoticeData;
import com.aksapps.collageappadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class DeleteNoticeActivity extends AppCompatActivity {
    private RecyclerView deleteNoticerRecyclerView;
    private ProgressBar progressBar;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);

        deleteNoticerRecyclerView = findViewById(R.id.delete_notice_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        reference = FirebaseDatabase.getInstance().getReference().child("Notice");

        deleteNoticerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deleteNoticerRecyclerView.setHasFixedSize(true);

        getNotice();
    }

    private void getNotice() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoticeData data = snapshot.getValue(NoticeData.class);
                    list.add(0, data);
                    adapter = new NoticeAdapter(DeleteNoticeActivity.this, list);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    deleteNoticerRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeleteNoticeActivity.this, "Something went wrong: " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}