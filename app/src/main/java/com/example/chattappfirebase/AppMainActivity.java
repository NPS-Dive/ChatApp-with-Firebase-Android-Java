package com.example.chattappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chattappfirebase.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppMainActivity extends AppCompatActivity {
    private static final String TAG = "AppMainActivity";
    private ImageView profileImage;
    private TextView profileUsername;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);


        init();
    }

    private void init() {
        toolbar = findViewById(R.id.appMainActivity_toolbar);
        setSupportActionBar(toolbar);
        profileImage = findViewById(R.id.appMainActivity_profile_pic);
        profileUsername = findViewById(R.id.appMainActivity_profile_username);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.my_viewPager);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                profileUsername.setText(user.getUsername());

                //set profile image on top toolbar
                if (user.getImageURL().equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    Glide.with(AppMainActivity.this).load(user.getImageURL()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        initViewPager();
    }

    private void initViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Contacts");
                        break;
                    case 1:
                        tab.setText("Chat");
                        break;
                    case 2:
                        tab.setText("Profile");
                        break;
                }
            }
        }).attach();
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
}