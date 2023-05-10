package com.example.myapplication.ui.signup;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.SignupBinding;
import com.example.myapplication.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private SignupBinding binding;

    FirebaseAuth mAuth;
    EditText email;
    EditText password;
    EditText city;
    EditText name;
    TextView error;
    Button register;
    Map<String, Object> data = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        city = findViewById(R.id.city);
        name = findViewById(R.id.name);
        error = findViewById(R.id.error);
        mAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temail = email.getText().toString();
                String tpassword = password.getText().toString();

                if (temail.isEmpty() || tpassword.isEmpty() || city.getText().toString().isEmpty() || name.getText().toString().isEmpty())
                {
                    error.setText("Please fill in all entry boxes");
                    return;
                }
                try{
                    data.put("city", city.getText().toString());
                    data.put("name", name.getText().toString());
                    data.put("caryear", "2000");
                    data.put("privacy", false);
                    mAuth.fetchSignInMethodsForEmail(temail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                            if (isNewUser) {
                                mAuth.createUserWithEmailAndPassword(temail, tpassword)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    user.sendEmailVerification()
                                                            .addOnCompleteListener(emailTask -> {
                                                                if (emailTask.isSuccessful()) {
                                                                    error.setText("Verification email sent to " + user.getEmail());
                                                                } else {
                                                                    error.setText("Verification email seems to have hit an issue");
                                                                }
                                                            });
                                                    String uid = user.getUid();
                                                    DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(uid);
                                                    userRef.set(data);

                                                    startActivity(new Intent(SignUp.this, LoginActivity.class));
                                                    finish();
                                                } else {
                                                    error.setText("Error creating user. Please try again.");
                                                    // handle creating user failure
                                                }
                                            }
                                        });
                            } else {
                                error.setText("This email is already in use. Please try again with a different email.");
                            }
                        }
                    });
                }
                catch (RuntimeException e)
                {
                    error.setText("Please put make sure the email is formatted properly");
                }

            }
        });

    }

    private void createUserInFirebase(FirebaseUser user) {
        // create user instance in Firebase database
    }


}