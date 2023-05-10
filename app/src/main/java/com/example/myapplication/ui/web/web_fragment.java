package com.example.myapplication.ui.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
public class web_fragment extends Fragment {

    private SharedPreferences qpref;

    private SharedPreferences preferences;
    private TextView debug;
    private WebView webView;
    private TextView clock;
    private final long startTime = 3600000; // 24 hours in milliseconds
    private final long interval = 1000; // 1 second in milliseconds
    private CountDownTimer countDownTimer;
    ArrayList<String> list = new ArrayList<>();
    public web_fragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_web_fragment,container,false);
        debug = rootView.findViewById(R.id.TestView);
        webView = rootView.findViewById(R.id.web);
        clock = rootView.findViewById(R.id.Test_text);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        qpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Enable JavaScript in the WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation
        list.add("travel");
        list.add("food");
        list.add("home");
        list.add("work");
        list.add("cleaning up");
        list.add("driving");
        list.add("cars");

        String query = qpref.getString("key", "Carbon Footprint Reducing Tips");

        loadUrlFromGoogle(query);

        // Load a default URL in the WebView
        webView.loadUrl("https://www.austintexas.gov/blog/top-10-ways-reduce-your-carbon-footprint-and-save-money");


        clock = rootView.findViewById(R.id.Test_text);
        long timeLeft = preferences.getLong("timeLeft", startTime);

        //----------------------------------------------------------------import bit to modify because this is just for testing v ------
        //startTimer(timeLeft);
        editor.putLong("timeLeft", startTime);
        editor.apply();
        preferences.edit().putLong("timeLeft", startTime).apply();
        startTimer(startTime);
        //------------------------------------------------------------------------------------------------------------------------------

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
                SharedPreferences.Editor editor = qpref.edit();
                int index = (int)(Math.random() * (list.size() - 1));
                String query = "Tips reduce carbon footprint " + list.get(index);
                debug.setText("Lucky number: " + index);
                loadUrlFromGoogle(query);
                editor.putString("key", query);
                editor.apply();
                preferences.edit().putLong("timeLeft", startTime).apply();
                startTimer(preferences.getLong("timeLeft",startTime));
            }
        }.start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    private void loadUrlFromGoogle(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Build the URL for the Custom Search JSON API request
                    String cx = "22b68542a8daf40da"; // Replace with your own Custom Search Engine ID
                    String key = "AIzaSyAl1hZ8rTfmSfV-l052tigB3jwUneGCJ64"; // Replace with your own API key
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