package com.example.androidmobileclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidmobileclient.databinding.ActivityMenuBinding;
import com.google.android.material.textview.MaterialTextView;

public class Activity_Menu extends AppCompatActivity {

    private ActivityMenuBinding binding;
    private MaterialTextView LBL_user;

    private String systemID;
    private String userEmail;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.BTNNewPlant.setOnClickListener(v -> addNewPlant());
        binding.BTNViewAllPlant.setOnClickListener(v -> viewAllPlants());
        binding.BTNNewSenor.setOnClickListener(v -> newSenor());
        binding.BTNViewSystemSensors.setOnClickListener(v -> viewSystemSensors());
        binding.BTNEditProfile.setOnClickListener(v -> editProfile());

        systemID = getIntent().getStringExtra("systemID");
        userEmail = getIntent().getStringExtra("userEmail");
        username = getIntent().getStringExtra("username");

        LBL_user = binding.LBLUser;
        LBL_user.setText(getString(R.string.hello_text,username));
    }

    private void addNewPlant() {

    }

    private void viewAllPlants() {
        Intent intent = new Intent(this, Activity_Plants.class);
        intent.putExtra("systemID", systemID);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    private void newSenor() {

    }

    private void viewSystemSensors() {

    }

    private void editProfile() {

    }


}