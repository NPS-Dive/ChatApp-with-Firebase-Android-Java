package com.example.chattappfirebase.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chattappfirebase.Chat;
import com.example.chattappfirebase.R;
import com.example.chattappfirebase.User;
import com.example.chattappfirebase.adapter.ContactsRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "Chat";
    private RecyclerView chatsRecyclerView;
    private FirebaseUser currentUser;
    private DatabaseReference dbReference;
    private List<String> senderOrReceiverID;
    private List<User> usersList;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        initializeChat(root);
        return root;
    }

    private void initializeChat(View root) {
        chatsRecyclerView = root.findViewById(R.id.chats_recycler_view);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        senderOrReceiverID = new ArrayList<>();
        usersList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Chats");

        getSenderOrReceiver();
    }

    private void getSenderOrReceiver() {
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderOrReceiverID.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getSender().equals(currentUser.getUid())) {
                        if (senderOrReceiverID.contains(chat.getReceiver())) {
                            senderOrReceiverID.add(chat.getReceiver());
                        }
                    } else if (chat.getReceiver().equals(currentUser.getUid())) {
                        if (senderOrReceiverID.contains(chat.getSender())
                                && !chat.getSender().equals(currentUser.getUid())) {
                            senderOrReceiverID.add(chat.getSender());
                        }
                    }

                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats() {

            dbReference= FirebaseDatabase.getInstance().getReference("Users");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        User user = dataSnapshot.getValue(User.class);

                        for(String userID: senderOrReceiverID){
                            if(user.getId().equals(userID)){
                                usersList.add(user);
                            }
                        }
                    }
                    ContactsRVAdapter adapter= new ContactsRVAdapter(usersList,getContext());
                    chatsRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

}