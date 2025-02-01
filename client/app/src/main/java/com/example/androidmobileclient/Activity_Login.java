package com.example.androidmobileclient;

import android.os.Bundle;

import android.content.Intent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidmobileclient.databinding.ActivityLoginBinding;
import com.example.androidmobileclient.user.User;
import com.example.androidmobileclient.user.UserController;

public class Activity_Login extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        binding.BTNLogin.setOnClickListener(view -> loginClicked());
        binding.BTNSignUp.setOnClickListener(view -> signUpClicked());

        binding.IMGLogo.setOnClickListener(view -> binding.EDTEmail.getEditText().setText("sensor@default.com"));

    }

    private void signUpClicked() {
        Intent intent = new Intent(Activity_Login.this, Activity_SignUp.class);
        startActivity(intent);
        finish();
    }

    private void loginClicked() {

        String email = binding.EDTEmail.getEditText().getText().toString();

        UserController userController = new UserController();

        userController.userLogin(App.SYSTEM_ID, email, new UserController.UserCallBack<User>() {
            @Override
            public void ready(User data) {
                userReceived(data);
            }

           @Override
           public void failed(Throwable throwable) {
               Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
           }
        });


    }

    private void userReceived(User data) {
        Toast.makeText(this, data.getUsername() + " (" + data.getRole() + ") signed in", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Activity_Login.this, Activity_Menu.class);
        intent.putExtra("systemID", data.getUserId().getSystemID());
        intent.putExtra("userEmail", data.getUserId().getEmail());
        intent.putExtra("username", data.getUsername());
        startActivity(intent);
        finish();
    }
}