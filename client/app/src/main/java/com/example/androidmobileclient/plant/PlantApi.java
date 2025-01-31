package com.example.androidmobileclient.plant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlantApi {

    @GET("/aii/objects")
    Call<List<Plant>> getAllObjects(
            @Query("userSystemID") String _userSystemID,
            @Query("userEmail") String _userEmail/*,
            @Query("size") int _size,
            @Query("page") int _page*/
    );

    @GET("/aii/objects/search/byType/Plant")
    Call<List<Plant>> getAllPlants(
            @Query("userSystemID") String _userSystemID,
            @Query("userEmail") String _userEmail,
            @Query("size") int _size,
            @Query("page") int _page
    );

    @GET("/aii/objects/{systemID}/{id}")
    Call<Plant> getPlant(
            @Path("systemID") String systemID,
            @Path("id") String id,
            @Query("userSystemID") String userSystemID,
            @Query("userEmail") String userEmail
    );

    @Headers({"Content-Type: application/json"})
    @POST("/aii/objects")
    Call<Plant> createPlant(
            @Body Plant plant
    );

    @Headers({"Content-Type: application/json"})
    @PUT("/aii/objects/{systemID}/{id}")
    Call<Plant> updatePlant(
            @Path("systemID") String systemID,
            @Path("id") String id,
            @Query("userSystemID") String userSystemID,
            @Query("userEmail") String userEmail,
            @Body Plant plant
    );
}
