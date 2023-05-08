package com.example.myapplication.ui.editaccount;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class editAccount extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private TextView email;
    private TextView name;
    private TextView city;
    private TextView yearmake;
    private Spinner year;
    private List<String> list;
    private ArrayAdapter<String> adapter;
    private Button logout;
    private Button lockin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DocumentReference uRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        email = findViewById(R.id.thisemail);
        name = findViewById(R.id.thisname);
        city = findViewById(R.id.thiscity);
        yearmake = findViewById(R.id.carYear);
        lockin = findViewById(R.id.lockin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        uRef = db.collection("users").document(uid);

        year = findViewById(R.id.years);
        list = new ArrayList<>();

        for (int i = 2000; i <= 2023; i++) {
            list.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year.setAdapter(adapter);

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedYear = list.get(position);
                // Do something with the selected year
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(editAccount.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        lockin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            uRef.update("caryear", year.getSelectedItem().toString());
                            yearmake.setText(year.getSelectedItem().toString());
                        }
                    }
                });
            }
        });
        uRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    email.setText(currentUser.getEmail());
                    name.setText(documentSnapshot.getString("name"));
                    city.setText(documentSnapshot.getString("city"));
                    if (documentSnapshot.contains("caryear"))
                    {
                        yearmake.setText(documentSnapshot.getString("caryear"));
                    }
                }
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}