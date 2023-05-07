package com.example.myapplication.ui.gallery;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class GalleryFragment extends Fragment {
    LineDataSet dataSet = new LineDataSet(new ArrayList<>(), "Label");
    EditText distanceEditText;
    EditText fuelEditText;
    Button calculateButton;
    TextView resultTextView;
    private LineChart lineChart;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
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
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();

                String userId = mAuth.getInstance().getCurrentUser().getUid();

                lineChart = root.findViewById(R.id.lineChart);

                lineChart.setTouchEnabled(true);
                lineChart.setPinchZoom(true);
                lineChart.getDescription().setEnabled(false);
                calculateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final float distance = (float)Double.parseDouble(distanceEditText.getText().toString());
                        final float fuelEfficiency = (float)Double.parseDouble(fuelEditText.getText().toString());
                        float co2Emissions = (distance / fuelEfficiency);
                        try
                        {
                            float xLast = (dataSet.getEntryForIndex(dataSet.getEntryCount()).getX());
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("#ofentries", dataSet.getEntryForIndex((int)xLast + 1));
                            userData.put("co2Emissions", co2Emissions);

                            DocumentReference userRef = db.collection("users").document(userId);

                            Entry entry = new Entry((xLast + 1), co2Emissions);


                            dataSet.addEntry(entry);

                            LineData lineData = new LineData(dataSet);

                            lineChart.setData(lineData);

                            lineChart.invalidate();

                            userRef.set(userData);
                        }
                        catch (Exception e)
                        {
                            Entry entry = new Entry(1,co2Emissions);

                            dataSet.addEntry(entry);
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
        binding = null;
    }
}