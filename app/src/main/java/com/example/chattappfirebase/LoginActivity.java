package com.example.chattappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private EditText loginEmail, loginPassword;
    private Button loginBTN;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginBTN = findViewById(R.id.login_BTN);

        auth = FirebaseAuth.getInstance();
        viewEvent();
    }

    private void viewEvent() {
        loginBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        checkIfEverythingIsOk();
    }

    private void checkIfEverythingIsOk() {
        if (!isFieldEmpty()) {
            auth.signInWithEmailAndPassword(loginEmail.getText().toString().trim(), loginPassword.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, AppMainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.cantEnterYourAccount, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, R.string.fillEmpties, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFieldEmpty() {
        if (loginEmail.getText().toString().trim().equals("")
                || loginPassword.getText().toString().trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }
}