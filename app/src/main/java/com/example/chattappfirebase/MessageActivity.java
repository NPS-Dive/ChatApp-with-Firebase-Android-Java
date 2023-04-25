package com.example.chattappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chattappfirebase.adapter.MessageActivityAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MessageActivity";
    private Toolbar toolbar;
    private ImageView profileImage, profileStatus;
    private TextView profileUsername;
    private EditText inputTXT;
    private ImageButton sendBTN;
    private RecyclerView messageRecyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference usersReference;
    private DatabaseReference chatsReference;
    private String userID;
    private List<Chat> chatsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        init();
    }

    private void init() {
        userID = getIntent().getStringExtra("userID");
        toolbar = findViewById(R.id.message_toolbar);
        profileImage = findViewById(R.id.message_profile_pic);
        profileStatus = findViewById(R.id.message_profile_status);
        profileUsername = findViewById(R.id.message_profile_username);
        inputTXT = findViewById(R.id.messageActivity_inputTXT);
        sendBTN = findViewById(R.id.messageActivity_send_BTN);
        messageRecyclerView = findViewById(R.id.message_recyclerView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        chatsReference = FirebaseDatabase.getInstance().getReference("Chats");

        setupToolBar();
        viewEvents();
    }


    private void viewEvents() {

        sendBTN.setOnClickListener(this);
    }

    private void setupToolBar() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                profileUsername.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    Glide.with(getBaseContext()).load(user.getImageURL()).into(profileImage);
                }

                if (user.getStatus().equals("Online")) {
                    profileStatus.setVisibility(View.VISIBLE);
                } else {
                    profileStatus.setVisibility(View.INVISIBLE);
                }

                readMessages(user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        String message = inputTXT.getText().toString().trim();

        if (!message.equals("")) {
            sendMessage(message);
            inputTXT.setText("");
        }
    }

    private void sendMessage(String message) {
        DatabaseReference messageREF = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> messageMap = new HashMap<>();

        messageMap.put("sender", firebaseUser.getUid());
        messageMap.put("receiver", userID);
        messageMap.put("message", message);

        messageREF.child("Chats").push().setValue(messageMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SetStatus.setStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SetStatus.setStatus("Offline");
    }

    private void readMessages(String imageURL) {
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())
                            && chat.getReceiver().equals(userID)
                            || chat.getReceiver().equals(firebaseUser.getUid())
                            && chat.getSender().equals(userID)) {

                        chatsList.add(chat);
                    }
                }
                MessageActivityAdapter adapter = new MessageActivityAdapter(chatsList, imageURL, MessageActivity.this);
                messageRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}