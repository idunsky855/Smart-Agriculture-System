package com.example.androidmobileclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidmobileclient.databinding.ActivityEditPlantBinding;
import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantController;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class Activity_EditPlant extends AppCompatActivity {

    ActivityEditPlantBinding binding;

    private String plantSystemID;
    private String plantId;
    private String userSystemID;
    private String userEmail;

    private Plant plant = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPlantBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.BTNDiscardChanges.setOnClickListener(view -> saveChangesClicked());

        binding.txtEditAlias.addTextChangedListener(addPlantWatcher);
        binding.ddEditStatus.addTextChangedListener(addPlantWatcher);
        binding.txtEditLocationLat.addTextChangedListener(addPlantWatcher);
        binding.txtEditLocationLng.addTextChangedListener(addPlantWatcher);
        binding.txtEditOptimalMoistureLvl.addTextChangedListener(addPlantWatcher);
        binding.txtEditOptimalLightIntensity.addTextChangedListener(addPlantWatcher);

        binding.BTNSaveChanges.setOnClickListener(view -> saveChangesClicked());
        binding.BTNDiscardChanges.setOnClickListener(view -> discardChangesClicked());

        String[] items = new String[] {
                "Sprout",
                "Seedling",
                "Vegetative",
                "Budding",
                "Flowering",
                "Ripening"
        };
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, items);
        ((MaterialAutoCompleteTextView) binding.EDTEditStatus.getEditText()).setAdapter(adapter);

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
                plant = data;
                Log.d("ptttt","data_for_edit: "+data);
                updateUI();
            }
            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void saveChangesClicked() {
        PlantController plantController = new PlantController();

        String alias = binding.EDTEditAlias.getEditText().getText().toString().trim();
        String status = binding.EDTEditStatus.getEditText().getText().toString().trim();
        double lat = Double.parseDouble( binding.EDTEditLocationLat.getEditText().getText().toString());
        double lng = Double.parseDouble( binding.EDTEditLocationLng.getEditText().getText().toString());
        int optimalMoistureLvl = Integer.parseInt(binding.EDTEditOptimalMoistureLvl.getEditText().getText().toString());
        int optimalLightIntensity = Integer.parseInt(binding.EDTEditOptimalLightIntensity.getEditText().getText().toString());
        boolean active = binding.switchEditActive.isChecked();

        if(optimalMoistureLvl > 100 || optimalLightIntensity > 100)
            Toast.makeText(this,"Invalid input",Toast.LENGTH_SHORT).show();
        else {
            Plant updatedPlant = new Plant();
            if (!alias.isBlank() && !status.isBlank() /*&& !lat.isBlank()  && !lng.isBlank() && !optimalMoistureLvl.isBlank() && !optimalLightIntensity.isBlank()*/) {
                updatedPlant = new Plant()
                        .setType("Plant")
                        .setAlias(alias)
                        .setStatus(status)
                        .setLocation(lat, lng)
                        .setOptimalSoilMoistureLevel(optimalMoistureLvl)
                        .setOptimalLightLevelIntensity(optimalLightIntensity)
                        .setActive(active);

                plantController.updatePlant(plantSystemID, plantId, userSystemID, userEmail, updatedPlant, new PlantController.MyCallBack<Void>() {
                    @Override
                    public void ready(Void data) {
                        plantUpdated();
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    private void updateUI() {
        if (plant == null) {
            Toast.makeText(getApplicationContext(), "no plant",Toast.LENGTH_SHORT).show();
        }

        String optimalMoisture = "0";
        String optimalLighting = "0";

        try {
            optimalMoisture = plant.getOptimalSoilMoistureLevel() + "";
        } catch (Exception ex) {
            Log.e("ERROR", "Error occurred :" + ex.getMessage());
        }

        try {
            optimalLighting = plant.getOptimalLightLevelIntensity() + "";
        } catch (Exception ex) {
            Log.e("ERROR", "Error occurred :" + ex.getMessage());
        }

        String name = plant.getAlias();
        String status = plant.getStatus();
        String lat = String.valueOf(plant.getLocation().lat);
        String lng = String.valueOf(plant.getLocation().lng);

        binding.txtEditAlias.setText(name);
        binding.ddEditStatus.setText(status);
        binding.txtEditLocationLat.setText(lat);
        binding.txtEditLocationLng.setText(lng);
        binding.txtEditOptimalMoistureLvl.setText(optimalMoisture);
        binding.txtEditOptimalLightIntensity.setText(optimalLighting);
        binding.switchEditActive.setChecked(plant.isActive());
    }


    private void plantUpdated() {
        Toast.makeText(this, plant.getAlias() + " updated successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Activity_Plant.class);
        intent.putExtra("plantSystemID", plantSystemID);
        intent.putExtra("plantId", plantId);
        intent.putExtra("userSystemID", userSystemID);
        intent.putExtra("userEmail", userEmail);
        //startActivity(intent);
        finish();
    }

    private void discardChangesClicked() {
        Toast.makeText(this, "discard changes", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Activity_Plant.class);
        intent.putExtra("plantSystemID", plantSystemID);
        intent.putExtra("plantId", plantId);
        intent.putExtra("userSystemID", userSystemID);
        intent.putExtra("userEmail", userEmail);
        //startActivity(intent);
        finish();
    }

    TextWatcher addPlantWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            String alias = binding.EDTEditAlias.getEditText().getText().toString();
            String status = binding.EDTEditStatus.getEditText().getText().toString();
            String lat = binding.EDTEditLocationLat.getEditText().getText().toString();
            String lng = binding.EDTEditLocationLng.getEditText().getText().toString();
            String optimalMoistureLvl = binding.EDTEditOptimalMoistureLvl.getEditText().getText().toString();
            String optimalLightIntensity = binding.EDTEditOptimalLightIntensity.getEditText().getText().toString();
            binding.BTNDiscardChanges.setEnabled(!alias.isBlank() && !status.isBlank() && !lat.isBlank()  && !lng.isBlank() && !optimalMoistureLvl.isBlank() && !optimalLightIntensity.isBlank());

        }

        @Override
        public void afterTextChanged(Editable editable)
        {
        }
    };
}