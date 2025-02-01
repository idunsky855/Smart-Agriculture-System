package com.example.androidmobileclient.user;

import static com.example.androidmobileclient.App.BASE_URL;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserController {

    private UserCallBack<User> userCallBack;


    private UserApi getAPI() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserApi UserApi = retrofit.create(UserApi.class);

        return UserApi;
    }

    public void userLogin(String systemID, String userEmail, UserCallBack<User> userCallBack) {
        this.userCallBack = userCallBack;

        Call<User> call = getAPI().userLogin(systemID, userEmail);
//        Call<User> call = getAPI().userLogin();

        call.enqueue(RetrofitCallback);
    }

    public void userSignUp(SignUpModel signUpModel, UserCallBack<User> userCallBack) {
        this.userCallBack = userCallBack;

        Call<User> call = getAPI().userSignUp(signUpModel);

        call.enqueue(RetrofitCallback);
    }

    private Callback<User> RetrofitCallback = new Callback<>() {

        @Override
        public void onResponse(Call<User> call, Response<User> response) {

            if(response.code() >= 200 && response.code() < 300)
                userCallBack.ready(response.body());
            else {
                String error;
                if(response.code() == 404 || response.code() == 401){
                    error = "Could not find user or user is unauthorized";
                }else{
                    error = "HTTP ERROR: " + response.code() + " Bad Request";
                }
                userCallBack.failed(new Exception(error));
                Log.d("ptttt", error);
            }

        }

        @Override
        public void onFailure(Call<User> call, Throwable throwable) {
            Log.d("ptttt","error - " + throwable.getMessage());

            userCallBack.failed(throwable);
        }
    };

    public interface UserCallBack<T> {
        void ready(T data);
        void failed(Throwable throwable);
    }

}