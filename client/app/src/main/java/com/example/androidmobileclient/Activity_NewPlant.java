package com.example.androidmobileclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidmobileclient.databinding.ActivityNewPlantBinding;
import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantController;
import com.example.androidmobileclient.user.User;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class Activity_NewPlant extends AppCompatActivity {

    ActivityNewPlantBinding binding;
    private String systemID;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPlantBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        systemID = getIntent().getStringExtra("systemID");
        userEmail = getIntent().getStringExtra("userEmail");

        binding.BTNAddPlant.setOnClickListener(view -> addPlantClicked());

        binding.BTNAddPlant.setEnabled(false);
        binding.txtAlias.addTextChangedListener(addPlantWatcher);
        binding.ddStatus.addTextChangedListener(addPlantWatcher);
        binding.txtLocationLat.addTextChangedListener(addPlantWatcher);
        binding.txtLocationLng.addTextChangedListener(addPlantWatcher);
        binding.txtOptimalMoistureLvl.addTextChangedListener(addPlantWatcher);
        binding.txtOptimalLightIntensity.addTextChangedListener(addPlantWatcher);


        String[] items = new String[] {
                "Sprout",
                "Seedling",
                "Vegetative",
                "Budding",
                "Flowering",
                "Ripening"
        };
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, items);
        ((MaterialAutoCompleteTextView) binding.EDTStstus.getEditText()).setAdapter(adapter);
    }

    private void addPlantClicked() {
        PlantController plantController = new PlantController();

        String alias = binding.EDTAlias.getEditText().getText().toString().trim();
        String status = binding.EDTStstus.getEditText().getText().toString().trim();
        double lat = Double.parseDouble( binding.EDTLocationLat.getEditText().getText().toString());
        double lng = Double.parseDouble( binding.EDTLocationLng.getEditText().getText().toString());
        int optimalMoistureLvl = Integer.parseInt(binding.EDTOptimalMoistureLvl.getEditText().getText().toString());
        int optimalLightIntensity = Integer.parseInt(binding.EDTOptimalLightIntensity.getEditText().getText().toString());
        boolean active = binding.swichActive.isChecked();

        if(!alias.isBlank() && !status.isBlank() /*&& !lat.isBlank()  && !lng.isBlank() && !optimalMoistureLvl.isBlank() && !optimalLightIntensity.isBlank()*/){
            Plant plant = new Plant()
                    .setType("Plant")
                    .setAlias(alias)
                    .setStatus(status)
                    .setLocation(lat,lng)
                    .setOptimalSoilMoistureLevel(optimalMoistureLvl)
                    .setOptimalLightLevelIntensity(optimalLightIntensity)
                    .setActive(active)
                    .setCreatedBy(systemID,userEmail);

            plantController.addNewPlant(plant,new PlantController.MyCallBack<Plant>() {
                @Override
                public void ready(Plant data) {
                    plantCreated(data);
                }

                @Override
                public void failed(Throwable throwable) {
                    Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void plantCreated(Plant plant) {
        Toast.makeText(this, plant.getAlias() + " created successfully", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, Activity_Plants.class);
        //intent.putExtra("systemID",systemID);
        //intent.putExtra("userEmail", userEmail);
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
            String alias = binding.EDTAlias.getEditText().getText().toString();
            String status = binding.EDTStstus.getEditText().getText().toString();
            String lat = binding.EDTLocationLat.getEditText().getText().toString();
            String lng = binding.EDTLocationLng.getEditText().getText().toString();
            String optimalMoistureLvl = binding.EDTOptimalMoistureLvl.getEditText().getText().toString();
            String optimalLightIntensity = binding.EDTOptimalLightIntensity.getEditText().getText().toString();
            binding.BTNAddPlant.setEnabled(!alias.isBlank() && !status.isBlank() && !lat.isBlank()  && !lng.isBlank() && !optimalMoistureLvl.isBlank() && !optimalLightIntensity.isBlank());

        }

        @Override
        public void afterTextChanged(Editable editable)
        {
        }
    };
}