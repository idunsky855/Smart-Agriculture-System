package com.example.androidmobileclient.plant;

import static com.example.androidmobileclient.App.BASE_URL;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantController {

    //private static final String BASE_URL = "http://192.168.1.103:8081/";

    private MyCallBack<List<Plant>> myPlantsCallBack;
    private MyCallBack<Plant> myPlantCallBack;

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

    private final Callback<Plant> plantCallback = new Callback<Plant>() {
        @Override
        public void onResponse(Call<Plant> call, Response<Plant> response) {
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
                myPlantsCallBack.failed(new Exception(error));
                Log.d("ptttt", error);
            }
        }

        @Override
        public void onFailure(Call<Plant> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());
            myPlantCallBack.failed(throwable);
        }
    };

    private final Callback<List<Plant>> plantsCallback = new Callback<List<Plant>>() {
        @Override
        public void onResponse(Call<List<Plant>> call, Response<List<Plant>> response) {
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
            myPlantsCallBack.failed(throwable);
        }
    };

    public interface MyCallBack<T> {
        void ready(T data);
        void failed(Throwable throwable);
    }

}