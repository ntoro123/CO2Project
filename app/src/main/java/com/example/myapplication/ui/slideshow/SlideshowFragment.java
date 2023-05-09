package com.example.myapplication.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        recyclerView = root.findViewById(R.id.userlist);
        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        name = documentSnapshot.getString("name");
                    }
                });
        Button driveRankingBtn = root.findViewById(R.id.drive_sort_button);
        Button elecRankingBtn = root.findViewById(R.id.elec_sort_button);
        Button gasRankingBtn = root.findViewById(R.id.gas_sort_button);

        driveRankingBtn.setOnClickListener(view -> {
            getRankingBasedOnDriveAverage();
        });

        elecRankingBtn.setOnClickListener(view -> {
            getRankingBasedOnElecAverage();
        });

        gasRankingBtn.setOnClickListener(view -> {
            getRankingBasedOnGasAverage();
        });
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("driveaverage", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    int position = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                        if (user.getName().equals(name)) {
                            user.setCurrentUser(true);
                        }
                        position++;
                    }

                    // Create adapter and set as adapter for RecyclerView
                    UserRankingAdapter adapter = new UserRankingAdapter(userList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                });

        return root;
    }

    private void getRankingBasedOnDriveAverage() {
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("driveaverage", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    int position = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                        if (user.getName().equals(name)) {
                            user.setCurrentUser(true);
                        }
                        position++;
                    }

                    // Create adapter and set as adapter for RecyclerView
                    UserRankingAdapter adapter = new UserRankingAdapter(userList);
                    adapter.picker = 1;
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                });
    }

    private void getRankingBasedOnElecAverage() {
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("elecaverage", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    int position = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                        if (user.getName().equals(name)) {
                            user.setCurrentUser(true);
                        }
                        position++;
                    }

                    // Create adapter and set as adapter for RecyclerView
                    UserRankingAdapter adapter = new UserRankingAdapter(userList);
                    adapter.picker = 2;
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                });
    }

    private void getRankingBasedOnGasAverage() {
        FirebaseFirestore.getInstance().collection("users")
                .orderBy("gasaverage", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> userList = new ArrayList<>();
                    int position = 0;
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                        if (user.getName().equals(name)) {
                            user.setCurrentUser(true);
                        }
                        position++;
                    }

                    // Create adapter and set as adapter for RecyclerView
                    UserRankingAdapter adapter = new UserRankingAdapter(userList);
                    adapter.picker = 3;
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                });
                }
}
