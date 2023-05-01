package com.example.myapplication.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginActivity#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences mPrefs;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mLogoutButton;
    private static final String PREF_FIRST_LOGIN = "first_login";
    public static LoginActivity newInstance(String param1, String param2) {
        LoginActivity fragment = new LoginActivity();
        Bundle args = new Bundle();
        return fragment;
    }

    public LoginActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super.onCreate() first

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // check if the user has already logged in before
        if (mPrefs.getBoolean(PREF_FIRST_LOGIN, true)) {
            setContentView(R.layout.activity_login);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(PREF_FIRST_LOGIN, false);
            editor.apply();
        } else {
            // navigate to the main activity if the user has already logged in before
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        mLogoutButton = findViewById(R.id.logout); // add logout button

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
                                    // handle login failure
                                }
                            }
                        });
            }
        });

        // add listener for logout button
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut(); // sign out of Firebase
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(PREF_FIRST_LOGIN, true); // reset first login preference
                editor.apply();
                startActivity(new Intent(LoginActivity.this, LoginActivity.class)); // restart LoginActivity
                finish();
            }
        });
    }
    private void createUserInFirebase(FirebaseUser user) {
        // create user instance in Firebase database
    }
}