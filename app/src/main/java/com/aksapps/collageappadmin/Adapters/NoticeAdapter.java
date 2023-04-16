package com.aksapps.collageappadmin.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aksapps.collageappadmin.Models.NoticeData;
import com.aksapps.collageappadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {
    private Context context;
    private ArrayList<NoticeData> list;

    public NoticeAdapter() {}

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {
        NoticeData currentItem = list.get(holder.getAdapterPosition());

        try {
            if (currentItem.getAuthor() != null) {
                holder.authorNameTV.setText(currentItem.getAuthor().toString());
            }

            if (holder.authorNameTV.getText().toString().equals("Collage App Admin")) {
                holder.authorNameTV.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_verified), null);
    //            holder.authorNameTV.setCompoundDrawablePadding(15);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.deleteNoticeTitle.setText(currentItem.getTitle().toString());
        try {
            if (currentItem.getImage() != null)
                Picasso.get().load(currentItem.getImage()).into(holder.deleteNoticeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.deleteNoticeBtn.setText("Delete");
        holder.deleteNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Attention!");
                builder.setMessage("Do you really want to delete this notice?");
                builder.setIcon(R.drawable.launcher_icon);
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
                        reference.child(currentItem.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Notice successfully deleted.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Deleting Notice Failed. Reason: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        notifyItemRemoved(holder.getAdapterPosition());
                        dialogInterface.dismiss();
                        return;
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        return;
                    }
                });
                AlertDialog dialog = null;
                try {
                    dialog = builder.create();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dialog != null)
                    dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {
        private MaterialButton deleteNoticeBtn;
        private TextView authorNameTV, deleteNoticeTitle;
        private ImageView deleteNoticeImage;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteNoticeBtn = itemView.findViewById(R.id.delete_notice_btn);
            authorNameTV = itemView.findViewById(R.id.author_name_tv);
            deleteNoticeTitle = itemView.findViewById(R.id.delete_notice_title);
            deleteNoticeImage = itemView.findViewById(R.id.delete_notice_image);
        }
    }
}
