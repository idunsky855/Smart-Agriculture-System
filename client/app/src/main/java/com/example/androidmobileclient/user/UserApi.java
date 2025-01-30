package com.example.androidmobileclient.user;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {

    @GET("/aii/users/login/{systemID}/{userEmail}")
    Call<User> userLogin(
            @Path(value = "systemID", encoded = true) String _systemID,
            @Path(value = "userEmail", encoded = true) String _userEmail
    );

    //@GET("raw/Wd7qE5eM")
    //Call<User> userLogin();

    @Headers({"Content-Type: application/json"})
    @POST("/aii/users")
    Call<User> userSignUp(@Body SignUpModel feature);
}
