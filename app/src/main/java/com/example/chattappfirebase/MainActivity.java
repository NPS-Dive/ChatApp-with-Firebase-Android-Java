package com.example.chattappfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView login, forgetPass, register;
    private FirebaseUser firebaseUser;

    //for autologin
    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(MainActivity.this, AppMainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        login = findViewById(R.id.loginTXT);
        forgetPass = findViewById(R.id.forgetPassTXT);
        register = findViewById(R.id.registerTXT);

        viewsEvents();
    }

    private void viewsEvents() {
        login.setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.loginTXT) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.forgetPassTXT) {
            startActivity(new Intent(MainActivity.this, ForgetPassActivity.class));
        } else if (id == R.id.registerTXT) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        }
    }
}