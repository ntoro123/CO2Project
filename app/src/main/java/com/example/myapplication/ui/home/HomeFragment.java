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
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView error;
    String startMonth;
    String endMonth;
    ValueFormatter customFormatter;
    LineChart lineChart;
    LineDataSet dataSet1 = new LineDataSet(new ArrayList<Entry>(), "DataSet 1");
    LineDataSet dataSet2 = new LineDataSet(new ArrayList<Entry>(), "DataSet 2");
    LineDataSet dataSet3 = new LineDataSet(new ArrayList<Entry>(), "DataSet 3");
    DocumentReference userRef;
    private FragmentHomeBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    List<String> emonths = Arrays.asList("All","Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner startSpinner = root.findViewById(R.id.start_month_spinner);
        Spinner endSpinner = root.findViewById(R.id.end_month_spinner);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, emonths);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startSpinner.setAdapter(monthAdapter);
        endSpinner.setAdapter(monthAdapter);

        lineChart = root.findViewById(R.id.aggreChart);
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users").document(uid);
        error = root.findViewById(R.id.errortext);

        customFormatter = new ValueFormatter() {
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
                    if (documentSnapshot.exists()) {
                        String drivedataSetStr = documentSnapshot.getString("drivedataSet");
                        String elecdataSetStr = documentSnapshot.getString("elecdataSet");
                        String gasdataSetStr = documentSnapshot.getString("gasdataSet");
                        if (drivedataSetStr != null) {
                            dataSet1 = stringToLineDataSet(drivedataSetStr);
                            dataSet1 = getMonthlyAverages(dataSet1);
                            dataSet1.setColor(Color.RED);
                            dataSet1.setValueTextColor(Color.BLACK);
                            dataSet1.setDrawValues(false);
                            dataSet1.setDrawCircles(false);
                            dataSet1.setLineWidth(2f);
                            dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        }
                        if (elecdataSetStr != null) {
                            dataSet2 = stringToLineDataSet(elecdataSetStr);
                            dataSet2.setColor(Color.BLUE);
                            dataSet2.setValueTextColor(Color.BLACK);
                            dataSet2.setDrawValues(false);
                            dataSet2.setDrawCircles(false);
                            dataSet2.setLineWidth(2f);
                            dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        }
                        if (gasdataSetStr != null) {
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
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                // Convert the index of the label to the corresponding month name
                                int index = (int) value + 1;
                                return months.get((index % 12));
                            }
                        });

                        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String startMonth = (String) parent.getItemAtPosition(position);
                                String endMonth = (String) endSpinner.getSelectedItem();
                                filterByMonth(startMonth, endMonth);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String startMonth = (String) startSpinner.getSelectedItem();
                                String endMonth = (String) parent.getItemAtPosition(position);
                                filterByMonth(startMonth, endMonth);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });

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

    private void filterByMonth(String startMonth, String endMonth) {
        if (!startMonth.equals("All") && !endMonth.equals("All")) {
            int startIndex = months.indexOf(startMonth);
            int endIndex = months.indexOf(endMonth);
            LineDataSet tempdataSet1 = new LineDataSet(new ArrayList<Entry>(), "DataSet 1");
            LineDataSet tempdataSet2 = new LineDataSet(new ArrayList<Entry>(), "DataSet 2");
            LineDataSet tempdataSet3 = new LineDataSet(new ArrayList<Entry>(), "DataSet 3");
            tempdataSet1 = getMonthlyAverages(tempdataSet1);
            tempdataSet1.setColor(Color.RED);
            tempdataSet1.setValueTextColor(Color.BLACK);
            tempdataSet1.setDrawValues(false);
            tempdataSet1.setDrawCircles(false);
            tempdataSet1.setLineWidth(2f);
            tempdataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            tempdataSet2 = getMonthlyAverages(tempdataSet2);
            tempdataSet2.setColor(Color.BLUE);
            tempdataSet2.setValueTextColor(Color.BLACK);
            tempdataSet2.setDrawValues(false);
            tempdataSet2.setDrawCircles(false);
            tempdataSet2.setLineWidth(2f);
            tempdataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            tempdataSet3 = getMonthlyAverages(tempdataSet3);
            tempdataSet3.setColor(Color.GREEN);
            tempdataSet3.setValueTextColor(Color.BLACK);
            tempdataSet3.setDrawValues(false);
            tempdataSet3.setDrawCircles(false);
            tempdataSet3.setLineWidth(2f);
            tempdataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            tempdataSet1.setValues(filterEntries(dataSet1.getValues(), startIndex, endIndex));
            tempdataSet2.setValues(filterEntries(dataSet2.getValues(), startIndex, endIndex));
            tempdataSet3.setValues(filterEntries(dataSet3.getValues(), startIndex, endIndex));
            ValueFormatter customFormatter = new ValueFormatter() {
                private final DecimalFormat format = new DecimalFormat("#.##");

                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    return format.format(value);
                }
            };
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setAxisMinimum(0f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    // Convert the index of the label to the corresponding month name
                    int index = (int) value;
                    return months.get(index % 12);
                }
            });

            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setValueFormatter(customFormatter);

            YAxis rightAxis = lineChart.getAxisRight();
            rightAxis.setValueFormatter(customFormatter);
            lineChart.setVisibleXRangeMinimum(1);
            lineChart.getLegend().setEnabled(false);

            LineData lineData = new LineData(tempdataSet1, tempdataSet2, tempdataSet3);
            lineChart.setData(lineData);
            lineChart.invalidate();
        }
        else if (startMonth.equals("All") && endMonth.equals("All"))
        {

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    dataSet1 = new LineDataSet(new ArrayList<Entry>(), "DataSet 1");
                    dataSet2 = new LineDataSet(new ArrayList<Entry>(), "DataSet 2");
                    dataSet3 = new LineDataSet(new ArrayList<Entry>(), "DataSet 3");
                    if (documentSnapshot.exists()) {
                        String drivedataSetStr = documentSnapshot.getString("drivedataSet");
                        String elecdataSetStr = documentSnapshot.getString("elecdataSet");
                        String gasdataSetStr = documentSnapshot.getString("gasdataSet");
                        if (drivedataSetStr != null) {
                            dataSet1 = stringToLineDataSet(drivedataSetStr);
                            dataSet1 = getMonthlyAverages(dataSet1);
                            dataSet1.setColor(Color.RED);
                            dataSet1.setValueTextColor(Color.BLACK);
                            dataSet1.setDrawValues(false);
                            dataSet1.setDrawCircles(false);
                            dataSet1.setLineWidth(2f);
                            dataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        }
                        if (elecdataSetStr != null) {
                            dataSet2 = stringToLineDataSet(elecdataSetStr);
                            dataSet2.setColor(Color.BLUE);
                            dataSet2.setValueTextColor(Color.BLACK);
                            dataSet2.setDrawValues(false);
                            dataSet2.setDrawCircles(false);
                            dataSet2.setLineWidth(2f);
                            dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        }
                        if (gasdataSetStr != null) {
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
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getAxisLabel(float value, AxisBase axis) {
                                // Convert the index of the label to the corresponding month name
                                int index = (int) value;
                                return months.get(index % 12);
                            }
                        });

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
        }
    }
    private List<Entry> filterEntries(List<Entry> entries, int startMonth, int endMonth) {
        List<Entry> filteredEntries = new ArrayList<>();

        // Calculate the number of months between startMonth and endMonth
        int numMonths = endMonth - startMonth + 1;
        if (numMonths < 1) {
            numMonths += 12;
        }

        // Initialize the filtered entries list with null values
        for (int i = 0; i < numMonths; i++) {
            filteredEntries.add(null);
        }

        // Iterate over the entries and add the values for the specified months
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            int month = getMonthFromXValue(entry.getX()) - 1; // Subtract 1 to use January as 0
            if (isMonthInRange(month, startMonth, endMonth)) {
                // Calculate the index of the month in the filtered entries list
                int filteredIndex = (month - startMonth + numMonths) % numMonths;
                filteredEntries.set(filteredIndex, entry);
            }
        }

        // Remove any null values from the filtered entries list
        filteredEntries.removeAll(Collections.singleton(null));

        return filteredEntries;
    }


    private int getMonthFromXValue(float xValue) {
        // Assuming xValue is an integer representing the month (January = 1, February = 2, etc.)
        return (int) xValue;
    }

    private boolean isMonthInRange(int month, int startMonth, int endMonth) {
        if (startMonth <= endMonth) {
            return month >= startMonth && month <= endMonth;
        } else {
            return month >= startMonth || month <= endMonth;
        }
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
                entries.add(new Entry(x - 1, y));
            }

            LineDataSet dataSet = new LineDataSet(entries, "");
            return dataSet;
        } else {
            LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Label");
            return dataSet;
        }
    }

    public static LineDataSet getMonthlyAverages(LineDataSet oldDataSet) {
        List<Entry> oldEntries = oldDataSet.getValues();
        List<Entry> newEntries = new ArrayList<>();

        float sum = 0;
        int count = 0;
        int daysInMonth = 0;
        int currentMonth = -1;
        int monthCounter = -1;

        for (Entry entry : oldEntries) {
            long dateInMillis = (long) entry.getX();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dateInMillis);
            int month = cal.get(Calendar.MONTH);

            if (currentMonth != month) {
                daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                currentMonth = month;
                monthCounter++;
            }

            sum += entry.getY();
            count++;

            if (count == daysInMonth) {
                float avg = sum / count;
                newEntries.add(new Entry(monthCounter++, avg));
                sum = 0;
                count = 0;
            }
        }

        if (count > 0) {
            float avg = sum / count;
            newEntries.add(new Entry(monthCounter, avg));
        }

        LineDataSet newDataSet = new LineDataSet(newEntries, oldDataSet.getLabel());
        return newDataSet;
    }


}