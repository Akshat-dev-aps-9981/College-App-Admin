package com.aksapps.collageappadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aksapps.collageappadmin.Activities.UpdateTeacherActivity;
import com.aksapps.collageappadmin.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aksapps.collageappadmin.Models.TeacherData;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewAdapter> {
    private List<TeacherData> list;
    private Context context;
    private String category;

    public TeacherAdapter() {}

    public TeacherAdapter(List<TeacherData> list, Context context, String category) {
        this.list = list;
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public TeacherViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculty_item_layout, parent, false);
        return new TeacherViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewAdapter holder, int position) {
        TeacherData items = list.get(position);
        holder.name.setText(items.getName());
        holder.email.setText(items.getEmail());
        holder.phone.setText(items.getPhone());

        try {
            Picasso.get().load(items.getImage()).into(holder.imageView);
        } catch (Exception e){
            e.printStackTrace();
        }

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateTeacherActivity.class);
                intent.putExtra("name", items.getName());
                intent.putExtra("email", items.getEmail());
                intent.putExtra("phone", items.getPhone());
                intent.putExtra("image", items.getImage());
                intent.putExtra("key", items.getUniqueKey());
                intent.putExtra("category", category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TeacherViewAdapter extends RecyclerView.ViewHolder {
        private TextView name, email, phone;
        private MaterialButton updateBtn;
        private CircleImageView imageView;

        public TeacherViewAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacher_name);
            email = itemView.findViewById(R.id.teacher_email);
            phone = itemView.findViewById(R.id.teacher_phone_number);
            updateBtn = itemView.findViewById(R.id.teacher_update_btn);
            imageView = itemView.findViewById(R.id.teacher_image);
        }
    }
}
