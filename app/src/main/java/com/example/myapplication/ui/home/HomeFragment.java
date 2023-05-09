package com.example.myapplication.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.login.LoginActivity;
import com.example.myapplication.ui.signup.SignUp;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{

    LineChart lineChart;
    LineDataSet dataSet1 = new LineDataSet(new ArrayList<Entry>(), "DataSet 1");
    LineDataSet dataSet2 = new LineDataSet(new ArrayList<Entry>(), "DataSet 2");
    LineDataSet dataSet3 = new LineDataSet(new ArrayList<Entry>(), "DataSet 3");

    private FragmentHomeBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        lineChart = root.findViewById(R.id.aggreChart);
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        ValueFormatter customFormatter = new ValueFormatter() {
            private final DecimalFormat format = new DecimalFormat("#.##");
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return format.format(value);
            }
        };
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                dataSet1 = new LineDataSet(new ArrayList<Entry>(), "DataSet 1");
                dataSet2 = new LineDataSet(new ArrayList<Entry>(), "DataSet 2");
                dataSet3 = new LineDataSet(new ArrayList<Entry>(), "DataSet 3");
                if (documentSnapshot.exists())
                {
                    String drivedataSetStr = documentSnapshot.getString("drivedataSet");
                    String elecdataSetStr = documentSnapshot.getString("elecdataSet");
                    String gasdataSetStr = documentSnapshot.getString("gasdataSet");
                    if (drivedataSetStr != null) {
                        dataSet1 = stringToLineDataSet(drivedataSetStr);
                        dataSet1.setColor(Color.RED);
                        dataSet1.setValueTextColor(Color.BLACK);
                        dataSet1.setDrawValues(false);
                        dataSet1.setDrawCircles(false);
                        dataSet1.setLineWidth(2f);
                        dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    }
                    if (elecdataSetStr != null)
                    {
                        dataSet2 = stringToLineDataSet(elecdataSetStr);
                        dataSet2.setColor(Color.BLUE);
                        dataSet2.setValueTextColor(Color.BLACK);
                        dataSet2.setDrawValues(false);
                        dataSet2.setDrawCircles(false);
                        dataSet2.setLineWidth(2f);
                        dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    }
                    if (gasdataSetStr != null)
                    {
                        dataSet3 = stringToLineDataSet(gasdataSetStr);
                        dataSet3.setColor(Color.GREEN);
                        dataSet3.setValueTextColor(Color.BLACK);
                        dataSet3.setDrawValues(false);
                        dataSet3.setDrawCircles(false);
                        dataSet3.setLineWidth(2f);
                        dataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    }
                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setAxisMinimum(0f);
                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setValueFormatter(customFormatter);

                    YAxis rightAxis = lineChart.getAxisRight();
                    rightAxis.setValueFormatter(customFormatter);
                    lineChart.setVisibleXRangeMinimum(1);
                    lineChart.getLegend().setEnabled(false);

                    LineData lineData = new LineData(dataSet1, dataSet2, dataSet3);

                    lineChart.setData(lineData);
                    lineChart.invalidate();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private LineDataSet stringToLineDataSet(String dataSetString) {
        if (dataSetString != null && !dataSetString.isEmpty()) {
            String[] parts = dataSetString.split("Entry, ");
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                String[] values = parts[i].split(": ");
                float x = Float.parseFloat(values[1].split(" ")[0]);
                float y = Float.parseFloat(values[2]);
                entries.add(new Entry(x, y));
            }

            LineDataSet dataSet = new LineDataSet(entries, "");
            return dataSet;
        }
        else{
            LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Label");
            return dataSet;
        }
    }
}