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

import com.example.androidmobileclient.command.Command;
import com.example.androidmobileclient.command.CommandController;
import com.example.androidmobileclient.command.IrrigationControllerObject;
import com.example.androidmobileclient.databinding.ActivityIrrigationControlBinding;
import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantController;

import java.util.ArrayList;
import java.util.List;

public class Activity_IrrigationControl extends AppCompatActivity {

    private ActivityIrrigationControlBinding binding;

    private Adapter_Plant adapter;
    private List<Plant> plantList;
    private  IrrigationControllerObject targetObject = null;
    private String systemID ;
    private String userEmail;
    private final String irrigationControlUserEmail = "irrigation@default.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIrrigationControlBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        systemID = getIntent().getStringExtra("systemID");
        userEmail = getIntent().getStringExtra("userEmail");

        binding.BTNWater.setOnClickListener(v->waterPlantsClicked());

        initList();
        initTargetObject();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
        initTargetObject();
    }

    private void initList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.LSTPlantsToWater.setLayoutManager(gridLayoutManager);

        plantList = new ArrayList<>();

        adapter = new Adapter_Plant(this,plantList);
        adapter.setListener((plant, position) -> plantClicked(plant));
        binding.LSTPlantsToWater.setAdapter(adapter);
    }

    private void initTargetObject(){
        PlantController plantController = new PlantController();
        plantController.getIrrigationControlObject(systemID, userEmail, new PlantController.MyCallBack<List<IrrigationControllerObject>>() {
            @Override
            public void ready(List<IrrigationControllerObject> data) {
                Log.d("ptttt","data: "+data);
                if(!data.isEmpty()) {
                    targetObject = data.get(0);
                    updateList();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateList() {
        CommandController commandController = new CommandController();

        Command getPlantsForWateringCommand = new Command()
                .setCommand("Get_plants_for_watering")
                .setTargetObject(targetObject.getObjectId().systemID,targetObject.getObjectId().id)
                .setInvokedBy(systemID, irrigationControlUserEmail);

        commandController.invokeCommand(getPlantsForWateringCommand, new CommandController.MyCallBack<List<Plant>>() {
            @Override
            public void ready(List<Plant> data) {
                Log.d("ptttt","data irrigation: " + data);
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
        intent.putExtra("plantSystemID", plant.getObjectId().systemID);
        intent.putExtra("plantId", plant.getObjectId().id);
        intent.putExtra("userSystemID", systemID);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }

    private void waterPlantsClicked() {
        CommandController commandController = new CommandController();

        Command waterPlantsCommand = new Command()
                .setCommand("Water_plants")
                .setTargetObject(targetObject.getObjectId().systemID,targetObject.getObjectId().id)
                .setInvokedBy(systemID, irrigationControlUserEmail);

        commandController.invokeCommand(waterPlantsCommand, new CommandController.MyCallBack<List<Plant>>() {
            @Override
            public void ready(List<Plant> data) {
                Log.d("ptttt","data irrigation: " + data);
                if(!data.isEmpty()) {
                    if(data.get(0).getObjectId() != null){
                        updateListUI(data);
                    }
                }else {
                    updateListUI(data);
                    Toast.makeText(getApplicationContext(),"All plants have been watered",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }



}