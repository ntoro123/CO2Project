package com.example.myapplication.ui.slideshow;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.User;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserRankingAdapter extends RecyclerView.Adapter<UserRankingAdapter.UserViewHolder> {

    protected int picker = 1;
    private List<User> userList;

    public UserRankingAdapter(List<User> userList) {
        this.userList = userList;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user.isPrivacy())
        {
            holder.userNameTextView.setText("#" + (position + 1) + " " + "Anonymous");
        }
        else {
            holder.userNameTextView.setText("#" + (position + 1) + " " + user.getName());
        }
        if (picker == 1)
        {
            holder.userDriveAverageTextView.setText("Emission Average: " + String.format("%.2f", (float)user.getDriveaverage()) + " Grams");
        }
        else if(picker == 2)
        {
            holder.userDriveAverageTextView.setText("Emission Average: " + String.format("%.2f", (float)user.getElecaverage() / 1000) + " Kg");
        }
        else if(picker == 3)
        {
            holder.userDriveAverageTextView.setText("Emission Average: " + String.format("%.2f", (float)user.getGasaverage() / 1000) + " Kg");
        }


        if (position == 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffd700"));
            holder.userNameTextView.setTextColor(Color.WHITE);
            holder.userDriveAverageTextView.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if (user.isCurrentUser()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.highlighted_user));
            holder.userNameTextView.setTextColor(Color.WHITE);
            holder.userDriveAverageTextView.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView userDriveAverageTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name);
            userDriveAverageTextView = itemView.findViewById(R.id.user_drive_average);
        }
    }

}