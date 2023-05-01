package com.example.myapplication.ui.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link web_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class web_fragment extends Fragment {
    private WebView web;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public web_fragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment web_fragment.
     */
    // TODO: Rename and change types and number of parameters
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
        WebView mWebView = rootView.findViewById(R.id.web);
        mWebView.loadUrl("https://sustainability.georgetown.edu/community-engagement/things-you-can-do/");
        return rootView;
    }
}