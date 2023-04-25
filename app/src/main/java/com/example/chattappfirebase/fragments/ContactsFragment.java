package com.example.chattappfirebase.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chattappfirebase.R;
import com.example.chattappfirebase.User;
import com.example.chattappfirebase.adapter.ContactsRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
    private static final String TAG = "Contacts";

    private SearchView searchView;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private List<User> usersList;
    private ContactsRVAdapter adapter;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        initial(root);
        return root;
    }

    private void initial(View root) {
        searchView = root.findViewById(R.id.contacts_searchBar);
        recyclerView = root.findViewById(R.id.contacts_recycleView);
        usersList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getContactsFromFireBase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    getContactsFromFireBase();
                } else {
                    searchForContacts(newText);
                }
                return true;
            }
        });
    }

    private void searchForContacts(String newText) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance()
                .getReference("Users")
                .orderByChild("username")
                .startAt(newText)
                .endAt(newText + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user1 = dataSnapshot.getValue(User.class);

                    if (user1 != null && user != null) {

                        //to be show within recycle view
                        if (!user1.getId().equals(user.getUid())) {
                            usersList.add(user1);

                        }
                    }
                }
                adapter = new ContactsRVAdapter(usersList, getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getContactsFromFireBase() {

        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (!user.getId().equals(firebaseUser.getUid())) {
                                usersList.add(user);
                            }
                        }
                        adapter = new ContactsRVAdapter(usersList, getContext());
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}