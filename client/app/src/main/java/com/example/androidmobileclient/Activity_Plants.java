package com.example.androidmobileclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.androidmobileclient.databinding.ActivityPlantsBinding;
import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantController;

import java.util.ArrayList;
import java.util.List;

public class Activity_Plants extends AppCompatActivity {

    ActivityPlantsBinding binding;

    private Adapter_Plant adapter;
    private List<Plant> plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlantsBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.FABAdd.setOnClickListener(view -> addNewClicked());

        initList();
        updateList();

    }

    private void initList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.LSTPlants.setLayoutManager(gridLayoutManager);

        plantList = new ArrayList<>();

        adapter = new Adapter_Plant(plantList);
        adapter.setListener((plant, position) -> plantClicked(plant));
        binding.LSTPlants.setAdapter(adapter);
    }

    private void updateList() {
        PlantController plantController = new PlantController();

        String systemID = getIntent().getStringExtra("systemID");
        String userEmail = getIntent().getStringExtra("userEmail");

        Log.d("ptttt",systemID+"@@"+userEmail);

        plantController.getAllPlants(systemID, userEmail, new PlantController.MyCallBack<List<Plant>>() {

            @Override
            public void ready(List<Plant> data) {
                Log.d("ptttt","plants: " + data);
                updateListUI(data);
            }
            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateListUI(List<Plant> plants) {
        adapter.updateList(plants);
    }

    private void plantClicked(Plant plant) {
        Intent intent = new Intent(this, Activity_Plant.class);
        intent.putExtra("plantSystemID", plant.objectId.systemID);
        intent.putExtra("plantId", plant.objectId.id);
        intent.putExtra("userSystemID", plant.createdBy.userId.systemID);
        intent.putExtra("userEmail", plant.createdBy.userId.email);
        startActivity(intent);
    }

    private void addNewClicked() {

    }

}