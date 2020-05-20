package com.example.beerstoragemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    FirebaseAuth mAuth;
    EditText etIdEmail, etIdPassword;
    Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate executed.");

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart executed.");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart executed.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume executed.");

        btnLogin = findViewById(R.id.login_btnIdLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        registerUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause executed.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop executed.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
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
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), HomeView.class);
                    startActivity(explicitIntent);
                    Log.i(TAG, "Logged in successfully.");
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
                Log.i(TAG, "Registration view opened.");
            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
