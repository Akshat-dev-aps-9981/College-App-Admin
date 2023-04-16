package com.aksapps.collageappadmin.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aksapps.collageappadmin.Adapters.TeacherAdapter;
import com.aksapps.collageappadmin.Models.TeacherData;
import com.aksapps.collageappadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UpdateFacultyActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private RecyclerView csDepartment, mathDepartment, physicsDepartment, chemistryDepartment;
    private LinearLayout csNoData, mathNoData, physicsNoData, chemistryNoData;
    private List<TeacherData> list1, list2, list3, list4;
    private TeacherAdapter adapter;

    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty);

        fab = findViewById(R.id.fab);
        csDepartment = findViewById(R.id.cs_department);
        mathDepartment = findViewById(R.id.math_department);
        physicsDepartment = findViewById(R.id.physics_department);
        chemistryDepartment = findViewById(R.id.chemistry_department);
        csNoData = findViewById(R.id.cs_no_data);
        mathNoData = findViewById(R.id.math_no_data);
        physicsNoData = findViewById(R.id.physics_no_data);
        chemistryNoData = findViewById(R.id.chemistry_no_data);

        reference = FirebaseDatabase.getInstance().getReference().child("Teacher");

        csDepartment();
        mathDepartment();
        physicsDepartment();
        chemistryDepartment();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateFacultyActivity.this, AddTeacherActivity.class));
            }
        });
    }

    private void mathDepartment() {
        dbRef = reference.child("Matematics");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list2 = new ArrayList<>();
                if (!dataSnapshot.exists()) {
                    mathNoData.setVisibility(View.VISIBLE);
                    mathDepartment.setVisibility(View.GONE);
                } else {
                    mathNoData.setVisibility(View.GONE);
                    mathDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TeacherData data = snapshot.getValue(TeacherData.class);
                        list2.add(data);
                    }
                    mathDepartment.setHasFixedSize(true);
                    mathDepartment.setLayoutManager(new LinearLayoutManager(UpdateFacultyActivity.this));
                    adapter = new TeacherAdapter(list2, UpdateFacultyActivity.this, "Mathematics");
                    mathDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFacultyActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void physicsDepartment() {
        dbRef = reference.child("Physics");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3 = new ArrayList<>();
                if (!dataSnapshot.exists()) {
                    physicsNoData.setVisibility(View.VISIBLE);
                    physicsDepartment.setVisibility(View.GONE);
                } else {
                    physicsNoData.setVisibility(View.GONE);
                    physicsDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TeacherData data = snapshot.getValue(TeacherData.class);
                        list3.add(data);
                    }
                    physicsDepartment.setHasFixedSize(true);
                    physicsDepartment.setLayoutManager(new LinearLayoutManager(UpdateFacultyActivity.this));
                    adapter = new TeacherAdapter(list3, UpdateFacultyActivity.this, "Physics");
                    physicsDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFacultyActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chemistryDepartment() {
        dbRef = reference.child("Chemistry");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list4 = new ArrayList<>();
                if (!dataSnapshot.exists()) {
                    chemistryNoData.setVisibility(View.VISIBLE);
                    chemistryDepartment.setVisibility(View.GONE);
                } else {
                    chemistryNoData.setVisibility(View.GONE);
                    chemistryDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TeacherData data = snapshot.getValue(TeacherData.class);
                        list4.add(data);
                    }
                    chemistryDepartment.setHasFixedSize(true);
                    chemistryDepartment.setLayoutManager(new LinearLayoutManager(UpdateFacultyActivity.this));
                    adapter = new TeacherAdapter (list4, UpdateFacultyActivity.this, "Chemistry");
                    chemistryDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFacultyActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void csDepartment() {
        dbRef = reference.child("Computer Science");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1 = new ArrayList<>();
                if (!dataSnapshot.exists()) {
                    csNoData.setVisibility(View.VISIBLE);
                    csDepartment.setVisibility(View.GONE);
                } else {
                    csNoData.setVisibility(View.GONE);
                    csDepartment.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TeacherData data = snapshot.getValue(TeacherData.class);
                        list1.add(data);
                    }
                    csDepartment.setHasFixedSize(true);
                    csDepartment.setLayoutManager(new LinearLayoutManager(UpdateFacultyActivity.this));
                    adapter = new TeacherAdapter(list1, UpdateFacultyActivity.this, "Computer Science");
                    csDepartment.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateFacultyActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}