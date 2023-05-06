package com.example.myapplication.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences mPrefs;
    private TextView error;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mSignUp;
    private static final String PREF_FIRST_LOGIN = "first_login";

    public LoginActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super.onCreate() first
        setContentView(R.layout.activity_login);

        // check if the user has already logged in before

        mAuth = FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        error = findViewById(R.id.errorText);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    createUserInFirebase(user);
                                    // navigate to the main activity on successful login
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    error.setText("Email, Username, or Password incorrect. Please try again.");
                                    // handle login failure
                                }
                            }
                        });
            }
        });
        mSignUp = findViewById(R.id.signUp);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                createUserInFirebase(user);
                                                // navigate to the main activity on successful login
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
        });

        // add listener for logout button

    }
    private void createUserInFirebase(FirebaseUser user) {
        // create user instance in Firebase database
    }
}