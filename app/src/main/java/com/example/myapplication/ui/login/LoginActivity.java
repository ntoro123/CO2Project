package com.example.myapplication.ui.login;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.signup.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 *
 */
public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private TextView error;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mSignUp;
    private Button forgotpass;

    public LoginActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super.onCreate() first
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // User is already authenticated, start MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        forgotpass = findViewById(R.id.forgotpassword);
        error = findViewById(R.id.errorText);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    error.setText("Please enter your email and password");
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user.isEmailVerified()) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        error.setText("Email not verified yet, please click on the verification link sent");
                                    }
                                    // navigate to the main activity on successful login

                                } else {
                                    error.setText("Email or Password incorrect. Please try again.");
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
                startActivity(new Intent(LoginActivity.this, SignUp.class));
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailField.getText().toString().isEmpty()) {
                    error.setText("Please put your email in the space provided");
                    return;
                }
                try {
                    String email = mEmailField.getText().toString();
                    mAuth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean emailExists = !task.getResult().getSignInMethods().isEmpty();

                                    if (emailExists) {
                                        mAuth.sendPasswordResetEmail(email)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            error.setText("An email to reset your password has been sent");
                                                        } else {
                                                            error.setText("Error sending password reset email");
                                                        }
                                                    }
                                                });
                                    } else {
                                        error.setText("The email is not registered");
                                    }
                                }
                            });
                } catch (Exception e) {
                    error.setText("Invalid email format");
                }
            }
        });
    }
}
