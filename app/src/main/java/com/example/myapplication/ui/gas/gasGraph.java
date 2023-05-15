package com.example.myapplication.ui.gas;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
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
import java.util.Map;

public class gasGraph extends Fragment {
    TextView fefficiency;
    double conversion = 5443.11;
    int clockwork = 0;
    String theCar;
    String userId;
    LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Label");
    EditText distanceEditText;
    Button calculateButton;
    Button last7;
    TextView resultTextView;
    LineChart lineChart;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gas_graph,container,false);

        distanceEditText = root.findViewById(R.id.distanceEditText3);
        fefficiency = root.findViewById(R.id.fueleff3);
        calculateButton = root.findViewById(R.id.calculateButton3);
        resultTextView = root.findViewById(R.id.resultTextView3);
        lineChart = root.findViewById(R.id.lineChart3);
        last7 = root.findViewById(R.id.last7button3);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getInstance().getCurrentUser().getUid();
        userRef = db.collection("users").document(userId);

        InputFilter inputFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i)) && source.charAt(i) != ',' && source.charAt(i) != '.') {
                        return "";
                    }
                }
                return null;
            }
        };
        distanceEditText.setFilters(new InputFilter[] { inputFilter });
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String dataSetStr = documentSnapshot.getString("gasdataSet");
                    fefficiency.setText("Average Emission per Therm: " + String.format("%.2f", conversion));
                    if (dataSetStr != null) {
                        stringToLineDataSet(dataSetStr);
                    }
                }
            }
        });
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceEditText.getText().toString().isEmpty()) {
                    resultTextView.setText("Error please input a distance");
                    return;
                }
                String[] values = distanceEditText.getText().toString().split(",");
                for (int i = 0; i < values.length;i++)
                {
                    final float distance = (float) Double.parseDouble(values[i]);
                    final float fuelEfficiency = (float) conversion;
                    float co2Emissions = (distance * (fuelEfficiency));
                    try {
                        float xLast = 0;
                        if (dataSet.getEntryCount() > 0) {
                            xLast = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX();
                        }
                        Entry entry = new Entry(xLast + 1, co2Emissions);
                        dataSet.addEntry(entry);


                    } catch (Exception e) {
                        resultTextView.setText("Error");
                    }
                    ValueFormatter customFormatter = new ValueFormatter() {
                        private final DecimalFormat format = new DecimalFormat("#.##");
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            return format.format(value);
                        }
                    };

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("gasdataSet", dataSet.toString());
                    float sum = 0;
                    int count = 0;

                    for (Entry tentry : dataSet.getValues()) {
                        sum += tentry.getY();
                        count++;
                    }

                    float average = sum / count;

                    userData.put("gasaverage", average);

                    userRef.update(userData);

                    LineData lineData = new LineData(dataSet);
                    dataSet.setDrawValues(false);
                    dataSet.setDrawCircles(false);
                    dataSet.setLineWidth(2f);
                    dataSet.setColor(Color.GREEN);
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                    distanceEditText.setText("");
                }



            }
        });
        last7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (dataSet != null && dataSet.getEntryCount() > 0)
                {
                    dataSet.removeEntry(dataSet.getEntryCount()-1);
                    LineData lineData = new LineData(dataSet);
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
            ValueFormatter customFormatter = new ValueFormatter() {
                private final DecimalFormat format = new DecimalFormat("#.##");
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return format.format(value);
                }
            };

            dataSet = new LineDataSet(entries, "");
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(false);
            dataSet.setLineWidth(2f);
            dataSet.setColor(Color.GREEN);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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
            lineChart.setData(new LineData(dataSet));
            lineChart.invalidate();
        }
        else{
            dataSet = new LineDataSet(new ArrayList<>(), "Label");
            resultTextView.setText("It's empty");
        }
    }
}