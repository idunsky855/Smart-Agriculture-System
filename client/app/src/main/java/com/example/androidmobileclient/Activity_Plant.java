package com.example.androidmobileclient;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidmobileclient.databinding.ActivityPlantBinding;
import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantController;

public class Activity_Plant extends AppCompatActivity {

    private ActivityPlantBinding binding;

    private String plantSystemID;
    private String plantId;
    private String userSystemID;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlantBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.LBLName.setText("");
        binding.LBLStatus.setText("");
        binding.LBLLocation.setText("");
        binding.LBLMoisture.setText("");
        binding.LBLMoistureDescription.setText("");
        binding.LBLLightning.setText("");
        binding.LBLLightningDescription.setText("");

        updateFromServer();
    }

    private void updateFromServer() {
        PlantController plantController = new PlantController();

        plantSystemID = getIntent().getStringExtra("plantSystemID");
        plantId = getIntent().getStringExtra("plantId");
        userSystemID = getIntent().getStringExtra("userSystemID");
        userEmail = getIntent().getStringExtra("userEmail");

        plantController.getPlant(plantSystemID, plantId, userSystemID, userEmail, new PlantController.MyCallBack<Plant>() {
            @Override
            public void ready(Plant data) {
                updateUI(data);
            }
            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(Plant plant) {
        binding.LAYMoisture.setOnClickListener(view -> moistureClicked(plant));
        binding.LAYLighting.setOnClickListener(view -> lightingClicked(plant));
        binding.BTNEdit.setOnClickListener(view -> editClicked(plant));

        String moisture = "NA";
        String moistureDesc= "NA";
        String lightning = "NA";
        String lightningDesc= "NA";

        try {
            String s = plant.getObjectDetails().get("currentSoilMoistureLevel");
            moisture = Integer.valueOf(s) + "";
        } catch (Exception ex) {}

        try {
            String s = plant.getObjectDetails().get("optimalSoilMoistureLevel");
            moistureDesc = "optimal soil level intensity is " + Integer.valueOf(s);
        } catch (Exception ex) {}

        try {
            String s = plant.getObjectDetails().get("currentLightLevelIntensity");
            lightning = Integer.valueOf(s) + "";
        } catch (Exception ex) {}

        try {
            String s = plant.getObjectDetails().get("optimalLightLevelIntensity");
            lightningDesc = "optimal light level intensity is " + Integer.valueOf(s);
        } catch (Exception ex) {}

        String name = plant.getAlias();
        String status = plant.status;
        String location = plant.location.lat + ", " + plant.location.lng;


        binding.LBLName.setText(name);
        binding.LBLStatus.setText(status);
        binding.LBLLocation.setText(location);
        binding.LBLMoisture.setText(moisture);
        binding.LBLMoistureDescription.setText(moistureDesc);
        binding.LBLLightning.setText(lightning);
        binding.LBLLightningDescription.setText(lightningDesc);
    }

    private void moistureClicked(Plant plant) {

    }

    private void lightingClicked(Plant plant) {

    }

    private void editClicked(Plant plant) {

    }
}