package com.example.chattappfirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattappfirebase.MessageActivity;
import com.example.chattappfirebase.R;
import com.example.chattappfirebase.User;

import java.util.List;

public class ContactsRVAdapter extends RecyclerView.Adapter<ContactsRVAdapter.Holder> {
    private static final String TAG = "ContactsRVAdapter";

    private List<User> usersList;
    private Context context;

    //constructor


    public ContactsRVAdapter(List<User> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        User user = usersList.get(position);
        holder.username.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            holder.profileImage.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userID", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private static final String TAG = "Holder";
        ImageView profileImage;
        TextView username;

        public Holder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.item_pic);
            username = itemView.findViewById(R.id.item_username);
        }
    }
}
