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

import com.example.androidmobileclient.databinding.ActivitySignupBinding;
import com.example.androidmobileclient.user.SignUpModel;
import com.example.androidmobileclient.user.USER_ROLE;
import com.example.androidmobileclient.user.User;
import com.example.androidmobileclient.user.UserController;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class Activity_SignUp extends AppCompatActivity {

    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.BTNSignUp.setOnClickListener(view -> signUpClicked());

        binding.BTNSignUp.setEnabled(false);
        binding.txtEmail.addTextChangedListener(signupWatcher);
        binding.txtUsername.addTextChangedListener(signupWatcher);
        binding.ddRole.addTextChangedListener(signupWatcher);


        String[] items = new String[] {
                "END_USER",
                "OPERATOR",
                "ADMIN",
        };
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, items);
        ((MaterialAutoCompleteTextView) binding.EDTRole.getEditText()).setAdapter(adapter);

    }

    private void signUpClicked() {
        UserController userController = new UserController();

        String email = binding.EDTEmail.getEditText().getText().toString();
        String userName = binding.EDTUserName.getEditText().getText().toString();
        String roleStr = binding.EDTRole.getEditText().getText().toString();
        if(!roleStr.isBlank() && !userName.isBlank() && !email.isBlank()) {
            USER_ROLE role = USER_ROLE.valueOf(roleStr);

            SignUpModel signUpModel = new SignUpModel()
                    .setUsername(userName)
                    .setAvatar("userNameAvatar")
                    .setEmail(email)
                    .setRole(role);

            //userController.userSignUp(signUpModel, data -> userSignedUp(data));
            userController.userSignUp(signUpModel,new UserController.UserCallBack<User>() {
                @Override
                public void ready(User data) {
                    userSignedUp(data);
                }

                @Override
                public void failed(Throwable throwable) {
                    Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void userSignedUp(User data) {
        Toast.makeText(this, data.getUsername() + " (" + data.getRole() + ") created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, Activity_Menu.class);
        intent.putExtra("systemID", data.getUserId().getSystemID());
        intent.putExtra("userEmail", data.getUserId().getEmail());
        intent.putExtra("username", data.getUsername());
        startActivity(intent);
        finish();
    }

    TextWatcher signupWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
            String email = binding.EDTEmail.getEditText().getText().toString();
            String userName = binding.EDTUserName.getEditText().getText().toString();
            String roleStr = binding.EDTRole.getEditText().getText().toString();
            binding.BTNSignUp.setEnabled(!email.isBlank() && !userName.isBlank() && !roleStr.isBlank());
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
        }
    };
}
