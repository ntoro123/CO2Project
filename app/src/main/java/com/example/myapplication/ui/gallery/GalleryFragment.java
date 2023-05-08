package com.example.myapplication.ui.gallery;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryFragment extends Fragment {

    String userId;
    LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Label");
    EditText distanceEditText;
    EditText fuelEditText;
    Button calculateButton;
    TextView resultTextView;
    LineChart lineChart;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference userRef;
    private FragmentGalleryBinding binding;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                binding = FragmentGalleryBinding.inflate(inflater, container, false);
                View root = binding.getRoot();

                distanceEditText = root.findViewById(R.id.distanceEditText);
                fuelEditText = root.findViewById(R.id.fuelEditText);
                calculateButton = root.findViewById(R.id.calculateButton);
                resultTextView = root.findViewById(R.id.resultTextView);
                lineChart = root.findViewById(R.id.lineChart);
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();

                userId = mAuth.getInstance().getCurrentUser().getUid();
                userRef = db.collection("users").document(userId);

                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String dataSetStr = documentSnapshot.getString("dataSet");
                            if (dataSetStr != null) {
                                stringToLineDataSet(dataSetStr);

                            }
                        }
                    }
                });

                ValueFormatter customFormatter = new ValueFormatter() {
                    private final DecimalFormat format = new DecimalFormat("#.##");
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return format.format(value);
                    }
                };

                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setValueFormatter(customFormatter);

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setValueFormatter(customFormatter);


                calculateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (distanceEditText.getText().toString().isEmpty() || fuelEditText.getText().toString().isEmpty()) {
                            resultTextView.setText("Error please input values");
                            return;
                        }
                        final float distance = (float) Double.parseDouble(distanceEditText.getText().toString());
                        final float fuelEfficiency = (float) Double.parseDouble(fuelEditText.getText().toString());
                        float co2Emissions = (distance / fuelEfficiency);

                        try {
                            float xLast = 0;
                            if (dataSet.getEntryCount() > 0) {
                                xLast = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX();
                            }
                            Entry entry = new Entry(xLast + 1, co2Emissions);
                            dataSet.addEntry(entry);

                            LineData lineData = new LineData(dataSet);
                            lineChart.setData(lineData);
                            lineChart.invalidate();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("dataSet", dataSet.toString());

                            float sum = 0;
                            int count = 0;

                            for (Entry tentry : dataSet.getValues()) {
                                sum += tentry.getY();
                                count++;
                            }

                            float average = sum / count;

                            userData.put("average", average);

                            userRef.set(userData);

                        } catch (Exception e) {
                            resultTextView.setText("Error");
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
    private void stringToLineDataSet(String dataSetString) {
        if (dataSetString != null && !dataSetString.isEmpty()) {
            String[] parts = dataSetString.split("Entry, ");
            ArrayList<Entry> entries = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                String[] values = parts[i].split(": ");
                float x = Float.parseFloat(values[1].split(" ")[0]);
                float y = Float.parseFloat(values[2]);
                entries.add(new Entry(x, y));
            }

            dataSet = new LineDataSet(entries, "");
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(2f);
            dataSet.setColor(Color.RED);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setAxisMinimum(0f);
            lineChart.getLegend().setEnabled(false);
            lineChart.setData(new LineData(dataSet));
            lineChart.invalidate();
        }
        else{
            dataSet = new LineDataSet(new ArrayList<>(), "Label");
            resultTextView.setText("It's empty");
        }
    }
}