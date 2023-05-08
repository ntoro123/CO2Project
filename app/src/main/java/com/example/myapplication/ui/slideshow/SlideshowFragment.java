package com.example.myapplication.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class SlideshowFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private String currentUserID;
    private int currentUserRank;

    private TextView specText, fplace, splace, tplace, uplace;

    private List<DocumentSnapshot> userDocs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        specText = root.findViewById(R.id.specText);
        fplace = root.findViewById(R.id.First);
        splace = root.findViewById(R.id.Second);
        tplace = root.findViewById(R.id.Third);
        uplace = root.findViewById(R.id.uplace);

        usersRef.orderBy("average", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userDocs = queryDocumentSnapshots.getDocuments();

                    for (int i = 0; i < userDocs.size(); i++) {
                        if (userDocs.get(i).getId().equals(currentUserID)) {
                            currentUserRank = i + 1;
                            break;
                        }
                    }

                    if (currentUserRank == 0)
                    {
                        specText.setText("You don't seem to have any recorded values, input some of your adventures into the graphs tab!");
                    }
                    else {
                        specText.setText("Your Place on the Leaderboards is: #" + currentUserRank);
                    }


                    if (userDocs.size() >= 3) {
                        fplace.setText(userDocs.get(0).getString("name") + ": " + String.format("%.2f",userDocs.get(0).getDouble("average")));
                        splace.setText(userDocs.get(1).getString("name") + ": " + String.format("%.2f",userDocs.get(1).getDouble("average")));
                        tplace.setText(userDocs.get(2).getString("name") + ": " + String.format("%.2f",userDocs.get(2).getDouble("average")));
                    }

                    for (int i = 0; i < userDocs.size(); i++) {
                        if (userDocs.get(i).getId().equals(currentUserID)) {
                            uplace.setText(Integer.toString(i + 1));
                            break;
                        }
                    }
                });

        return root;
    }
}