package com.example.chattappfirebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chattappfirebase.Chat;
import com.example.chattappfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageActivityAdapter extends RecyclerView.Adapter<MessageActivityAdapter.Holder> {
    private static final String TAG = "MessageActivityAdapter";
    public static final int SENDER_MSG = 75;
    public static final int RECEIVER_MSG = 85;
    private List<Chat> chatList;
    private String imageURL;
    private Context context;

    //constructors


    public MessageActivityAdapter(List<Chat> chatList, String imageURL, Context context) {
        this.chatList = chatList;
        this.imageURL = imageURL;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_MSG) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_item_layout, parent, false));
        } else {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_item_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Chat chat = chatList.get(position);
        holder.message.setText(chat.getMessage());

        if (imageURL.equals("default")) {
            holder.profileImage.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            Glide.with(context).load(imageURL).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private static final String TAG = "Holder-MessageActivityAdapter";

        ImageView profileImage;
        TextView message;

        public Holder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.chat_item_profile_pic);
            message = itemView.findViewById(R.id.chat_item_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return SENDER_MSG;
        } else {
            return RECEIVER_MSG;
        }

    }
}
