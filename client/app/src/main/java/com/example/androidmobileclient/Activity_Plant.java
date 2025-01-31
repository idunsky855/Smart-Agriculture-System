package com.example.androidmobileclient;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidmobileclient.databinding.ActivityPlantBinding;
import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantController;

import android.util.Log;

public class Activity_Plant extends AppCompatActivity {

    private ActivityPlantBinding binding;

    private String plantSystemID;
    private String plantId;
    private String userSystemID;
    private String userEmail;

    private final int DELAY = 1000;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private Plant plant = null;

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

        runnable = () -> {
            updateFromServer();
            handler.postDelayed(runnable, DELAY);
        };

        binding.LAYMoisture.setOnClickListener(view -> moistureClicked(false));
        binding.LAYMoisture.setOnLongClickListener(view -> {
            moistureClicked(true);
            return true;
        });
        binding.BTNLightningP.setOnClickListener(view -> lightingClicked(true));
        binding.BTNLightningM.setOnClickListener(view -> lightingClicked(false));
        binding.BTNEdit.setOnClickListener(view -> editClicked());
    }


    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, 0);
    }


    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
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
                plant = data;
                Log.d("ptttt","data: "+data);
                updateUI();
            }
            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void updateUI() {
        if (plant == null) {
            Toast.makeText(getApplicationContext(), "no plant",Toast.LENGTH_SHORT).show();
        }

        String moisture = "NA";
        String moistureDesc= "NA";
        String lightning = "NA";
        String lightningDesc= "NA";

        try {
            moisture = plant.getCurrentSoilMoistureLevel() + ""; //plant.getObjectDetails().get("currentSoilMoistureLevel") + "";
        } catch (Exception ex) {
            Log.e("ERROR", "Error occurred :" + ex.getMessage());
        }

        try {
            moistureDesc = "optimal soil level intensity is " + plant.getOptimalSoilMoistureLevel()+"%"; //plant.getObjectDetails().get("optimalSoilMoistureLevel");
        } catch (Exception ex) {
            Log.e("ERROR", "Error occurred :" + ex.getMessage());
        }

        try {
            lightning = plant.getCurrentLightLevelIntensity() + ""; //plant.getObjectDetails().get("currentLightLevelIntensity") + "";
        } catch (Exception ex) {
            Log.e("ERROR", "Error occurred :" + ex.getMessage());
        }

        try {
            lightningDesc = "optimal light level intensity is " + plant.getOptimalLightLevelIntensity() +"%"; //plant.getObjectDetails().get("optimalLightLevelIntensity");
        } catch (Exception ex) {
            Log.e("ERROR", "Error occurred :" + ex.getMessage());
        }

        String name = plant.getAlias();
        String status = plant.getStatus();
        String location = plant.getLocation().lat + ", " + plant.getLocation().lng;


        binding.LBLName.setText(name);
        binding.LBLStatus.setText(status);
        binding.LBLLocation.setText(location);
        binding.LBLMoisture.setText(moisture);
        binding.LBLMoistureDescription.setText(moistureDesc);
        binding.LBLLightning.setText(lightning);
        binding.LBLLightningDescription.setText(lightningDesc);
    }


    private void moistureClicked(boolean toReset) {
        if (plant == null) {
            Toast.makeText(getApplicationContext(), "no plant",Toast.LENGTH_SHORT).show();
        }

        if ((boolean) plant.getObjectDetails().getOrDefault("isRaining", false)) {
            Toast.makeText(getApplicationContext(), "already raining",Toast.LENGTH_SHORT).show();
            return;
        }

        PlantController plantController = new PlantController();

        plantSystemID = getIntent().getStringExtra("plantSystemID");
        plantId = getIntent().getStringExtra("plantId");
        userSystemID = getIntent().getStringExtra("userSystemID");
        userEmail = getIntent().getStringExtra("userEmail");

        Plant temp = new Plant();
        temp.getObjectDetails().put("currentSoilMoistureLevel", plant.getOptimalSoilMoistureLevel());
        if (toReset) {
            temp.getObjectDetails().put("currentSoilMoistureLevel", 0);
        }
        // don't change the image
        temp.getObjectDetails().put("Image",this.plant.getImage());

        plantController.updatePlant(plantSystemID, plantId, userSystemID, userEmail, temp, null);

    }

    private void lightingClicked(boolean isPlus) {
        if (plant == null) {
            Toast.makeText(getApplicationContext(), "no plant",Toast.LENGTH_SHORT).show();
        }

        PlantController plantController = new PlantController();

        plantSystemID = getIntent().getStringExtra("plantSystemID");
        plantId = getIntent().getStringExtra("plantId");
        userSystemID = getIntent().getStringExtra("userSystemID");
        userEmail = getIntent().getStringExtra("userEmail");

        Plant temp = new Plant();
        Log.d("ptttt","test: "+ plant.getObjectDetails());
        Log.d("ptttt","test: "+ plant.getObjectDetails().get("currentLightLevelIntensity"));
        int value = plant.getCurrentLightLevelIntensity();
        int newValue = value + (isPlus ? 10 : -10);
        newValue = Math.min(newValue, 100);
        newValue = Math.max(newValue, 0);
        temp.getObjectDetails().put("currentLightLevelIntensity", newValue);

        // don't change the image
        temp.getObjectDetails().put("Image",this.plant.getImage());

        plantController.updatePlant(plantSystemID, plantId, userSystemID, userEmail, temp, null);
    }

    private void editClicked() {
        if (plant == null) {
            Toast.makeText(getApplicationContext(), "no plant",Toast.LENGTH_SHORT).show();
        }
    }

}