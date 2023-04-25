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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ForgetPassActivity";
    private EditText forgetPassEmail;
    private Button forgetPasswordBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);

        init();
    }

    private void init() {
        forgetPassEmail = findViewById(R.id.forgetPass_email);
        forgetPasswordBTN = findViewById(R.id.forgetPass_BTN);

        viewEvents();
    }

    private void viewEvents() {
        forgetPasswordBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CheckIfEverythingIsOk();
    }

    private void CheckIfEverythingIsOk() {
        if (!isFieldEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(forgetPassEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPassActivity.this, R.string.checkEmail, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgetPassActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ForgetPassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, R.string.fillEmpties, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFieldEmpty() {
        if (forgetPassEmail.getText().toString().trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }
}