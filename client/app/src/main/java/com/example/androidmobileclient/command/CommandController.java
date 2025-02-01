package com.example.androidmobileclient.command;

import static com.example.androidmobileclient.App.BASE_URL;

import android.util.Log;

import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.plant.PlantApi;
import com.example.androidmobileclient.plant.PlantController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommandController {
    private CommandController.MyCallBack<List<Plant>> myCommandCallBack;

    private CommandApi getAPI() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CommandApi CommandApi = retrofit.create(CommandApi.class);

        return CommandApi;
    }


    public void invokeCommand(Command command, CommandController.MyCallBack<List<Plant>> myCallBack){
        this.myCommandCallBack = myCallBack;

        Call<List<Plant>> call = getAPI().invokeCommand(command);

        call.enqueue(CommandCallback);
    }

    private final Callback<List<Plant>> CommandCallback = new Callback<List<Plant>>() {
        @Override
        public void onResponse(Call<List<Plant>> call, Response<List<Plant>> response) {
            Log.d("ptttt", "enter test1: " + response.body());
            if (myCommandCallBack == null) {
                Log.d("ptttt", "response: " + response.body());
                return;
            }
            if(response.code() >= 200 && response.code() < 300) {
                Log.d("ptttt", "response: " + response.body());
                myCommandCallBack.ready(response.body());
            }
            else {
                String error;
                if(response.code() == 404 || response.code() == 401){
                    error = "User is unauthorized";
                }else{
                    error = "HTTP ERROR: " + response.code() + " Bad Request";
                }
                myCommandCallBack.failed(new Exception(error));
                Log.d("ptttt", error);
            }
        }
        @Override
        public void onFailure(Call<List<Plant>> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());
            if (myCommandCallBack == null) {
                return;
            }
            myCommandCallBack.failed(throwable);
        }
    };

    public interface MyCallBack<T> {
        void ready(T data);
        void failed(Throwable throwable);
    }
}
