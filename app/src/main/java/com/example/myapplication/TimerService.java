package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

public class TimerService extends Service {
    private final long startTime = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    private final long interval = 1000; // 1 second in milliseconds
    private CountDownTimer countDownTimer;
    private SharedPreferences preferences;
    private long timeLeft;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        timeLeft = preferences.getLong("timeLeft", startTime);
        startTimer(timeLeft);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        preferences.edit().putLong("timeLeft", timeLeft).apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer(long timeLeft) {
        countDownTimer = new CountDownTimer(timeLeft, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                final long finalTimeLeft = millisUntilFinished;
                preferences.edit().putLong("timeLeft", finalTimeLeft).apply();
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        };

        countDownTimer.start();
    }
}