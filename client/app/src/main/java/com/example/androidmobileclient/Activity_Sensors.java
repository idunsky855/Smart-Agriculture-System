package com.example.androidmobileclient;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.androidmobileclient.databinding.ActivitySensorsBinding;

import java.util.ArrayList;
import java.util.List;

public class Activity_Sensors extends AppCompatActivity {

    ActivitySensorsBinding binding;

    private Adapter_Sensor adapter;
    private List<Sensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySensorsBinding.inflate(getLayoutInflater());
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
        binding.LSTSensors.setLayoutManager(gridLayoutManager);

        sensorList = new ArrayList<>();

        adapter = new Adapter_Sensor(sensorList);
        adapter.setListener((sensor, position) -> sensorClicked(sensor));
        binding.LSTSensors.setAdapter(adapter);
    }

    private void updateList() {
        sensorList.clear();
        sensorList.add(new Sensor("Rose", ""));

        adapter.updateList(sensorList);
    }

    private void sensorClicked(Sensor plant) {
//        Intent intent = new Intent(this, Activity_Sensor.class);
//        startActivity(intent);
    }

    private void addNewClicked() {

    }

}