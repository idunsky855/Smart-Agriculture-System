package com.example.androidmobileclient.command;

import com.example.androidmobileclient.plant.Plant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CommandApi {

    @Headers({"Content-Type: application/json"})
    @POST("/aii/commands")
    Call<List<Plant>> invokeCommand(
            @Body Command command
    );
}
