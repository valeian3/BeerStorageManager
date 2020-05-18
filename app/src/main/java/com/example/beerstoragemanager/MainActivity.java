package com.example.beerstoragemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText etIdEmail, etIdPassword;
    Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        btnLogin = findViewById(R.id.login_btnIdLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        registerUser();
    }


    private  void loginUser(){

        etIdEmail = findViewById(R.id.login_etIdEmail);
        etIdPassword = findViewById(R.id.login_etIdPassword);

        String email = etIdEmail.getText().toString().trim();
        String password = etIdPassword.getText().toString().trim();

        if(email.isEmpty()){
            etIdEmail.setError("Email is required");
            etIdEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etIdPassword.setError("Password is required");
            etIdPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    displayToast("Logged in successfully.");
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), HomeView.class);
                    startActivity(explicitIntent);
                }else{
                    displayToast(task.getException().getMessage());
                }
            }
        });
    }

    private void registerUser(){

        btnRegister = findViewById(R.id.login_btnIdRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), RegisterView.class);
                startActivity(explicitIntent);
                displayToast("Registration page opened.");
            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
