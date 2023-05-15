package com.example.myapplication.ui.editaccount;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editAccount extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText upname;
    private EditText upcity;
    private TextView email;
    private TextView name;
    private TextView city;
    private TextView yearmake;
    private Spinner year;
    private List<String> list;
    private ArrayAdapter<String> adapter;
    private CheckBox checkbox;
    private Button logout;
    private Button lockin;
    private Button uname;
    private Button ucity;
    private Button deleteDataButton;
    private boolean isChecked = false;
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
        lockin = findViewById(R.id.lockin3);
        upname = findViewById(R.id.updateName);
        upcity = findViewById(R.id.updateCity);
        uname = findViewById(R.id.uname);
        ucity = findViewById(R.id.ucity);
        checkbox = findViewById(R.id.checkbox);
        deleteDataButton = findViewById(R.id.deletedata);

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
        deleteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("drivedataSet", FieldValue.delete());
                updates.put("elecdataSet", FieldValue.delete());
                updates.put("gasdataSet", FieldValue.delete());

                db.collection("users").document(currentUser.getUid()).set(updates, SetOptions.merge());

            }
        });
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uRef.update("privacy",isChecked);

            }
        });
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
        uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uRef.update("name", upname.getText().toString());
                name.setText(upname.getText().toString());
            }
        });
        ucity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uRef.update("city", upcity.getText().toString());
                city.setText(upcity.getText().toString());
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
                    boolean isPrivacyChecked = documentSnapshot.getBoolean("privacy");
                    checkbox.setChecked(isPrivacyChecked);
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