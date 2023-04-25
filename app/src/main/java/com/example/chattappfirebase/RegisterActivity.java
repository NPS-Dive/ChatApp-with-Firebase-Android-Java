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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private EditText username, email, password, password2;
    private Button registerBTN;
    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        password2 = findViewById(R.id.register_password2);
        registerBTN = findViewById(R.id.register_BTN);

        auth = FirebaseAuth.getInstance();

        viewsEvents();
    }

    private void viewsEvents() {
        registerBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CheckIfEverythingIsOk();
    }

    private void CheckIfEverythingIsOk() {
        if (!isFieldEmpty()) {
            if (arePasswordsIdentical()) {
                registerAccount();
            } else {
                Toast.makeText(this, R.string.passwordsMismatch, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.fillEmpties, Toast.LENGTH_SHORT).show();
        }
    }

    private void registerAccount() {
        auth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                String uid = user.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", uid);
                                hashMap.put("username", username.getText().toString().trim());
                                hashMap.put("imageURL", "default");

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(RegisterActivity.this, AppMainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, R.string.cantMakeNewAccount, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, R.string.cantMakeNewAccount, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean arePasswordsIdentical() {
        if (password2.getText().toString().trim().equals(password.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFieldEmpty() {
        if (username.getText().toString().trim().equals("")
                || email.getText().toString().trim().equals("")
                || password.getText().toString().trim().equals("")
                || password2.getText().toString().trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }
}