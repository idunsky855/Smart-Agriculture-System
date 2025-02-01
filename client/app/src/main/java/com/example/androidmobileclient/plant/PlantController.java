package com.example.androidmobileclient.plant;

import static com.example.androidmobileclient.App.BASE_URL;

import android.util.Log;

import com.example.androidmobileclient.command.IrrigationControllerObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantController {

    private MyCallBack<List<Plant>> myPlantsCallBack;
    private MyCallBack<Plant> myPlantCallBack;
    private MyCallBack<Void> myVoidCallback;
    private MyCallBack<List<IrrigationControllerObject>> myIrrigationControllerObjectCallback;

    private PlantApi getAPI() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PlantApi PlantApi = retrofit.create(PlantApi.class);

        return PlantApi;
    }

    public void getAllPlants(String systemID, String userEmail, MyCallBack<List<Plant>> myCallBack) {
        this.myPlantsCallBack = myCallBack;

        //Call<List<Plant>> call = getAPI().getAllObjects(systemID, userEmail);
        Call<List<Plant>> call = getAPI().getAllPlants(systemID, userEmail,100,0);

        call.enqueue(plantsCallback);
    }

    public void getPlant(String plantSystemID, String plantId, String userSystemID, String userEmail, MyCallBack<Plant> myCallBack) {
        this.myPlantCallBack = myCallBack;

        Call<Plant> call = getAPI().getPlant(plantSystemID, plantId, userSystemID, userEmail);

        call.enqueue(plantCallback);
    }

    public void addNewPlant(Plant plant, MyCallBack<Plant> myCallBack){
        this.myPlantCallBack = myCallBack;

        Call<Plant> call = getAPI().createPlant(plant);

        call.enqueue(plantCallback);
    }

    public void getIrrigationControlObject(String userSystemID, String userEmail, MyCallBack<List<IrrigationControllerObject>> myCallBack) {
        this.myIrrigationControllerObjectCallback = myCallBack;

        Call<List<IrrigationControllerObject>> call = getAPI().getIrrigationControllerObject(userSystemID, userEmail,1,0);

        call.enqueue(irrigationControlCallback);
    }


    public void updatePlant(String plantSystemID, String plantId, String userSystemID, String userEmail, Plant plant, MyCallBack<Void> myCallBack) {
        this.myVoidCallback = myCallBack;

        Call<Void> call = getAPI().updatePlant(plantSystemID, plantId, userSystemID, userEmail, plant);

        call.enqueue(updatePlantCallback);
    }


    private final Callback<Plant> plantCallback = new Callback<Plant>() {
        @Override
        public void onResponse(Call<Plant> call, Response<Plant> response) {
            Log.d("ptttt", "enter test: " + response.body());
            if (myPlantCallBack == null) {
                Log.d("ptttt", "response: " + response.body());
                return;
            }
            if(response.code() >= 200 && response.code() < 300) {
                Log.d("ptttt", "response: " + response.body());
                myPlantCallBack.ready(response.body());
            }
            else {
                String error;
                if(response.code() == 404 || response.code() == 401){
                    error = "User is unauthorized";
                }else{
                    error = "HTTP ERROR: " + response.code() + " Bad Request";
                }
                myPlantCallBack.failed(new Exception(error));
                Log.d("ptttt", error);
            }
        }

        @Override
        public void onFailure(Call<Plant> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());
           if (myPlantCallBack == null) {
               return;
           }
            myPlantCallBack.failed(throwable);
        }
    };

    private final Callback<List<Plant>> plantsCallback = new Callback<List<Plant>>() {
        @Override
        public void onResponse(Call<List<Plant>> call, Response<List<Plant>> response) {
            if (myPlantsCallBack == null) {
                return;
            }
            if(response.code() >= 200 && response.code() < 300) {
                Log.d("ptttt", "response: " + response.body());
                myPlantsCallBack.ready(response.body());
            }
            else {
                String error;
                if(response.code() == 404 || response.code() == 401){
                    error = "User is unauthorized";
                }else{
                    error = "HTTP ERROR: " + response.code() + " Bad Request";
                }
                myPlantsCallBack.failed(new Exception(error));
                Log.d("ptttt", error);
            }
        }

        @Override
        public void onFailure(Call<List<Plant>> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());
            if (myPlantsCallBack == null) {
                return;
            }
            myPlantsCallBack.failed(throwable);
        }
    };

    private final Callback<Void> updatePlantCallback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            Log.d("ptttt", "enter test: " + response.body());

            if (myVoidCallback == null) {
                Log.d("ptttt", "response: " + response.body());
                return;
            }
            if(response.code() >= 200 && response.code() < 300) {
                Log.d("ptttt", "response: " + response.body());
                myVoidCallback.ready(response.body());
            }
            else {
                String error;
                if(response.code() == 404 || response.code() == 401){
                    error = "User is unauthorized";
                }else{
                    error = "HTTP ERROR: " + response.code() + " Bad Request";
                }
                myVoidCallback.failed(new Exception(error));
                Log.d("ptttt", error);
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());
            if (myVoidCallback == null) {
                return;
            }
            myVoidCallback.failed(throwable);
        }
    };

    private final Callback<List<IrrigationControllerObject>> irrigationControlCallback = new Callback<List<IrrigationControllerObject>>() {
        @Override
        public void onResponse(Call<List<IrrigationControllerObject>> call, Response<List<IrrigationControllerObject>> response) {
            if (myIrrigationControllerObjectCallback == null) {
                return;
            }
            if(response.code() >= 200 && response.code() < 300) {
                Log.d("ptttt", "response: " + response.body());
                myIrrigationControllerObjectCallback.ready(response.body());
            }
            else {
                String error;
                if(response.code() == 404 || response.code() == 401){
                    error = "User is unauthorized";
                }else{
                    error = "HTTP ERROR: " + response.code() + " Bad Request";
                }
                myIrrigationControllerObjectCallback.failed(new Exception(error));
                Log.d("ptttt", error);
            }
        }

        @Override
        public void onFailure(Call<List<IrrigationControllerObject>> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());
            if (myIrrigationControllerObjectCallback == null) {
                return;
            }
            myPlantsCallBack.failed(throwable);
        }
    };


    public interface MyCallBack<T> {
        void ready(T data);
        void failed(Throwable throwable);
    }

}