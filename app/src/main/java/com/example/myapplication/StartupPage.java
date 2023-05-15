package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.login.LoginActivity;

public class StartupPage extends AppCompatActivity {

    private static final int FADE_OUT_ANIMATION_DURATION = 2500; // duration of fade-out animation in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);

        // get the layout to apply the animation to
        LinearLayout layout = findViewById(R.id.main_layout);

        // create the fade-out animation
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
        fadeOutAnimation.setDuration(FADE_OUT_ANIMATION_DURATION);

        // apply the fade-out animation to the layout
        layout.startAnimation(fadeOutAnimation);

        // start the LoginActivity after the animation finishes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartupPage.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, FADE_OUT_ANIMATION_DURATION);
    }
}