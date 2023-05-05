package com.example.myapplication.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class GalleryFragment extends Fragment {
    EditText cityEditText;
    EditText distanceEditText;
    EditText fuelEditText;
    Button calculateButton;
    TextView resultTextView;

    FirebaseFirestore db;
    private FragmentGalleryBinding binding;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                binding = FragmentGalleryBinding.inflate(inflater, container, false);
                View root = binding.getRoot();

                cityEditText = root.findViewById(R.id.cityEditText);
                distanceEditText = root.findViewById(R.id.distanceEditText);
                fuelEditText = root.findViewById(R.id.fuelEditText);
                calculateButton = root.findViewById(R.id.calculateButton);
                resultTextView = root.findViewById(R.id.resultTextView);

                db = FirebaseFirestore.getInstance();

                calculateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String cityName = cityEditText.getText().toString();
                        final double distance = Double.parseDouble(distanceEditText.getText().toString());
                        final double fuelEfficiency = Double.parseDouble(fuelEditText.getText().toString());

                        db.collection("cities").whereEqualTo("name", cityName).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                            double co2EmissionsFactor = documentSnapshot.getDouble("co2EmissionsFactor");
                                            double co2Emissions = (distance / fuelEfficiency) * co2EmissionsFactor;
                                            resultTextView.setText(String.format("%.2f kg", co2Emissions));
                                        } else {
                                            resultTextView.setText("City not found");
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        resultTextView.setText("Error: " + e.getMessage());
                                    }
                                });
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