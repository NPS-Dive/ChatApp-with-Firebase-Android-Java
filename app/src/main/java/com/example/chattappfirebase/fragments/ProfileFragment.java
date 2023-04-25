package com.example.chattappfirebase.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chattappfirebase.MainActivity;
import com.example.chattappfirebase.R;
import com.example.chattappfirebase.SetStatus;
import com.example.chattappfirebase.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "Profile";
    private ImageView profileImage;
    private TextView profileUsername;
    private Button profileLogoutBTN;
    private FirebaseUser firebaseUser;
    private DatabaseReference dbReference;
    private StorageReference storageReference;


    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeProfile(root);
        return root;
    }

    private void initializeProfile(View root) {
        profileImage = root.findViewById(R.id.profile_pic);
        profileUsername = root.findViewById(R.id.profile_username);
        profileLogoutBTN = root.findViewById(R.id.profile_logout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                profileUsername.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    profileImage.setImageResource(R.drawable.ic_launcher_foreground);
                } else {
                    Glide.with(getContext()).load(user.getImageURL()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewEvents();

    }

    private void viewEvents() {
        profileImage.setOnClickListener(this);
        profileLogoutBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_pic) {
            openPictureGallery();
        } else if (v.getId() == R.id.profile_logout) {
            SetStatus.setStatus("Offline");
            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        }
    }

    private void openPictureGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        profileImage.setImageURI(result.getData().getData());
                        uploadImage(result.getData().getData());
                    }
                }
            });

    private void uploadImage(Uri uri) {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(String.valueOf(R.string.uploading));
        dialog.show();
        if (uri != null) {
            StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri));
            StorageTask task = imageReference.putFile(uri);
            task.continueWithTask(new Continuation() {
                        @Override
                        public Task<Uri> then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imageReference.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Uri imageURI = (Uri) task1.getResult();
                            String uriString = imageURI.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", uriString);
                            dbReference.updateChildren(map);

                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), R.string.unSucceeded, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        } else {
            Toast.makeText(getContext(), R.string.noPicChosen, Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContext().getContentResolver().getType(uri));
    }


}