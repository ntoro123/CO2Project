package com.example.myapplication.ui.web;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link web_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class web_fragment extends Fragment {

    private SharedPreferences preferences;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WebView webView;
    private TextView clock;
    private final long startTime = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    private final long interval = 1000; // 1 second in milliseconds
    private CountDownTimer countDownTimer;
    public web_fragment() {
        // Required empty public constructor

    }


    public static web_fragment newInstance(String param1, String param2) {
        web_fragment fragment = new web_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_web_fragment,container,false);
        webView = rootView.findViewById(R.id.web);
        clock = rootView.findViewById(R.id.Test_text);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        // Enable JavaScript in the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation
        String query = "android studio";
        loadUrlFromGoogle(query);

        // Load a default URL in the WebView
        webView.loadUrl("https://www.google.com");

        clock = rootView.findViewById(R.id.Test_text);

        long timeLeft = preferences.getLong("timeLeft", startTime);
        startTimer(timeLeft);


        return rootView;
    }

    public void startTimer(long milliseconds) {
        new CountDownTimer(milliseconds, interval) {

            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                clock.setText(timeLeftFormatted);
                preferences.edit().putLong("timeLeft", millisUntilFinished).apply();
            }

            public void onFinish() {
                preferences.edit().putLong("timeLeft", startTime).apply();
                startTimer(preferences.getLong("timeLeft",startTime));
            }
        }.start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
    }


    private void loadUrlFromGoogle(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Build the URL for the Custom Search JSON API request
                    String cx = "22b68542a8daf40da"; // Replace with your own Custom Search Engine ID
                    String key = "AIzaSyAqxrfuuJ-gp3lo-pBmCknC6U3gxrQXJMo"; // Replace with your own API key
                    String url = "https://www.googleapis.com/customsearch/v1?q=" + URLEncoder.encode(query, "UTF-8") + "&cx=" + cx + "&key=" + key;

                    // Fetch the search results JSON from the Custom Search JSON API using HttpURLConnection and BufferedReader
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    String response = responseBuilder.toString();

                    // Parse the search results JSON and extract the first search result URL
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray items = jsonResponse.getJSONArray("items");
                    String resultUrl = items.getJSONObject(0).getString("link");

                    // Load the first search result URL in the WebView on the UI thread
                    final String finalResultUrl = resultUrl;
                    webView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(finalResultUrl);
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}