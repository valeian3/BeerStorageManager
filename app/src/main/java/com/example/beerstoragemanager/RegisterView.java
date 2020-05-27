package com.example.beerstoragemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerstoragemanager.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterView extends AppCompatActivity {

    private static final String TAG = "RegisterView";

    TextView tvIdAlreadyHaveAccount;
    EditText etIdName, etIdEmail, etIdUsername, etIdPassword;
    Button btnSignUp;

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etIdName = findViewById(R.id.register_etIdName);
        etIdEmail = findViewById(R.id.register_etIdEmail);
        etIdUsername = findViewById(R.id.register_etIdUsername);
        etIdPassword = findViewById(R.id.register_etIdPassword);
        tvIdAlreadyHaveAccount = findViewById(R.id.register_tvIdAlreadyHaveAccount);
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

        btnSignUp = findViewById(R.id.register_btnIdSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();
            }
        });
        AlreadyHaveAccount();
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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy executed.");
        finishAndRemoveTask();
    }

    private void RegisterUser(){
        String name = etIdName.getText().toString().trim();
        String email = etIdEmail.getText().toString().trim();
        String username = etIdUsername.getText().toString().trim();
        String password = etIdPassword.getText().toString();

        if(name.isEmpty()){
            etIdName.setError("Name is required");
            etIdName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etIdEmail.setError("Email is required");
            etIdEmail.requestFocus();
            return;
        }
        if(username.isEmpty()){
            etIdUsername.setError("Username is required");
            etIdUsername.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etIdPassword.setError("Password is required");
            etIdPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etIdEmail.setError("Please enter a valid email");
            etIdEmail.requestFocus();
            return;
        }
        if(password.length() <= 6){
            etIdPassword.setError("Minimum length of password should be 6");
            etIdPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(TAG, "User registered successfully.");
                    Intent explicitIntent = new Intent();
                    explicitIntent.setClass(getApplicationContext(), MainActivity.class);
                    startActivity(explicitIntent);
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        displayToast("This account already exist.");
                    }else{
                        displayToast(task.getException().getMessage());
                    }
                }
            }
        });

        databaseReference = database.getReference().child("User");
        if(!TextUtils.isEmpty(name)){

            String id = databaseReference.push().getKey();

            user = new User(id, name, email, username, password);
            databaseReference.child(id).setValue(user);
            Log.i(TAG, "User inserted into database");
        } else {
            Log.i(TAG, "User is not inserted into database.");
        }
    }

    private void AlreadyHaveAccount(){
        tvIdAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent explicitIntent = new Intent();
                explicitIntent.setClass(getApplicationContext(), MainActivity.class);
                startActivity(explicitIntent);
            }
        });
    }

    private void displayToast(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
